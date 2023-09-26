package graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.processmining.goaldrivenprocessmining.algorithms.LogSkeletonUtils;
import org.processmining.goaldrivenprocessmining.objectHelper.ActivityHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.CategoryObject;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeHashTable;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.objectHelper.MapActivityCategoryObject;
import org.processmining.goaldrivenprocessmining.objectHelper.ValueCategoryObject;
import org.processmining.goaldrivenprocessmining.objectHelper.enumaration.NodeType;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;

import graph.action.CustomColorNodeFillAction;
import graph.action.CustomEdgeStrokeWidthAction;
import graph.action.CustomizedEdgeRenderer;
import graph.action.SetNodeSizeAction;
import graph.controls.BackgroundDoubleClickControl;
import graph.controls.BorderNodeControl;
import graph.controls.CustomPanControl;
import graph.controls.DragMultipleNodesControl;
import graph.controls.GraphObjectClickControl;
import graph.controls.RightClickControl;
import graph.controls.SelectMultipleNodesControl;
import graph.controls.SquareSelectControl;
import graph.utils.node.NodeRenderer;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
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
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

public class GoalDrivenDFG extends Display {
	private GDPMLogSkeleton log;
	private int beginNodeRow;
	private int endNodeRow;
	private Graph graph;
	//	private HashMap<EdgeObject, StatEdgeObject> frequencyEdge;
	//	private HashMap<String, StatNodeObject> frequencyNode;
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
	private ColorAction nodeFillColorAction;
	private ColorAction edgeStrokeColorAction;
	private StrokeAction edgeStrokeWidthAction;
	private StrokeAction nodeStrokeWidthAction;
	private ColorAction arrowFillColorAction;
	private ColorAction textColorAction;

	public GoalDrivenDFG(GDPMLogSkeleton gdpmLogSkeleton, Boolean isHighLevel) {
		super(new Visualization());
		this.log = gdpmLogSkeleton;

		// action
		this.nodeStrokeColorAction = null;
		this.nodeFillColorAction = null;
		this.edgeStrokeColorAction = null;
		this.edgeStrokeWidthAction = null;
		this.arrowFillColorAction = null;
		this.textColorAction = null;

		// repaint
		ActionList repaint = new ActionList();
		repaint.add(new RepaintAction());
		m_vis.putAction("repaint", repaint);

		if (this.log != null && !this.log.getLogSkeleton().getLog().isEmpty()) {
			//			this.frequencyEdge = gdpmLogSkeleton.getStatObject().getMapStatEdge();
			//			this.frequencyNode = gdpmLogSkeleton.getStatObject().getMapStatNode();
			this.graph = this.makeGraph(isHighLevel);
			m_vis.addGraph("graph", graph);
			// control
			this.setDefaultControl();
			// layout
			this.setDefaultLayout();
			// action
			this.configDefaultGraph();
			// center graph
			this.centerGraph();

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

	public Graph makeGraph(Boolean isHighLevel) {

		// Create tables for node and edge data, and configure their columns.
		// init node table
		Table nodeTable = this.initNodeTable();
		// init edge table
		Table edgeTable = this.initEdgeTable();
		// init graph
		Graph g = new Graph(nodeTable, edgeTable, true);
		if (!this.log.getLogSkeleton().getLog().isEmpty()) {
			// add begin and end node;
			this.addBeginToTable(g);
			// add activities in log to node table and add edges
			this.addActToTable(g, isHighLevel);
			return g;
		} else {
			return new Graph();
		}

	}

	public void configDefaultGraph() {

		this.setDefaultArrowFillColor();
		this.setDefaultEdgeStrokeColor();
		this.setDefaultEdgeStrokeWidth();
		this.setDefaultNodeStrokeWidth();
		this.setDefaultNodeFillColor();
		this.setDefaultNodeStrokeColor();
		this.setDefaultTextColor();
		this.setDefaultNodeSize();
		this.setDefaultRenderer();
	}

	public void setDefaultControl() {
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

	public void removeAllControls() {
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

	public void resetControl() {
		this.removeControlListener(this.focusControl);
		this.removeControlListener(this.customPanControl);
		this.removeControlListener(this.backgroundDoubleClickControl);
		this.removeControlListener(this.borderNodeControl);
		this.removeControlListener(this.dragMultipleNodesControl);
		this.removeControlListener(this.selectMultipleNodesControl);
		this.removeControlListener(this.wheelZoomControl);
		this.removeControlListener(this.edgeClickControl);
		this.removeControlListener(this.squareSelectControl);
		this.setDefaultControl();
	}

	public void setDefaultRenderer() {
		CustomizedEdgeRenderer edgeR = new CustomizedEdgeRenderer(prefuse.Constants.EDGE_TYPE_CURVE,
				prefuse.Constants.EDGE_ARROW_FORWARD);
		edgeR.setArrowHeadSize(GraphConstants.ARROW_HEAD_WIDTH, GraphConstants.ARROW_HEAD_HEIGHT);
		edgeR.setArrowDoubleHeadSize(GraphConstants.ARROW_HEAD_WIDTH + 5, GraphConstants.ARROW_HEAD_HEIGHT);

		LabelRenderer label = new NodeRenderer("label");
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

	public void setDefaultLayout() {
		NodeLinkTreeLayout treeLayout = new NodeLinkTreeLayout("graph", Constants.ORIENT_TOP_BOTTOM, 400, 220, 300);
		m_vis.putAction(GraphConstants.LAYOUT_ACTION, treeLayout);
		treeLayout.setLayoutAnchor(new Point2D.Double(500, 100));
		m_vis.run(GraphConstants.LAYOUT_ACTION);
	}

	public void setDefaultTextColor() {
		this.textColorAction = new ColorAction(GraphConstants.NODE_GROUP, VisualItem.TEXTCOLOR,
				GraphConstants.TEXT_COLOR);
		m_vis.putAction(GraphConstants.TEXT_COLOR_ACTION, this.textColorAction);
		m_vis.run(GraphConstants.TEXT_COLOR_ACTION);
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

	public void setDefaultEdgeStrokeWidth() {

		HashMap<EdgeObject, Integer> frequencyEdge = new HashMap<EdgeObject, Integer>();
		// calculate frequency edge
		for (Map.Entry<EdgeObject, Map<Integer, List<Integer[]>>> entry : this.getLog().getLogSkeleton()
				.getEdgeHashTable().getEdgeTable().entrySet()) {
			EdgeObject edge = entry.getKey();
			Map<Integer, List<Integer[]>> allPos = entry.getValue();
			int total = 0;
			for (List<Integer[]> pos : allPos.values()) {
				total += pos.size();
			}
			frequencyEdge.put(edge, total);
		}
		// transform frequency to color
		HashMap<EdgeObject, Float> mapEdgeStrokeWidth = new HashMap<>();
		float min = GraphConstants.LOWER_BOUND_EDGE_STROKE_WIDTH;
		float max = GraphConstants.UPPER_BOUND_EDGE_STROKE_WIDTH;
		int minFreq = Integer.MAX_VALUE;
		int maxFreq = 0;
		for (EdgeObject edgeObject : frequencyEdge.keySet()) {
			if (frequencyEdge.get(edgeObject) >= maxFreq) {
				maxFreq = frequencyEdge.get(edgeObject);
			}
			if (frequencyEdge.get(edgeObject) <= minFreq) {
				minFreq = frequencyEdge.get(edgeObject);
			}
		}
		if (maxFreq == minFreq) {
			for (EdgeObject edge : frequencyEdge.keySet()) {
				mapEdgeStrokeWidth.put(edge, 3f);
			}
		} else {
			// Calculate the ratio between the range and the range of data values
			float range = max - min;
			int dataRange = maxFreq - minFreq;
			float ratio = range / dataRange;

			for (EdgeObject edge : frequencyEdge.keySet()) {
				int value = frequencyEdge.get(edge);
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
	}

	public void setDefaultNodeStrokeColor() {
		this.nodeStrokeColorAction = new ColorAction("graph.nodes", VisualItem.STROKECOLOR);
		this.nodeStrokeColorAction.setDefaultColor(GraphConstants.NODE_STROKE_COLOR);
		m_vis.putAction(GraphConstants.NODE_STROKE_COLOR_ACTION, this.nodeStrokeColorAction);
		m_vis.run(GraphConstants.NODE_STROKE_COLOR_ACTION);
	}

	public void setDefaultNodeFillColor() {
		HashMap<String, Integer> frequencyNode = new HashMap<String, Integer>();
		// calculate frequency node
		for (Map.Entry<String, Map<Integer, List<Integer>>> entry : this.log.getLogSkeleton().getActivityHashTable()
				.getActivityTable().entrySet()) {
			String act = LogSkeletonUtils.getTrueActivityLabel(this.getLog(), entry.getKey());
			Map<Integer, List<Integer>> allPos = entry.getValue();
			int total = 0;
			for (List<Integer> pos : allPos.values()) {
				total += pos.size();
			}
			frequencyNode.put(act, total);
		}

		// Find the minimum and maximum values in the data array
		int minFreq = Integer.MAX_VALUE;
		int maxFreq = 0;
		for (String act : frequencyNode.keySet()) {
			if (frequencyNode.get(act) >= maxFreq) {
				maxFreq = frequencyNode.get(act);
			}
			if (frequencyNode.get(act) <= minFreq) {
				minFreq = frequencyNode.get(act);
			}
		}
		HashMap<String, Color> mapActColor = new HashMap<String, Color>();
		if (maxFreq == minFreq) {
			for (String act : frequencyNode.keySet()) {
				mapActColor.put(act, GraphConstants.NODE_FILL_DARK_COLOR);
			}
		} else {
			double valueRange = maxFreq - minFreq;
			// Calculate the color gradient for each data value
			Color darkColor = GraphConstants.NODE_FILL_DARK_COLOR;
			Color lightColor = GraphConstants.NODE_FILL_LIGHT_COLOR;

			for (String act : frequencyNode.keySet()) {
				int value = frequencyNode.get(act);
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

	private void addActToTable(Graph g, Boolean isHighLevel) {
		ActivityHashTable activityHashTable = this.log.getLogSkeleton().getActivityHashTable();
		EdgeHashTable edgeHashTable = this.log.getLogSkeleton().getEdgeHashTable();
		List<String> addedNodes = new ArrayList<String>();

		// add node
		for (String act : activityHashTable.getActivityTable().keySet()) {
			if (Arrays.asList(this.getLog().getLogSkeleton().getConfig().getSelectedActs()).contains(act)) {
				String trueActLabel = LogSkeletonUtils.getTrueActivityLabel(this.log, act);
				if (!addedNodes.contains(trueActLabel)) {
					Node node1 = null;
					node1 = g.addNode();
					if (this.log.getLogSkeleton().isAGroupSkeleton(trueActLabel)) {
						this.configNode(node1, trueActLabel, true);
					} else {
						this.configNode(node1, trueActLabel, false);
					}
					addedNodes.add(trueActLabel);
				}
			}
		}
		// add edge
		for (EdgeObject edge : edgeHashTable.getEdgeTable().keySet()) {
			String source = edge.getNode1().getCurrentName();
			String target = edge.getNode2().getCurrentName();
			Node node1;
			Node node2;
			if (source.equals("begin")) {
				node1 = g.getNode(this.beginNodeRow);
			} else if (source.equals("end")) {
				node1 = g.getNode(this.endNodeRow);
			} else {
				node1 = this.getNodeByLabel(g, edge.getNode1().getCurrentName());
			}
			if (target.equals("begin")) {
				node2 = g.getNode(this.beginNodeRow);
			} else if (target.equals("end")) {
				node2 = g.getNode(this.endNodeRow);
			} else {
				node2 = this.getNodeByLabel(g, edge.getNode2().getCurrentName());
			}
			Edge e = g.addEdge(node1, node2);
			this.configEdge(e, edge);
		}

	}

	private Node getNodeByLabel(Graph g, String label) {
		for (int i = 0; i < g.getNodeCount(); i++) {
			if (g.getNode(i).getString(GraphConstants.LABEL_FIELD).equals(label)) {
				return g.getNode(i);
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

	private void configEdge(Edge e, EdgeObject edgeObject) {
		if (edgeObject.getIsIndirected()) {
			e.setBoolean(GraphConstants.IS_INDIRECTED_EDGE_FIELD, true);
		} else {
			e.setBoolean(GraphConstants.IS_INDIRECTED_EDGE_FIELD, false);
		}
		e.setString(GraphConstants.LABEL_FIELD, "(" + edgeObject.getNode1() + " ," + edgeObject.getNode2() + ")");
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

	}

	private Table initNodeTable() {
		Table nodeData = new Table(0, 1);
		nodeData.addColumn(GraphConstants.LABEL_FIELD, String.class);
		nodeData.addColumn(GraphConstants.BEGIN_FIELD, boolean.class);
		nodeData.addColumn(GraphConstants.END_FIELD, boolean.class);
		nodeData.addColumn(GraphConstants.SELECT_FIELD, boolean.class);
		nodeData.addColumn(GraphConstants.FREQUENCY_FILL_COLOR_NODE_FIELD, int.class);
		nodeData.addColumn(GraphConstants.NODE_TYPE_FIELD, NodeType.class);
		return nodeData;
	}

	private Table initEdgeTable() {
		Table edgeData = new Table(0, 1);
		edgeData.addColumn(Graph.DEFAULT_SOURCE_KEY, int.class);
		edgeData.addColumn(Graph.DEFAULT_TARGET_KEY, int.class);
		edgeData.addColumn(GraphConstants.LABEL_FIELD, String.class);
		edgeData.addColumn(GraphConstants.IS_INDIRECTED_EDGE_FIELD, boolean.class);
		return edgeData;
	}

	public HashMap<String, Color> getNodeStrokeColorFromMapActCat(MapActivityCategoryObject mapActCategory,
			CategoryObject selectedCategory) {
		HashMap<String, Color> res = new HashMap<>();

		Table nodeTable = this.getGraph().getNodeTable();
		for (int i = 0; i < nodeTable.getRowCount(); i++) {
			VisualItem node = this.getVisualization().getVisualItem(GraphConstants.NODE_GROUP, nodeTable.getTuple(i));
			if (!nodeTable.get(i, GraphConstants.LABEL_FIELD).equals("")) {
				String actName = nodeTable.getString(i, GraphConstants.LABEL_FIELD);
				Boolean isDefault = true;
				// Check if the act is assigned to any category
				for (AttributeClassifier att : mapActCategory.getMapActivityCategory().keySet()) {
					if (att.toString().equals(actName)) {
						// Check if the assignment is in the current selected mode view
						for (ValueCategoryObject vCO : mapActCategory.getMapActivityCategory().get(att)) {
							if (vCO.getCategory().equals(selectedCategory.getName())) {
								// Change the border color of the act
								Color color = vCO.getValueColor();
								res.put(node.getString(GraphConstants.LABEL_FIELD), color);
								isDefault = false;
								break;
							}
						}
					}
				}
				if (isDefault) {
					res.put(node.getString(GraphConstants.LABEL_FIELD), Color.WHITE);
				}
			} else {
				res.put(node.getString(GraphConstants.LABEL_FIELD), new Color(10, 10, 50));
			}
		}
		return res;
	}

	public void repaintNodeStrokeColor(final HashMap<String, Color> map) {
		this.getVisualization().removeAction(GraphConstants.NODE_STROKE_COLOR_ACTION);
		ColorAction conditionalColorAction = new ColorAction(GraphConstants.NODE_GROUP, VisualItem.STROKECOLOR) {
			public int getColor(VisualItem item) {
				if (item.getBoolean(GraphConstants.BEGIN_FIELD) || item.getBoolean(GraphConstants.END_FIELD)) {
					return ColorLib.color(Color.WHITE);
				} else {
					int color = GraphConstants.NODE_STROKE_COLOR;
					for (String label : map.keySet()) {
						if (item.getString(GraphConstants.LABEL_FIELD).equals(label)) {
							color = ColorLib.color(map.get(label));
							break;
						}
					}
					return color;
				}

			}
		};
		this.setNodeStrokeColorAction(conditionalColorAction);
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

	public SelectMultipleNodesControl getSelectMultipleNodesControl() {
		return selectMultipleNodesControl;
	}

	public BackgroundDoubleClickControl getBackgroundDoubleClickControl() {
		return backgroundDoubleClickControl;
	}

	public CustomPanControl getCustomPanControl() {
		return customPanControl;
	}

	public FocusControl getFocusControl() {
		return focusControl;
	}

	public DragMultipleNodesControl getDragMultipleNodesControl() {
		return dragMultipleNodesControl;
	}

	public BorderNodeControl getBorderNodeControl() {
		return borderNodeControl;
	}

	public ColorAction getNodeStrokeColorAction() {
		return nodeStrokeColorAction;
	}

	public void setNodeStrokeColorAction(ColorAction nodeStrokeColorAction) {
		this.nodeStrokeColorAction = nodeStrokeColorAction;
	}

	public ColorAction getNodeFillColorAction() {
		return nodeFillColorAction;
	}

	public void setNodeFillColorAction(ColorAction nodeFillColorAction) {
		this.nodeFillColorAction = nodeFillColorAction;
	}

	public ColorAction getEdgeStrokeColorAction() {
		return edgeStrokeColorAction;
	}

	public void setEdgeStrokeColorAction(ColorAction edgeStrokeColorAction) {
		this.edgeStrokeColorAction = edgeStrokeColorAction;
	}

	public StrokeAction getEdgeStrokeWidthAction() {
		return edgeStrokeWidthAction;
	}

	public void setEdgeStrokeWidthAction(StrokeAction edgeStrokeWidthAction) {
		this.edgeStrokeWidthAction = edgeStrokeWidthAction;
	}

	public ColorAction getTextColorAction() {
		return textColorAction;
	}

	public void setTextColorAction(ColorAction textColorAction) {
		this.textColorAction = textColorAction;
	}

	public GraphObjectClickControl getEdgeClickControl() {
		return edgeClickControl;
	}

	public void setEdgeClickControl(GraphObjectClickControl edgeClickControl) {
		this.edgeClickControl = edgeClickControl;
	}

}
