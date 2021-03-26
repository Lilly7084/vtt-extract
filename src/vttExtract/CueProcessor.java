package vttExtract;

import java.util.List;

/**
 * @author Wesley Mays (WMays287)
 * @version 2.3
 * @since 2.1
 */
public class CueProcessor {
	
	private String videoID;
	private int triggerCount;
	private String[] terms;
	private int progress;
	String timeStamp;
	String phrase;
	
	public CueProcessor(String videoID, String filter) {
		
		filter = filter.toLowerCase();
		
		// Set up other variables
		this.videoID = videoID;
		triggerCount = 0;
		
		// Set up filter registers
		terms = filter.split(" ");
		progress = 0;
		timeStamp = "00:00:00.000";
		phrase = filter;
		
	}
	
	/**
	 * Process a cue returned from a LineProcessor
	 * @param stamp Time stamp of the input cue
	 * @param cue Data of the input cue
	 * @param triggers List to contain triggers
	 */
	public void processCue(String stamp, String cue, List<String> triggers) {
		if (terms[progress].equals(cue)) {
			if (progress == 0) {
				timeStamp = stamp;
			}
			progress++;
			if (progress >= terms.length) {
				progress = 0;
				triggers.add("Video " + videoID + ", at " + timeStamp + " (\"" + phrase + "\")");
				triggerCount++;
			}
		} else {
			progress = 0;
		}
	}
	
	/**
	 * Get how many triggers have been generated
	 * @return Total generated results
	 */
	public int getTriggerCount() {
		return triggerCount;
	}

}
