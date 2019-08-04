package gui;

/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * -----------------
 * BarChartDemo.java
 * -----------------
 * (C) Copyright 2002-2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: BarChartDemo.java,v 1.16 2004/04/29 10:06:34 mungady Exp $
 *
 * Changes
 * -------
 * 11-Jun-2002 : Version 1 (DG);
 * 25-Jun-2002 : Removed redundant imports (DG);
 * 09-Oct-2002 : Added frame centering (DG);
 * 18-Nov-2002 : Changed from DefaultCategoryDataset to DefaultTableDataset (DG);
 * 28-Oct-2003 : Changed to display gradient paint (DG);
 * 10-Nov-2003 : Renamed BarChartDemo.java (DG);
 *
 */

import gui.BarChart3DDemo4.CustomBarRenderer3D;
import historicalInformationManager.HIM;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

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
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

import javax.swing.JLabel;

import java.awt.Font;
import java.util.ArrayList;

/**
 * BarChartx class creates and displays an area chart depending on selected
 * experiment number and using data from experimental results stored in HIR by
 * HIM.
 * 
 * This class inherits the JAVA JFrame class which provides the window frame for
 * the displayed chart
 * 
 */
public class BarChart extends JFrame {

	// Parameter variables 
	public static DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	private static final long serialVersionUID = 1L;
	private static int expNum; // requested experiment index
	public static ArrayList<String> agentStrategies = new ArrayList<String>();
	
	
	class DifferenceBarRenderer extends BarRenderer {
		
		// Private variables
		private static final long serialVersionUID = 1L;
		
		/**
         * Creates a new renderer.
         */
		public DifferenceBarRenderer() {
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
          JOptionPane.showMessageDialog(null, agentId);
            if (agentStrategies.get(agentId-1).equalsIgnoreCase("Advanced_C")) 
                return Color.CYAN;
            else if (agentStrategies.get(agentId-1).equalsIgnoreCase("Naive_C")) 
                return Color.BLUE;
            else if (agentStrategies.get(agentId-1).equalsIgnoreCase("Naive_D")) 
                return Color.YELLOW;
            else
                return Color.RED;
        }
    }
	
	
	
	
	
	/**
	 * BarChartx constructor. (For invocation by subclass constructors, typically
	 * implicit.) Creates a bar chart of Players' pay-offs against each
	 * tournament of game played. BarChartx constructor also defines a title for
	 * the chart as well as the frame. Modified sections identified
	 * 
	 * @param frameTitle
	 *            Title of the current window
	 * @param chartTitle
	 *            Title for the tournament chart
	 * 
	 */
	public BarChart(String frameTitle, String chartTitle) {

		super(frameTitle);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	//	final JFreeChart chart = ChartFactory.createBarChart(chartTitle,
	//			"Tournament", "Cummulative Pay-Off", /** Modified from original code **/
	//			createDataset(), PlotOrientation.VERTICAL, true, true, false);
        
		CategoryDataset dataset = createDataset();

		JFreeChart chart = createChart(dataset);
		
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(1200, 770));
		setContentPane(chartPanel);
		chartPanel.setLayout(null);

	}

	/**
	 * createDataset method updates information about the progress of players in
	 * a specific experiment by retrieving agents' scores for different
	 * tournaments from the Dataset stored HIR
	 * 
	 * @return dataset contains up-to-date information about players' scores,
	 *         and tournament information.
	 */
	private DefaultCategoryDataset createDataset() {
		
		dataset.clear();
		
		// Query HIM to submit data for bar chart display 
		HIM.getDataset(expNum);
		//JOptionPane.showMessageDialog(null, agentStrategies);

		return dataset; // add the data point (y-value, variable, x-value)
	}

	
    /**
     * Creates a chart.
     *
     * @param dataset  the dataset.
     *
     * @return The chart.
     */
    private static JFreeChart createChart(CategoryDataset dataset) {

        JFreeChart chart = ChartFactory.createBarChart(
            "Experiment : "+ (expNum+1), 	// chart title
        	"Tournament",               // domain axis label
            "Cummulative Pay-Off",      // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            true,                     // include legend
            true,                     // tooltips
            false                     // urls
        );
      //  chart.getLegend().setPosition(RectangleEdge.RIGHT);
        LegendTitle legend = chart.getLegend();
        Font labelFont = new Font("Arial", Font.BOLD, 12);
        legend.setItemFont(labelFont);
        
       /* 
        TextTitle legendText = new TextTitle("\n Naive_C     Naive_D    Advanced_C   Advanced_D \n \n");
        legendText.setPosition(RectangleEdge.BOTTOM);
        chart.addSubtitle(legendText);
        */
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
         
        // set the color (r,g,b) or (r,g,b,a)
        Color color = new Color(79, 129, 189);
 
        
        for (int i = 0; i < dataset.getRowCount(); i++){
            String s = dataset.getRowKey(i).toString(); 
        	int agentId = Integer.parseInt(s.substring(6,7));
        
        	switch(agentStrategies.get(agentId-1)){
        	
        	case "Advanced_C": 
            	color = new Color(248,171,38);
            	break;
        	
        	case "Naive_C": 
            	color = new Color(252,197,90);
            	break;
            	
        	case "Naive_D": 
            	color = new Color(254,251,188);
            	break;
            	
        	case "Advanced_D": 
            	color = new Color(30,104,94);
            	break;
        	}
            renderer.getLegendItems();
            renderer.setSeriesPaint(i, color);
            
        }
        

        return chart;

    }
	
	
	
	
	
	/**
	 * Main method that creates Application frame, centers it on the screen, and
	 * makes the chart visible.
	 * 
	 * Main method is known to be the class' application entry point
	 * 
	 * @param args
	 *            Java main array of command-line arguments whose data type is
	 *            string passed to this method
	 * 
	 */
	public static void main(final String[] args) {
		expNum = Integer.parseInt(args[0]);
		final BarChart demo = new BarChart(
				"Bar Chart  of Agent Pay-Off vrs Tournament", " ");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}
}


