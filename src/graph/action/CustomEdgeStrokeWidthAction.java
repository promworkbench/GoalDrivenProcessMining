package graph.action;

import java.awt.BasicStroke;
import java.util.HashMap;

import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;

import graph.GraphConstants;
import prefuse.action.assignment.StrokeAction;
import prefuse.data.Edge;
import prefuse.visual.VisualItem;

public class CustomEdgeStrokeWidthAction extends StrokeAction {
	private HashMap<EdgeObject, Float> mapEdgeStrokeWidth;

	public CustomEdgeStrokeWidthAction(String group, HashMap<EdgeObject, Float> mapEdgeStrokeWidth) {
		super(group);
		this.mapEdgeStrokeWidth = mapEdgeStrokeWidth;
	}

	public BasicStroke getStroke(VisualItem item) {
		if (item instanceof Edge) {
			Edge edge = (Edge) item;
			// Get the source and target nodes of the edge
			String source;
			String target;
			if (edge.getSourceNode().getRow() == 0) {
				source = "begin";
			} else if (edge.getSourceNode().getRow() == 1) {
				source = "end";
			} else {
				source = edge.getSourceNode().getString(GraphConstants.LABEL_FIELD);
			}
			if (edge.getTargetNode().getRow() == 0) {
				target = "begin";
			} else if (edge.getTargetNode().getRow() == 1) {
				target = "end";
			} else {
				target = edge.getTargetNode().getString(GraphConstants.LABEL_FIELD);
			}
			return new BasicStroke(this.mapEdgeStrokeWidth.get(new EdgeObject(source, target)));
		} else {
			return super.getStroke(item);
		}

	}
//	public static void main(String[] args) throws Exception {
//		File file = new File("C:\\D\\data\\receipt.xes");
//
//		// Create an input stream for the XES file
//		InputStream is = new FileInputStream(file);
//
//		// Create a parser for XES files
//		XesXmlParser parser = new XesXmlParser();
//
//		XLog log = parser.parse(is).get(0);
//		
//		for (int i = 0; i < log.size(); i++) {
//			XTrace trace = log.get(i);
//			XEvent event = trace.get(trace.size()-1);
//			if (event.getAttributes().get("concept:name").toString().equals("Confirmation of receipt")) {
//			}
//		}
//
//
//
//	}
}
