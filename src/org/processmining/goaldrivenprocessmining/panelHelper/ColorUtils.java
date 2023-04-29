package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ColorUtils {
	public static List<Color> getGradientFromBaseColor(Color baseColor, int numItem) {
		List<Color> colors = new ArrayList<>();
		Color boundColor = getBoundColorFromBaseColor(baseColor);

		for (int i = 0; i < numItem; i++) {
		    // calculate the fraction between 0 and 1 for this item
		    float fraction = (i) / ((float) numItem - 1);
		    
		    // interpolate between the base color and white using this fraction
		    Color color = new Color(
		        (int) (baseColor.getRed() * (1 - fraction) + boundColor.getRed() * fraction),
		        (int) (baseColor.getGreen() * (1 - fraction) + boundColor.getGreen() * fraction),
		        (int) (baseColor.getBlue() * (1 - fraction) + boundColor.getBlue() * fraction)
		    );
		    
		    // add the interpolated color to the list
		    colors.add(color);
		}
		return colors;
	}
	
	public static Color getBoundColorFromBaseColor(Color baseColor) {
		int red = baseColor.getRed();
		int green = baseColor.getGreen();
		int blue = baseColor.getBlue();
		Color res = null;
		
		if (red >= green && red >= blue) {
			res = new Color(red, 255 - (255 - green)*10/100, 255 - (255 - blue)*10/100);
		} else if (green >= red && green >= blue) {
			res = new Color( 255 - (255 - red)*10/100, green, 255 - (255 - blue)*10/100);
		} else if (blue >= red && blue >= green) { 
			res = new Color( 255 - (255 - red)*10/100, 255 - (255 - green)*10/100 , blue);
		}
		return res;
	}
}
