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
import org.processmining.goaldrivenprocessmining.objectHelper.ActivityHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.GroupSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.enumaration.NodeType;

import graph.action.CustomColorNodeFillAction;
import graph.action.CustomEdgeStrokeWidthAction;
import graph.action.CustomizedEdgeRenderer;
import graph.action.NodeRenderer;
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
	private Graph inviGraph;
	private Boolean isHighLevel;
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
	// group
	private HashMap<String, HashMap<Graph, Node>> mapGroupNode;

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
		this.mapGroupNode = new HashMap<>();

		// repaint
		ActionList repaint = new ActionList();
		repaint.add(new RepaintAction());
		m_vis.putAction("repaint", repaint);

		if (this.log != null && !this.log.getActivityHashTable().getActivityTable().isEmpty()) {
			//			this.frequencyEdge = gdpmLogSkeleton.getStatObject().getMapStatEdge();
			//			this.frequencyNode = gdpmLogSkeleton.getStatObject().getMapStatNode();
			this.makeInviGraph();
			this.makeGraph();
			m_vis.addGraph("graph", this.graph);
			m_vis.addGraph("inviGraph", inviGraph);

			// control
			this.setDefaultControl();
			// layout
			this.setDefaultLayout();
			// action
			this.configDefaultGraph();
			// center graph
			this.centerGraph();
			this.test();
			//			this.dragMultipleNodesControl.initInvisibleNodes();

		}

	}

	private void centerGraph() {
		double zoomWidth = this.getBounds().getWidth() / this.getVisualization().getBounds("graph").getWidth();
		double zoomHeight = this.getBounds().getHeight() / this.getVisualization().getBounds("graph").getHeight();
		double zoomRatio = zoomWidth < zoomHeight ? zoomWidth : zoomHeight;

		double x = this.getVisualization().getBounds("graph").getCenterX();
		double y = this.getVisualization().getBounds("graph").getCenterY();
		this.animatePanAndZoomTo(new Point2D.Double(x, y), zoomRatio, 1000);
		this.revalidate();
		this.repaint();
	}

	//	public static void main(String[] args) throws Exception {
	//				File file = new File("C:\\D\\data\\receipt.xes");
	//				File file2 = new File("C:\\D\\data\\complaint-handling.xes");
	//		
	//				// Create an input stream for the XES file
	//				InputStream is = new FileInputStream(file);
	//		
	//				// Create a parser for XES files
	//				XesXmlParser parser = new XesXmlParser();
	//		
	//				XLog log = parser.parse(is).get(0);
	//		
	//				// Create an input stream for the XES file
	//				InputStream is2 = new FileInputStream(file2);
	//		
	//				// Create a parser for XES files
	//				XesXmlParser parser2 = new XesXmlParser();
	//		
	//				XLog log2 = parser2.parse(is2).get(0);
	//		
	//				GoalDrivenDFG ex = new GoalDrivenDFG(null);
	//				GoalDrivenDFG ex2 = new GoalDrivenDFG(new GDPMLogSkeleton(log));
	//				GoalDrivenDFG ex3 = new GoalDrivenDFG(new GDPMLogSkeleton(log2));
	//				
	//				
	//			
	//				System.out.println(ex.getVisualization().getBounds("graph"));
	//				System.out.println(ex.getBounds());
	//				
	//
	//				
	//				double zoomWidth = ex.getBounds().getWidth() / ex.getVisualization().getBounds("graph").getWidth();
	//				double zoomHeight = ex.getBounds().getHeight() / ex.getVisualization().getBounds("graph").getHeight();
	//				double zoomRatio = zoomWidth < zoomHeight ? zoomWidth : zoomHeight;
	//				
	//				
	//				
	//				double x = 300;
	//				double y = -450;
	//				System.out.println(zoomRatio);
	//				ex.animatePanAndZoomTo(new Point2D.Double(x, y), zoomRatio, 1000);
	//				
	//				ex.validate();
	//				ex.repaint();
	//
	//				JFrame frame = new JFrame("prefuse example");
	//				JPanel p = new JPanel();
	//				p.setBackground(Color.BLACK);
	//				p.add(ex);
	//				ex.setBackground(Color.BLACK);
	//				frame.getContentPane().add(p);
	//				frame.pack(); // layout components in window
	//				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//				frame.setVisible(true); // show the window
	//	}

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

	private void makeInviGraph() {
		// Create tables for node and edge data, and configure their columns.
		// init node table
		Table nodeTable = new Table(0, 1);
		nodeTable.addColumn(GraphConstants.LABEL_FIELD, String.class);
		nodeTable.addColumn(GraphConstants.IS_INVISIBLE, boolean.class);
		nodeTable.addColumn(GraphConstants.BEGIN_FIELD, boolean.class);
		nodeTable.addColumn(GraphConstants.END_FIELD, boolean.class);
		nodeTable.addColumn(GraphConstants.IS_DISPLAY, boolean.class);
		nodeTable.addColumn(GraphConstants.IS_SELECTED, boolean.class);
		nodeTable.addColumn(GraphConstants.NODE_TYPE_FIELD, NodeType.class);
		this.inviGraph = new Graph(nodeTable, true);
	}

	private void configDefaultGraph() {

		this.setDefaultEdgeStrokeColor();
		this.setDefaultEdgeStrokeWidth();

		this.setDefaultNodeStrokeWidth();
		this.setDefaultNodeFillColor();
		this.setDefaultNodeStrokeColor();
		this.setDefaultTextColorAndSize();
		this.setDefaultNodeSize();

		this.setDefaultRenderer();
		this.testInviNode();
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
			backgroundDoubleClickControl = new BackgroundDoubleClickControl(this.graph.getNodeTable());
			addControlListener(backgroundDoubleClickControl);
			/*************************/
			/* drag multiple nodes */
			dragMultipleNodesControl = new DragMultipleNodesControl(this);
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
		dragMultipleNodesControl = new DragMultipleNodesControl(this);
		addControlListener(dragMultipleNodesControl);
		rightClickControl = new RightClickControl(this);
		addControlListener(rightClickControl);

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
		CustomizedEdgeRenderer edgeR = new CustomizedEdgeRenderer(prefuse.Constants.EDGE_TYPE_CURVE,
				prefuse.Constants.EDGE_ARROW_FORWARD);
		edgeR.setArrowHeadSize(GraphConstants.ARROW_HEAD_WIDTH, GraphConstants.ARROW_HEAD_HEIGHT);
		edgeR.setArrowDoubleHeadSize(GraphConstants.ARROW_HEAD_WIDTH + 5, GraphConstants.ARROW_HEAD_HEIGHT);

		NodeRenderer label = new NodeRenderer("label");
		label.setRoundedCorner(8, 8);
		label.setHorizontalPadding(10);
		label.setVerticalPadding(10);
		DefaultRendererFactory drf = new DefaultRendererFactory();
		drf.setDefaultRenderer(label);

		/* begin end shape */
		Predicate beginPredicate = (Predicate) ExpressionParser.parse("begin = true");
		ShapeAction shapeAction = new ShapeAction("graph.nodes", Constants.SHAPE_RECTANGLE);
		ShapeAction shapeAction1 = new ShapeAction("inviGraph.nodes", Constants.SHAPE_RECTANGLE);
		shapeAction.add(beginPredicate, Constants.SHAPE_TRIANGLE_RIGHT);
		ActionList shape = new ActionList();
		shape.add(shapeAction);
		shape.add(shapeAction1);
		m_vis.putAction("shape", shape);
		m_vis.run("shape");
		/******************/
		drf.setDefaultEdgeRenderer(edgeR);
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
		NodeLinkTreeLayout treeLayout = new NodeLinkTreeLayout("graph", Constants.ORIENT_TOP_BOTTOM, 300, 220, 300);
		m_vis.putAction(GraphConstants.LAYOUT_ACTION, treeLayout);
		treeLayout.setLayoutAnchor(new Point2D.Double(500, 100));
		m_vis.run(GraphConstants.LAYOUT_ACTION);
	}

	public void setDefaultTextColorAndSize() {
		this.textColorAction = new ColorAction(GraphConstants.NODE_GROUP, VisualItem.TEXTCOLOR,
				GraphConstants.TEXT_COLOR);
		m_vis.putAction(GraphConstants.TEXT_COLOR_ACTION, this.textColorAction);
		m_vis.run(GraphConstants.TEXT_COLOR_ACTION);

		FontAction fontAction = new FontAction(GraphConstants.NODE_GROUP, new Font("Arial", Font.BOLD, 14));
		m_vis.putAction(GraphConstants.FONT_ACTION, fontAction);
		m_vis.run(GraphConstants.FONT_ACTION);
	}

	public void setDefaultArrowFillColor() {
		this.arrowFillColorAction = new ColorAction("graph.edges", VisualItem.FILLCOLOR,
				GraphConstants.EDGE_STROKE_COLOR);
		m_vis.putAction(GraphConstants.ARROW_FILL_COLOR_ACTION, this.arrowFillColorAction);
		m_vis.run(GraphConstants.ARROW_FILL_COLOR_ACTION);

	}

	public void setDefaultNodeStrokeWidth() {
		this.nodeStrokeWidthAction = new StrokeAction(GraphConstants.NODE_GROUP);
		this.nodeStrokeWidthAction.setDefaultStroke(new BasicStroke(7));
		m_vis.putAction(GraphConstants.NODE_STROKE_WIDTH_ACTION, this.nodeStrokeWidthAction);
		m_vis.run(GraphConstants.NODE_STROKE_WIDTH_ACTION);
	}

	private void setDefaultEdgeStrokeWidth() {
		this.calculateFrequencyEdge();
		this.currentFrequencyEdge = this.frequencyEdge;
		this.runCustomEdgeStrokeWidthAction();
	}

	public void setEdgeStrokeWidthWithGroup(GroupSkeleton groupSkeleton) {
		List<GroupSkeleton> collapsedGroups = new ArrayList<GroupSkeleton>();
		for (GroupSkeleton group : this.log.getConfig().getListGroupSkeletons()) {
			if (!group.equals(groupSkeleton)) {
				collapsedGroups.add(group);
			}
		}
		HashMap<EdgeObject, Integer> newFrequencyEdge = this.currentFrequencyEdge;
		for (EdgeObject edge : this.frequencyEdge.keySet()) {
			String trueSourceLabel = LogSkeletonUtils.getTrueActivityLabel(this.getLog(), collapsedGroups,
					edge.getNode1());
			String trueTargeLabel = LogSkeletonUtils.getTrueActivityLabel(this.getLog(), collapsedGroups,
					edge.getNode2());
			String oldSourceLabel = LogSkeletonUtils.getTrueActivityLabel(this.getLog(),
					this.getLog().getConfig().getListGroupSkeletons(), edge.getNode1());
			String oldTargeLabel = LogSkeletonUtils.getTrueActivityLabel(this.getLog(),
					this.getLog().getConfig().getListGroupSkeletons(), edge.getNode2());
			EdgeObject newEdge = new EdgeObject(trueSourceLabel, trueTargeLabel);
			EdgeObject oldEdge = new EdgeObject(oldSourceLabel, oldTargeLabel);
			newFrequencyEdge.put(newEdge, this.frequencyEdge.get(edge));
			if (newFrequencyEdge.containsKey(oldEdge) && !oldEdge.equals(newEdge)) {
				newFrequencyEdge.remove(oldEdge);
			}
		}
		this.currentFrequencyEdge = newFrequencyEdge;
		this.runCustomEdgeStrokeWidthAction();

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
	}

	public void runCustomEdgeStrokeWidthAction() {
		// transform frequency to color
		HashMap<EdgeObject, Float> mapEdgeStrokeWidth = new HashMap<>();
		float min = GraphConstants.LOWER_BOUND_EDGE_STROKE_WIDTH;
		float max = GraphConstants.UPPER_BOUND_EDGE_STROKE_WIDTH;
		int minFreq = Integer.MAX_VALUE;
		int maxFreq = 0;
		for (EdgeObject edgeObject : this.currentFrequencyEdge.keySet()) {
			if (this.currentFrequencyEdge.get(edgeObject) >= maxFreq) {
				maxFreq = this.currentFrequencyEdge.get(edgeObject);
			}
			if (this.currentFrequencyEdge.get(edgeObject) <= minFreq) {
				minFreq = this.currentFrequencyEdge.get(edgeObject);
			}
		}
		if (maxFreq == minFreq) {
			for (EdgeObject edge : this.currentFrequencyEdge.keySet()) {
				mapEdgeStrokeWidth.put(edge, 3f);
			}
		} else {
			// Calculate the ratio between the range and the range of data values
			float range = max - min;
			int dataRange = maxFreq - minFreq;
			float ratio = range / dataRange;

			for (EdgeObject edge : this.currentFrequencyEdge.keySet()) {
				int value = this.currentFrequencyEdge.get(edge);
				float assignedValue = min + ((value - minFreq) * ratio);
				mapEdgeStrokeWidth.put(edge, assignedValue);
			}
		}
		CustomEdgeStrokeWidthAction customEdgeStrokeWidthAction = new CustomEdgeStrokeWidthAction(
				GraphConstants.EDGE_GROUP, mapEdgeStrokeWidth);
		m_vis.putAction(GraphConstants.EDGE_STROKE_WIDTH_ACTION, customEdgeStrokeWidthAction);
		m_vis.run(GraphConstants.EDGE_STROKE_WIDTH_ACTION);
	}

	public void setDefaultEdgeStrokeColor() {
		this.edgeStrokeColorAction = new ColorAction("graph.edges", VisualItem.STROKECOLOR,
				GraphConstants.EDGE_STROKE_COLOR);
		m_vis.putAction(GraphConstants.EDGE_STROKE_COLOR_ACTION, this.edgeStrokeColorAction);
		m_vis.run(GraphConstants.EDGE_STROKE_COLOR_ACTION);
		this.arrowFillColorAction = new ColorAction("graph.edges", VisualItem.FILLCOLOR,
				GraphConstants.EDGE_STROKE_COLOR);
		m_vis.putAction(GraphConstants.ARROW_FILL_COLOR_ACTION, this.arrowFillColorAction);
		m_vis.run(GraphConstants.ARROW_FILL_COLOR_ACTION);

	}

	public void setDefaultNodeStrokeColor() {
		this.nodeStrokeColorAction = new ColorAction("graph.nodes", VisualItem.STROKECOLOR);
		this.nodeStrokeColorAction.setDefaultColor(GraphConstants.NODE_STROKE_COLOR);
		m_vis.putAction(GraphConstants.NODE_STROKE_COLOR_ACTION, this.nodeStrokeColorAction);
		m_vis.run(GraphConstants.NODE_STROKE_COLOR_ACTION);

	}

	private void setDefaultNodeFillColor() {
		this.calculateFrequencyNode();
		HashMap<String, Integer> newFrequencyNode = new HashMap<String, Integer>();
		for (String act : this.frequencyNode.keySet()) {
			String trueLabel = LogSkeletonUtils.getTrueActivityLabel(this.getLog(),
					this.getLog().getConfig().getListGroupSkeletons(), act);
			if (newFrequencyNode.containsKey(trueLabel)) {
				newFrequencyNode.put(trueLabel, this.frequencyNode.get(act) + newFrequencyNode.get(trueLabel));
			} else {
				newFrequencyNode.put(trueLabel, this.frequencyNode.get(act));
			}

		}
		this.currentFrequencyNode = newFrequencyNode;
		this.runCustomColorNodeFillAction();
	}

	public void testInviNode() {
		// stroke color
		ColorAction testC = new ColorAction("inviGraph.nodes", VisualItem.STROKECOLOR);
		testC.setDefaultColor(ColorLib.color(Color.WHITE));

		float[] dashPattern = { 2.0f, 2.0f }; // 2-pixel dash, 2-pixel gap
		// Create a BasicStroke with the specified dash pattern
		BasicStroke dashedStroke = new BasicStroke(2.0f, // Width of the stroke
				BasicStroke.CAP_BUTT, // End cap style
				BasicStroke.JOIN_MITER, // Join style
				10.0f, // Miter limit
				dashPattern, // Dash pattern
				0.0f // Dash phase (offset into the dash pattern)
		);
		StrokeAction testStroke = new StrokeAction("inviGraph.nodes", dashedStroke);
		ColorAction testColor = new ColorAction("inviGraph.nodes", VisualItem.FILLCOLOR);
		testColor.setDefaultColor(ColorLib.color(Color.RED));
		ActionList test = new ActionList();
		test.add(testC);
		test.add(testStroke);
		test.add(testColor);
		m_vis.putAction("test", test);
		m_vis.run("test");
	}

	public void setNodeFillColorWithGroup(GroupSkeleton groupSkeleton) {
		HashMap<String, Integer> newFrequencyNode = this.currentFrequencyNode;

		List<String> acts = groupSkeleton.getListAct();
		List<GroupSkeleton> groups = groupSkeleton.getListGroup();

		int newFreq = 0;
		for (String act : acts) {
			newFreq += this.frequencyNode.get(act);
		}
		for (GroupSkeleton group : groups) {
			if (newFrequencyNode.containsKey(group.getGroupName())) {
				newFreq += newFrequencyNode.get(group.getGroupName());
			}
		}
		newFrequencyNode.put(groupSkeleton.getGroupName(), newFreq);
		this.currentFrequencyNode = newFrequencyNode;
		this.runCustomColorNodeFillAction();
	}

	public void runCustomColorNodeFillAction() {
		// Find the minimum and maximum values in the data array
		int minFreq = Integer.MAX_VALUE;
		int maxFreq = 0;
		for (String act : this.currentFrequencyNode.keySet()) {
			if (this.currentFrequencyNode.get(act) >= maxFreq) {
				maxFreq = this.currentFrequencyNode.get(act);
			}
			if (this.currentFrequencyNode.get(act) <= minFreq) {
				minFreq = this.currentFrequencyNode.get(act);
			}
		}
		HashMap<String, Color> mapActColor = new HashMap<String, Color>();
		if (maxFreq == minFreq) {
			for (String act : this.currentFrequencyNode.keySet()) {
				mapActColor.put(act, GraphConstants.NODE_FILL_DARK_COLOR);
			}
		} else {
			double valueRange = maxFreq - minFreq;
			// Calculate the color gradient for each data value
			Color darkColor = GraphConstants.NODE_FILL_DARK_COLOR;
			Color lightColor = GraphConstants.NODE_FILL_LIGHT_COLOR;

			for (String act : this.currentFrequencyNode.keySet()) {
				int value = this.currentFrequencyNode.get(act);
				double normalizedValue = (value - minFreq) / valueRange;

				// Interpolate the color based on the normalized value
				int red = interpolate(lightColor.getRed(), darkColor.getRed(), normalizedValue);
				int green = interpolate(lightColor.getGreen(), darkColor.getGreen(), normalizedValue);
				int blue = interpolate(lightColor.getBlue(), darkColor.getBlue(), normalizedValue);

				// Create the gradient color
				mapActColor.put(act, new Color(red, green, blue));
			}
		}
		CustomColorNodeFillAction customFillByLabel = new CustomColorNodeFillAction(GraphConstants.NODE_GROUP,
				mapActColor);
		m_vis.putAction(GraphConstants.NODE_FILL_COLOR_ACTION, customFillByLabel);
		m_vis.run(GraphConstants.NODE_FILL_COLOR_ACTION);
	}

	private void calculateFrequencyNode() {
		for (Map.Entry<String, Map<Integer, List<Integer>>> entry : this.log.getActivityHashTable().getActivityTable()
				.entrySet()) {
			String act = entry.getKey();
			Map<Integer, List<Integer>> allPos = entry.getValue();
			int total = 0;
			for (List<Integer> pos : allPos.values()) {
				total += pos.size();
			}
			this.frequencyNode.put(act, total);
		}
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
		ActivityHashTable activityHashTable = this.log.getActivityHashTable();
		EdgeHashTable edgeHashTable = this.log.getEdgeHashTable();
		// add all node, set display true
		for (String act : activityHashTable.getActivityTable().keySet()) {
			Node node1 = null;
			node1 = this.graph.addNode();
			this.configNode(node1, act, false);
		}
		// add edge
		for (EdgeObject edge : edgeHashTable.getEdgeTable().keySet()) {
			Node node1 = this.getNodeByLabelInGraph(this.graph, edge.getNode1());
			Node node2 = this.getNodeByLabelInGraph(this.graph, edge.getNode2());
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
			if (graph.getEdgeTable().isValidRow(nodeRow)) {
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
		int row = node.getRow();
		// make it true in the table
		graph.getNodeTable().setBoolean(node.getRow(), GraphConstants.IS_DISPLAY, true);
		graph.getNodeTable().setBoolean(node.getRow(), GraphConstants.IS_SELECTED, false);
		// make the regarding edges shown
		for (int i = 0; i < graph.getEdgeTable().getMaximumRow(); i++) {
			if (graph.getEdgeTable().isValidRow(i)) {
				int source = graph.getEdgeTable().getTuple(i).getInt(Graph.DEFAULT_SOURCE_KEY);
				int target = graph.getEdgeTable().getTuple(i).getInt(Graph.DEFAULT_TARGET_KEY);
				// if the node is the source
				if (source == row) {
					// check if the target is displaying
					if (graph.getNodeTable().getBoolean(target, GraphConstants.IS_DISPLAY)) {
						graph.getEdgeTable().setBoolean(i, GraphConstants.IS_DISPLAY, true);
					}
				} else if (target == row) {
					if (graph.getNodeTable().getBoolean(source, GraphConstants.IS_DISPLAY)) {
						graph.getEdgeTable().setBoolean(i, GraphConstants.IS_DISPLAY, true);
					}
				}
			}
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
		Table inviNodeTable = this.inviGraph.getNodeTable();
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
		// get the invi node
		nodes = inviNodeTable.iterator();
		while (nodes.hasNext()) {
			int row = nodes.nextInt();
			if (inviNodeTable.isValidRow(row)) {
				Node node = this.inviGraph.getNode(row);
				if (node.getString(GraphConstants.LABEL_FIELD).equals(label)) {
					nodeToRemove = node;
					break;
				}
			}
		}
		// remove invi node
		this.removeNode(this.inviGraph, nodeToRemove);
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
	}

	private void configEndNode(Node node) {
		node.setString(GraphConstants.LABEL_FIELD, GraphConstants.END_NODE_NAME);
		node.setBoolean(GraphConstants.BEGIN_FIELD, false);
		node.setBoolean(GraphConstants.END_FIELD, true);
		node.set(GraphConstants.NODE_TYPE_FIELD, NodeType.ACT_NODE);
	}

	public void configInvisibleNode(Node node, String label) {
		node.setString(GraphConstants.LABEL_FIELD, label);
		node.setBoolean(GraphConstants.BEGIN_FIELD, false);
		node.setBoolean(GraphConstants.END_FIELD, false);
		node.set(GraphConstants.NODE_TYPE_FIELD, NodeType.GROUP_NODE);
		node.setBoolean(GraphConstants.IS_INVISIBLE, true);
		node.setBoolean(GraphConstants.IS_DISPLAY, true);
		node.setBoolean(GraphConstants.IS_SELECTED, false);
	}

	public void configGroupNode(Node node, String label) {
		node.setString(GraphConstants.LABEL_FIELD, label);
		node.setBoolean(GraphConstants.BEGIN_FIELD, false);
		node.setBoolean(GraphConstants.END_FIELD, false);
		node.set(GraphConstants.NODE_TYPE_FIELD, NodeType.GROUP_NODE);
		node.setBoolean(GraphConstants.IS_DISPLAY, true);
		node.setBoolean(GraphConstants.IS_SELECTED, false);
	}

	private void configNode(Node node, String label, Boolean isGroup) {
		node.setString(GraphConstants.LABEL_FIELD, label);
		node.setBoolean(GraphConstants.BEGIN_FIELD, false);
		node.setBoolean(GraphConstants.END_FIELD, false);
		if (isGroup) {
			node.set(GraphConstants.NODE_TYPE_FIELD, NodeType.GROUP_NODE);
		} else {
			node.set(GraphConstants.NODE_TYPE_FIELD, NodeType.ACT_NODE);
		}
		node.setBoolean(GraphConstants.IS_DISPLAY, true);
	}

	public void configEdge(Edge e, EdgeObject edgeObject) {
		if (edgeObject.getIsIndirected()) {
			e.setBoolean(GraphConstants.IS_INDIRECTED_EDGE_FIELD, true);
		} else {
			e.setBoolean(GraphConstants.IS_INDIRECTED_EDGE_FIELD, false);
		}
		e.setString(GraphConstants.LABEL_FIELD, "(" + edgeObject.getNode1() + " ," + edgeObject.getNode2() + ")");
		e.setBoolean(GraphConstants.IS_DISPLAY, true);
	}

	private Table initNodeTable() {
		Table nodeData = new Table(0, 1);
		nodeData.addColumn(GraphConstants.LABEL_FIELD, String.class);
		nodeData.addColumn(GraphConstants.BEGIN_FIELD, boolean.class);
		nodeData.addColumn(GraphConstants.END_FIELD, boolean.class);
		nodeData.addColumn(GraphConstants.IS_SELECTED, boolean.class);
		nodeData.addColumn(GraphConstants.FREQUENCY_FILL_COLOR_NODE_FIELD, int.class);
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

	public Graph getInviGraph() {
		return inviGraph;
	}

	public void setInviGraph(Graph inviGraph) {
		this.inviGraph = inviGraph;
	}

	public HashMap<String, HashMap<Graph, Node>> getMapGroupNode() {
		return mapGroupNode;
	}

	public void setMapGroupNode(HashMap<String, HashMap<Graph, Node>> mapGroupNode) {
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
}
