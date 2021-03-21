package vttExtract;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wesley Mays (WMays287)
 * @version 2.1
 * @since 2.1
 */
public class Deduplicator {
	
	List<String> data;
	int index;
	
	public Deduplicator(List<String> input) {
		
		data = new ArrayList<String>();
		for (String foo : input) {
			if (foo.isBlank() || foo.isEmpty()) continue;
			data.add(foo);
		}
		index = data.size() - 1;
		
	}
	
	/**
	 * De-duplicate the input buffer
	 * @return The de-duplicated buffer
	 */
	public List<String> deduplicate() {
		
		while (index > 0) {
			tick();
		}
		
		// Remove any blank entries
		List<String> newData = new ArrayList<String>();
		for (String foo : data) {
			if (foo.isBlank() || foo.isEmpty()) continue;
			newData.add(foo);
		}
		data = newData;
		
		return data;
		
	}
	
	/**
	 * Tick the de-duplication process
	 */
	private void tick() {
		
		// Value format:
		// Video ID: 5KgZC9PfEdo, time stamp: 00:12:48.320, filter: 1 ("fan art")
		// 01234567890123456789012345678901234567890123456789012345678901234567890
		// 00000000001111111111222222222233333333334444444444555555555566666666667
		
		// Read current value and peek up 1 line
		String valueCurrent = data.get(index);
		String valuePeek = data.get(index - 1);
		
		// Extract and compare video IDs
		String vidCurrent = valueCurrent.substring(10, 20);
		String vidPeek = valuePeek.substring(10, 20);
		if (!vidCurrent.equals(vidPeek)) {
			index--;
			return;
		}
		
		// Extract and compare time stamps
		int stampCurrent = parseStamp(valueCurrent.substring(35, 46));
		int stampPeek = parseStamp(valuePeek.substring(35, 46));
		int stampDelta = Math.abs(stampCurrent - stampPeek);
		if (stampDelta <= 1500) {
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
		int hours = 0;
		// Time stamps won't always define hours
		// You need to support those cases
		if (foo.length >= 3) {
			hours = Integer.parseInt(foo[foo.length - 3]);
		}
		int minutes = Integer.parseInt(foo[foo.length - 2]);
		foo = foo[foo.length - 1].split("\\.");
		int seconds = Integer.parseInt(foo[0]);
		int millis = Integer.parseInt(foo[1]);
		minutes += hours * 60;
		seconds += minutes * 60;
		return seconds * 1000 + millis;
	}

}
