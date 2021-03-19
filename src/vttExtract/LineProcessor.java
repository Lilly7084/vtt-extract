package vttExtract;

import java.util.List;
import java.util.regex.Pattern;

public class LineProcessor {
	
	// Buffer values for FSM
	private String cueBuffer = "";
	private String tagBuffer = "";
	
	// State value for FSM
	// 0 = Read out cue
	// 1 = Read out tag
	private int state = 0;
	
	// Buffer and config values for other code
	private Pattern stampRegex;
	private String timestamp;
	
	public LineProcessor(String stampRegex, String timestamp) {
		this.stampRegex = Pattern.compile(stampRegex);
		this.timestamp = timestamp;
	}
	
	public void resetFSM(List<String[]> cues) {
		handleCue(cueBuffer, cues);
		cueBuffer = "";
		handleTag(tagBuffer);
		tagBuffer = "";
		state = 0;
	}
	
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
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
	
	private void handleTag(String tagBuffer) {
		// If the tag is a valid time stamp (Cue time)
		if (stampRegex.matcher(tagBuffer).find()) {
			timestamp = tagBuffer;
		}
	}
	
	private void handleCue(String cueBuffer, List<String[]> cues) {
		cues.add(new String[] { timestamp, cueBuffer });
	}

}
