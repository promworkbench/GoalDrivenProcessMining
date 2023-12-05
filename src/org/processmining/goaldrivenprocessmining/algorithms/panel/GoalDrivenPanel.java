package org.processmining.goaldrivenprocessmining.algorithms.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.processmining.framework.plugin.ProMCanceller;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConfiguration;
import org.processmining.goaldrivenprocessmining.algorithms.GoalDrivenConstants;
import org.processmining.goaldrivenprocessmining.objectHelper.GDPMLogSkeleton;
import org.processmining.goaldrivenprocessmining.panelHelper.ConfigCards;
import org.processmining.goaldrivenprocessmining.panelHelper.ControlBar;
import org.processmining.goaldrivenprocessmining.panelHelper.SidePanel;
import org.processmining.plugins.InductiveMiner.AttributeClassifiers.AttributeClassifier;
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
		contentPanel = new JPanel();
		double contentPanelSize[][] = { { 0.5, 0.5 },
				{ TableLayoutConstants.FILL } };
		contentPanel.setLayout(new TableLayout(contentPanelSize));
		layeredPanel.add(contentPanel, new Integer(0));

		//		setDynamicBounds(GoalDrivenPanel.this, contentPanel);
		layeredPanel.add(configCards, new Integer(1), 0);
		add(layeredPanel, BorderLayout.CENTER);
		//controls the margin on the left side of the settings panel
		sidePanel = new SidePanel();

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
//			GridBagConstraints gbcContentLeftPanel = createGridBagConstraints(0, 0, 0.5);

			contentPanel.add(contentLeftPanel, "0,0");
		}

		{
			contentRightPanel = new JPanel();
			contentRightPanel.setLayout(new BorderLayout());
			contentRightPanel.setBackground(GoalDrivenConstants.CONTENT_CARD_BACKGROUND_COLOR);
			contentRightPanel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
			lowDfgTitle = new JLabel("Low-level DFG");
			lowDfgTitle.setForeground(Color.WHITE);
			lowDfgTitle.setFont(GoalDrivenConstants.BOLD_XL_FONT);
			contentRightPanel.add(lowDfgTitle, BorderLayout.NORTH);
			GDPMLogSkeleton log = null;
			lowDfgPanel = new GoalDrivenDFG(log, false);
			lowDfgPanel.setBorder(GoalDrivenConstants.BETWEEN_PANEL_BORDER);
			lowDfgPanel.setBackground(GoalDrivenConstants.CONTENT_CARD_COLOR);
			contentRightPanel.add(lowDfgPanel, BorderLayout.CENTER);

//			GridBagConstraints gbcContentRightPanel = createGridBagConstraints(1, 0, 0.5);
			contentPanel.add(contentRightPanel, "1,0");

		}
		//controller view
		{
			controllerView = new ControllerView<>(this);
		}

	}

//	public static GridBagConstraints createGridBagConstraints(int gridx, int gridy, double weightx) {
//		GridBagConstraints gbc = new GridBagConstraints();
//		gbc.gridx = gridx;
//		gbc.gridy = gridy;
//		gbc.weightx = weightx;
//		gbc.weighty = 1;
//		gbc.fill = GridBagConstraints.BOTH;
//		return gbc;
//	}

	private static void setDynamicBounds(JPanel parentPanel, JPanel childPanel) {
		int parentWidth = parentPanel.getWidth();
		int parentHeight = parentPanel.getHeight();
		childPanel.setBounds(0, 0, parentWidth, parentHeight);
	}

	public JButton drawButton(String name) {
		return new JButton(name);
	}

	public JTable drawConfigTable(AttributeClassifier[] acts, String type) {
		String[] columnNames = { "Activities", "" };
		if (acts.length == 0) {
			return new JTable(new DefaultTableModel(null, columnNames));
		}
		Object[][] data = new Object[acts.length][2];
		for (int i = 0; i < acts.length; i++) {
			data[i][0] = acts[i];
			data[i][1] = type;
		}
		return new JTable(new DefaultTableModel(data, columnNames));
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

	public ControllerView<DataState> getControllerView() {
		return controllerView;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("JLayeredPane Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLayeredPane layeredPane = new JLayeredPane();
        frame.getContentPane().add(layeredPane);

        JPanel panelB = new JPanel();
        panelB.setBackground(Color.BLUE);
        panelB.setBounds(50, 50, 300, 200); // Example bounds for panel B

        JPanel panelC = new JPanel();
        panelC.setBackground(Color.RED);
        
        // Set bounds for panel C relative to the top-right corner of panel B
        int widthC = 200;
        int heightC = 200;
        int xC = panelB.getX() + panelB.getWidth() - widthC;
        int yC = panelB.getY();
        panelC.setBounds(xC, yC, widthC, heightC);

        layeredPane.add(panelB, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(panelC, JLayeredPane.PALETTE_LAYER);

        frame.setSize(400, 300);
        frame.setVisible(true);
	}

	private static Image getScaledImage(Image srcImg, int width, int height) {
		BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resizedImg.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.drawImage(srcImg, 0, 0, width, height, null);
		g2d.dispose();
		return resizedImg;
	}

}