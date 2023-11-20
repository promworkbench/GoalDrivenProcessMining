package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.fluxicon.slickerbox.components.NiceDoubleSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.factory.SlickerFactory;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class FilterConfigPanel extends JPanel {
	private JLabel rangeSliderLabel1 = new JLabel();
	private JLabel rangeSliderValue1 = new JLabel();
	private JLabel rangeSliderLabel2 = new JLabel();
	private JLabel rangeSliderValue2 = new JLabel();
	private final RangeSlider activitiesSlider;
	private final NiceDoubleSlider pathsSlider;
	private final JButton filterCancelButton;
	private final JButton filterDoneButton;

	public FilterConfigPanel(int width) {
		double filterConfigSize[][] = { { 0.5 * width, 0.5 * width},
				{ TableLayoutConstants.FILL, TableLayoutConstants.MINIMUM } };
		setLayout(new TableLayout(filterConfigSize));
		//activities slider
		{
			JPanel activitySliderPanel = new JPanel();
			activitySliderPanel.setLayout(new GridBagLayout());

			rangeSliderLabel1.setText("Lower value:" );
			rangeSliderLabel2.setText("Upper value:");
			rangeSliderValue1.setHorizontalAlignment(JLabel.LEFT);
			rangeSliderValue2.setHorizontalAlignment(JLabel.LEFT);
			activitiesSlider = new RangeSlider();
			activitiesSlider.setMinimum(0);
			activitiesSlider.setMaximum(100);
			activitiesSlider.setValue(0);
			activitiesSlider.setUpperValue(100);
			rangeSliderValue1.setText(String.valueOf(activitiesSlider.getValue()));
			rangeSliderValue2.setText(String.valueOf(activitiesSlider.getUpperValue()/100f));
			activitiesSlider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					RangeSlider slider = (RangeSlider) e.getSource();
					rangeSliderValue1.setText(String.valueOf(slider.getValue() / 100f));
					rangeSliderValue2.setText(String.valueOf(slider.getUpperValue() / 100f));
				}
			});
			activitySliderPanel.add(rangeSliderLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 3, 3), 0, 0));
			activitySliderPanel.add(rangeSliderValue1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 3, 0), 0, 0));
			activitySliderPanel.add(rangeSliderLabel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 3, 3), 0, 0));
			activitySliderPanel.add(rangeSliderValue2, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
					GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 6, 0), 0, 0));
			activitySliderPanel.add(activitiesSlider, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
					GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			add(activitySliderPanel, "0, 0");
		}

		//paths slider
		{
			pathsSlider = SlickerFactory.instance().createNiceDoubleSlider("Paths", 0, 1.0, 1, Orientation.VERTICAL);
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

	public NiceDoubleSlider getPathsSlider() {
		return pathsSlider;
	}

	public JSlider getActivitiesSlider() {
		return activitiesSlider;
	}

}
