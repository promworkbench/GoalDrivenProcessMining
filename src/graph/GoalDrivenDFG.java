package graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.processmining.goaldrivenprocessmining.algorithms.LogSkeletonUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.ThroughputTimeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.enumaration.NodeType;

import graph.action.CustomColorNodeFillAction;
import graph.action.CustomEdgeFillColorAction;
import graph.action.CustomEdgeStrokeColorAction;
import graph.action.CustomEdgeStrokeWidthAction;
import graph.action.CustomizedEdgeRenderer;
import graph.action.NodeRenderer;
import graph.action.SetEndNodePositionAction;
import graph.action.SetNodeSizeAction;
import graph.controls.BackgroundDoubleClickControl;
import graph.controls.BorderNodeControl;
import graph.controls.CustomPanControl;
import graph.controls.DragMultipleNodesControl;
import graph.controls.GraphObjectClickControl;
import graph.controls.RightClickControl;
import graph.controls.SelectMultipleNodesControl;
import graph.controls.SquareSelectControl;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.assignment.ShapeAction;
import prefuse.action.assignment.StrokeAction;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.action.layout.graph.TreeLayout;
import prefuse.controls.FocusControl;
import prefuse.controls.WheelZoomControl;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.expression.Predicate;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.data.util.TableIterator;
import prefuse.render.DefaultRendererFactory;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

public class GoalDrivenDFG extends Display {
	/**
	 * 
	 */
	private static final long serialVersionUID = -701413385694384404L;
	private GDPMLogSkeleton log;
	private int beginNodeRow;
	private int endNodeRow;
	private Graph graph;
	private Boolean isHighLevel;

	// renderer
	private CustomizedEdgeRenderer edgeRenderer;
	// control 
	private SelectMultipleNodesControl selectMultipleNodesControl;
	private BackgroundDoubleClickControl backgroundDoubleClickControl;
	private CustomPanControl customPanControl;
	private WheelZoomControl wheelZoomControl;
	private FocusControl focusControl;
	private DragMultipleNodesControl dragMultipleNodesControl;
	private BorderNodeControl borderNodeControl;
	private GraphObjectClickControl edgeClickControl;
	private SquareSelectControl squareSelectControl;
	private RightClickControl rightClickControl;
	// action
	private ColorAction nodeStrokeColorAction;
	private ColorAction edgeStrokeColorAction;
	private StrokeAction nodeStrokeWidthAction;
	private ColorAction arrowFillColorAction;
	private ColorAction textColorAction;
	// frequency
	private HashMap<String, Integer> frequencyNode = new HashMap<String, Integer>();
	private HashMap<String, Integer> currentFrequencyNode = new HashMap<String, Integer>();
	private HashMap<EdgeObject, Integer> frequencyEdge = new HashMap<EdgeObject, Integer>();
	private HashMap<EdgeObject, Integer> currentFrequencyEdge = new HashMap<EdgeObject, Integer>();
	// throughput
	private HashMap<EdgeObject, ThroughputTimeObject> throughputEdge = new HashMap<EdgeObject, ThroughputTimeObject>();
	private HashMap<EdgeObject, ThroughputTimeObject> currentThroughputEdge = new HashMap<EdgeObject, ThroughputTimeObject>();
	// group
	private HashMap<String, Node> mapGroupNode;

	public GoalDrivenDFG(GDPMLogSkeleton gdpmLogSkeleton, Boolean isHighLevel) {
		super(new Visualization());
		this.log = gdpmLogSkeleton;
		this.isHighLevel = isHighLevel;

		// action
		this.nodeStrokeColorAction = null;
		this.edgeStrokeColorAction = null;
		this.arrowFillColorAction = null;
		this.textColorAction = null;

		// group
		this.mapGroupNode = new HashMap<String, Node>();

		// repaint
		ActionList repaint = new ActionList();
		repaint.add(new RepaintAction());
		m_vis.putAction("repaint", repaint);

		if (this.log != null && !this.log.getEdgeHashTable().getEdgeTable().isEmpty()) {
			this.calculateFrequencyNode();
			this.calculateFrequencyEdge();
			this.calculateThroughputEdge();

			this.makeGraph();
			m_vis.addGraph("graph", this.graph);

			// control
			this.setDefaultControl();
			// layout
			this.setDefaultLayout();
			// action
			this.configDefaultGraph();

		}

	}

	private void makeGraph() {

		// Create tables for node and edge data, and configure their columns.
		// init node table
		Table nodeTable = this.initNodeTable();
		// init edge table
		Table edgeTable = this.initEdgeTable();
		// init graph
		this.graph = new Graph(nodeTable, edgeTable, true);
		// add begin and end node;
		this.addBeginToTable(this.graph);
		// add activities in log to node table and add edges
		this.addActToTable();

	}

	private void configDefaultGraph() {

		this.setDefaultEdgeColor();
		this.setDefaultEdgeStrokeWidth();

		this.setDefaultNodeStrokeWidth();
		this.setDefaultNodeFillColor();
		this.setDefaultNodeStrokeColor();
		this.setDefaultTextColorSizeAndFont();
		this.setDefaultNodeSize();

		this.setDefaultRenderer();
	}

	private void setDefaultControl() {
		setSize(300, 500); // set display size
		//		pan(150, 250);
		setHighQuality(true);
		/* pan, zoom focus control */
		customPanControl = new CustomPanControl();
		addControlListener(customPanControl);
		wheelZoomControl = new WheelZoomControl();
		addControlListener(wheelZoomControl);
		focusControl = new FocusControl();
		addControlListener(focusControl);
		/***************************/

		if (this.log != null) {
			/* select multiple nodes */
			selectMultipleNodesControl = new SelectMultipleNodesControl(this.graph.getNodeTable(), this);
			addControlListener(selectMultipleNodesControl);
			/************************/
			/* double click background */
			backgroundDoubleClickControl = new BackgroundDoubleClickControl(this);
			addControlListener(backgroundDoubleClickControl);
			/*************************/
			/* drag multiple nodes */
			dragMultipleNodesControl = new DragMultipleNodesControl();
			addControlListener(dragMultipleNodesControl);
			squareSelectControl = new SquareSelectControl(this.graph.getNodeTable(), this);
			addControlListener(squareSelectControl);
			rightClickControl = new RightClickControl(this);
			addControlListener(rightClickControl);
		}

		/*********************/
		/* Detect border click - not done */
		//		borderNodeControl = new BorderNodeControl();
		//		addControlListener(borderNodeControl);
		/*********************/
	}

	public void addSeeOnlyControls() {
		this.removeAllControls();
		setSize(300, 500); // set display size
		//		pan(150, 250);
		setHighQuality(true);
		/* pan, zoom focus control */
		customPanControl = new CustomPanControl();
		addControlListener(customPanControl);
		wheelZoomControl = new WheelZoomControl();
		addControlListener(wheelZoomControl);
		focusControl = new FocusControl();
		addControlListener(focusControl);
		dragMultipleNodesControl = new DragMultipleNodesControl();
		addControlListener(dragMultipleNodesControl);
		rightClickControl = new RightClickControl(this);
		addControlListener(rightClickControl);
		backgroundDoubleClickControl = new BackgroundDoubleClickControl(this);
		addControlListener(backgroundDoubleClickControl);

	}

	private void removeAllControls() {
		this.removeControlListener(this.focusControl);
		this.removeControlListener(this.customPanControl);
		this.removeControlListener(this.backgroundDoubleClickControl);
		this.removeControlListener(this.borderNodeControl);
		this.removeControlListener(this.dragMultipleNodesControl);
		this.removeControlListener(this.selectMultipleNodesControl);
		this.removeControlListener(this.wheelZoomControl);
		this.removeControlListener(this.edgeClickControl);
		this.removeControlListener(this.squareSelectControl);
	}

	private void setDefaultRenderer() {
		this.edgeRenderer = new CustomizedEdgeRenderer(prefuse.Constants.EDGE_TYPE_CURVE,
				prefuse.Constants.EDGE_ARROW_FORWARD);
		this.edgeRenderer.setArrowHeadSize(GraphConstants.ARROW_HEAD_WIDTH, GraphConstants.ARROW_HEAD_HEIGHT);
		this.edgeRenderer.setArrowDoubleHeadSize(GraphConstants.ARROW_HEAD_WIDTH + 5, GraphConstants.ARROW_HEAD_HEIGHT);
		// pass the frequency edge to the custom edge renderer
		HashMap<EdgeObject, String> mapEdgeLabel = new HashMap<EdgeObject, String>();
		for (Map.Entry<EdgeObject, Integer> entry : this.currentFrequencyEdge.entrySet()) {
			mapEdgeLabel.put(entry.getKey(), Integer.toString(entry.getValue()));
		}
		this.edgeRenderer.setMapEdgeLabel(mapEdgeLabel);

		NodeRenderer label = new NodeRenderer(this, GraphConstants.DISPLAY_LABEL_FIELD);
		label.setRoundedCorner(8, 8);
		label.setHorizontalPadding(10);
		label.setVerticalPadding(10);
		DefaultRendererFactory drf = new DefaultRendererFactory();
		drf.setDefaultRenderer(label);

		/* begin end shape */
		Predicate beginPredicate = (Predicate) ExpressionParser.parse("begin = true");
		ShapeAction shapeAction = new ShapeAction("graph.nodes", Constants.SHAPE_RECTANGLE);
		shapeAction.add(beginPredicate, Constants.SHAPE_TRIANGLE_RIGHT);
		ActionList shape = new ActionList();
		shape.add(shapeAction);
		m_vis.putAction("shape", shape);
		m_vis.run("shape");
		/******************/
		drf.setDefaultEdgeRenderer(this.edgeRenderer);
		m_vis.setRendererFactory(drf);
	}

	public void setDefaultNodeSize() {
		ActionList nodeSize = new ActionList();
		SetNodeSizeAction setSizeAction = new SetNodeSizeAction(1.4);
		nodeSize.add(setSizeAction);
		m_vis.putAction(GraphConstants.NODE_SIZE_ACTION, nodeSize);
		m_vis.run(GraphConstants.NODE_SIZE_ACTION);
	}

	private void setDefaultLayout() {
		TreeLayout treeLayout = new NodeLinkTreeLayout("graph", Constants.ORIENT_TOP_BOTTOM, 400, 500, 400);
		treeLayout.setLayoutAnchor(new Point2D.Double(500, 100));
		m_vis.putAction(GraphConstants.LAYOUT_ACTION, treeLayout);
		m_vis.run(GraphConstants.LAYOUT_ACTION);

		SetEndNodePositionAction setEndNodePositionAction = new SetEndNodePositionAction(
				m_vis.getVisualItem(GraphConstants.NODE_GROUP, this.graph.getNodeTable().getTuple(endNodeRow)));
		m_vis.putAction("setEndNodePostion", setEndNodePositionAction);
		m_vis.alwaysRunAfter(GraphConstants.LAYOUT_ACTION, "setEndNodePostion");
	}

	public void setDefaultTextColorSizeAndFont() {
		this.textColorAction = new ColorAction(GraphConstants.NODE_GROUP, VisualItem.TEXTCOLOR,
				GraphConstants.TEXT_COLOR);
		m_vis.putAction(GraphConstants.TEXT_COLOR_ACTION, this.textColorAction);
		m_vis.run(GraphConstants.TEXT_COLOR_ACTION);

		FontAction fontActionNode = new FontAction(GraphConstants.NODE_GROUP, new Font("Arial", Font.BOLD, 20));
		FontAction fontActionEdge = new FontAction(GraphConstants.EDGE_GROUP, new Font("Arial", Font.BOLD, 24));
		m_vis.putAction(GraphConstants.FONT_ACTION, fontActionNode);
		m_vis.putAction("fontEdge", fontActionEdge);
		m_vis.run(GraphConstants.FONT_ACTION);
		m_vis.run("fontEdge");
	}

	public void setDefaultArrowFillColor() {
		this.arrowFillColorAction = new ColorAction(GraphConstants.EDGE_GROUP, VisualItem.FILLCOLOR,
				GraphConstants.EDGE_STROKE_COLOR);
		m_vis.putAction(GraphConstants.ARROW_FILL_COLOR_ACTION, this.arrowFillColorAction);
		m_vis.run(GraphConstants.ARROW_FILL_COLOR_ACTION);

	}

	public void setDefaultNodeStrokeWidth() {
		this.nodeStrokeWidthAction = new StrokeAction(GraphConstants.NODE_GROUP);
		this.nodeStrokeWidthAction.setDefaultStroke(new BasicStroke(2));
		m_vis.putAction(GraphConstants.NODE_STROKE_WIDTH_ACTION, this.nodeStrokeWidthAction);
		m_vis.run(GraphConstants.NODE_STROKE_WIDTH_ACTION);
	}

	private void setDefaultEdgeStrokeWidth() {
		this.currentFrequencyEdge = this.frequencyEdge;
		this.runCustomEdgeStrokeWidthAction(this.currentFrequencyEdge);
	}

	private void calculateThroughputEdge() {
		this.throughputEdge = (HashMap<EdgeObject, ThroughputTimeObject>) this.getLog().getEdgeThroughputTime();
		this.currentThroughputEdge = this.throughputEdge;
	}

	private void calculateFrequencyEdge() {
		// calculate frequency edge
		for (Map.Entry<EdgeObject, Map<Integer, List<Integer[]>>> entry : this.getLog().getEdgeHashTable()
				.getEdgeTable().entrySet()) {
			EdgeObject edge = entry.getKey();
			Map<Integer, List<Integer[]>> allPos = entry.getValue();
			int total = 0;
			for (List<Integer[]> pos : allPos.values()) {
				total += pos.size();
			}
			this.frequencyEdge.put(edge, total);
		}
		this.currentFrequencyEdge = this.frequencyEdge;
	}

	public void runCustomEdgeStrokeWidthAction(HashMap<EdgeObject, ?> mapEdge) {
		// transform frequency to color
		HashMap<EdgeObject, Float> mapEdgeStrokeWidth = new HashMap<>();
		float min = GraphConstants.LOWER_BOUND_EDGE_STROKE_WIDTH;
		float max = GraphConstants.UPPER_BOUND_EDGE_STROKE_WIDTH;
		long minFreq = Integer.MAX_VALUE;
		long maxFreq = 0;
		for (EdgeObject edgeObject : mapEdge.keySet()) {
			if (((Number) mapEdge.get(edgeObject)).longValue() >= maxFreq) {
				maxFreq = ((Number) mapEdge.get(edgeObject)).longValue();
			}
			if (((Number) mapEdge.get(edgeObject)).longValue() <= minFreq) {
				minFreq = ((Number) mapEdge.get(edgeObject)).longValue();
			}
		}
		if (maxFreq == minFreq) {
			for (EdgeObject edge : mapEdge.keySet()) {
				mapEdgeStrokeWidth.put(edge, 3f);
			}
		} else {
			// Calculate the ratio between the range and the range of data values
			float range = max - min;
			long dataRange = maxFreq - minFreq;
			float ratio = range / dataRange;

			for (EdgeObject edge : mapEdge.keySet()) {
				long value = ((Number) mapEdge.get(edge)).longValue();
				float assignedValue = min + ((value - minFreq) * ratio);
				mapEdgeStrokeWidth.put(edge, assignedValue);
			}
		}
		CustomEdgeStrokeWidthAction customEdgeStrokeWidthAction = new CustomEdgeStrokeWidthAction(
				GraphConstants.EDGE_GROUP, mapEdgeStrokeWidth);
		m_vis.putAction(GraphConstants.EDGE_STROKE_WIDTH_ACTION, customEdgeStrokeWidthAction);
		m_vis.run(GraphConstants.EDGE_STROKE_WIDTH_ACTION);
	}

	public void runCustomEdgeColorAction(HashMap<EdgeObject, ?> mapEdge) {
		// transform frequency to color
		HashMap<EdgeObject, Color> mapEdgeStrokeColor = new HashMap<>();
		long minFreq = Long.MAX_VALUE;
		long maxFreq = 0;
		for (EdgeObject edgeObject : mapEdge.keySet()) {
			if (((Number) mapEdge.get(edgeObject)).longValue() >= maxFreq) {
				maxFreq = ((Number) mapEdge.get(edgeObject)).longValue();
			}
			if (((Number) mapEdge.get(edgeObject)).longValue() <= minFreq) {
				minFreq = ((Number) mapEdge.get(edgeObject)).longValue();
			}
		}
		if (maxFreq == minFreq) {
			for (EdgeObject edge : mapEdge.keySet()) {
				mapEdgeStrokeColor.put(edge, GraphConstants.EDGE_TIME_LONG_COLOR);
			}
		} else {
			double valueRange = maxFreq - minFreq;
			// Calculate the color gradient for each data value
			Color darkColor = GraphConstants.EDGE_TIME_LONG_COLOR;
			Color lightColor = GraphConstants.EDGE_TIME_NORMAL_COLOR;

			for (EdgeObject edgeObject : mapEdge.keySet()) {
				long value = ((Number) mapEdge.get(edgeObject)).longValue();
				double normalizedValue = (value - minFreq) / valueRange;

				// Interpolate the color based on the normalized value
				int red = interpolate(lightColor.getRed(), darkColor.getRed(), normalizedValue);
				int green = interpolate(lightColor.getGreen(), darkColor.getGreen(), normalizedValue);
				int blue = interpolate(lightColor.getBlue(), darkColor.getBlue(), normalizedValue);

				// Create the gradient color
				mapEdgeStrokeColor.put(edgeObject, new Color(red, green, blue));
			}
		}

		CustomEdgeStrokeColorAction customEdgeStrokeColorAction = new CustomEdgeStrokeColorAction(
				GraphConstants.EDGE_GROUP, mapEdgeStrokeColor);
		m_vis.putAction("edgeStrokeColor", customEdgeStrokeColorAction);
		m_vis.run("edgeStrokeColor");

		CustomEdgeFillColorAction customEdgeFillColorAction = new CustomEdgeFillColorAction(GraphConstants.EDGE_GROUP,
				mapEdgeStrokeColor);
		m_vis.putAction("edgeFillColor", customEdgeFillColorAction);
		m_vis.run("edgeFillColor");

	}

	public void setDefaultEdgeColor() {
		this.edgeStrokeColorAction = new ColorAction(GraphConstants.EDGE_GROUP, VisualItem.STROKECOLOR,
				GraphConstants.EDGE_STROKE_COLOR);
		m_vis.putAction(GraphConstants.EDGE_STROKE_COLOR_ACTION, this.edgeStrokeColorAction);
		m_vis.run(GraphConstants.EDGE_STROKE_COLOR_ACTION);
		this.arrowFillColorAction = new ColorAction(GraphConstants.EDGE_GROUP, VisualItem.FILLCOLOR,
				GraphConstants.EDGE_STROKE_COLOR);
		m_vis.putAction(GraphConstants.ARROW_FILL_COLOR_ACTION, this.arrowFillColorAction);
		m_vis.run(GraphConstants.ARROW_FILL_COLOR_ACTION);

	}

	public void setNodeFillColorWith(Color color) {
		List<Integer> changeColorNode = new ArrayList<>();
		TableIterator nodes = this.graph.getNodeTable().iterator();
		while (nodes.hasNext()) {
			int row = nodes.nextInt();
			if (this.graph.getNodeTable().isValidRow(row)) {
				changeColorNode.add(row);

			}
		}
		for (Integer i : changeColorNode) {
			this.graph.getNodeTable().setInt(i, GraphConstants.NODE_FILL_COLOR_FIELD, ColorLib.color(color));
		}
		ColorAction nodeFillColor = new ColorAction(GraphConstants.NODE_GROUP, VisualItem.FILLCOLOR);
		nodeFillColor.setDefaultColor(ColorLib.color(color));
		m_vis.putAction(GraphConstants.NODE_FILL_COLOR_ACTION, nodeFillColor);
		m_vis.run(GraphConstants.NODE_FILL_COLOR_ACTION);
	}

	public void setDefaultNodeStrokeColor() {
		this.nodeStrokeColorAction = new ColorAction(GraphConstants.NODE_GROUP, VisualItem.STROKECOLOR);
		this.nodeStrokeColorAction.setDefaultColor(GraphConstants.NODE_STROKE_COLOR);
		m_vis.putAction(GraphConstants.NODE_STROKE_COLOR_ACTION, this.nodeStrokeColorAction);

		m_vis.run(GraphConstants.NODE_STROKE_COLOR_ACTION);

	}

	private void setDefaultNodeFillColor() {
		this.currentFrequencyNode = this.frequencyNode;
		this.runCustomColorNodeFillAction(this.currentFrequencyNode);
	}

	public void runCustomColorNodeFillAction(HashMap<String, ?> mapNode) {
		HashMap<String, Color> mapActColor = new HashMap<String, Color>();
		if (mapNode.isEmpty()) {
			for (String act : this.currentFrequencyNode.keySet()) {
				mapActColor.put(act, GraphConstants.NODE_TIME_DEFAULT_COLOR);
			}
		} else {
			// Find the minimum and maximum values in the data array
			int minFreq = Integer.MAX_VALUE;
			int maxFreq = 0;
			for (String act : mapNode.keySet()) {
				if (((Number) mapNode.get(act)).intValue() >= maxFreq) {
					maxFreq = ((Number) mapNode.get(act)).intValue();
				}
				if (((Number) mapNode.get(act)).intValue() <= minFreq) {
					minFreq = ((Number) mapNode.get(act)).intValue();
				}
			}

			if (maxFreq == minFreq) {
				for (String act : mapNode.keySet()) {
					mapActColor.put(act, GraphConstants.NODE_FILL_DARK_COLOR);
				}
			} else {
				double valueRange = maxFreq - minFreq;
				// Calculate the color gradient for each data value
				Color darkColor = GraphConstants.NODE_FILL_DARK_COLOR;
				Color lightColor = GraphConstants.NODE_FILL_LIGHT_COLOR;

				for (String act : mapNode.keySet()) {
					int value = ((Number) mapNode.get(act)).intValue();
					double normalizedValue = (value - minFreq) / valueRange;

					// Interpolate the color based on the normalized value
					int red = interpolate(lightColor.getRed(), darkColor.getRed(), normalizedValue);
					int green = interpolate(lightColor.getGreen(), darkColor.getGreen(), normalizedValue);
					int blue = interpolate(lightColor.getBlue(), darkColor.getBlue(), normalizedValue);

					// Create the gradient color
					mapActColor.put(act, new Color(red, green, blue));
				}
			}
		}

		Predicate displayPredicate = (Predicate) ExpressionParser.parse("isDisplay = true");
		CustomColorNodeFillAction customFillByLabel = new CustomColorNodeFillAction(GraphConstants.NODE_GROUP,
				displayPredicate, mapActColor);
		m_vis.putAction(GraphConstants.NODE_FILL_COLOR_ACTION, customFillByLabel);
		m_vis.run(GraphConstants.NODE_FILL_COLOR_ACTION);
	}

	private void calculateFrequencyNode() {
		for (Map.Entry<EdgeObject, Map<Integer, List<Integer[]>>> entry : this.log.getEdgeHashTable().getEdgeTable()
				.entrySet()) {
			EdgeObject edgeObject = entry.getKey();
			String source = edgeObject.getNode1();
			String target = edgeObject.getNode2();
			Map<Integer, List<Integer[]>> allPos = entry.getValue();
			int total = 0;
			for (List<Integer[]> pos : allPos.values()) {
				total += pos.size();
			}
			if (this.frequencyNode.containsKey(source)) {
				this.frequencyNode.put(source, this.frequencyNode.get(source) + total);
			} else {
				this.frequencyNode.put(source, total);
			}
			if (this.frequencyNode.containsKey(target)) {
				this.frequencyNode.put(target, this.frequencyNode.get(target) + total);
			} else {
				this.frequencyNode.put(target, total);
			}
		}
		for (Map.Entry<String, Integer> entry : this.frequencyNode.entrySet()) {
			this.frequencyNode.replace(entry.getKey(), entry.getValue() / 2);
		}
		this.currentFrequencyNode = this.frequencyNode;
	}

	private int interpolate(int start, int end, double t) {
		return (int) (start + (end - start) * t);
	}

	private void addBeginToTable(Graph g) {
		Node beginNode = g.addNode();
		this.beginNodeRow = beginNode.getRow();
		this.configBeginNode(beginNode);

		Node endNode = g.addNode();
		this.endNodeRow = endNode.getRow();
		this.configEndNode(endNode);
	}

	private void addActToTable() {
		//		ActivityHashTable activityHashTable = this.log.getActivityHashTable();
		EdgeHashTable edgeHashTable = this.log.getEdgeHashTable();

		// get all using nodes
		List<String> usingActs = LogSkeletonUtils.getUsingActsInLog(this.log);

		// add all node, set display true
		//		for (String act : activityHashTable.getActivityTable().keySet()) {
		//			if (usingActs.contains(act)) {
		//				Node node1 = null;
		//				node1 = this.graph.addNode();
		//				this.configNode(node1, act, false);
		//			}
		//		}
		// add edge
		for (EdgeObject edge : edgeHashTable.getEdgeTable().keySet()) {
			Node node1 = this.getNodeByLabelInGraph(this.graph, edge.getNode1());
			if (node1 == null) {
				node1 = this.graph.addNode();
				this.configNode(node1, edge.getNode1(), false);
			}
			Node node2 = this.getNodeByLabelInGraph(this.graph, edge.getNode2());
			if (node2 == null) {
				node2 = this.graph.addNode();
				this.configNode(node2, edge.getNode2(), false);
			}
			Edge e = this.graph.addEdge(node1, node2);
			this.configEdge(e, edge);
		}
	}

	public void hideNode(Graph graph, Node node) {
		Table edgeTable = graph.getEdgeTable();
		int nodeRow = node.getRow();
		// make it false in the table
		graph.getNodeTable().setBoolean(node.getRow(), GraphConstants.IS_DISPLAY, false);
		graph.getNodeTable().setBoolean(node.getRow(), GraphConstants.IS_SELECTED, false);
		// make the regarding edges hidden
		TableIterator edges = edgeTable.iterator();
		List<Integer> affectedRows = new ArrayList<Integer>();
		while (edges.hasNext()) {
			int row = edges.nextInt();
			if (graph.getEdgeTable().isValidRow(row)) {
				int source = graph.getEdgeTable().getTuple(row).getInt(Graph.DEFAULT_SOURCE_KEY);
				int target = graph.getEdgeTable().getTuple(row).getInt(Graph.DEFAULT_TARGET_KEY);
				if (source == nodeRow || target == nodeRow) {
					affectedRows.add(row);

				}
			}
		}
		for (Integer row : affectedRows) {
			graph.getEdgeTable().setBoolean(row, GraphConstants.IS_DISPLAY, false);
		}

	}

	public void displayNode(Graph graph, Node node) {
		Table edgeTable = graph.getEdgeTable();
		int nodeRow = node.getRow();
		// make it true in the table
		graph.getNodeTable().setBoolean(node.getRow(), GraphConstants.IS_DISPLAY, true);
		graph.getNodeTable().setBoolean(node.getRow(), GraphConstants.IS_SELECTED, false);

		// make the regarding edges shown
		TableIterator edges = edgeTable.iterator();
		List<Integer> affectedRows = new ArrayList<Integer>();
		while (edges.hasNext()) {
			int row = edges.nextInt();
			if (graph.getEdgeTable().isValidRow(row)) {
				int source = graph.getEdgeTable().getTuple(row).getInt(Graph.DEFAULT_SOURCE_KEY);
				int target = graph.getEdgeTable().getTuple(row).getInt(Graph.DEFAULT_TARGET_KEY);
				// if the node is the source
				if (source == nodeRow) {
					// check if the target is displaying
					if (graph.getNodeTable().getBoolean(target, GraphConstants.IS_DISPLAY)) {
						affectedRows.add(row);
					}
				} else if (target == nodeRow) {
					if (graph.getNodeTable().getBoolean(source, GraphConstants.IS_DISPLAY)) {
						affectedRows.add(row);
					}
				}
			}
		}
		for (Integer row : affectedRows) {
			graph.getEdgeTable().setBoolean(row, GraphConstants.IS_DISPLAY, true);
		}
	}

	public void removeNode(Graph graph, Node nodeToRemove) {
		Table nodeTable = graph.getNodeTable();
		Table edgeTable = graph.getEdgeTable();
		List<Integer> removingEdgeIndex = new ArrayList<Integer>();
		// remove node
		if (nodeToRemove != null) {
			// remove associated edges
			int nodeIdToRemove = nodeToRemove.getRow();
			TableIterator edges = edgeTable.iterator();
			while (edges.hasNext()) {
				int row = edges.nextInt();
				Edge edge = graph.getEdge(row);
				int sourceId = edge.getSourceNode().getRow();
				int targetId = edge.getTargetNode().getRow();
				if (sourceId == nodeIdToRemove || targetId == nodeIdToRemove) {
					// store the index
					removingEdgeIndex.add(row);
				}
			}
			for (Integer i : removingEdgeIndex) {
				edgeTable.removeRow(i);
			}
			// remove node
			nodeTable.removeRow(nodeIdToRemove);
		}
	}

	public void removeNode(String label) {
		Table nodeTable = this.graph.getNodeTable();
		// get the remove node 
		Node nodeToRemove = null;
		TableIterator nodes = nodeTable.iterator();
		while (nodes.hasNext()) {
			int row = nodes.nextInt();
			if (nodeTable.isValidRow(row)) {
				Node node = this.graph.getNode(row);
				if (node.getString(GraphConstants.LABEL_FIELD).equals(label)) {
					nodeToRemove = node;
					break;
				}
			}
		}
		// remove node in graph
		this.removeNode(this.graph, nodeToRemove);
	}

	public Node getNodeByLabelInGraph(Graph g, String label) {
		if (label.equals("begin")) {
			return this.graph.getNode(this.beginNodeRow);
		} else if (label.equals("end")) {
			return this.graph.getNode(this.endNodeRow);
		} else {
			TableIterator nodes = g.getNodeTable().iterator();
			while (nodes.hasNext()) {
				int row = nodes.nextInt();
				if (g.getNodeTable().isValidRow(row)) {
					if (g.getNode(row).getString(GraphConstants.LABEL_FIELD).equals(label)) {
						return g.getNode(row);
					}
				}
			}
		}
		return null;
	}

	private void configBeginNode(Node node) {
		node.setString(GraphConstants.LABEL_FIELD, GraphConstants.BEGIN_NODE_NAME);
		node.setBoolean(GraphConstants.BEGIN_FIELD, true);
		node.setBoolean(GraphConstants.END_FIELD, false);
		node.set(GraphConstants.NODE_TYPE_FIELD, NodeType.ACT_NODE);
		node.setBoolean(GraphConstants.IS_DISPLAY, true);
	}

	private void configEndNode(Node node) {
		node.setString(GraphConstants.LABEL_FIELD, GraphConstants.END_NODE_NAME);
		node.setBoolean(GraphConstants.BEGIN_FIELD, false);
		node.setBoolean(GraphConstants.END_FIELD, true);
		node.set(GraphConstants.NODE_TYPE_FIELD, NodeType.ACT_NODE);
		node.setBoolean(GraphConstants.IS_DISPLAY, true);
	}

	public void configGroupNode(Node node, String label) {
		node.setString(GraphConstants.LABEL_FIELD, label);
		node.setString(GraphConstants.DISPLAY_LABEL_FIELD, "Group: " + label);
		node.setBoolean(GraphConstants.BEGIN_FIELD, false);
		node.setBoolean(GraphConstants.END_FIELD, false);
		node.set(GraphConstants.NODE_TYPE_FIELD, NodeType.GROUP_NODE);
		node.setBoolean(GraphConstants.IS_DISPLAY, true);
		node.setBoolean(GraphConstants.IS_SELECTED, false);
		node.setInt(GraphConstants.NODE_STROKE_COLOR_FIELD, GraphConstants.NODE_STROKE_COLOR);
	}

	private void configNode(Node node, String label, Boolean isGroup) {
		node.setString(GraphConstants.LABEL_FIELD, label);
		node.setString(GraphConstants.DISPLAY_LABEL_FIELD, label + "\n" + this.currentFrequencyNode.get(label));
		node.setBoolean(GraphConstants.BEGIN_FIELD, false);
		node.setBoolean(GraphConstants.END_FIELD, false);
		if (isGroup) {
			node.set(GraphConstants.NODE_TYPE_FIELD, NodeType.GROUP_NODE);
		} else {
			node.set(GraphConstants.NODE_TYPE_FIELD, NodeType.ACT_NODE);
		}
		node.setBoolean(GraphConstants.IS_DISPLAY, true);
		node.setInt(GraphConstants.NODE_STROKE_COLOR_FIELD, GraphConstants.NODE_STROKE_COLOR);
	}

	public void configEdge(Edge e, EdgeObject edgeObject) {
		if (edgeObject.getIsIndirected()) {
			e.setBoolean(GraphConstants.IS_INDIRECTED_EDGE_FIELD, true);
			e.setString(GraphConstants.LABEL_FIELD, edgeObject.getNode1() + " \u21a0 " + edgeObject.getNode2());
		} else {
			e.setBoolean(GraphConstants.IS_INDIRECTED_EDGE_FIELD, false);
			e.setString(GraphConstants.LABEL_FIELD, edgeObject.getNode1() + " \u2192 " + edgeObject.getNode2());
		}

		e.setBoolean(GraphConstants.IS_DISPLAY, true);
		e.setInt(GraphConstants.EDGE_FILL_COLOR_FIELD, GraphConstants.EDGE_STROKE_COLOR);
	}

	private Table initNodeTable() {
		Table nodeData = new Table(0, 1);
		nodeData.addColumn(GraphConstants.LABEL_FIELD, String.class);
		nodeData.addColumn(GraphConstants.DISPLAY_LABEL_FIELD, String.class);
		nodeData.addColumn(GraphConstants.BEGIN_FIELD, boolean.class);
		nodeData.addColumn(GraphConstants.END_FIELD, boolean.class);
		nodeData.addColumn(GraphConstants.IS_SELECTED, boolean.class);
		nodeData.addColumn(GraphConstants.NODE_FILL_COLOR_FIELD, int.class);
		nodeData.addColumn(GraphConstants.NODE_STROKE_COLOR_FIELD, int.class);
		nodeData.addColumn(GraphConstants.NODE_TYPE_FIELD, NodeType.class);
		nodeData.addColumn(GraphConstants.IS_DISPLAY, boolean.class);
		return nodeData;
	}

	private Table initEdgeTable() {
		Table edgeData = new Table(0, 1);
		edgeData.addColumn(Graph.DEFAULT_SOURCE_KEY, int.class);
		edgeData.addColumn(Graph.DEFAULT_TARGET_KEY, int.class);
		edgeData.addColumn(GraphConstants.LABEL_FIELD, String.class);
		edgeData.addColumn(GraphConstants.IS_INDIRECTED_EDGE_FIELD, boolean.class);
		edgeData.addColumn(GraphConstants.IS_DISPLAY, boolean.class);
		edgeData.addColumn(GraphConstants.EDGE_FILL_COLOR_FIELD, int.class);
		return edgeData;
	}

	public void test() {
		//		Node n1 = this.getNodeByLabelInGraph(this.graph, "a");
		//		this.hideNode(n1);
	}

	public HashMap<String, Integer> getFrequencyNode() {
		return frequencyNode;
	}

	public void setFrequencyNode(HashMap<String, Integer> frequencyNode) {
		this.frequencyNode = frequencyNode;
	}

	public HashMap<EdgeObject, Integer> getFrequencyEdge() {
		return frequencyEdge;
	}

	public void setFrequencyEdge(HashMap<EdgeObject, Integer> frequencyEdge) {
		this.frequencyEdge = frequencyEdge;
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public GDPMLogSkeleton getLog() {
		return log;
	}

	public void setNodeStrokeColorAction(ColorAction nodeStrokeColorAction) {
		this.nodeStrokeColorAction = nodeStrokeColorAction;
	}

	public GraphObjectClickControl getEdgeClickControl() {
		return edgeClickControl;
	}

	public void setEdgeClickControl(GraphObjectClickControl edgeClickControl) {
		this.edgeClickControl = edgeClickControl;
	}

	public Boolean getIsHighLevel() {
		return isHighLevel;
	}

	public int getBeginNodeRow() {
		return beginNodeRow;
	}

	public int getEndNodeRow() {
		return endNodeRow;
	}

	public HashMap<String, Node> getMapGroupNode() {
		return mapGroupNode;
	}

	public void setMapGroupNode(HashMap<String, Node> mapGroupNode) {
		this.mapGroupNode = mapGroupNode;
	}

	public HashMap<String, Integer> getCurrentFrequencyNode() {
		return currentFrequencyNode;
	}

	public void setCurrentFrequencyNode(HashMap<String, Integer> currentFrequencyNode) {
		this.currentFrequencyNode = currentFrequencyNode;
	}

	public HashMap<EdgeObject, Integer> getCurrentFrequencyEdge() {
		return currentFrequencyEdge;
	}

	public void setCurrentFrequencyEdge(HashMap<EdgeObject, Integer> currentFrequencyEdge) {
		this.currentFrequencyEdge = currentFrequencyEdge;
	}

	public HashMap<EdgeObject, ThroughputTimeObject> getCurrentThroughputEdge() {
		return currentThroughputEdge;
	}

	public void setCurrentThroughputEdge(HashMap<EdgeObject, ThroughputTimeObject> currentThroughputEdge) {
		this.currentThroughputEdge = currentThroughputEdge;
	}

	public CustomizedEdgeRenderer getEdgeRenderer() {
		return edgeRenderer;
	}

	public void setEdgeRenderer(CustomizedEdgeRenderer edgeRenderer) {
		this.edgeRenderer = edgeRenderer;
	}

}
