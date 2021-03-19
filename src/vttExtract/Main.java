package vttExtract;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
	
	public static void main(String[] args) throws IOException {
		
		long startTime = System.currentTimeMillis();
		
		List<String> results = new ArrayList<String>();
		
		String workDir = System.getProperty("user.dir");
		File[] inputs = new File(workDir).listFiles();
		for (File input : inputs) {
			processFile(input, args, results);
		}
		
		System.out.println("Filter results:");
		BufferedWriter writer = new BufferedWriter(new FileWriter("results.txt"));
		for (String result : results) {
			writer.write(result + "\n");
			writer.flush();
			System.out.println("  " + result);
		}
		writer.close();
		
		long duration = Math.round(System.currentTimeMillis() - startTime) / 1000;
		System.out.println("Process completed in " + duration + " seconds.");
		
	}
	
	public static void processFile(File source, String[] filters, List<String> results) throws IOException {

		// Open file
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
