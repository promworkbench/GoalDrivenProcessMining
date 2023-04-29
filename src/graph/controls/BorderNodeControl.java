package graph.controls;

import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import graph.GraphConstants;
import prefuse.Display;
import prefuse.controls.ControlAdapter;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

public class BorderNodeControl extends ControlAdapter{
	
	final InGroupPredicate nodeFilter = new InGroupPredicate(GraphConstants.NODE_GROUP); 
	
	public void itemClicked(VisualItem item, MouseEvent e) {
		if (nodeFilter.getBoolean(item)) {
			NodeItem i = (NodeItem) item;
			Rectangle2D border = item.getBounds();
			Point2D p = new Point2D.Double();
			((Display) e.getComponent()).getAbsoluteCoordinate(e.getPoint(), p);
			if (isOnBorder(i, p.getX(), p.getY())) {
				System.out.println("sdfa");
			}
		}
		
    } 
	
	public boolean isOnBorder(Rectangle2D rect, Point2D point) {

	    // Create lines for each side of the rectangle
	    Line2D top = new Line2D.Double(rect.getMinX(), rect.getMinY(), rect.getMaxX(), rect.getMinY());
	    Line2D left = new Line2D.Double(rect.getMinX(), rect.getMinY(), rect.getMinX(), rect.getMaxY());
	    Line2D bottom = new Line2D.Double(rect.getMinX(), rect.getMaxY(), rect.getMaxX(), rect.getMaxY());
	    Line2D right = new Line2D.Double(rect.getMaxX(), rect.getMinY(), rect.getMaxX(), rect.getMaxY());

	    if (top.contains(point) || left.contains(point) || bottom.contains(point) || right.contains(point)) {
	    	return true;
	    }

	    return false;
	}
	
	public boolean isOnBorder(NodeItem node, double x, double y) {
	    // Get the position of the node
	    double nodeX = node.getX();
	    double nodeY = node.getY();
	    
	    // Get the dimensions of the node
	    double width = node.getBounds().getWidth();
	    double height = node.getBounds().getHeight();
	    
	    // Calculate the bounds of the border
	    double borderX = nodeX - width/2;
	    double borderY = nodeY - height/2;
	    double borderWidth = width;
	    double borderHeight = height;
	    
	    // Reduce the size of the border by the stroke width
	    double strokeWidth = node.getStroke().getLineWidth();
	    borderX += strokeWidth;
	    borderY += strokeWidth;
	    borderWidth -= 2*strokeWidth;
	    borderHeight -= 2*strokeWidth;
	    
	    // Check if the point is on the border but not inside the body
	    return x >= borderX && x <= borderX + borderWidth && y >= borderY && y <= borderY + borderHeight && !(x >= nodeX - width/2 && x <= nodeX + width/2 && y >= nodeY - height/2 && y <= nodeY + height/2);
	}
}
