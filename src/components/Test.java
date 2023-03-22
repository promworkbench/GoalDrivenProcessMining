package components;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.icepear.echarts.Graph;
import org.icepear.echarts.Option;
import org.icepear.echarts.charts.graph.GraphEdgeItem;
import org.icepear.echarts.charts.graph.GraphEdgeLineStyle;
import org.icepear.echarts.charts.graph.GraphNodeItem;
import org.icepear.echarts.charts.graph.GraphSeries;
import org.icepear.echarts.components.dataZoom.DataZoom;
import org.icepear.echarts.components.series.SeriesLabel;
import org.icepear.echarts.render.Engine;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

public class Test extends JPanel {

	public Test() {
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

		setLayout(new BorderLayout());
		JFXPanel fxPanel = new JFXPanel();
		add(fxPanel, BorderLayout.CENTER);
		Platform.runLater(() -> {
			WebView webView = new WebView();
			webView.getEngine().load("file:///C:/D/eclipse/GoalDrivenProcessMining/index1.html");
			fxPanel.setScene(new Scene(webView));
		});

	}

	public static void main(String[] args) {
		Graph graph = new Graph()
		        .addSeries(new GraphSeries().setSymbolSize(50)
		                .setLabel(new SeriesLabel().setShow(true))
		                .setEdgeSymbol(new String[] { "circle", "arrow" })
		                .setData(new GraphNodeItem[] {
		                        new GraphNodeItem().setName("Node 1").setX(300).setY(300),
		                        new GraphNodeItem().setName("Node 2").setX(800).setY(300),
		                        new GraphNodeItem().setName("Node 3").setX(550).setY(100),
		                        new GraphNodeItem().setName("Node 4").setX(550).setY(500)
		                })
		                .setLinks(new GraphEdgeItem[] {
		                        new GraphEdgeItem().setSource("Node 1").setTarget("Node 2")
		                                .setLineStyle(new GraphEdgeLineStyle().setCurveness(0.2)),
		                        new GraphEdgeItem().setSource("Node 2").setTarget("Node 1")
		                                .setLineStyle(new GraphEdgeLineStyle().setCurveness(0.2)),
		                        new GraphEdgeItem().setSource("Node 1").setTarget("Node 3"),
		                        new GraphEdgeItem().setSource("Node 2").setTarget("Node 3"),
		                        new GraphEdgeItem().setSource("Node 2").setTarget("Node 4"),
		                        new GraphEdgeItem().setSource("Node 1").setTarget("Node 4")
		                })
		                .setLineStyle(new GraphEdgeLineStyle().setOpacity(0.9).setWidth(2).setCurveness(0)));
		Option option = new Option();
		DataZoom z = new DataZoom();
//		z.setType("inside");
		option.setDataZoom(z);
		Engine engine = new Engine();
		// The render method will generate our EChart into a HTML file saved locally in the current directory.
		// The name of the HTML can also be set by the first parameter of the function.
		engine.render("index1.html", graph);
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("HTML Panel Example");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			frame.getContentPane().add(new Test());
			frame.pack();
			frame.setVisible(true);
		});
	}

}