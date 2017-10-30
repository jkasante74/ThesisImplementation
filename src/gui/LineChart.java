package gui;

/**
 * 
 * This code was adopted and modified from 
 * http://www.java2s.com/Code/Java/Chart/CatalogChart.htm
 */

/**
 * LineChart is the base Java ApplicationFrame class for all graphics 
 * which allow the simulator to illustrate in a line graph the progress 
 * of each player participating in a customized repeated round robin tournament
 * against each round.
 * 
 * As a Graphics component, this class encapsulates the state information needed
 * for the various rendering operations that Java supports.  This
 * state information includes:
 * 
 * <ul>
 * 	<li>The Component to draw on
 * 	<li>A translation origin for rendering and clipping coordinates
 * 	<li>The current clip
 * 	<li>The current color
 * 	<li>The current font
 * 	<li>The current logical pixel operation function (XOR or Paint)
 * 	<li>The current XOR alternation color
 * </ul>
 * All coordinates which appear as arguments to the methods of this
 * Graphics object are considered relative to the translation origin
 * of this Graphics object prior to the invocation of the method.
 * 
 * @author Jonathan Asante Nyantakyi
 * @version 1.0
 * @since   12-07-2016 
 */

import historicalInformationManager.HIM;

import java.awt.Color;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Font;

/**
 * LineChart class creates and displays a line chart depending on selected
 * experiment number and using data from experimental results stored in HIR by
 * HIM.
 * 
 * This class inherits the JAVA JFrame class which provides the window frame for
 * the displayed chart
 * 
 */

public class LineChart extends JFrame {

	/* Variables Declaration and initialization */
	private static final long serialVersionUID = 1L;
	public static DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	private static int expNum; // requested Experiment index
	protected JLabel lblExpNum;

	/**
	 * LineChart constructor. (For invocation by subclass constructors,
	 * typically implicit.) Creates a line chart of Players' pay-offs against
	 * each round of game played. LineChart method also defines a title for the
	 * chart as well as the frame. Modified sections identified
	 * 
	 * @param frameTitle
	 *            Title of the current window
	 * @param chartTitle
	 *            Title for the tournament chart
	 * 
	 */
	public LineChart(String frameTitle, String chartTitle) {
		super(frameTitle); // Display frame title

		final JFrame frame = new JFrame(frameTitle);
		frame.setDefaultCloseOperation(frame.getDefaultCloseOperation());
		JFreeChart lineChart = ChartFactory.createLineChart(chartTitle,
				"Tournament", "Pay-Off", /** Modified from original code **/
				createDataset(), PlotOrientation.VERTICAL, true, true, false); // plot
																				// graph
		lineChart.setBackgroundPaint(Color.white); // set background color

		ChartPanel chartPanel = new ChartPanel(lineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(1200, 700)); // Create
																		// dimensions
																		// for
																		// window
		setContentPane(chartPanel);
		chartPanel.setLayout(null);

		JLabel lblNewLabel = new JLabel("Experiment ");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.BOLD, 14));
		lblNewLabel.setBounds(67, 6, 86, 25);
		chartPanel.add(lblNewLabel);

		lblExpNum = new JLabel("");
		lblExpNum.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblExpNum.setBounds(155, 11, 43, 16);
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
		/* clear dataset */
		dataset.clear();

		/* Query HIM to submit data for line chart display */
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
	public static void main(String[] args) {
		expNum = Integer.parseInt(args[0]);

		LineChart chart = new LineChart(
				"Line Graph of Agent Pay-Off vrs Tournament", " ");
		/** Title Modified from original code **/
		chart.pack(); // resize the window to fit the graph
		RefineryUtilities.centerFrameOnScreen(chart); // display chart on center
														// screen
		chart.setVisible(true); // show the chart
	}
}