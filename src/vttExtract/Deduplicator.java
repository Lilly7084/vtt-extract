package vttExtract;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wesley Mays (WMays287)
 * @version 2.3
 * @since 2.1
 */
public class Deduplicator {
	
	List<String> data;
	int index;
	
	/**
	 * @param input Data to be added to be de-duplicated
	 */
	public Deduplicator(List<String> input) {
		
		data = new ArrayList<String>();
		
		// Initial removal of voids
		for (String foo : input) {
			if (foo.isBlank() || foo.isEmpty()) continue;
			data.add(foo);
		}
		
		// Initial pointer
		index = data.size() - 1;
		
	}
	
	/**
	 * De-duplicate the input buffer
	 */
	public void run() {
		
		// Tick until you reach the top of the file
		while (index > 0) {
			tick();
		}
		
		// Final removal of voids
		List<String> newData = new ArrayList<String>();
		for (String foo : data) {
			if (foo.isBlank() || foo.isEmpty()) continue;
			newData.add(foo);
		}
		data = newData;
		
	}
	
	/**
	 * Tick the de-duplication process
	 */
	private void tick() {
		
		// Value format:
		// Video ###########, at ##:##:##.###, filter # ("...")
		// 01234567890123456789012345678901234567890123456789012345678901234567890
		// 00000000001111111111222222222233333333334444444444555555555566666666667
		
		// Read current value and peek up 1 line
		String valueCurrent = data.get(index);
		String valuePeek = data.get(index - 1);
		
		// Extract and compare video IDs
		String vidCurrent = valueCurrent.substring(6, 16);
		String vidPeek = valuePeek.substring(6, 16);
		if (!vidCurrent.equals(vidPeek)) {
			index--;
			return;
		}
		
		// Extract and compare time stamps
		int stampCurrent = parseStamp(valueCurrent.substring(22, 33));
		int stampPeek = parseStamp(valuePeek.substring(22, 33));
		int stampDelta = Math.abs(stampCurrent - stampPeek);
		if (stampDelta <= 5000) {
			//data.set(index, "#" + valueCurrent);
			data.set(index, "");
			index--;
			return;
		}
		
		index--;
		return;
		
	}
	
	/**
	 * Parse a stamp from String to float
	 * @param stamp The string to be parsed
	 * @return The total number of milliseconds
	 */
	private int parseStamp(String stamp) {
		
		// Stamp format:
		// 00:12:48.320
		// H  M  S  ms
		String[] foo = stamp.split(":");

		// Time stamps won't always define hours
		// You need to support those cases
		int hours = 0;
		if (foo.length >= 3) {
			hours = Integer.parseInt(foo[foo.length - 3]);
		}
		
		// Parse minutes, seconds, and milliseconds
		int minutes = Integer.parseInt(foo[foo.length - 2]);
		foo = foo[foo.length - 1].split("\\.");
		int seconds = Integer.parseInt(foo[0]);
		int millis = Integer.parseInt(foo[1]);
		
		// Reassemble values
		minutes += hours * 60;
		seconds += minutes * 60;
		return seconds * 1000 + millis;
		
	}
	
	/**
	 * Get the de-duplicated data
	 * @return List containing data
	 */
	public List<String> getData() {
		return data;
	}

}
