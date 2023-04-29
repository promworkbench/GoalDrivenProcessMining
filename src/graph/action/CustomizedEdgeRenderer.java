package graph.action;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import graph.GraphConstants;
import prefuse.Constants;
import prefuse.render.EdgeRenderer;
import prefuse.util.GraphicsLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;

public class CustomizedEdgeRenderer extends EdgeRenderer {
	public CustomizedEdgeRenderer(int edgeTypeCurve, int edgeArrowForward) {
		super(edgeTypeCurve, edgeArrowForward);
	}
	protected Polygon m_arrowDoubleHead   = updateArrowDoubleHead(
            m_arrowWidth, m_arrowHeight);

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
			start = m_tmpPoints[forward ? 0 : 1];
			end = m_tmpPoints[forward ? 1 : 0];

			// compute the intersection with the target bounding box
			VisualItem dest = forward ? e.getTargetItem() : e.getSourceItem();
			int i = GraphicsLib.intersectLineRectangle(start, end, dest.getBounds(), m_isctPoints);
			if (i > 0)
				end = m_isctPoints[0];

			// create the arrow head shape
			AffineTransform at = getArrowTrans(start, end, m_curWidth);
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
				m_cubic.setCurve(n1x, n1y, m_ctrlPoints[0].getX(), m_ctrlPoints[0].getY(), m_ctrlPoints[1].getX(),
						m_ctrlPoints[1].getY(), n2x, n2y);
				shape = m_cubic;
				break;
			default :
				throw new IllegalStateException("Unknown edge type");
		}

		// return the edge shape
		return shape;
	}
	protected Polygon updateArrowDoubleHead(int w, int h) {
        if ( m_arrowDoubleHead == null ) {
        	m_arrowDoubleHead = new Polygon();
        } else {
        	m_arrowDoubleHead.reset();
        }
        m_arrowDoubleHead.addPoint(0, 0);
        m_arrowDoubleHead.addPoint(-w/2, -h*5/4);
        m_arrowDoubleHead.addPoint(0, -h);
        m_arrowDoubleHead.addPoint(-w/2, -h*5/2);
        m_arrowDoubleHead.addPoint(0, -h*2);
        m_arrowDoubleHead.addPoint( w/2, -h*5/2);
        m_arrowDoubleHead.addPoint( 0, -h);
        m_arrowDoubleHead.addPoint( w/2, -h*5/4);
        m_arrowDoubleHead.addPoint(0, 0);
        return m_arrowDoubleHead;
    }
	protected Polygon updateArrowHead(int w, int h) {
        if ( m_arrowHead == null ) {
            m_arrowHead = new Polygon();
        } else {
            m_arrowHead.reset();
        }
        m_arrowHead.addPoint(0, 0);
        m_arrowHead.addPoint(-w/2, -h*5/4);
        m_arrowHead.addPoint( 0, -h);
        m_arrowHead.addPoint(w/2, -h*5/4);
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
