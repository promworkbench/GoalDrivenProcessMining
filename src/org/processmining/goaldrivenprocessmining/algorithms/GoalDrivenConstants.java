package org.processmining.goaldrivenprocessmining.algorithms;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class GoalDrivenConstants {
	// date time format
	public static final List<String> DATA_TIME_FORMAT = Arrays.asList(
			"yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
			"yyyy-MM-dd'T'HH:mm:ssXXX"
			);
	// Color in panel
	public static final Color BACKGROUND_COLOR = new Color(32,46,59);
	public static final Color CONTENT_CARD_COLOR = new Color(32,46,59);
	public static final Color CONTENT_CARD_BACKGROUND_COLOR = new Color(54,77,99);
	public static final Color SIDE_PANEL_CARD_COLOR = new Color(255, 255, 255);
	public static final Color STATISTIC_PANEL_BACKGROUND_COLOR = new Color(34, 51, 64);
	public static final Color STATISTIC_PANEL_TAB_SELECTED_COLOR = new Color(151, 89, 57);
	public static final Color STATISTIC_PANEL_TAB_UNSELECTED_COLOR = new Color(100, 138, 170);
	public static final Color CONTROL_BAR_BACKGROUND_COLOR = new Color(32,46,59);
	// Color in border
	public static final Color BORDER_COLOR = new Color(36,52,67);
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
	public static final Font BOLD_BIG_FONT = new Font("Dialog", Font.BOLD, 20);
}
