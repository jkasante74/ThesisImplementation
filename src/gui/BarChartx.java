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

import historicalInformationManager.HIM;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RefineryUtilities;

import javax.swing.JLabel;

import java.awt.Font;

/**
 * BarChartx class creates and displays an area chart depending on selected
 * experiment number and using data from experimental results stored in HIR by
 * HIM.
 * 
 * This class inherits the JAVA JFrame class which provides the window frame for
 * the displayed chart
 * 
 */
public class BarChartx extends JFrame {

	/* Parameter variables */
	public static DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	private static final long serialVersionUID = 1L;
	private static int expNum; // requested experiment index

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
	public BarChartx(String frameTitle, String chartTitle) {

		super(frameTitle);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		final JFreeChart chart = ChartFactory.createBarChart(chartTitle,
				"Tournament", "Pay-Off", /** Modified from original code **/
				createDataset(), PlotOrientation.VERTICAL, true, true, false);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(1200, 770));
		setContentPane(chartPanel);
		chartPanel.setLayout(null);

		JLabel label = new JLabel("Experiment ");
		label.setFont(new Font("Lucida Grande", Font.BOLD, 14));
		label.setBounds(56, 6, 86, 25);
		chartPanel.add(label);

		JLabel lblExpNum = new JLabel("0");
		lblExpNum.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblExpNum.setBounds(144, 11, 43, 16);
		chartPanel.add(lblExpNum);
		lblExpNum.setText(String.valueOf(expNum + 1));

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
	//	JOptionPane.showMessageDialog(null, agentStrategies);
		
		
		
		return dataset; // add the data point (y-value, variable, x-value)
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
		final BarChartx demo = new BarChartx(
				"Bar Chart  of Agent Pay-Off vrs Tournament", " ");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);

	}
}