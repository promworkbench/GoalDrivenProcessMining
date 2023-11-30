package graph;

import java.awt.BasicStroke;
import java.awt.Color;

import prefuse.util.ColorLib;

public class GraphConstants {
	// graph att
	public static final float UPPER_BOUND_EDGE_STROKE_WIDTH = 12f;
	public static final float LOWER_BOUND_EDGE_STROKE_WIDTH = 3f;
	public static final int ARROW_HEAD_WIDTH = 60;
	public static final int ARROW_HEAD_HEIGHT = 30;
	//graph field
	public static final String LABEL_FIELD = "label";
	public static final String DISPLAY_LABEL_FIELD = "displayLabel";
	public static final String BEGIN_FIELD = "begin";
	public static final String END_FIELD = "end";
	public static final String IS_SELECTED = "select";
	public static final String IS_INDIRECTED_EDGE_FIELD = "isDirectedEdge";
	public static final String STROKE_WIDTH_EDGE_FIELD = "strokeWidth";
	public static final String NODE_FILL_COLOR_FIELD = "freqFillColorNode";
	public static final String NODE_STROKE_COLOR_FIELD = "StrokeColorNode"; 
	public static final String EDGE_FILL_COLOR_FIELD = "edgeFillColor";
	public static final String NODE_TYPE_FIELD = "nodeType";
	public static final String IS_INVISIBLE = "isInvisible";
	public static final String IS_INVISIBLE_COLLAPSED = "isInvisibleCollapsed";
	public static final String IS_DISPLAY = "isDisplay";
	public static final String NODE_GROUP = "graph.nodes";
	public static final String EDGE_GROUP = "graph.edges";

	//color
	public static final Color[] CATEGORY_COLOR = { new Color(229, 83, 75), new Color(85, 22, 57), new Color(16, 52, 23),
			new Color(50, 50, 255) };
	public static final int BEGIN_END_NODE_COLOR = ColorLib.rgb(3, 218, 197);
	public static final int TEXT_COLOR = ColorLib.rgb(255, 255, 255);
	//color node
	public static final int SELECTED_NODE_FILL_COLOR = ColorLib.rgb(207, 102, 121);
	public static final int NODE_STROKE_COLOR = ColorLib.rgb(255, 255, 255);
	public static final int NODE_HIGH_DESIRED_STROKE_COLOR = ColorLib.rgb(102, 255, 105);
	public static final int NODE_LOW_DESIRED_STROKE_COLOR = ColorLib.rgb(0, 26, 0);
	public static final int NODE_HIGH_PRIORITY_STROKE_COLOR = ColorLib.rgb(255, 0, 0);
	public static final int NODE_LOW_PRIORITY_STROKE_COLOR = ColorLib.rgb(255, 204, 204);
	public static final Color NODE_FILL_DARK_COLOR = new Color(18, 92, 164);
	public static final Color NODE_FILL_LIGHT_COLOR = new Color(190, 216, 236);
	public static final Color NODE_TIME_DEFAULT_COLOR = new Color(105, 119, 150);
	//color edge
	public static final int EDGE_STROKE_COLOR = ColorLib.rgb(255, 255, 255);
	public static final Color EDGE_TIME_LONG_COLOR = new Color(246, 49, 35);
	public static final Color EDGE_TIME_NORMAL_COLOR = new Color(255, 255, 255);
	// highlight color
	public static final int HIGHLIGHT_STROKE_COLOR = ColorLib.rgb(255, 89, 0);
	public static final BasicStroke HIGHLIGHT_STROKE = new BasicStroke(10);
	// unhighlight color
	public static final int UNHIGHLIGHT_STROKE_COLOR = ColorLib.rgb(51, 51, 51);
	// table color
	
	
	//action name
	public static final String NODE_STROKE_COLOR_ACTION = "nodeStrokeColor";
	public static final String NODE_FILL_COLOR_ACTION = "nodeFillColor";
	public static final String EDGE_STROKE_COLOR_ACTION = "edgeStrokeColor";
	public static final String NODE_STROKE_WIDTH_ACTION = "nodeStrokeWidth";
	public static final String EDGE_STROKE_WIDTH_ACTION = "edgeStrokeWidth";
	public static final String ARROW_FILL_COLOR_ACTION = "arrowFillColor";
	public static final String TEXT_COLOR_ACTION = "textColor";
	public static final String FONT_ACTION = "fontSize";
	public static final String LAYOUT_ACTION = "layout";
	public static final String NODE_SIZE_ACTION = "nodeSize";

	//node name
	public static final String BEGIN_NODE_NAME = "**BEGIN**";
	public static final String END_NODE_NAME = "**END**";

	// mode constant
	public static final String MODE_FREQUENCY = "Frequency";
	public static final String MODE_MEAN_THROUGHPUT = "Mean throughput";
	public static final String MODE_MEDIAN_THROUGHPUT = "Median throughput";
	public static final String MODE_MIN_THROUGHPUT = "Min throughput";
	public static final String MODE_MAX_THROUGHPUT = "Max throughput";
	public static final String MODE_DESIRABILITY = "Desirability";
	public static final String MODE_PRIORITY = "Priority";

}
