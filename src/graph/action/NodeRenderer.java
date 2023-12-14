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

		if (item.getBoolean(GraphConstants.BEGIN_FIELD) || item.getBoolean(GraphConstants.END_FIELD)) {
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
		double width = m_baseSize * item.getSize() + 10;

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
			case Constants.SHAPE_STAR :
				return star((float) x, (float) y, (float) width);
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

	public Shape star(float x, float y, float height) {
		float s = (float) (height / (2 * Math.sin(Math.toRadians(54))));
		float shortSide = (float) (height / (2 * Math.tan(Math.toRadians(54))));
		float mediumSide = (float) (s * Math.sin(Math.toRadians(18)));
		float longSide = (float) (s * Math.cos(Math.toRadians(18)));
		float innerLongSide = (float) (s / (2 * Math.cos(Math.toRadians(36))));
		float innerShortSide = innerLongSide * (float) Math.sin(Math.toRadians(36));
		float innerMediumSide = innerLongSide * (float) Math.cos(Math.toRadians(36));

		m_path.reset();
		m_path.moveTo(x, y + shortSide);
		m_path.lineTo((x + innerLongSide), (y + shortSide));
		m_path.lineTo((x + height / 2), y);
		m_path.lineTo((x + height - innerLongSide), (y + shortSide));
		m_path.lineTo((x + height), (y + shortSide));
		m_path.lineTo((x + height - innerMediumSide), (y + shortSide + innerShortSide));
		m_path.lineTo((x + height - mediumSide), (y + height));
		m_path.lineTo((x + height / 2), (y + shortSide + longSide - innerShortSide));
		m_path.lineTo((x + mediumSide), (y + height));
		m_path.lineTo((x + innerMediumSide), (y + shortSide + innerShortSide));
		m_path.closePath();
		return m_path;
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
