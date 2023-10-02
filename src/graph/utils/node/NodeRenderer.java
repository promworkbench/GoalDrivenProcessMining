package graph.utils.node;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import graph.GraphConstants;
import prefuse.Constants;
import prefuse.render.LabelRenderer;
import prefuse.util.GraphicsLib;
import prefuse.visual.VisualItem;

public class NodeRenderer extends LabelRenderer {

	private int m_baseSize = 20;
	private Ellipse2D m_ellipse = new Ellipse2D.Double();
	private Rectangle2D m_rect = new Rectangle2D.Double();
	private GeneralPath m_path = new GeneralPath();
	private BasicStroke dashedStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
			new float[] { 5 }, 0);

	/** Transform used to scale and position images */

	/** The holder for the currently computed bounding box */

	public NodeRenderer(String string) {
		super(string);
	}

	public void render(Graphics2D g, VisualItem item) {
		if (item.getBoolean("begin") || item.getBoolean("end")) {
			Shape shape = getRawShapeNode(item);
			if (shape != null)
				drawShape(g, item, shape);
		} else if (item.getBoolean(GraphConstants.IS_INVISIBLE)) {
			Shape shape = getRawShape(item);
			GraphicsLib.paint(g, item, shape, dashedStroke, RENDER_TYPE_DRAW);
		} else {
			super.render(g, item);
		}
	}
	
	protected Shape getDashedRectangle(VisualItem item) {
		Rectangle2D bounds = super.getRawShape(item).getBounds2D();
        float x = (float) bounds.getX();
        float y = (float) bounds.getY();
        float width = (float) bounds.getWidth();
        float height = (float) bounds.getHeight();
        return new Rectangle2D.Float(x - 50, y - 50, width + 100, height + 100);
	}

	protected Shape getRawShape(VisualItem item) {
		if (!item.getBoolean(GraphConstants.IS_INVISIBLE)) {
			return super.getRawShape(item);
		} else {
			return this.getDashedRectangle(item);
		}
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
