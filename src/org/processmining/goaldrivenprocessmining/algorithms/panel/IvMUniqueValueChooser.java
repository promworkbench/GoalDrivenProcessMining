package org.processmining.goaldrivenprocessmining.algorithms.panel;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.MultiComboBox;

/**
 * Multi-selection combobox to select a classifier. It shows the classifiers
 * from the event log, as well as an option to construct one from data
 * attributes from the event log.
 * 
 * @author sleemans
 *
 */
public class IvMUniqueValueChooser extends JPanel {

	private static final long serialVersionUID = 3348039386637737989L;
	private final MultiComboBox<AttributeClassifier> combobox;

	/**
	 * Notice: this constructor walks through the event log to gather
	 * attributes.
	 * 
	 * @param log
	 */
	public IvMUniqueValueChooser(XLog log, String classifier) {
		setLayout(new BorderLayout());
		setOpaque(false);
		this.combobox = new MultiComboBox<>(AttributeClassifier.class, new AttributeClassifier[0]);
		add(combobox, BorderLayout.CENTER);
		
		if (log != null && !classifier.equals("")) {
			List<String> l = new ArrayList<String>();
			for (int i = 0; i < log.size(); i++) {
				XTrace tr = log.get(i);
				for (int j = 0; j < tr.size(); j++) {
					XEvent e = tr.get(j);
					if (!l.contains(e.getAttributes().get(classifier).toString())) {
						
						l.add(e.getAttributes().get(classifier).toString());
					}
				}
			}
			AttributeClassifier[] arrAtt = new AttributeClassifier[l.size()];
			replaceClassifiers(arrAtt);
		}
	}

	/**
	 * This constructor does not walk through the event log, but takes the list
	 * of event attributes provided.
	 * 
	 * @param log
	 * @param eventAttributes
	 */
//	public IvMUniqueValueChooser(XLog log, String classifier, int i) {
//		setLayout(new BorderLayout());
//		setOpaque(false);
////		this.combobox = new MultiComboBox<>(TestUniqueValue.class, new TestUniqueValue[0]);
//		this.combobox = new MultiComboBox<>(AttributeClassifier.class, new AttributeClassifier[0]);
//		add(combobox, BorderLayout.CENTER);
//		
//		if (log != null && uniqueValues != null) {
//			Pair<AttributeClassifier[], AttributeClassifier> p = AttributeClassifiers.getAttributeClassifiers(log,
//					uniqueValues, filterLifeCycleTransition);
//			replaceClassifiers(p.getA());
//		}
//	}

	/**
	 * This construct does not walk through the event log, but takes the list of
	 * event attributes provided. Life cycle transition classifiers and
	 * attributes are filtered if requested.
	 * 
	 * @param log
	 * @param eventAttributes
	 * @param filterLifeCycleTransition
	 */
	

	public void addActionListener(ActionListener actionListener) {
		combobox.addActionListener(actionListener);
	}

	public AttributeClassifier[] getSelectedClassifier() {
		return combobox.getSelectedObjects();
	}

	/**
	 * Replace the classifiers in the combobox and select (only) one.
	 * 
	 * @param attributeClassifiers
	 * @param selectedClassifier
	 */
	public void replaceClassifiers(AttributeClassifier[] attributeClassifiers) {
		combobox.removeAllItems();
		for (AttributeClassifier classifier : attributeClassifiers) {
			combobox.addItem(classifier, classifier.isClassifier());
		}
	}

	public MultiComboBox<AttributeClassifier> getMultiComboBox() {
		return combobox;
	}

	/**
	 * 
	 * @param log
	 * @return A list of all event attributes (keys). Linear in the size of the
	 *         event log.
	 */
	public static String[] getEventAttributes(XLog log, String classifier) {
		
		List<String> l = new ArrayList<String>();
		
		if (classifier.equals("")) {
			classifier = "concept:name";
		}
		for (XTrace trace : log) {
			for (XEvent event : trace) {
				if (!l.contains(event.getAttributes().get(classifier).toString())) {
					l.add(event.getAttributes().get(classifier).toString());
				}
			}
		}
		return (String[]) l.toArray();
	}
}