package org.processmining.goaldrivenprocessmining.algorithms;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class GoalDrivenConstants {
	// log constant:
	public static final String CASE_NAME = "concept:name";
	public static final String EVENT_ACTIVITY = "concept:name";
	public static final String EVENT_TIME = "time:timestamp";
	
	
	// date time format
	public static final List<String> DATA_TIME_FORMAT = Arrays.asList(
			"yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
			"yyyy-MM-dd'T'HH:mm:ssXXX"
			);
	// Color in panel
	public static final Color BACKGROUND_COLOR = new Color(26, 26, 26);
	public static final Color CONTENT_CARD_COLOR = BACKGROUND_COLOR;
	public static final Color CONTENT_CARD_BACKGROUND_COLOR = new Color(38, 38, 38);
	public static final Color DISPLAY_SETTING_BACKGROUND_COLOR = new Color(51, 51, 51);
	public static final Color SIDE_PANEL_CARD_COLOR = new Color(255, 255, 255);
	public static final Color STATISTIC_PANEL_BACKGROUND_COLOR = BACKGROUND_COLOR;
	public static final Color STATISTIC_PANEL_TAB_SELECTED_COLOR = new Color(151, 89, 57);
	public static final Color STATISTIC_PANEL_TAB_UNSELECTED_COLOR = new Color(100, 138, 170);
	public static final Color CONTROL_BAR_BACKGROUND_COLOR = BACKGROUND_COLOR;
	// Color in border
	public static final Color BORDER_COLOR = new Color(38, 38, 38);
	// Border
	public static final Border BETWEEN_PANEL_BORDER = BorderFactory.createLineBorder(GoalDrivenConstants.BORDER_COLOR, 
			6, false);
//	public static final Border BETWEEN_CARD_BORDER = BorderFactory.createLineBorder(GoalDrivenConstants.BORDER_COLOR,
//			15, false);
	// Button
	public static final Color BUTTON_BACKGROUND_COLOR = new Color(196, 201, 205);
	public static final Color BUTTON_FOREGROUND_COLOR = Color.BLACK;
	public static final Color BUTTON_HOVER_BACKGROUND_COLOR = Color.WHITE;
	public static final Color BUTTON_HOVER_FOREGROUND_COLOR = Color.BLACK;
	// Font
	public static final Font BOLD_XL_FONT = new Font("Dialog", Font.BOLD, 20);
	public static final Font BOLD_L_FONT = new Font("Dialog", Font.BOLD, 18);
	public static final Font BOLD_M_FONT = new Font("Dialog", Font.BOLD, 16);
	
	public static final Font PLAIN_XL_FONT = new Font("Dialog", Font.PLAIN, 20);
	public static final Font PLAIN_L_FONT = new Font("Dialog", Font.PLAIN, 18);
	public static final Font PLAIN_M_FONT = new Font("Dialog", Font.PLAIN, 16);
	public static final Font PLAIN_S_FONT = new Font("Dialog", Font.PLAIN, 14);
}
