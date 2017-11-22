package gui;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextArea;
import java.awt.Color;

public class GUI_Simulation {

	// Parameter variables 
	private JFrame frmSimulation;
	public static JTextArea txtSim = new JTextArea();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					GUI_Simulation window = new GUI_Simulation();
					window.frmSimulation.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI_Simulation() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSimulation = new JFrame();
		frmSimulation.setResizable(false);
		frmSimulation.setTitle("Simulation");
		frmSimulation.setBounds(100, 100, 510, 502);
		frmSimulation.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSimulation.getContentPane().setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(6, 6, 498, 468);
		frmSimulation.getContentPane().add(scrollPane);
		txtSim.setForeground(new Color(47, 79, 79));

		scrollPane.setViewportView(txtSim);
	}
}
