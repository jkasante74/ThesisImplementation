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
 * ------------------
 * AreaChartDemo.java
 * ------------------
 * (C) Copyright 2002-2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: AreaChartDemo.java,v 1.34 2004/05/26 13:04:14 mungady Exp $
 *
 * Changes
 * -------
 * 11-Jun-2002 : Version 1 (DG);
 * 25-Jun-2002 : Removed unnecessary imports (DG);
 * 10-Oct-2002 : Renamed AreaChartForCategoryDataDemo --> AreaChartDemo (DG);
 * 05-Nov-2003 : Added category label position (DG);
 *
 */

import historicalInformationManager.HIM;

import java.awt.Color;
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
 * AreaChart class creates and displays an area chart depending on selected
 * experiment number and using data from experimental results stored in HIR by
 * HIM.
 * 
 * This class inherits the JAVA JFrame class which provides the window frame for
 * the displayed chart
 * 
 */
public class AreaChart extends JFrame {

	/* Variables Declaration and initialization */
	private static final long serialVersionUID = 1L;
	public static DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	private static int expNum; // requested experiment index

	/**
	 * AreaChart constructor. (For invocation by subclass constructors,
	 * typically implicit.) Creates an Area chart of agents' pay-offs against
	 * each tournament of game played. AreaChart constructor also defines a
	 * title for the chart as well as the frame. Modified sections identified
	 * 
	 * @param frameTitle
	 *            Title of the current window
	 * @param chartTitle
	 *            Title for the tournament chart
	 * 
	 */
	public AreaChart(String frameTitle, String chartTitle) {

		super(frameTitle); // Display frame title
		JFrame frame = new JFrame(frameTitle);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		JFreeChart areaChart = ChartFactory.createAreaChart(chartTitle,
				"Tournament", "Pay-Off", /** Modified from original code **/
				createDataset(), PlotOrientation.VERTICAL, true, true, false); // plot
																				// graph
		areaChart.setBackgroundPaint(Color.white); // set background color
		final ChartPanel chartPanel = new ChartPanel(areaChart);
		chartPanel.setPreferredSize(new Dimension(1200, 700));
		chartPanel.setEnforceFileExtensions(false);

		setContentPane(chartPanel);
		chartPanel.setLayout(null);

		JLabel lblExpNum = new JLabel("0");
		lblExpNum.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblExpNum.setBounds(146, 11, 43, 16);
		chartPanel.add(lblExpNum);
		lblExpNum.setText(String.valueOf(expNum + 1));

		JLabel label_1 = new JLabel("Experiment ");
		label_1.setFont(new Font("Lucida Grande", Font.BOLD, 14));
		label_1.setBounds(58, 6, 86, 25);
		chartPanel.add(label_1);

	}

	/**
	 * createDataset method updates information about the progress of players in
	 * a specific experiment by retrieving agents' scores for different
	 * tournaments from the dataset stored HIR
	 * 
	 * @return dataset contains up-to-date information about players' scores,
	 *         and tournament information.
	 */
	private DefaultCategoryDataset createDataset() {
		/* clear dataset */
		dataset.clear();

		/* Query HIM to submit data for area chart display */
		HIM.getDataset(expNum);

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
		AreaChart chart = new AreaChart(
				"Area Chart of Agent Pay-Off vrs Tournament", " ");
		chart.pack();
		RefineryUtilities.centerFrameOnScreen(chart);
		chart.setVisible(true);

	}
}