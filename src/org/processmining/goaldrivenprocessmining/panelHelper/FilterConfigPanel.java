package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.fluxicon.slickerbox.components.NiceDoubleSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.factory.SlickerFactory;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class FilterConfigPanel extends JPanel{
	private final NiceDoubleSlider activitiesSlider;
	private final NiceDoubleSlider pathsSlider;
	private final JButton filterCancelButton;
	private final JButton filterDoneButton;
	public FilterConfigPanel(int width) {
		double filterConfigSize[][] = { { 0.2*width, 0.2*width, 0.6*width },
				{ TableLayoutConstants.FILL, TableLayoutConstants.MINIMUM} };
		setLayout(new TableLayout(filterConfigSize));
		//activities slider
		{
			activitiesSlider = SlickerFactory.instance().createNiceDoubleSlider("Activities", 0, 1.0, 1.0,
					Orientation.VERTICAL);
			add(activitiesSlider, "0, 0");
		}

		//paths slider
		{
			pathsSlider = SlickerFactory.instance().createNiceDoubleSlider("Paths", 0, 1.0, 1,
					Orientation.VERTICAL);
			add(pathsSlider, "1, 0");
		}
		JPanel filterEndPanel = new JPanel();
		filterEndPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		filterCancelButton = this.drawButton("Cancel");
		filterDoneButton = this.drawButton("Done");
		filterEndPanel.add(filterCancelButton);
		filterEndPanel.add(filterDoneButton);
		add(filterEndPanel, "0,1,2,1");
		
	}
	public JButton drawButton(String name) {
		return new JButton(name);
	}
	
	public JButton getFilterCancelButton() {
		return filterCancelButton;
	}
	public JButton getFilterDoneButton() {
		return filterDoneButton;
	}
	public NiceDoubleSlider getActivitiesSlider() {
		return activitiesSlider;
	}
	public NiceDoubleSlider getPathsSlider() {
		return pathsSlider;
	}
	
}
