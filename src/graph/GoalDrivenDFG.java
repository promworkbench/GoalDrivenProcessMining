package graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.goaldrivenprocessmining.objectHelper.CategoryObject;
import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyEdgeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.FrequencyNodeObject;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLog;
import org.processmining.goaldrivenprocessmining.objectHelper.IndirectedEdgeCarrierObject;
import org.processmining.goaldrivenprocessmining.objectHelper.MapActivityCategoryObject;
import org.processmining.goaldrivenprocessmining.objectHelper.ValueCategoryObject;
import org.processmining.goaldrivenprocessmining.objectHelper.enumaration.NodeType;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;
import org.processmining.plugins.inductiveVisualMiner.chain.DataChain;

import graph.action.CustomizedEdgeRenderer;
import graph.action.SetNodeSizeAction;
import graph.controls.BackgroundDoubleClickControl;
import graph.controls.BorderNodeControl;
import graph.controls.CustomPanControl;
import graph.controls.DragMultipleNodesControl;
import graph.controls.EdgeClickControl;
import graph.controls.SelectMultipleNodesControl;
import graph.controls.SquareSelectControl;
import graph.utils.edge.GraphEdgeUtils;
import graph.utils.node.GraphNodeUtils;
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
	private GDPMLog log;
	private String eventClassifier;
	private int beginNodeRow;
	private int endNodeRow;
	private Graph graph;
	private FrequencyEdgeObject frequencyEdge;
	private FrequencyNodeObject frequencyNode;
	// control 
	private SelectMultipleNodesControl selectMultipleNodesControl;
	private BackgroundDoubleClickControl backgroundDoubleClickControl;
	private CustomPanControl customPanControl;
	private WheelZoomControl wheelZoomControl;
	private FocusControl focusControl;
	private DragMultipleNodesControl dragMultipleNodesControl;
	private BorderNodeControl borderNodeControl;
	private EdgeClickControl edgeClickControl;
	private SquareSelectControl squareSelectControl;
	// action
	private ColorAction nodeStrokeColorAction;
	private ColorAction nodeFillColorAction;
	private ColorAction edgeStrokeColorAction;
	private StrokeAction edgeStrokeWidthAction;
	private StrokeAction nodeStrokeWidthAction;
	private ColorAction arrowFillColorAction;
	private ColorAction textColorAction;

	private DataChain<GoalDrivenConfiguration> chain;

	public GoalDrivenDFG(GDPMLog log) {
		this(log, new FrequencyEdgeObject(), new FrequencyNodeObject());
	}

	public GoalDrivenDFG(GDPMLog gdpmLog, FrequencyEdgeObject frequencyEdge, FrequencyNodeObject frequencyNode) {
		super(new Visualization());
		this.log = gdpmLog;
		this.frequencyEdge = frequencyEdge;
		this.frequencyNode = frequencyNode;
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

		if (this.log != null) {
			this.eventClassifier = log.getLog().getClassifiers().get(0).getDefiningAttributeKeys()[0].toString();
			this.graph = makeGraph(log, frequencyEdge, frequencyNode);
			m_vis.addGraph("graph", graph);
			// control
			this.setDefaultControl();
			// layout
			this.setDefaultLayout();
			// action
			this.configDefaultGraph(frequencyEdge);
		}
	}

	public static void main(String[] args) throws Exception {
		//		File file = new File("C:\\D\\data\\receipt.xes");
		//		File file2 = new File("C:\\D\\data\\my_log.xes");
		//
		//		// Create an input stream for the XES file
		//		InputStream is = new FileInputStream(file);
		//
		//		// Create a parser for XES files
		//		XesXmlParser parser = new XesXmlParser();
		//
		//		XLog log = parser.parse(is).get(0);
		//
		//		// Create an input stream for the XES file
		//		InputStream is2 = new FileInputStream(file2);
		//
		//		// Create a parser for XES files
		//		XesXmlParser parser2 = new XesXmlParser();
		//
		//		XLog log2 = parser2.parse(is2).get(0);
		//
		//		GoalDrivenDFG ex = new GoalDrivenDFG(null, new IndirectedEdgeCarrierObject(), new FrequencyEdgeObject(),
		//				new FrequencyNodeObject());
		//		GoalDrivenDFG ex2 = new GoalDrivenDFG(log, new IndirectedEdgeCarrierObject(), new FrequencyEdgeObject(),
		//				new FrequencyNodeObject());
		//		GoalDrivenDFG ex3 = new GoalDrivenDFG(log2, new IndirectedEdgeCarrierObject(), new FrequencyEdgeObject(),
		//				new FrequencyNodeObject());
		//		ex.updateDFG(ex3);
		//		ex.updateDFG(ex3);
		//		Action conditionalColorAction = new ColorAction(GraphConstants.NODE_GROUP, VisualItem.STROKECOLOR) {
		//			public int getColor(VisualItem item) {
		//				return ColorLib.color(Color.RED);
		//			}
		//		};
		//		ex.getVisualization().removeAction("nodeStrokeColor");
		//		ex.getVisualization().putAction("nodeStrokeColor", conditionalColorAction);
		//		ex.getVisualization().run("nodeStrokeColor");
		//
		//		ex.validate();
		//		ex.repaint();

		//		Graph g2 = ex2.getGraph();
		//		ex.getVisualization().removeGroup("graph");
		//		ex.getVisualization().addGraph("graph", g2);
		//		ex.setGraph(g2);
		//		ex.configGraph();
		//		ex.getVisualization().run("repaint");
		//		ex.getVisualization().repaint();
		//
		//		JFrame frame = new JFrame("prefuse example");
		//		JPanel p = new JPanel();
		//		p.setBackground(Color.BLACK);
		//		p.add(ex3);
		//		ex3.setBackground(Color.BLACK);
		//		frame.getContentPane().add(p);
		//		frame.pack(); // layout components in window
		//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//		frame.setVisible(true); // show the window
	}

	public Graph makeGraph(GDPMLog log, FrequencyEdgeObject frequencyEdge, FrequencyNodeObject frequencyNode) {

		// Create tables for node and edge data, and configure their columns.
		// init node table
		Table nodeTable = this.initNodeTable();
		// init edge table
		Table edgeTable = this.initEdgeTable();
		// init graph
		Graph g = new Graph(nodeTable, edgeTable, true);
		// add begin and end node;
		this.addBeginToTable(g);
		// add activities in log to node table and add edges
		this.addActToTable(g, log, frequencyEdge, frequencyNode);
		return g;
	}

	public void configDefaultGraph(FrequencyEdgeObject frequencyEdge) {

		this.setDefaultArrowFillColor();
		this.setDefaultEdgeStrokeColor();
		this.setDefaultEdgeStrokeWidth(frequencyEdge);
		this.setDefaultNodeStrokeWidth();
		this.setDefaultNodeFillColor(frequencyNode);
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
			dragMultipleNodesControl = new DragMultipleNodesControl();
			addControlListener(dragMultipleNodesControl);

			squareSelectControl = new SquareSelectControl(this.graph.getNodeTable(), this);
			addControlListener(squareSelectControl);
		}

		/*********************/
		/* Detect border click - not done */
		//		borderNodeControl = new BorderNodeControl();
		//		addControlListener(borderNodeControl);
		/*********************/
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
		edgeR.setArrowHeadSize(20, 10);
		edgeR.setArrowDoubleHeadSize(20, 10);

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
		NodeLinkTreeLayout treeLayout = new NodeLinkTreeLayout("graph", Constants.ORIENT_TOP_BOTTOM, 250, 80, 80);
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

	public void setDefaultEdgeStrokeWidth(FrequencyEdgeObject frequencyEdge) {
		HashMap<EdgeObject, Float> mapEdgeStrokeWidth = GraphEdgeUtils.getStrokeWidth(frequencyEdge,
				GraphEdgeUtils.getMapFreqStrokeWidth(frequencyEdge));
		this.edgeStrokeWidthAction = new StrokeAction(GraphConstants.EDGE_GROUP);
		List<Float> listStrokeWidth = new ArrayList<>();
		for (EdgeObject edge : frequencyEdge.getFrequencyEdge().keySet()) {
			float strokeWidth = mapEdgeStrokeWidth.get(edge);
			if (!listStrokeWidth.contains(strokeWidth)) {
				listStrokeWidth.add(strokeWidth);
				Predicate strokePredicate = (Predicate) ExpressionParser
						.parse(GraphConstants.STROKE_WIDTH_EDGE_FIELD + " = " + Float.toString(strokeWidth) + "f");
				this.edgeStrokeWidthAction.add(strokePredicate, new BasicStroke(strokeWidth));
			}
		}
		m_vis.putAction(GraphConstants.EDGE_STROKE_WIDTH_ACTION, this.edgeStrokeWidthAction);
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

	public void setDefaultNodeFillColor(FrequencyNodeObject frequencyNode) {
		this.nodeFillColorAction = new ColorAction(GraphConstants.NODE_GROUP, VisualItem.FILLCOLOR);
		Predicate beginPredicate = (Predicate) ExpressionParser.parse("begin = true");
		Predicate endPredicate = (Predicate) ExpressionParser.parse("end = true");
		for (int i = 0; i < GraphConstants.GRADIENT_NODE_FILL_COLOR.length; i++) {
			Predicate nodeFillPredicate = (Predicate) ExpressionParser
					.parse(GraphConstants.FREQUENCY_FILL_COLOR_NODE_FIELD + " = " + Integer.toString(i));
			this.nodeFillColorAction.add(nodeFillPredicate, ColorLib.color(GraphConstants.GRADIENT_NODE_FILL_COLOR[i]));
		}
		this.nodeFillColorAction.add(beginPredicate, GraphConstants.BEGIN_END_NODE_COLOR);
		this.nodeFillColorAction.add(endPredicate, GraphConstants.BEGIN_END_NODE_COLOR);
		m_vis.putAction(GraphConstants.NODE_FILL_COLOR_ACTION, this.nodeFillColorAction);
		m_vis.run(GraphConstants.NODE_FILL_COLOR_ACTION);
	}

	private void addBeginToTable(Graph g) {
		Node beginNode = g.addNode();
		this.beginNodeRow = beginNode.getRow();
		this.configBeginNode(beginNode);

		Node endNode = g.addNode();
		this.endNodeRow = endNode.getRow();
		this.configEndNode(endNode);
	}

	private void addActToTable(Graph g, GDPMLog log, FrequencyEdgeObject frequencyEdge,
			FrequencyNodeObject frequencyNode) {
		List<String> listActName = new ArrayList<>();
		List<EdgeObject> listEdges = new ArrayList<>();
		HashMap<EdgeObject, Float> mapEdgeStrokeWidth = GraphEdgeUtils.getStrokeWidth(frequencyEdge,
				GraphEdgeUtils.getMapFreqStrokeWidth(frequencyEdge));
		HashMap<String, Integer> mapNodeFillColor = GraphNodeUtils.getNodeFillColor(frequencyNode,
				GraphNodeUtils.getMapFreqColor(frequencyNode));
		for (XTrace trace : log.getLog()) {
			for (int i = 0; i < trace.size() - 1; i++) {
				XEvent ev1 = trace.get(i);
				XEvent ev2 = trace.get(i + 1);
				String value1 = ev1.getAttributes().get(this.eventClassifier).toString();
				String value2 = ev2.getAttributes().get(this.eventClassifier).toString();
				Node node1 = null;
				Node node2 = null;
				if (!listActName.contains(value1)) {
					listActName.add(value1);
					node1 = g.addNode();
					this.configNode(node1, value1, mapNodeFillColor, log.getMapNodeType());
				} else {
					node1 = this.getNodeByLabel(g, value1);
				}
				if (!listActName.contains(value2)) {
					listActName.add(value2);
					node2 = g.addNode();
					this.configNode(node2, value2, mapNodeFillColor, log.getMapNodeType());
				} else {
					node2 = this.getNodeByLabel(g, value2);
				}
				EdgeObject edgeObject = new EdgeObject(value1, value2);
				if (!listEdges.contains(edgeObject)) {
					Edge e = g.addEdge(node1, node2);
					listEdges.add(edgeObject);
					this.configEdge(e, edgeObject, log.getIndirectedEdges(), mapEdgeStrokeWidth);
				}
				// if begin act
				if (i == 0) {
					Node beginNode = g.getNode(this.beginNodeRow);
					edgeObject = new EdgeObject("begin", value1);
					if (!listEdges.contains(edgeObject)) {
						Edge e1 = g.addEdge(beginNode, node1);
						listEdges.add(edgeObject);
						this.configEdge(e1, edgeObject, log.getIndirectedEdges(), mapEdgeStrokeWidth);
					}

				}
				// if end act
				if (i + 1 == trace.size() - 1) {
					Node endNode = g.getNode(this.endNodeRow);
					edgeObject = new EdgeObject(value2, "end");
					if (!listEdges.contains(edgeObject)) {
						Edge e1 = g.addEdge(node2, endNode);
						listEdges.add(edgeObject);
						this.configEdge(e1, edgeObject, log.getIndirectedEdges(), mapEdgeStrokeWidth);
					}
				}

			}
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
		node.setString(GraphConstants.LABEL_FIELD, "");
		node.setBoolean(GraphConstants.BEGIN_FIELD, true);
		node.setBoolean(GraphConstants.END_FIELD, false);
		node.set(GraphConstants.NODE_TYPE_FIELD, NodeType.ACT_NODE);
	}

	private void configEndNode(Node node) {
		node.setString(GraphConstants.LABEL_FIELD, "");
		node.setBoolean(GraphConstants.BEGIN_FIELD, false);
		node.setBoolean(GraphConstants.END_FIELD, true);
		node.set(GraphConstants.NODE_TYPE_FIELD, NodeType.ACT_NODE);
	}

	private void configEdge(Edge e, EdgeObject edgeObject, IndirectedEdgeCarrierObject indirectedEdges,
			HashMap<EdgeObject, Float> mapEdgeStrokeWidth) {
		if (indirectedEdges.getListIndirectedEdge().contains(edgeObject)) {
			e.setBoolean(GraphConstants.IS_INDIRECTED_EDGE_FIELD, true);
		} else {
			e.setBoolean(GraphConstants.IS_INDIRECTED_EDGE_FIELD, false);
		}
		e.setString(GraphConstants.LABEL_FIELD, "(" + edgeObject.getNode1() + " ," + edgeObject.getNode2() + ")");
		if (!mapEdgeStrokeWidth.isEmpty()) {
			e.setFloat(GraphConstants.STROKE_WIDTH_EDGE_FIELD, mapEdgeStrokeWidth.get(edgeObject));
		} else {
			e.setFloat(GraphConstants.STROKE_WIDTH_EDGE_FIELD, 3);
		}

	}

	private void configNode(Node node, String label, HashMap<String, Integer> mapNodeFillColor,
			Map<String, NodeType> mapNodeType) {
		node.setString(GraphConstants.LABEL_FIELD, label);
		node.setBoolean(GraphConstants.BEGIN_FIELD, false);
		node.setBoolean(GraphConstants.END_FIELD, false);
		if (!mapNodeFillColor.isEmpty()) {
			node.setInt(GraphConstants.FREQUENCY_FILL_COLOR_NODE_FIELD, mapNodeFillColor.get(label));
		} else {
			node.setInt(GraphConstants.FREQUENCY_FILL_COLOR_NODE_FIELD, 0);
		}
		if (mapNodeType.containsKey(label)) {
			node.set(GraphConstants.NODE_TYPE_FIELD, mapNodeType.get(label));
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
		edgeData.addColumn(GraphConstants.STROKE_WIDTH_EDGE_FIELD, float.class);
		return edgeData;
	}

	public void updateDFG(GoalDrivenDFG dfg) {
		this.log = dfg.getLog();
		this.getVisualization().removeGroup("graph");
		this.getVisualization().addGraph("graph", dfg.getGraph());
		this.setGraph(dfg.getGraph());
		// node stroke color
		if (dfg.getNodeStrokeColorAction() != null) {
			this.setNodeStrokeColorAction(dfg.getNodeStrokeColorAction());
			m_vis.putAction(GraphConstants.NODE_STROKE_COLOR_ACTION, this.getNodeStrokeColorAction());
			m_vis.run(GraphConstants.NODE_STROKE_COLOR_ACTION);
		} else {
			this.setDefaultNodeStrokeColor();
		}
		// node fill color
		if (dfg.getNodeFillColorAction() != null) {
			this.setNodeFillColorAction(dfg.getNodeFillColorAction());
			m_vis.putAction(GraphConstants.NODE_FILL_COLOR_ACTION, this.getNodeFillColorAction());
			m_vis.run(GraphConstants.NODE_FILL_COLOR_ACTION);
		} else {
			this.setDefaultNodeFillColor(dfg.getFrequencyNode());
		}

		this.setDefaultArrowFillColor();
		this.setDefaultEdgeStrokeColor();
		this.setDefaultEdgeStrokeWidth(dfg.getFrequencyEdge());
		this.setDefaultNodeStrokeWidth();
		this.setDefaultTextColor();
		this.setDefaultNodeSize();
		this.setDefaultRenderer();
		this.resetControl();
		this.getVisualization().removeAction(GraphConstants.LAYOUT_ACTION);
		this.setDefaultLayout();
		// edge click control
		if (dfg.getEdgeClickControl() != null) {
			this.setEdgeClickControl(dfg.getEdgeClickControl());
			this.addControlListener(dfg.getEdgeClickControl());
		}
		this.revalidate();
		this.repaint();
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

	public void repaintNodeStrokeColor(HashMap<String, Color> map) {
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

	public GDPMLog getLog() {
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

	public FrequencyEdgeObject getFrequencyEdge() {
		return frequencyEdge;
	}

	public FrequencyNodeObject getFrequencyNode() {
		return frequencyNode;
	}

	public EdgeClickControl getEdgeClickControl() {
		return edgeClickControl;
	}

	public void setEdgeClickControl(EdgeClickControl edgeClickControl) {
		this.edgeClickControl = edgeClickControl;
	}

}
