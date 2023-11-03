package graph.controls;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import graph.GoalDrivenDFGUtils;
import graph.GraphConstants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.controls.ControlAdapter;
import prefuse.data.Table;
import prefuse.util.display.PaintListener;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualItem;

public class SquareSelectControl extends ControlAdapter {
	private Point startPoint;
	private Point currentPoint;

	private Table nodeTable;

	private Display display;

	private boolean isFirstPressed;

	private boolean showPopup = false;

	private Rectangle2D rectangle;

	public SquareSelectControl(Table nodeTable, Display display) {
		this.nodeTable = nodeTable;
		this.display = display;
	}

	public void mousePressed(MouseEvent e) {
		if (UILib.isButtonPressed(e, MouseEvent.BUTTON1_MASK) && e.isShiftDown()) {
			isFirstPressed = true;
			showPopup = false;
			startPoint = e.getPoint();
			currentPoint = e.getPoint();
			rectangle = null;
		}
		display.repaint();
	}

	public void mouseDragged(MouseEvent e) {
		if (UILib.isButtonPressed(e, MouseEvent.BUTTON1_MASK) && e.isShiftDown()) {
			isFirstPressed = false;
			currentPoint = e.getPoint();
			rectangle = makeRectangle(startPoint, currentPoint);
			SquareSelectGraphic squareSelectGraphic = new SquareSelectGraphic();
			display.addPaintListener(squareSelectGraphic);
			display.repaint();
		}
	}

	public void mouseReleased(MouseEvent e) {
		// get all vItem in the rectangle
		Visualization vis = display.getVisualization();
		List<VisualItem> allVisualItems = GoalDrivenDFGUtils.getAllNodes(vis);
		if (rectangle != null) {
			for (VisualItem item : allVisualItems) {
				if (isInsideRectangle(item, rectangle)) {
					nodeTable.set(item.getRow(), GraphConstants.IS_SELECTED, true);
					item.setFillColor(GraphConstants.SELECTED_NODE_FILL_COLOR);
				}
			}
			showPopup = true;
		}
		rectangle = null;
		display.revalidate();
		display.repaint();
		// TODO popup
		if (showPopup) {
			showPopup = false;
		}
	}

	private Rectangle2D makeRectangle(Point startPoint, Point endPoint) {
		int minX = Math.min(startPoint.x, endPoint.x);
		int minY = Math.min(startPoint.y, endPoint.y);
		int maxX = Math.max(startPoint.x, endPoint.x);
		int maxY = Math.max(startPoint.y, endPoint.y);
		return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
	}

	private boolean isInsideRectangle(VisualItem item, Rectangle2D rectangle) {
		double scaleFactor = display.getScale();
		AffineTransform transform = display.getTransform();
		Point2D p = new Point2D.Double(item.getBounds().getX(), item.getBounds().getY());
		p = transform.transform(p, p);
		Rectangle2D scaledBound = new Rectangle2D.Double(p.getX(), p.getY(), item.getBounds().getHeight() * scaleFactor,
				item.getBounds().getWidth() * scaleFactor);
		if (rectangle.contains(scaledBound)) {
			return true;
		} else {
			return false;
		}
	}

	private class SquareSelectGraphic implements PaintListener {
		public void prePaint(Display d, Graphics2D g) {
		}

		public void postPaint(Display d, Graphics2D g) {
			if (!isFirstPressed) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setColor(Color.LIGHT_GRAY);
				if (rectangle != null) {
					g2d.draw(rectangle);
				}
			}
		}
	}

}
