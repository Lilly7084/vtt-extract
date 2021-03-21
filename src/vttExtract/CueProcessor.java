package vttExtract;

import java.util.List;

/**
 * @author Wesley Mays (WMays287)
 * @version 2.1
 * @since 2.1
 */
public class CueProcessor {
	
	private String videoID;
	private int newResults;
	private int filterCount;
	private String[][] filterTerms;
	private int[] filterProgress;
	String[] filterStamps;
	
	public CueProcessor(String videoID, String[] filters) {
		
		this.videoID = videoID;
		newResults = 0;
		filterCount = filters.length;
		
		// Set up filter registers
		filterTerms = new String[filterCount][];
		filterProgress = new int[filterCount];
		filterStamps = new String[filterCount];
		for (int i = 0; i < filterCount; i++) {
			filterTerms[i] = filters[i].toLowerCase().split(" ");
			filterProgress[i] = 0;
			filterStamps[i] = "00:00:00.000";
		}
		
	}
	
	/**
	 * Process a cue returned from a LineProcessor
	 * @param stamp Time stamp of the input cue
	 * @param cue Data of the input cue
	 * @param results List to contain search results
	 */
	public void processCue(String stamp, String cue, List<String> results) {
		
		// For each filter
		for (int i = 0; i < filterCount; i++) {
			
			// If the current cue is equivalent to the next item in the filter
			if (cue.equals(filterTerms[i][filterProgress[i]])) {
				
				// If the filter hasn't started yet
				if (filterProgress[i] == 0) {
					
					// Update filter stamp
					filterStamps[i] = stamp;
					
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
					result += reassembleFilter(i);
					result += "\")";
					results.add(result);
					
					// Update new result count
					newResults++;
					
				}
				
			}
			
		}
		
	}
	
	/**
	 * Get how many results have been generated
	 * @return Total generated results
	 */
	public int getNewResults() {
		return newResults;
	}
	
	/**
	 * Reassemble a filter from terms
	 * @param index Index of filter to reassemble
	 * @return Reassembled filter
	 */
	private String reassembleFilter(int index) {
		String foo = "";
		for (String term : filterTerms[index]) {
			foo += " " + term;
		}
		return foo.substring(1);
	}

}
