package vttExtract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FileProcessor {
	
	public static final String STAMP_REGEX = "(\\d{2}:)*\\d{2}\\.\\d{3}";
	public static final Pattern SPAN_REGEX = Pattern.compile(STAMP_REGEX + " --> " + STAMP_REGEX);
	
	private String videoID;
	private String[] filters;
	private BufferedReader reader;
	private LineProcessor processor;
	
	public FileProcessor(File source, String videoID, String[] filters) {
		try {
			
			// Set up reader
			reader = new BufferedReader(new FileReader(source));
			
			this.videoID = videoID;
			
			// Set up filters
			this.filters = filters;
			for (int i = 0; i < filters.length; i++) {
				filters[i] = filters[i].toLowerCase().replaceAll("[%20\\+]", " ");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int processFile(List<String> results) throws IOException {
		
		// Skip to the first span header, to ignore file header
		String spanHeader = readSpanHeader();
		
		// Process the span header, using it as the initial time tag
		String timeTag = spanHeader.split(" ")[0];
		
		// Set up the line processor
		processor = new LineProcessor(STAMP_REGEX, timeTag);
		
		// Set up a buffer to hold all cues
		List<String[]> cues = new ArrayList<String[]>();
		
		while (reader.ready()) {
			
			// Read next populated line in file
			String line = reader.readLine();
			if (line.isBlank()) {
				continue;
			}
			
			// If it's a span header, update time stamp accordingly
			if (isSpanHeader(line)) {
				processor.setTimestamp(line.split(" ")[0]);
				continue;
			}
			
			// Get the processor ready for this text line
			processor.resetFSM(cues);
			
			// Let the processor run for every character
			// Undocumented feature of String.split()?
			// Empty regex splits input into individual characters
			for (String chr : line.split("")) {
				processor.handleChar(chr, cues);
			}
			
		}
		
		// Set up filter registers
		String[][] filterTerms = new String[filters.length][];
		int[] filterProgress = new int[filters.length];
		String[] filterStamps = new String[filters.length];
		for (int i = 0; i < filters.length; i++) {
			filterTerms[i] = filters[i].toLowerCase().split(" ");
			filterProgress[i] = 0;
			filterStamps[i] = "00:00:00.000";
		}
		
		int newResults = 0;
		
		// Iterate over accumulated cues
		for (String[] cue : cues) {
			
			// Get current cue into checkable format
			String currentCue = cue[1].toLowerCase();
			
			// For each filter
			for (int i = 0; i < filters.length; i++) {
				
				// If the current cue is equivalent to the next item in the filter
				if (currentCue.equals(filterTerms[i][filterProgress[i]])) {
					
					// If the filter hasn't started yet
					if (filterProgress[i] == 0) {
						
						// Update filter stamp
						filterStamps[i] = cue[0];
						
					}
					
					// Update filter progress
					filterProgress[i]++;
					
					// If the filter is now done
					if (filterProgress[i] >= filterTerms[i].length) {
						
						// Reset filter progress
						filterProgress[i] = 0;
						
						// Push data to results buffer
						String result = "Video ID: ";
						result += videoID;
						result += ", time stamp: ";
						result += filterStamps[i];
						result += ", filter: ";
						result += i;
						result += " (\"";
						result += filters[i];
						result += "\")";
						results.add(result);
						
						// Update new result count
						newResults++;
						
					}
					
				}
				
			}
			
		}
		
		return newResults;
		
	}
	
	private String readSpanHeader() throws IOException {
		
		String foo = null;
		
		// Skip ahead until the next span header
		while (reader.ready() && !isSpanHeader(foo = reader.readLine())) {
			// Do nothing
		}
		
		return foo;
		
	}
	
	private boolean isSpanHeader(String data) {
		return SPAN_REGEX.matcher(data).find();
	}

}
