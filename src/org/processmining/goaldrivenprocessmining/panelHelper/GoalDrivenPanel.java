package org.processmining.goaldrivenprocessmining.panelHelper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConstants;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.plugins.inductiveVisualMiner.chain.DataState;
import org.processmining.plugins.inductiveVisualMiner.helperClasses.ControllerView;

import graph.GoalDrivenDFG;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

public class GoalDrivenPanel extends JPanel {

	private static final long serialVersionUID = -1078786029763735572L;
	private static final Insets margins = new Insets(2, 0, 0, 0);

	//gui elements
	private final JPanel contentLeftPanel;
	private final JPanel contentRightPanel;
	private GoalDrivenDFG highDfgPanel;
	private GoalDrivenDFG lowDfgPanel;
	private JLabel lowDfgTitle;
	private JLabel lowDfgEdgeTitle;
	// display setting 
	private DisplaySettingPanel displaySettingPanel;
	//stat sidebar
	private final SidePanel sidePanel;
	//config elemnets
	private final ControlBar controlBar;
	private final ConfigCards configCards;
	private final JPanel contentPanel;
	private final JLayeredPane layeredPanel;

	private final ControllerView<DataState> controllerView;

	public GoalDrivenPanel(GoalDrivenConfiguration configuration, ProMCanceller canceller) {
		setBackground(GoalDrivenConstants.BACKGROUND_COLOR);
		setLayout(new BorderLayout());

		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				// Update bounds when parent panel size changes
				setDynamicBounds(GoalDrivenPanel.this, contentPanel);
			}
		});

		controlBar = new ControlBar();
		configCards = new ConfigCards();
		add(controlBar, BorderLayout.NORTH);
		layeredPanel = new JLayeredPane();

		layeredPanel.add(configCards, new Integer(1), 0);
		add(layeredPanel, BorderLayout.CENTER);

		//controls the margin on the left side of the settings panel
		sidePanel = new SidePanel();

		contentPanel = new JPanel();
		double contentPanelSize[][] = { { 0.5, 0.5 }, { TableLayoutConstants.MINIMUM, TableLayoutConstants.FILL } };
		contentPanel.setLayout(new TableLayout(contentPanelSize));
		layeredPanel.add(contentPanel, new Integer(0));

		displaySettingPanel = new DisplaySettingPanel();
		displaySettingPanel.setBackground(GoalDrivenConstants.DISPLAY_SETTING_BACKGROUND_COLOR);
		displaySettingPanel.setForeground(Color.WHITE);
		contentPanel.add(displaySettingPanel, "0,0,1,0");

		//graph panel
		{
			contentLeftPanel = new JPanel();
			contentLeftPanel.setLayout(new BorderLayout());
			contentLeftPanel.setBackground(GoalDrivenConstants.CONTENT_CARD_BACKGROUND_COLOR);
			contentLeftPanel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));

			JLabel hightitle = new JLabel("High-level DFG");
			hightitle.setForeground(Color.WHITE);
			hightitle.setFont(GoalDrivenConstants.BOLD_XL_FONT);
			contentLeftPanel.add(hightitle, BorderLayout.NORTH);
			GDPMLogSkeleton log = null;

			// Create and add the highDfgPanel to the default layer
			highDfgPanel = new GoalDrivenDFG(log, true);
			highDfgPanel.setBorder(GoalDrivenConstants.BETWEEN_PANEL_BORDER);
			highDfgPanel.setBackground(GoalDrivenConstants.CONTENT_CARD_COLOR);
			contentLeftPanel.add(highDfgPanel, BorderLayout.CENTER);
			Border border = BorderFactory.createMatteBorder(0, 5, 0, 2,
					GoalDrivenConstants.DISPLAY_SETTING_BACKGROUND_COLOR);
			contentLeftPanel.setBorder(border);
			contentPanel.add(contentLeftPanel, "0,1");
		}

		{
			contentRightPanel = new JPanel();
			contentRightPanel.setLayout(new BorderLayout());
			contentRightPanel.setBackground(GoalDrivenConstants.CONTENT_CARD_BACKGROUND_COLOR);
			contentRightPanel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
			lowDfgTitle = new JLabel("Low-level DFG");
			lowDfgTitle.setForeground(Color.WHITE);
			lowDfgTitle.setFont(GoalDrivenConstants.BOLD_XL_FONT);
			lowDfgEdgeTitle = new JLabel("");
			lowDfgEdgeTitle.setForeground(Color.WHITE);
			lowDfgEdgeTitle.setFont(GoalDrivenConstants.BOLD_XL_FONT);
			JPanel compoundLabel = new JPanel();
			compoundLabel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			compoundLabel.setBackground(GoalDrivenConstants.CONTENT_CARD_BACKGROUND_COLOR);
			compoundLabel.add(lowDfgTitle);
			compoundLabel.add(lowDfgEdgeTitle);
			lowDfgEdgeTitle.setVisible(false);
			contentRightPanel.add(compoundLabel, BorderLayout.NORTH);
			GDPMLogSkeleton log = null;
			lowDfgPanel = new GoalDrivenDFG(log, false);
			lowDfgPanel.setBorder(GoalDrivenConstants.BETWEEN_PANEL_BORDER);
			lowDfgPanel.setBackground(GoalDrivenConstants.CONTENT_CARD_COLOR);
			contentRightPanel.add(lowDfgPanel, BorderLayout.CENTER);
			Border border = BorderFactory.createMatteBorder(0, 2, 0, 5,
					GoalDrivenConstants.DISPLAY_SETTING_BACKGROUND_COLOR);
			contentRightPanel.setBorder(border);
			contentPanel.add(contentRightPanel, "1,1");

		}
		//controller view
		{
			controllerView = new ControllerView<>(this);
		}

	}

	private static void setDynamicBounds(JPanel parentPanel, JPanel childPanel) {
		int parentWidth = parentPanel.getWidth();
		int parentHeight = parentPanel.getHeight();
		childPanel.setBounds(0, 0, parentWidth, parentHeight);
	}

	public JButton drawButton(String name) {
		return new JButton(name);
	}

	public ControlBar getControlBar() {
		return controlBar;
	}

	public SidePanel getSidePanel() {
		return sidePanel;
	}

	public JPanel getContentPanel() {
		return contentPanel;
	}

	public JLayeredPane getLayeredPanel() {
		return layeredPanel;
	}

	public ConfigCards getConfigCards() {
		return configCards;
	}

	public GoalDrivenDFG getHighDfgPanel() {
		return highDfgPanel;
	}

	public void setHighDfgPanel(GoalDrivenDFG dfg) {
		this.highDfgPanel = dfg;
	}

	public GoalDrivenDFG getLowDfgPanel() {
		return lowDfgPanel;
	}

	public void setLowDfgPanel(GoalDrivenDFG dfg) {
		this.lowDfgPanel = dfg;
	}

	public JPanel getContentLeftPanel() {
		return contentLeftPanel;
	}

	public JPanel getContentRightPanel() {
		return contentRightPanel;
	}

	public JLabel getLowDfgTitle() {
		return lowDfgTitle;
	}

	public void setLowDfgTitle(JLabel lowDfgTitle) {
		this.lowDfgTitle = lowDfgTitle;
	}

	public JLabel getLowDfgEdgeTitle() {
		return lowDfgEdgeTitle;
	}

	public void setLowDfgEdgeTitle(JLabel lowDfgEdgeTitle) {
		this.lowDfgEdgeTitle = lowDfgEdgeTitle;
	}

	public ControllerView<DataState> getControllerView() {
		return controllerView;
	}

	public DisplaySettingPanel getDisplaySettingPanel() {
		return displaySettingPanel;
	}

	public void setDisplaySettingPanel(DisplaySettingPanel displaySettingPanel) {
		this.displaySettingPanel = displaySettingPanel;
	}

}