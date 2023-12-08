package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class RangeSliderPanel extends JPanel {

	private JLabel rangeSliderLabel1 = new JLabel();
	private JLabel rangeSliderValue1 = new JLabel();
	private JLabel rangeSliderLabel2 = new JLabel();
	private JLabel rangeSliderValue2 = new JLabel();
	private RangeSlider rangeSlider;

	public RangeSliderPanel(double width) {
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		setLayout(new GridBagLayout());

		rangeSliderLabel1.setText("Lower percentage:");
		rangeSliderLabel2.setText("Upper percentage:");
		rangeSliderValue1.setHorizontalAlignment(JLabel.LEFT);
		rangeSliderValue2.setHorizontalAlignment(JLabel.LEFT);
		rangeSlider = new RangeSlider();
		rangeSlider.setMinimum(0);
		rangeSlider.setMaximum(100);
		rangeSlider.setValue(0);
		rangeSlider.setUpperValue(100);
		rangeSlider.setPreferredSize(new Dimension((int) width, rangeSlider.getPreferredSize().height));
		rangeSliderValue1.setText(String.valueOf(rangeSlider.getValue()));
		rangeSliderValue2.setText(String.valueOf(rangeSlider.getUpperValue() / 100f));
		rangeSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				RangeSlider slider = (RangeSlider) e.getSource();
				rangeSliderValue1.setText(String.valueOf(slider.getValue() / 100f));
				rangeSliderValue2.setText(String.valueOf(slider.getUpperValue() / 100f));

			}
		});
		add(rangeSliderLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(0, 0, 3, 3), 0, 0));
		add(rangeSliderValue1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(0, 0, 3, 0), 0, 0));
		add(rangeSliderLabel2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(0, 0, 3, 3), 0, 0));
		add(rangeSliderValue2, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(0, 0, 6, 0), 0, 0));
		add(rangeSlider, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

	}

	public RangeSlider getRangeSlider() {
		return rangeSlider;
	}

	public void setRangeSlider(RangeSlider rangeSlider) {
		this.rangeSlider = rangeSlider;
	}

	public JLabel getRangeSliderLabel1() {
		return rangeSliderLabel1;
	}

	public void setRangeSliderLabel1(JLabel rangeSliderLabel1) {
		this.rangeSliderLabel1 = rangeSliderLabel1;
	}

	public JLabel getRangeSliderLabel2() {
		return rangeSliderLabel2;
	}

	public void setRangeSliderLabel2(JLabel rangeSliderLabel2) {
		this.rangeSliderLabel2 = rangeSliderLabel2;
	}
	

}
