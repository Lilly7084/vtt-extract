package vttExtract;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wesley Mays (WMays287)
 * @version 2.2
 * @since 1.0
 */
public class Main {
	
	public static void main(String[] args) throws IOException {
		
		long startTime = System.currentTimeMillis();
		
		List<String> triggers = new ArrayList<String>();
		
		// Handle all files in work directory
		String workDir = System.getProperty("user.dir");
		File[] inputs = new File(workDir).listFiles();
		for (File input : inputs) {
			processFile(input, args, triggers);
		}
		
		// De-duplicate results
		System.out.println();
		System.out.print("Deduplicating triggers...");
		int oldTriggerCount = triggers.size();
		Deduplicator dd = new Deduplicator(triggers);
		dd.run();
		triggers = dd.getData();
		System.out.println(" Done, " + triggers.size() + " remaining triggers out of " + oldTriggerCount + ".");
		
		// Print results to terminal and output file
		System.out.print("Writing triggers to file...");
		BufferedWriter writer = new BufferedWriter(new FileWriter("results.txt"));
		for (String result : triggers) {
			writer.write(result + "\n");
			writer.flush();
		}
		writer.close();
		System.out.println(" Done.");
		
		long duration = Math.round(System.currentTimeMillis() - startTime) / 1000;
		System.out.println();
		System.out.println("Process completed in " + duration + " seconds.");
		
	}
	
	/**
	 * Handler function for an arbitrary file in work directory
	 * @param source The file to be processed
	 * @param filters Array of raw search phrases
	 * @param triggers List to hold triggers
	 * @throws IOException
	 */
	public static void processFile(File source, String[] filters, List<String> triggers) throws IOException {
		
		String name = source.getName();
		
		// Get extension
		String[] foo = name.split("\\.");
		if (foo.length == 0) {
			return; // If it's a folder or has no extension
		}
		String extension = foo[foo.length - 1].toLowerCase();
		if (!extension.equals("vtt")) {
			return; // If it has the wrong extension
		}
		
		System.out.print("Processing: " + name + "...");
		
		// Get video ID from title (Assuming youtube-dl default name layout)
		String videoID = name.substring(name.length() - 18, name.length() - 7);
		
		FileProcessor fp = new FileProcessor(source, videoID, filters);
		fp.processFile(triggers);
		
		System.out.println(" Done, " + fp.getCueCount() + " cues, " + fp.getTriggerCount() + " triggers.");
		
	}

}
