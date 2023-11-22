package org.processmining.goaldrivenprocessmining.algorithms;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class StatUtils {

	public static String getDateString(long time) {
		String res = "";
		if (time != -1) {
			long day = time / 1000 / 60 / 60 / 24;
			long hour = (time - day * 1000 * 60 * 60 * 24) / 1000 / 60 / 60;
			long minute = (time - day * 1000 * 60 * 60 * 24 - hour * 1000 * 60 * 60) / 1000 / 60;
			long second = (time - day * 1000 * 60 * 60 * 24 - hour * 1000 * 60 * 60 - minute * 1000 * 60) / 1000;

			if (day != 0) {
				res += Long.toString(day) + " days ";
			}
			if (hour != 0) {
				res += Long.toString(hour) + " hours ";
			}
			if (minute != 0) {
				res += Long.toString(minute) + " minutes ";
			}
			if (second != 0) {
				res += Long.toString(second) + " seconds";
			}
		}
		return res;

	}

	public static long getDifferenceBetween2DateTime(LocalDateTime dateTime1, LocalDateTime dateTime2) {
		// Convert LocalDateTime to Instant.
		Instant instant1 = dateTime1.atZone(ZoneId.systemDefault()).toInstant();
		Instant instant2 = dateTime2.atZone(ZoneId.systemDefault()).toInstant();

		// Get the difference in milliseconds.
		return Math.abs(instant2.toEpochMilli() - instant1.toEpochMilli());

	}

	public static LocalDateTime getDateTime(String time) {
		for (String format : GoalDrivenConstants.DATA_TIME_FORMAT) {
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
				LocalDateTime date = LocalDateTime.parse(time, formatter);
				return date;
			} catch (DateTimeParseException e) {
				// If the date doesn't match the format, continue with the next format.
			}
		}
		return null;
	}

}
