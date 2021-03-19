package vttExtract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Main {
	
	// Why couldn't I just use String.matches(String)
	public static final Pattern spanRegex = Pattern.compile("\\d{2}:\\d{2}:\\d{2}\\.\\d{3} --> \\d{2}:\\d{2}:\\d{2}\\.\\d{3}");
	
	public static void main(String[] args) throws IOException {
		
		String[][] searchPhrases = new String[args.length][];
		
		if (args.length == 0) {
			System.out.println("Usage: vtt-extract [args...]");
			System.out.println("Each argument is a search phrase, with spaces replaced by + signs.");
			System.exit(0);
		}
		
		for (int i = 0; i < args.length; i++) {
			searchPhrases[i] = args[i].replaceAll("\\+", " ").split(" ");
		}
		
		List<String> results = new ArrayList<String>();
		
		File[] workDir = new File(System.getProperty("user.dir")).listFiles();
		for (File searchFile : workDir) {
			
			String videoID = searchFile.getName();
			String[] foo = videoID.split("\\.");
			String extension = foo[foo.length - 1];
			if (!extension.equals("vtt")) continue;
			
			videoID = videoID.substring(videoID.length() - 18, videoID.length() - 7);
			
			readoutFile(searchFile, videoID, results, searchPhrases);
			
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("results.txt"));
		
		System.out.println("\nCaption grep results:");
		for (String result : results) {
			System.out.println("  " + result);
			writer.write(result + "\n");
			writer.flush();
		}
		
		writer.close();
		
	}
	
	public static void readoutFile(File source, String videoID, List<String> results, String[][] phrases) throws IOException {
		System.out.print("Processing caption file: " + videoID + "...");
		
		List<String[]> cues = new ArrayList<String[]>();
		int newResults = 0;
		readoutSpans(source, cues);
		
		int[] searches = new int[phrases.length];
		for (int i = 0; i < searches.length; i++) {
			searches[i] = 0;
		}
		
		for (String[] cue : cues) {
			
			// Check for running searches
			for (int i = 0; i < searches.length; i++) {
				int search = searches[i];
				if (search >= phrases[i].length) {
					searches[i] = 0;
					results.add("Video ID: " + videoID + ", time stamp: " + cue[0] + ", search channel: " + i);
					newResults++;
					continue;
				}
				if (cue[1].equals(phrases[i][search])) {
					searches[i]++;
				} else {
					searches[i] = 0;
				}
			}
			
		}

		System.out.println(" Done, " + newResults + " filter triggers.");
	}
	
	public static void readoutSpans(File source, List<String[]> cues) throws IOException {
		
		BufferedReader reader = new BufferedReader(new FileReader(source));
		
		while (reader.ready()) {
			
			// Read span header
			String spanHeader = null;
			while (!spanRegex.matcher(spanHeader = reader.readLine()).find()) {
				if (!reader.ready()) {
					reader.close();
					return;
				}
			}
			
			// Read span data, if not blank
			reader.readLine(); // Skip existing line
			String spanData = reader.readLine();
			if (spanData.isBlank()) continue;
			
			// Process span header
			spanHeader = spanHeader.split(" ")[0];
			
			// Process span data
			spanData = spanData.replaceAll("(<c>)|(</c>)|\\s", "");
			spanData = "<" + spanHeader + ">" + spanData;
			
			// Process cues
			for (String cue : spanData.split("<")) {
				if (cue.isBlank()) continue;
				cues.add(cue.toLowerCase().split(">"));
			}
			
		}
		
		reader.close();
		
	}

}
