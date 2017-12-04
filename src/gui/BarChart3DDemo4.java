package gui;
/* --------------------
 * BarChart3DDemo4.java
 * --------------------
 * (C) Copyright 2005-2008, by Object Refinery Limited.
 *
 */



import historicalInformationManager.HIM;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.CategoryAnchor;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

/**
 * This chart shows how to override the getItemPaint() method to set a color
 * that depends on the data value.
 */
public class BarChart3DDemo4 extends JFrame {

	// Parameter variables 
	private static final long serialVersionUID = 1L;
	public static DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	private static int expNum; // requested experiment index
	public static ArrayList<String> agentStrategies = new ArrayList<String>();

	
    /**
     * A custom renderer that returns a different color for each item in a
     * single series.
     */
    static class CustomBarRenderer3D extends BarRenderer3D {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
         * Creates a new renderer.
         */
        public CustomBarRenderer3D() {
        }

        /**
         * Returns the paint for an item.  Overrides the default behaviour
         * inherited from AbstractSeriesRenderer.
         *
         * @param row  the series.
         * @param column  the category.
         *
         * @return The item color.
         */
        public Paint getItemPaint(int row, int column) {
          	String s = dataset.getRowKey(row).toString();
            int agentId = Integer.parseInt(s.substring(6));
            Color color = new Color(79, 129, 189);
            if (agentStrategies.get(agentId-1).equalsIgnoreCase("Advanced_C")) 
            	color = new Color(248,171,38);
            else if (agentStrategies.get(agentId-1).equalsIgnoreCase("Naive_C")) 
            	color = new Color(252,197,90);
            else if (agentStrategies.get(agentId-1).equalsIgnoreCase("Naive_D")) 
            	color = new Color(254,251,188);
            else
            	color = new Color(30,104,94);
            return color;
        
        }
    }
    
    
    
    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public BarChart3DDemo4(String title) {
        super(title);
        CategoryDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1200, 770));
        setContentPane(chartPanel);
    }

    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
   private static CategoryDataset createDataset() {
     
	   dataset.clear();
		
	   // Query HIM to submit data for bar chart display 
		HIM.getDataset(expNum);
		return dataset;
     }

    /**
     * Creates a chart.
     *
     * @param dataset  the dataset.
     *
     * @return The chart.
     */
    private static JFreeChart createChart(CategoryDataset dataset) {

        JFreeChart chart = ChartFactory.createBarChart3D(
            "Experiment "+ (expNum + 1),      // chart title
            "Tournament",               // domain axis label
            "Cummulative Pay-Off",                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            false,                     // include legend
            true,                     // tooltips
            false                     // urls
        );

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        CustomBarRenderer3D renderer = new CustomBarRenderer3D();
        renderer.setBaseItemLabelGenerator(
                new StandardCategoryItemLabelGenerator());
        renderer.setBaseItemLabelsVisible(true);
        renderer.setItemLabelAnchorOffset(10.0);
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
        plot.setRenderer(renderer);
        renderer.setBaseItemLabelsVisible(true);
        renderer.setMaximumBarWidth(0.05);

        // couldn't get the label above to appear in front, so using an
        // annotation instead...
        CategoryTextAnnotation a = new CategoryTextAnnotation(
                "Minimum grade to pass", "Robert", 0.71);
        a.setCategoryAnchor(CategoryAnchor.START);
        a.setFont(new Font("SansSerif", Font.PLAIN, 12));
        a.setTextAnchor(TextAnchor.BOTTOM_LEFT);
        plot.addAnnotation(a);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setUpperMargin(0.10);

        ChartUtilities.applyCurrentTheme(chart);

        return chart;

    }

    /**
     * Creates a panel for the demo (used by SuperDemo.java).
     *
     * @return A panel.
     */
    public static JPanel createDemoPanel() {
        JFreeChart chart = createChart(createDataset());
        return new ChartPanel(chart);
    }

    
   
    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {
		expNum = Integer.parseInt(args[0]);

        BarChart3DDemo4 demo = new BarChart3DDemo4(
                "Bar Chart of Agents Pay-off vrs Tournament");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}