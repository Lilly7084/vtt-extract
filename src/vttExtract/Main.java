package vttExtract;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wesley Mays (WMays287)
 * @version 2.1
 * @since 1.0
 */
public class Main {
	
	public static void main(String[] args) throws IOException {
		
		long startTime = System.currentTimeMillis();
		
		List<String> results = new ArrayList<String>();
		
		// Handle all files in work directory
		String workDir = System.getProperty("user.dir");
		File[] inputs = new File(workDir).listFiles();
		for (File input : inputs) {
			processFile(input, args, results);
		}
		
		// De-duplicate results
		System.out.print("\nDeduplicating results...");
		Deduplicator dd = new Deduplicator(results);
		results = dd.deduplicate();
		System.out.println(" Done!");
		
		// Print results to terminal and output file
		System.out.println("\nFilter results:");
		BufferedWriter writer = new BufferedWriter(new FileWriter("results.txt"));
		for (String result : results) {
			writer.write(result + "\n");
			writer.flush();
			System.out.println("  " + result);
		}
		writer.close();
		
		long duration = Math.round(System.currentTimeMillis() - startTime) / 1000;
		System.out.println("\nTotal results: " + results.size());
		System.out.println("Process completed in " + duration + " seconds.");
		
	}
	
	/**
	 * Handler function for an arbitrary file in work directory
	 * @param source The file to be processed
	 * @param filters Array of raw search phrases
	 * @param results List to hold results
	 * @throws IOException
	 */
	public static void processFile(File source, String[] filters, List<String> results) throws IOException {
		
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
		
		// Get video ID from title (Assuming youtube-dl default name layout)
		String videoID = name.substring(name.length() - 18, name.length() - 7);
		
		System.out.print("Processing caption file: " + videoID + "...");
		
		FileProcessor processor = new FileProcessor(source, videoID, filters);
		int newResults = processor.processFile(results);
		
		System.out.println(" Done, " + newResults + " new results.");
		
	}

}
