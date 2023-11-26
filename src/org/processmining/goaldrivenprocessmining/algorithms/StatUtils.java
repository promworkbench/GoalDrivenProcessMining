package org.processmining.goaldrivenprocessmining.algorithms;

public class StatUtils {

	public static String getDateString(long time) {
		String res = "";
		if (time != -1) {
			long day = time / 1000 / 60 / 60 / 24;
			long hour = (time - day * 1000 * 60 * 60 * 24) / 1000 / 60 / 60;

			if (day != 0) {
				if (day >= 3) {
					float comma = ((float) time) / 1000 / 60 / 60 / 24;
					res = Float.toString(Math.round(comma * 10) / 10f) + " d";
				} else {
					float comma = ((float) time) / 1000 / 60 / 60;
					res = Float.toString(Math.round(comma * 10) / 10f) + " hrs";
				}
			} else {
				if (hour != 0) {
					if (hour >= 2) {
						float comma = ((float) time) / 1000 / 60 / 60;
						res = Float.toString(Math.round(comma * 10) / 10f) + " hrs";
					} else {
						float comma = ((float) time) / 1000 / 60;
						res = Float.toString(Math.round(comma * 10) / 10f) + " mins";
					}
				} else {
					float comma = ((float) time) / 1000 / 60;
					res = Float.toString(Math.round(comma * 10) / 10f) + " mins";
				}
			}
		}
		return res;
	}
}
