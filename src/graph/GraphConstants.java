package graph;

import java.awt.Color;

import prefuse.util.ColorLib;

public class GraphConstants {
	// graph att
	public static final float UPPER_BOUND_EDGE_STROKE_WIDTH = 5f;
	public static final float LOWER_BOUND_EDGE_STROKE_WIDTH = 0.25f;
	//graph field
	public static final String LABEL_FIELD = "label";
	public static final String BEGIN_FIELD = "begin";
	public static final String END_FIELD = "end";
	public static final String SELECT_FIELD = "select";
	public static final String IS_INDIRECTED_EDGE_FIELD = "isDirectedEdge";
	public static final String STROKE_WIDTH_EDGE_FIELD = "strokeWidth";
	public static final String FREQUENCY_FILL_COLOR_NODE_FIELD = "freqFillColorNode";
	public static final String NODE_TYPE_FIELD = "nodeType";
	public static final String NODE_GROUP = "graph.nodes";
	public static final String EDGE_GROUP = "graph.edges";

	//color
	public static final Color[] CATEGORY_COLOR = { new Color(229,83,75), new Color(85, 22, 57), new Color(16, 52, 23),
			new Color(50, 50, 255) };
	public static final int BEGIN_END_NODE_COLOR =  ColorLib.rgb(3, 218, 197);
	public static final int TEXT_COLOR = ColorLib.rgb(255, 255, 255);
	//color node
	public static final int NODE_FILL_COLOR = ColorLib.rgb(55, 0, 179);
	public static final int SELECTED_NODE_FILL_COLOR = ColorLib.rgb(207, 102, 121);
	public static final int NODE_STROKE_COLOR = ColorLib.rgb(255, 255, 255);
	public static final Color[] GRADIENT_NODE_FILL_COLOR = new Color[] { new Color(185, 186, 239),
			new Color(149, 146, 229), new Color(117, 105, 215), new Color(86, 63, 199), new Color(55, 0, 179) };
	public static final Color NODE_FILL_DARK_COLOR = new Color(18,92,164);
	public static final Color NODE_FILL_LIGHT_COLOR = new Color(190,216,236);
	//color edge
	public static final int EDGE_STROKE_COLOR = ColorLib.rgb(255, 255, 255);
	public static final int CLICK_EDGE_STROKE_COLOR = ColorLib.rgb(141,108,46);

	//action name
	public static final String NODE_STROKE_COLOR_ACTION = "nodeStrokeColor";
	public static final String NODE_FILL_COLOR_ACTION = "nodeFillColor";
	public static final String EDGE_STROKE_COLOR_ACTION = "edgeStrokeColor";
	public static final String NODE_STROKE_WIDTH_ACTION = "nodeStrokeWidth";
	public static final String EDGE_STROKE_WIDTH_ACTION = "edgeStrokeWidth";
	public static final String ARROW_FILL_COLOR_ACTION = "arrowFillColor";
	public static final String TEXT_COLOR_ACTION = "textColor";
	public static final String LAYOUT_ACTION = "layout";
	public static final String NODE_SIZE_ACTION = "nodeSize";

}
