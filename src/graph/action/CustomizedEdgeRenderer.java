package graph.action;

import java.awt.BasicStroke;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

import graph.GraphConstants;
import prefuse.Constants;
import prefuse.render.EdgeRenderer;
import prefuse.util.GraphicsLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;

public class CustomizedEdgeRenderer extends EdgeRenderer {
	private float clickBoxSize = 0;

	public CustomizedEdgeRenderer(int edgeTypeCurve, int edgeArrowForward) {
		super(edgeTypeCurve, edgeArrowForward);
	}

	protected Polygon m_arrowDoubleHead = updateArrowDoubleHead(m_arrowWidth, m_arrowHeight);

	@Override
	public Shape getShape(VisualItem item) {
		if (item instanceof EdgeItem && item.isValid()) {
			EdgeItem edge = (EdgeItem) item;
			VisualItem item1 = edge.getSourceItem();
			VisualItem item2 = edge.getTargetItem();
			if(!item1.getString(GraphConstants.LABEL_FIELD).equals("a")) {
			
			}

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

				// Compute the midpoint of the curve
				double midX = curve.getCtrlX1() + (curve.getCtrlX2() - curve.getCtrlX1()) / 2.0;
				double midY = curve.getCtrlY1() + (curve.getCtrlY2() - curve.getCtrlY1()) / 2.0;

				// Compute the distance between the midpoint and the control points
				double dx1 = midX - ctrlX1;
				double dy1 = midY - ctrlY1;
				double dx2 = midX - ctrlX2;
				double dy2 = midY - ctrlY2;

				// Compute the distance between the midpoint and the edge points
				double dx3 = midX - x1;
				double dy3 = midY - y1;
				double dx4 = midX - x2;
				double dy4 = midY - y2;

				// Compute the maximum distance to cover the entire curve
				double maxDistance = Math.max(Math.max(Math.hypot(dx1, dy1), Math.hypot(dx2, dy2)),
						Math.max(Math.hypot(dx3, dy3), Math.hypot(dx4, dy4)));

				// Adjust the clickbox size to be a fraction of the maximum distance
				double clickBoxDistance = clickBoxSize / 2.0;
				double scaleFactor = clickBoxDistance / maxDistance;

				// Compute the new control points based on the adjusted size
				double newCtrlX1 = midX + dx1 * scaleFactor;
				double newCtrlY1 = midY + dy1 * scaleFactor;
				double newCtrlX2 = midX + dx2 * scaleFactor;
				double newCtrlY2 = midY + dy2 * scaleFactor;
				// Create a new shape with the adjusted control points
				CubicCurve2D adjustedCurve = new CubicCurve2D.Double(x1, y1, newCtrlX1, newCtrlY1, newCtrlX2, newCtrlY2,
						x2, y2);

				// Create a composite shape with the original shape and the boundary shape
				Area compositeShape = new Area();
				BasicStroke boundaryStroke = new BasicStroke(1f);
//				Shape boundaryShape = boundaryStroke.createStrokedShape(adjustedCurve);
				Shape boundaryShape = boundaryStroke.createStrokedShape(adjustedCurve);
				
				compositeShape.add(new Area(boundaryShape));

				return new Area(boundaryShape);
			}

		}

		return super.getShape(item);

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
				end.setLocation(end.getX() + item1.getBounds().getWidth() / 2 , end.getY());
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
				Point2D fakeStart = new Point2D.Double(end.getX() + 450, end.getY() + 500);
				Point2D fakeEnd = new Point2D.Double(end.getX(), end.getY() +20);
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
					m_cubic.setCurve(n1x, n1y - item1.getBounds().getHeight() / 2, n1x + radius, n1y - radius * 2 + 10,
							n1x + radius + 5, n1y + radius * 2, n1x + 10, n1y + item1.getBounds().getHeight() / 2 + 5);

					//					m_cubic.setCurve(startX, startY, controlX1, controlY1, controlX2, controlY2, endX, endY);
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

}
