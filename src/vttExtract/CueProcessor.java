package vttExtract;

import java.util.List;

/**
 * @author Wesley Mays (WMays287)
 * @version 2.2
 * @since 2.1
 */
public class CueProcessor {
	
	/**
	 * The video ID being processed, used for trigger messages
	 */
	private String videoID;
	
	/**
	 * How many triggers have been generated
	 */
	private int triggerCount;
	
	/**
	 * How many filters are being processed
	 */
	private int filterCount;
	
	/**
	 * Search-term chains for each filter
	 */
	private String[][] filterTerms;
	
	/**
	 * Progress values for each filter
	 */
	private int[] filterProgress;
	
	/**
	 * Time stamps for each filter
	 */
	String[] filterStamps;
	
	public CueProcessor(String videoID, String[] filters) {
		
		// Set up other variables
		this.videoID = videoID;
		triggerCount = 0;
		filterCount = filters.length;
		
		// Set up filter registers
		filterTerms = new String[filterCount][];
		filterProgress = new int[filterCount];
		filterStamps = new String[filterCount];
		for (int i = 0; i < filterCount; i++) {
			
			// filterTerms - Search-term chain
			filterTerms[i] = filters[i].toLowerCase().split(" ");
			
			// filterProgress - Progress value
			filterProgress[i] = 0;
			
			// filterStamps - Time stamp
			filterStamps[i] = "00:00:00.000";
			
		}
		
	}
	
	/**
	 * Process a cue returned from a LineProcessor
	 * @param stamp Time stamp of the input cue
	 * @param cue Data of the input cue
	 * @param triggers List to contain triggers
	 */
	public void processCue(String stamp, String cue, List<String> triggers) {
		
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
					String trigger = "Video " + videoID + ", at " + filterStamps[i];
					trigger += ", filter " + i + " (\"" + reassembleFilter(i) + "\")";
					triggers.add(trigger);
					
					// Update trigger count
					triggerCount++;
					
				}
				
			}
			
		}
		
	}
	
	/**
	 * Get how many triggers have been generated
	 * @return Total generated results
	 */
	public int getTriggerCount() {
		return triggerCount;
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
