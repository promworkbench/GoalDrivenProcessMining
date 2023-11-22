package graph.action;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import graph.GoalDrivenDFG;
import graph.GraphConstants;
import prefuse.Constants;
import prefuse.render.LabelRenderer;
import prefuse.visual.VisualItem;

public class NodeRenderer extends LabelRenderer {

	private int m_baseSize = 20;
	private Ellipse2D m_ellipse = new Ellipse2D.Double();
	private Rectangle2D m_rect = new Rectangle2D.Double();
	private GeneralPath m_path = new GeneralPath();

	/** Transform used to scale and position images */

	/** The holder for the currently computed bounding box */

	public NodeRenderer(GoalDrivenDFG goalDrivenDFG, String string) {
		super(string);
	}

	public void render(Graphics2D g, VisualItem item) {

		if (item.getBoolean("begin") || item.getBoolean("end")) {
			Shape shape = getRawShapeNode(item);
			if (shape != null)
				drawShape(g, item, shape);
		} else if (item.getBoolean(GraphConstants.IS_DISPLAY)) {
			super.render(g, item);
		}
	}

	protected Shape getRawShape(VisualItem item) {
		return super.getRawShape(item);
	}

	protected Shape getRawShapeNode(VisualItem item) {

		int stype = item.getShape();
		double x = item.getX();
		if (Double.isNaN(x) || Double.isInfinite(x))
			x = 0;
		double y = item.getY();
		if (Double.isNaN(y) || Double.isInfinite(y))
			y = 0;
		double width = m_baseSize * item.getSize();

		// Center the shape around the specified x and y
		if (width > 1) {
			x = x - width / 2;
			y = y - width / 2;
		}

		switch (stype) {
			case Constants.SHAPE_NONE :
				return null;
			case Constants.SHAPE_RECTANGLE :
				return rectangle(x, y, width, width);
			case Constants.SHAPE_ELLIPSE :
				return ellipse(x, y, width, width);
			case Constants.SHAPE_TRIANGLE_RIGHT :
				return triangle_right((float) x, (float) y, (float) width);
			default :
				throw new IllegalStateException("Unknown shape type: " + stype);
		}
	}

	public Shape rectangle(double x, double y, double width, double height) {
		m_rect.setFrame(x, y, width, height);
		return m_rect;
	}

	public Shape ellipse(double x, double y, double width, double height) {
		m_ellipse.setFrame(x, y, width, height);
		return m_ellipse;
	}

	public Shape triangle_right(float x, float y, float height) {
		m_path.reset();
		m_path.moveTo(x, y + height);
		m_path.lineTo(x + height, y + height / 2);
		m_path.lineTo(x, y);
		m_path.closePath();
		return m_path;
	}

}
