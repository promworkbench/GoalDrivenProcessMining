package graph.action;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.util.HashMap;

import org.processmining.goaldrivenprocessmining.objectHelper.EdgeObject;

import graph.GraphConstants;
import prefuse.Constants;
import prefuse.render.EdgeRenderer;
import prefuse.util.GraphicsLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;

public class CustomizedEdgeRenderer extends EdgeRenderer {
	private HashMap<EdgeObject, String> mapEdgeLabel;
	private HashMap<EdgeObject, String> customizedFrequencyEdge;

	public CustomizedEdgeRenderer(int edgeTypeCurve, int edgeArrowForward) {
		super(edgeTypeCurve, edgeArrowForward);
		this.customizedFrequencyEdge = new HashMap<>();
		this.mapEdgeLabel = new HashMap<>();
	}

	protected Polygon m_arrowDoubleHead = updateArrowDoubleHead(m_arrowWidth, m_arrowHeight);

	public void render(Graphics2D g, VisualItem item) {
		if (item.getBoolean(GraphConstants.IS_DISPLAY)) {
			// render the edge line
			super.render(g, item);
			this.drawLabelOfEdge(g, item);
		}
		item.getVisualization().repaint();
	}

	@Override
	public Shape getShape(VisualItem item) {
		if (item instanceof EdgeItem && item.isValid()) {
			EdgeItem edge = (EdgeItem) item;
			VisualItem item1 = edge.getSourceItem();
			VisualItem item2 = edge.getTargetItem();
			CubicCurve2D curve = (CubicCurve2D) getRawShape(item);
			if (item1 == item2) {
				return curve;
			} else {
				double x1 = curve.getX1();
				double y1 = curve.getY1();
				double ctrlX1 = curve.getCtrlX1();
				double ctrlY1 = curve.getCtrlY1();
				double ctrlX2 = curve.getCtrlX2();
				double ctrlY2 = curve.getCtrlY2();
				double x2 = curve.getX2();
				double y2 = curve.getY2();

				// Create a new shape with the adjusted control points
				CubicCurve2D adjustedCurve = new CubicCurve2D.Double(x1, y1, ctrlX1, ctrlY1, ctrlX2, ctrlY2, x2, y2);

				// Create a composite shape with the original shape and the boundary shape
				Area compositeShape = new Area();
				BasicStroke boundaryStroke = new BasicStroke(2f);
				Shape boundaryShape = boundaryStroke.createStrokedShape(adjustedCurve);

				compositeShape.add(new Area(adjustedCurve));
				//				compositeShape.add(new Area(adjustedCurve.getBounds()));

				//				return compositeShape;
				return boundaryShape;
			}

		}

		return super.getShape(item);

	}

	private void drawLabelOfEdge(Graphics2D g, VisualItem item) {
		if (item instanceof EdgeItem && item.isValid()) {
			EdgeItem edge = (EdgeItem) item;
			CubicCurve2D curve = (CubicCurve2D) getRawShape(item);
			Point2D midPoint = this.getMiddlePoint(curve);
			VisualItem item1 = edge.getSourceItem();
			VisualItem item2 = edge.getTargetItem();

			String source = item1.getString(GraphConstants.LABEL_FIELD);
			String target = item2.getString(GraphConstants.LABEL_FIELD);

			source = source.equals("**BEGIN**") ? "begin" : source;
			target = target.equals("**END**") ? "end" : target;
			String label = "";
			for (EdgeObject edgeObject : this.mapEdgeLabel.keySet()) {
				if (edgeObject.getNode1().equals(source) && edgeObject.getNode2().equals(target)) {
					if (this.customizedFrequencyEdge.containsKey(edgeObject)) {
						label = this.customizedFrequencyEdge.get(edgeObject);
					} else {
						label = this.mapEdgeLabel.get(edgeObject);
					}
					break;
				}
			}
			g.setFont(item.getFont());

			double x = midPoint.getX() - 20;
			double y = midPoint.getY() - 30;
			while (curve.contains(new Point2D.Double(x, y))) {
				x += 10;
				y -= 20;
			}

			g.drawString(label, (int) x, (int) y);
			item.getVisualization().repaint();
		}
	}

	private static Point2D getMiddlePoint(CubicCurve2D curve) {
		double t = 0.5; // Midpoint

		// Evaluate the cubic curve at t = 0.5
		double x = Math.pow(1 - t, 3) * curve.getX1() + 3 * Math.pow(1 - t, 2) * t * curve.getCtrlX1()
				+ 3 * (1 - t) * Math.pow(t, 2) * curve.getCtrlX2() + Math.pow(t, 3) * curve.getX2();

		double y = Math.pow(1 - t, 3) * curve.getY1() + 3 * Math.pow(1 - t, 2) * t * curve.getCtrlY1()
				+ 3 * (1 - t) * Math.pow(t, 2) * curve.getCtrlY2() + Math.pow(t, 3) * curve.getY2();

		return new Point2D.Double(x, y);
	}

	protected Shape getRawShape(VisualItem item) {
		EdgeItem edge = (EdgeItem) item;
		VisualItem item1 = edge.getSourceItem();
		VisualItem item2 = edge.getTargetItem();

		int type = m_edgeType;

		getAlignedPoint(m_tmpPoints[0], item1.getBounds(), m_xAlign1, m_yAlign1);
		getAlignedPoint(m_tmpPoints[1], item2.getBounds(), m_xAlign2, m_yAlign2);
		m_curWidth = (float) (m_width * getLineWidth(item));

		// create the arrow head, if needed
		EdgeItem e = (EdgeItem) item;
		if (e.isDirected() && m_edgeArrow != Constants.EDGE_ARROW_NONE) {
			// get starting and ending edge endpoints
			boolean forward = (m_edgeArrow == Constants.EDGE_ARROW_FORWARD);
			Point2D start = null, end = null;
			if (item1 == item2) {
				start = m_tmpPoints[forward ? 0 : 1];
				start.setLocation(start.getX() + item1.getBounds().getWidth() / 2, start.getY());
				end = m_tmpPoints[forward ? 1 : 0];
				end.setLocation(end.getX() + item1.getBounds().getWidth() / 2, end.getY());
			} else {
				start = m_tmpPoints[forward ? 0 : 1];
				end = m_tmpPoints[forward ? 1 : 0];
			}

			// compute the intersection with the target bounding box
			VisualItem dest = forward ? e.getTargetItem() : e.getSourceItem();
			int i = GraphicsLib.intersectLineRectangle(start, end, dest.getBounds(), m_isctPoints);
			if (i > 0)
				end = m_isctPoints[0];

			// create the arrow head shape
			AffineTransform at;
			if (item1 == item2) {
				Point2D fakeStart = new Point2D.Double(end.getX() + 500, end.getY() + 500);
				Point2D fakeEnd = new Point2D.Double(end.getX(), end.getY() + 35);
				at = getArrowTrans(fakeStart, fakeEnd, m_curWidth);
			} else {
				at = getArrowTrans(start, end, m_curWidth);
			}
			if (item.getBoolean(GraphConstants.IS_INDIRECTED_EDGE_FIELD)) {
				m_curArrow = at.createTransformedShape(m_arrowDoubleHead);
			} else {
				m_curArrow = at.createTransformedShape(m_arrowHead);
			}

			// update the endpoints for the edge shape
			// need to bias this by arrow head size
			Point2D lineEnd = m_tmpPoints[forward ? 1 : 0];
			lineEnd.setLocation(0, -m_arrowHeight);
			at.transform(lineEnd, lineEnd);
		} else {
			m_curArrow = null;
		}

		// create the edge shape
		Shape shape = null;
		double n1x = m_tmpPoints[0].getX();
		double n1y = m_tmpPoints[0].getY();
		double n2x = m_tmpPoints[1].getX();
		double n2y = m_tmpPoints[1].getY();
		switch (type) {
			case Constants.EDGE_TYPE_LINE :
				m_line.setLine(n1x, n1y, n2x, n2y);
				shape = m_line;
				break;
			case Constants.EDGE_TYPE_CURVE :
				getCurveControlPoints(edge, m_ctrlPoints, n1x, n1y, n2x, n2y);
				if (item1 == item2) {
					double radius = 80.0; // Radius of the circular path
					// Create the cubic curve for the circular path
					m_cubic.setCurve(n1x, n1y - item1.getBounds().getHeight() / 2 + 5, n1x + radius + 200,
							n1y - radius * 2 + 20, n1x + radius + 205, n1y + radius * 2 - 10, n1x + 10,
							n1y + item1.getBounds().getHeight() / 2 + 5);

				} else {
					m_cubic.setCurve(n1x, n1y, m_ctrlPoints[0].getX(), m_ctrlPoints[0].getY(), m_ctrlPoints[1].getX(),
							m_ctrlPoints[1].getY(), n2x, n2y);
				}

				shape = m_cubic;
				break;
			default :
				throw new IllegalStateException("Unknown edge type");
		}

		// return the edge shape
		return shape;
	}

	protected Polygon updateArrowDoubleHead(int w, int h) {
		if (m_arrowDoubleHead == null) {
			m_arrowDoubleHead = new Polygon();
		} else {
			m_arrowDoubleHead.reset();
		}
		m_arrowDoubleHead.addPoint(0, 0);
		m_arrowDoubleHead.addPoint(-w / 2, -h * 5 / 4);
		m_arrowDoubleHead.addPoint(0, -h);
		m_arrowDoubleHead.addPoint(-w / 2, -h * 5 / 2);
		m_arrowDoubleHead.addPoint(0, -h * 2);
		m_arrowDoubleHead.addPoint(w / 2, -h * 5 / 2);
		m_arrowDoubleHead.addPoint(0, -h);
		m_arrowDoubleHead.addPoint(w / 2, -h * 5 / 4);
		m_arrowDoubleHead.addPoint(0, 0);
		return m_arrowDoubleHead;
	}

	protected Polygon updateArrowHead(int w, int h) {
		if (m_arrowHead == null) {
			m_arrowHead = new Polygon();
		} else {
			m_arrowHead.reset();
		}
		m_arrowHead.addPoint(0, 0);
		m_arrowHead.addPoint(-w / 2, -h * 5 / 4);
		m_arrowHead.addPoint(0, -h);
		m_arrowHead.addPoint(w / 2, -h * 5 / 4);
		m_arrowHead.addPoint(0, 0);
		return m_arrowHead;
	}

	public void setArrowHeadSize(int width, int height) {
		m_arrowWidth = width;
		m_arrowHeight = height;
		m_arrowHead = updateArrowHead(width, height);
	}

	public void setArrowDoubleHeadSize(int width, int height) {
		m_arrowWidth = width;
		m_arrowHeight = height;
		m_arrowDoubleHead = updateArrowDoubleHead(width, height);
	}

	public HashMap<EdgeObject, String> getCustomizedFrequencyEdge() {
		return customizedFrequencyEdge;
	}

	public void setCustomizedFrequencyEdge(HashMap<EdgeObject, String> customizedFrequencyEdge) {
		this.customizedFrequencyEdge = customizedFrequencyEdge;
	}

	public HashMap<EdgeObject, String> getMapEdgeLabel() {
		return mapEdgeLabel;
	}

	public void setMapEdgeLabel(HashMap<EdgeObject, String> mapEdgeLabel) {
		this.mapEdgeLabel = mapEdgeLabel;
	}

}
