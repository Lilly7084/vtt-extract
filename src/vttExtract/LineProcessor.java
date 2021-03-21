package vttExtract;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Wesley Mays (WMays287)
 * @version 2.2
 * @since 2.0
 */
public class LineProcessor {
	
	/**
	 * Regex used to search for time stamps
	 */
	private static final Pattern STAMP_REGEX = Pattern.compile("(\\d{2}:)*\\d{2}\\.\\d{3}");
	
	// Buffer values for FSM
	private String cueBuffer = "";
	private String tagBuffer = "";
	
	/**
	 * State value for finite state machine.
	 * Value 0 = Reading cue,
	 * value 1 = Reading tag
	 */
	private int state = 0;
	
	// Buffer and config values for other code
	private String timestamp;
	
	/**
	 * @param timestamp Initial time stamp value
	 */
	public LineProcessor(String timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * Handle extraneous cues and tage, and reset state machine
	 * @param cues List to hold resulting cue data
	 */
	public void resetFSM(List<String[]> cues) {
		handleCue(cueBuffer, cues);
		cueBuffer = "";
		handleTag(tagBuffer);
		tagBuffer = "";
		state = 0;
	}
	
	/**
	 * Externally set the current time stamp
	 * @param timestamp New time stamp value
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * Process a character from the input file
	 * @param chr The character to be processed
	 * @param cues List to hold resulting cue data
	 */
	public void handleChar(String chr, List<String[]> cues) {
		
		// State 0, whitespace - Push and clear cue
		if (state == 0 && chr.isBlank()) {
			handleCue(cueBuffer, cues);
			cueBuffer = "";
		}
		
		// State 0, "<" - Enter state 1
		else if (state == 0 && chr.equals("<")) {
			state = 1;
		}
		
		// State 0, else - Append to cue buffer
		else if (state == 0) {
			cueBuffer += chr;
		}
		
		// State 1, ">" - Push and clear tag, enter state 0
		else if (state == 1 && chr.equals(">")) {
			handleTag(tagBuffer);
			tagBuffer = "";
			state = 0;
		}
		
		// State 1, else - Append to tag buffer
		else if (state == 1) {
			tagBuffer += chr;
		}
		
	}
	
	/**
	 * Process a tag
	 * @param tagBuffer Tag buffer to be processed
	 */
	private void handleTag(String tagBuffer) {
		// If the tag is a valid time stamp (Cue time)
		if (STAMP_REGEX.matcher(tagBuffer).find()) {
			timestamp = tagBuffer;
		}
	}
	
	/**
	 * Process a cue
	 * @param cueBuffer Cue buffer to be processed
	 * @param cues List to hold resulting cue data
	 */
	private void handleCue(String cueBuffer, List<String[]> cues) {
		cues.add(new String[] { timestamp, cueBuffer });
	}

}
