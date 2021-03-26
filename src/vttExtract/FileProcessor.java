package vttExtract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Wesley Mays (WMays287)
 * @version 2.3
 * @since 2.0
 */
public class FileProcessor {
	
	public static final Pattern SPAN_REGEX = Pattern.compile("(\\d{2}:)*\\d{2}\\.\\d{3} --> (\\d{2}:)*\\d{2}\\.\\d{3}");
	
	private String videoID;
	private String[] filters;
	private BufferedReader reader;
	private int cueCount;
	private int triggerCount;
	
	/**
	 * @param source The file to be processed
	 * @param videoID The video ID being processed, used for trigger messages
	 * @param filtersA set of filter phrases
	 */
	public FileProcessor(File source, String videoID, String[] filters) {
		try {
			
			this.videoID = videoID;
			
			// Set up reader
			reader = new BufferedReader(new FileReader(source));
			
			// Set up filters
			this.filters = new String[filters.length];
			for (int i = 0; i < filters.length; i++) {
				this.filters[i] = filters[i].toLowerCase().replaceAll("[%20\\+]", " ");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Process the loaded file
	 * @param results List to hold search locations
	 * @return Number of new items added to 'results'
	 * @throws IOException
	 */
	public void processFile(List<String> results) throws IOException {
		
		// Skip to the first span header, to ignore file header
		String spanHeader = null;
		while (reader.ready() && !SPAN_REGEX.matcher(spanHeader = reader.readLine()).find()) {
			// Do nothing
		}
		
		// Process the span header, using it as the initial time tag
		String timeStamp = spanHeader.split(" ")[0];
		
		// Set up the line processor
		LineProcessor lp = new LineProcessor(timeStamp);
		
		// Set up a buffer to hold all cues
		List<String[]> cues = new ArrayList<String[]>();
		
		while (reader.ready()) {
			
			// Read next populated line in file
			String line = reader.readLine();
			if (line.isBlank()) {
				continue;
			}
			
			// If it's a span header, update time stamp accordingly
			if (SPAN_REGEX.matcher(line).find()) {
				lp.setTimestamp(line.split(" ")[0]);
				continue;
			}
			
			// Get the processor ready for this text line
			lp.resetFSM(cues);
			
			// Let the processor run for every character
			// Undocumented feature of String.split()?
			// Empty regex splits input into individual characters
			for (String chr : line.split("")) {
				lp.handleChar(chr, cues);
			}
			
		}
		
		// Let a separate script process the cues
		for (String filter : filters) {
			CueProcessor cp = new CueProcessor(videoID, filter);
			for (String[] cue : cues) {
				cp.processCue(cue[0], cue[1].toLowerCase(), results);
			}
			triggerCount += cp.getTriggerCount();
		}
		
		cueCount = cues.size();
		
	}
	
	/**
	 * Get how many cues were generated
	 * @return Total number of cues
	 */
	public int getCueCount() {
		return cueCount;
	}
	
	/**
	 * Get how many triggers were generated
	 * @return Total number of triggers
	 */
	public int getTriggerCount() {
		return triggerCount;
	}

}
