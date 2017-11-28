package gui;

import historicalInformationManager.HIM;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.border.LineBorder;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;

import java.awt.Font;
import java.awt.SystemColor;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JSlider;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import setupManager.StrategySetupManager;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextArea;

public class GUI {

	// Parameters of GUI component
	public static boolean radomRequest = false;
	public static JComboBox<Object> cmbExpSel;

	// Private variables
	private JFrame frame;
	private JTextField txtTemptation;
	private JTextField txtReward;
	private JTextField txtSucker;
	private JTextField txtPunishment;
	private JTextField txtTournNum;
	private JTextField txtAdvanceCLimit;
	private JTextField txtAdvancedDLimit;
	private JTextField txtCNum;
	private JTextField txtDNum;
	private JTextField txtAdvancedCNum;
	private JTextField txtAdvancedDNum;
	private static boolean startSimulation = false;
	private static JTextArea txtLeaderBoard = new JTextArea();
	private static final JTextArea txtStats = new JTextArea();

	/**
	 * Main method that creates Application frame,
	 * 
	 * Main method is known to be the class' application entry point
	 * 
	 * @param args
	 *            Java main array of command- line arguments whose data type is
	 *            string passed to this method
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
					GUI_Simulation.main(null);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(192, 192, 192), 1, true));
		panel.setBounds(16, 419, 417, 42);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		frame.setLocation(800, 100); // Set the frame location
		JButton btnStart = new JButton("Start");
		final JRadioButton rdbtnLoadSetup = new JRadioButton(
				"Load Experiment Setup");
		final JRadioButton rdbtnLoadAg = new JRadioButton(
				"Load agents Strategies");
		final JLabel lblUncertainties = new JLabel("1.0");
		final JRadioButton rdbtnAssign = new JRadioButton("Assign");
		final JComboBox<String> cmbInfoReqApproach = new JComboBox<String>();

		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// Initialize parameters to be loaded into Setup reopsitory
				String[] param = new String[8];
				String[] agentNum = new String[5];
				startSimulation = true;

				// Check if we are not loading setup from batch file 
				if (!rdbtnLoadSetup.isSelected()) {

					if (rdbtnAssign.isSelected())
						// check if request limits textbox are empty 
						if ((txtAdvancedDLimit.getText().isEmpty())
								|| (txtAdvanceCLimit.getText().isEmpty())
								&& (!radomRequest)) {
							JOptionPane.showMessageDialog(null,
									"Request Limits must be entered");
							return;
						}

					// Store inputs of SetUp as an array 
					param[0] = (txtTemptation.getText());
					param[1] = (txtReward.getText());
					param[2] = (txtPunishment.getText());
					param[3] = (txtSucker.getText());
					param[4] = (txtTournNum.getText());
					param[5] = (lblUncertainties.getText());
					param[6] = (txtAdvanceCLimit.getText());
					param[7] = (txtAdvancedDLimit.getText());

					// Store agents SetUp array in the setup repository 
					try {
						String FileHeading = "T,R,P,S,NumOfTournament,Uncertainty,AdvancedCLimit,AdvancedDLimit";
						writeFile("SR/SetupParam.csv", FileHeading, param);
					} catch (IOException e1) {

						e1.printStackTrace();
					}
				}

				// Store inputs of agents Straategies Number as an array 
				if (!rdbtnLoadAg.isSelected()) {
					agentNum[0] = (txtCNum.getText());
					agentNum[1] = (txtDNum.getText());
					agentNum[2] = (txtAdvancedCNum.getText());
					agentNum[3] = (txtAdvancedDNum.getText());
					agentNum[4] = String.valueOf((cmbInfoReqApproach
							.getSelectedIndex()));

					
					 // Store number of agents Straategies in the setup repository
					 
					try {
						String FileHeading = "NaiveC,NaiveD,AdvancedC,AdvancedD,infoRequestApproach";
						writeFile("SR/AgentNum.csv", FileHeading, agentNum);
					} catch (IOException e1) {

						e1.printStackTrace();
					}

				}

				try {
					StrategySetupManager.main(null);
				} catch (IOException e1) {

					e1.printStackTrace();
				}
			}

			/**
			 * writeFile method picks up the experimenter's simulation inputs
			 * and stores it in the setup repository to be accessed and
			 * validated by the setup manager
			 * 
			 * @param fileName
			 *            : Setup Repository
			 * @param fileHeading
			 *            : Title of various parameters in setup repository
			 * @param param
			 *            : Setup values
			 * @throws IOException
			 */
			private void writeFile(String fileName, String fileHeading,
					String[] param) throws IOException {
				BufferedWriter outputWriter = null;
				outputWriter = new BufferedWriter(new FileWriter(fileName));

				outputWriter.write(fileHeading);
				outputWriter.newLine();
				for (int i = 0; i < param.length; i++) {
					outputWriter.write((param[i]) + ",");

				}
				outputWriter.flush();
				outputWriter.close();
			}
		});
		btnStart.setBounds(16, 6, 99, 32);
		panel.add(btnStart);

		JButton btnPause = new JButton("Pause");
		btnPause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnPause.setBounds(151, 6, 99, 32);
		panel.add(btnPause);

		JButton btnStop = new JButton("Stop");
		btnStop.setBounds(288, 6, 99, 32);
		panel.add(btnStop);

		final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		tabbedPane.setBounds(6, 6, 438, 411);
		frame.getContentPane().add(tabbedPane);

		JPanel tbAbout = new JPanel();
		tbAbout.setBackground(Color.WHITE);
		tabbedPane.addTab("About", null, tbAbout, null);
		tbAbout.setLayout(null);

		JLabel lblNewLabel = new JLabel(
				"<html> \r<h3> \r<font color = red>Simulation of Iterated Round Robin Tournament </font>\r</h3>\r<br> \rBy Jonathan Asante\n<br>\n<br>\r<p align = \"justify\">\rWe consider population of agents in an Iterated Round Robin Tournament game where in each tournament every agent engages in a single prisoner's dilemma game against every other agent. \n<br>\n<br>\nFor every tournament with n agents, each agent will participate in (n - 1) matches with a total of n/2 (n - 1) number of matches play the prisoner'sdilemma game based on the action set A= {C , D}.\r</p>\r<p align = \"justify\">\rPlayer's strategy assignments  include; \r<ul>\r<li><font color = blue>Naive Cooperator</font>\r<li><font color = blue>Naive Defector</font>\n<li><font color = blue>Advanced Cooperator</font>\n<li><font color = blue>Advanced Defector</font>\n</ul>\n\r<p align = \"justify\">\rPlayers will compete in this simulation experiment against parameters; \r<ul>\r<li><font color = red>Information acquisition limits.</font>\r<li><font color = red>Different payoff Matrix.</font>\r<li><font color = red>Information acquisition uncertainties.</font>\r</ul>\r</p>\r<br>\r</html>\r");
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 12));

		lblNewLabel.setBounds(6, 6, 405, 353);
		tbAbout.add(lblNewLabel);

		JPanel tbSetUp = new JPanel();
		tabbedPane.addTab("SetUp", null, tbSetUp, null);
		tbSetUp.setLayout(null);

		JPanel panel_1 = new JPanel();
		panel_1.setLayout(null);
		panel_1.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panel_1.setBounds(19, 36, 379, 144);
		tbSetUp.add(panel_1);

		JLabel label = new JLabel("Temptation PayOff [D, C]");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setForeground(SystemColor.controlShadow);
		label.setBounds(6, 12, 198, 16);
		panel_1.add(label);

		JLabel label_1 = new JLabel("Reward PayOff [C, C]");
		label_1.setHorizontalAlignment(SwingConstants.RIGHT);
		label_1.setForeground(SystemColor.controlShadow);
		label_1.setBounds(6, 46, 198, 16);
		panel_1.add(label_1);

		JLabel lblSuckersPayoffc = new JLabel("Sucker's PayOff [C, D]");
		lblSuckersPayoffc.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSuckersPayoffc.setForeground(SystemColor.controlShadow);
		lblSuckersPayoffc.setBounds(6, 113, 198, 16);
		panel_1.add(lblSuckersPayoffc);

		JLabel lblPunishmentPayoffd = new JLabel("Punishment PayOff [D, D]");
		lblPunishmentPayoffd.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPunishmentPayoffd.setForeground(SystemColor.controlShadow);
		lblPunishmentPayoffd.setBounds(6, 78, 198, 16);
		panel_1.add(lblPunishmentPayoffd);

		txtTemptation = new JTextField();
		txtTemptation.setText("5");
		txtTemptation.setColumns(10);
		txtTemptation.setBounds(227, 6, 146, 28);
		panel_1.add(txtTemptation);

		txtReward = new JTextField();
		txtReward.setText("3");
		txtReward.setColumns(10);
		txtReward.setBounds(227, 39, 146, 28);
		panel_1.add(txtReward);

		txtSucker = new JTextField();
		txtSucker.setText("0");
		txtSucker.setColumns(10);
		txtSucker.setBounds(227, 105, 146, 28);
		panel_1.add(txtSucker);

		txtPunishment = new JTextField();
		txtPunishment.setText("1");
		txtPunishment.setColumns(10);
		txtPunishment.setBounds(227, 72, 146, 28);
		panel_1.add(txtPunishment);

		final JRadioButton rdbtnRandom = new JRadioButton("Random");
		rdbtnRandom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (rdbtnRandom.isSelected()) {
					radomRequest = true;
					rdbtnAssign.setSelected(false);
					txtAdvanceCLimit.setEnabled(false);
					txtAdvancedDLimit.setEnabled(false);

				}

			}
		});

		rdbtnAssign.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (rdbtnAssign.isSelected()) {
					rdbtnRandom.setSelected(false);
					txtAdvanceCLimit.setEnabled(true);
					txtAdvanceCLimit.setText("");
					txtAdvancedDLimit.setEnabled(true);
					txtAdvancedDLimit.setText("");
					radomRequest = false;
				}

			}
		});
		final JSlider slider = new JSlider();
		final JButton btnDefault = new JButton("Default");
		final JButton btnClear = new JButton("Clear");

		rdbtnLoadSetup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (rdbtnLoadSetup.isSelected()) {
					txtTemptation.setEnabled(false);
					txtPunishment.setEnabled(false);
					txtReward.setEnabled(false);
					txtSucker.setEnabled(false);
					txtTournNum.setEnabled(false);
					txtAdvanceCLimit.setEnabled(false);
					txtAdvancedDLimit.setEnabled(false);
					rdbtnRandom.setEnabled(false);
					rdbtnAssign.setEnabled(false);
					slider.setEnabled(false);
					btnDefault.setEnabled(false);
					btnClear.setEnabled(false);

				} else {
					txtTemptation.setEnabled(true);
					txtPunishment.setEnabled(true);
					txtReward.setEnabled(true);
					txtSucker.setEnabled(true);
					txtTournNum.setEnabled(true);
					;
					txtAdvanceCLimit.setEnabled(true);
					txtAdvancedDLimit.setEnabled(true);
					rdbtnRandom.setEnabled(true);
					rdbtnAssign.setEnabled(true);
					slider.setEnabled(true);
					btnDefault.setEnabled(true);
					btnClear.setEnabled(true);

				}

			}
		});
		rdbtnLoadSetup.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {

			}
		});

		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				String ticker = "";
				ticker = (String.valueOf((float) slider.getValue() / 100));
				lblUncertainties.setText(ticker.substring(0, 3));
			}
		});

		btnDefault.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtTemptation.setText("5");
				txtPunishment.setText("1");
				txtReward.setText("3");
				txtSucker.setText("0");
				txtTournNum.setText("1");
				txtAdvanceCLimit.setText("100");
				txtAdvancedDLimit.setText("100");
				rdbtnRandom.setSelected(true);
				rdbtnAssign.setSelected(false);
				rdbtnLoadSetup.setSelected(false);
				slider.setValue(100);

			}
		});
		btnDefault.setBounds(214, 6, 90, 29);
		tbSetUp.add(btnDefault);

		btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtTemptation.setText("");
				txtPunishment.setText("");
				txtReward.setText("");
				txtSucker.setText("");
				txtTournNum.setText("");
				txtAdvanceCLimit.setText("");
				txtAdvancedDLimit.setText("");
				rdbtnRandom.setSelected(false);
				rdbtnAssign.setSelected(false);
				rdbtnLoadSetup.setSelected(false);

			}
		});
		btnClear.setBounds(308, 6, 90, 29);
		tbSetUp.add(btnClear);

		JPanel panel_2 = new JPanel();
		panel_2.setLayout(null);
		panel_2.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panel_2.setBounds(19, 187, 379, 66);
		tbSetUp.add(panel_2);

		JLabel lblNumberOfTournaments = new JLabel("Number of Tournaments");
		lblNumberOfTournaments.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNumberOfTournaments.setForeground(SystemColor.controlShadow);
		lblNumberOfTournaments.setBounds(6, 12, 197, 16);
		panel_2.add(lblNumberOfTournaments);

		txtTournNum = new JTextField();
		txtTournNum.setText("4");
		txtTournNum.setColumns(10);
		txtTournNum.setBounds(227, 6, 146, 28);
		panel_2.add(txtTournNum);

		slider.setValue(100);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(10);
		slider.setBounds(218, 40, 118, 20);
		panel_2.add(slider);

		lblUncertainties.setBounds(348, 32, 25, 28);
		panel_2.add(lblUncertainties);

		JLabel label_6 = new JLabel("Uncertainty Level");
		label_6.setHorizontalAlignment(SwingConstants.RIGHT);
		label_6.setForeground(SystemColor.controlShadow);
		label_6.setBounds(6, 40, 197, 16);
		panel_2.add(label_6);

		JPanel panel_3 = new JPanel();
		panel_3.setLayout(null);
		panel_3.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panel_3.setBounds(19, 265, 379, 94);
		tbSetUp.add(panel_3);

		JLabel lblInformationRequestLimit = new JLabel(
				"Information Request Limit");
		lblInformationRequestLimit.setHorizontalAlignment(SwingConstants.LEFT);
		lblInformationRequestLimit.setForeground(SystemColor.controlShadow);
		lblInformationRequestLimit.setBounds(6, 12, 176, 16);
		panel_3.add(lblInformationRequestLimit);

		JLabel lblAdvancedc = new JLabel("Advanced_C");
		lblAdvancedc.setHorizontalAlignment(SwingConstants.LEFT);
		lblAdvancedc.setForeground(SystemColor.controlShadow);
		lblAdvancedc.setBounds(161, 40, 86, 16);
		panel_3.add(lblAdvancedc);

		JLabel lblAdvancedd = new JLabel("Advanced_D");
		lblAdvancedd.setHorizontalAlignment(SwingConstants.LEFT);
		lblAdvancedd.setForeground(SystemColor.controlShadow);
		lblAdvancedd.setBounds(277, 40, 86, 16);
		panel_3.add(lblAdvancedd);

		rdbtnAssign.setBounds(6, 40, 86, 23);
		panel_3.add(rdbtnAssign);

		rdbtnRandom.setBounds(6, 62, 86, 23);
		panel_3.add(rdbtnRandom);

		txtAdvanceCLimit = new JTextField();
		txtAdvanceCLimit.setText("400");
		txtAdvanceCLimit.setColumns(10);
		txtAdvanceCLimit.setBounds(183, 60, 45, 28);
		panel_3.add(txtAdvanceCLimit);

		txtAdvancedDLimit = new JTextField();
		txtAdvancedDLimit.setText("400");
		txtAdvancedDLimit.setColumns(10);
		txtAdvancedDLimit.setBounds(287, 60, 45, 28);
		panel_3.add(txtAdvancedDLimit);

		rdbtnLoadSetup.setForeground(Color.RED);
		rdbtnLoadSetup.setBounds(19, 7, 183, 23);
		tbSetUp.add(rdbtnLoadSetup);

		JPanel tbModel = new JPanel();
		tabbedPane.addTab("Agents", null, tbModel, null);
		tbModel.setLayout(null);

		final JButton btnAgentDefault = new JButton("Default");
		final JButton btnAgentClear = new JButton("Clear");

		rdbtnLoadAg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (rdbtnLoadAg.isSelected()) {
					txtCNum.setEnabled(false);
					txtDNum.setEnabled(false);
					txtAdvancedCNum.setEnabled(false);
					txtAdvancedDNum.setEnabled(false);
					btnAgentDefault.setEnabled(false);
					btnAgentClear.setEnabled(false);
					cmbInfoReqApproach.setEnabled(false);
				} else {
					txtCNum.setEnabled(true);
					txtDNum.setEnabled(true);
					txtAdvancedCNum.setEnabled(true);
					txtAdvancedDNum.setEnabled(true);
					btnAgentDefault.setEnabled(true);
					btnAgentClear.setEnabled(true);
					cmbInfoReqApproach.setEnabled(true);
				}

			}
		});

		rdbtnLoadAg.setForeground(Color.RED);
		rdbtnLoadAg.setBounds(16, 18, 183, 23);
		tbModel.add(rdbtnLoadAg);

		btnAgentDefault.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				txtCNum.setText("10");
				txtDNum.setText("10");
				txtAdvancedCNum.setText("10");
				txtAdvancedDNum.setText("10");
				rdbtnLoadAg.setSelected(false);
			}
		});
		btnAgentDefault.setBounds(211, 17, 90, 29);
		tbModel.add(btnAgentDefault);

		btnAgentClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtCNum.setText("");
				txtDNum.setText("");
				txtAdvancedCNum.setText("");
				txtAdvancedDNum.setText("");
				rdbtnLoadAg.setSelected(false);

			}
		});
		btnAgentClear.setBounds(305, 17, 90, 29);
		tbModel.add(btnAgentClear);

		JPanel panel_4 = new JPanel();
		panel_4.setLayout(null);
		panel_4.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panel_4.setBounds(26, 66, 358, 189);
		tbModel.add(panel_4);

		JLabel lblCooperateall = new JLabel("Naive_Cooperator");
		lblCooperateall.setHorizontalAlignment(SwingConstants.RIGHT);
		lblCooperateall.setForeground(SystemColor.controlShadow);
		lblCooperateall.setBounds(16, 50, 188, 16);
		panel_4.add(lblCooperateall);

		JLabel lblDefectall = new JLabel("Naive_Defector");
		lblDefectall.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDefectall.setForeground(SystemColor.controlShadow);
		lblDefectall.setBounds(16, 84, 188, 16);
		panel_4.add(lblDefectall);

		JLabel lblAdvanced = new JLabel("Advanced_D");
		lblAdvanced.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAdvanced.setForeground(SystemColor.controlShadow);
		lblAdvanced.setBounds(16, 151, 188, 16);
		panel_4.add(lblAdvanced);

		JLabel lblAdvancedd_1 = new JLabel("Advanced_C");
		lblAdvancedd_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAdvancedd_1.setForeground(SystemColor.controlShadow);
		lblAdvancedd_1.setBounds(16, 118, 188, 16);
		panel_4.add(lblAdvancedd_1);

		txtCNum = new JTextField();
		txtCNum.setText("1");
		txtCNum.setColumns(10);
		txtCNum.setBounds(231, 44, 119, 28);
		panel_4.add(txtCNum);

		txtDNum = new JTextField();
		txtDNum.setText("1");
		txtDNum.setColumns(10);
		txtDNum.setBounds(231, 78, 119, 28);
		panel_4.add(txtDNum);

		txtAdvancedCNum = new JTextField();
		txtAdvancedCNum.setText("2");
		txtAdvancedCNum.setColumns(10);
		txtAdvancedCNum.setBounds(231, 112, 119, 28);
		panel_4.add(txtAdvancedCNum);

		txtAdvancedDNum = new JTextField();
		txtAdvancedDNum.setText("2");
		txtAdvancedDNum.setColumns(10);
		txtAdvancedDNum.setBounds(231, 147, 119, 28);
		panel_4.add(txtAdvancedDNum);

		JLabel lblStrategies = new JLabel("Strategies");
		lblStrategies.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		lblStrategies.setHorizontalAlignment(SwingConstants.RIGHT);
		lblStrategies.setForeground(new Color(199, 21, 133));
		lblStrategies.setBounds(16, 6, 188, 16);
		panel_4.add(lblStrategies);

		JLabel lblNumofplayers = new JLabel("Num Of Players");
		lblNumofplayers.setHorizontalAlignment(SwingConstants.LEFT);
		lblNumofplayers.setForeground(new Color(199, 21, 133));
		lblNumofplayers.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		lblNumofplayers.setBounds(234, 6, 116, 16);
		panel_4.add(lblNumofplayers);

		JPanel panel_5 = new JPanel();
		panel_5.setBounds(26, 273, 358, 75);
		tbModel.add(panel_5);
		panel_5.setLayout(null);
		panel_5.setBorder(new LineBorder(Color.LIGHT_GRAY));

		JLabel lblAgentsInfoRequest = new JLabel(
				"Agents' Info. Request Approach");
		lblAgentsInfoRequest.setHorizontalAlignment(SwingConstants.CENTER);
		lblAgentsInfoRequest.setForeground(SystemColor.controlShadow);
		lblAgentsInfoRequest.setBounds(6, 6, 335, 16);
		panel_5.add(lblAgentsInfoRequest);

		cmbInfoReqApproach.setModel(new DefaultComboBoxModel<String>(
				new String[] { "Opponent's First Action",
						"First Time Opponent Defected",
						"All Opponent's Past Actions",
						"Opponent's Past Action in Random Tournament",
						"Past Actions of other Agents against Opponent" }));
		cmbInfoReqApproach.setSelectedIndex(2);
		cmbInfoReqApproach.setBounds(6, 23, 346, 27);
		panel_5.add(cmbInfoReqApproach);

		JPanel tbDisplay = new JPanel();
		tabbedPane.addTab("Display", null, tbDisplay, null);
		tbDisplay.setLayout(null);

		final JTabbedPane tabbedPane_1 = new JTabbedPane(SwingConstants.TOP);
		tabbedPane_1.setBounds(6, 6, 405, 339);
		tbDisplay.add(tabbedPane_1);

		JPanel tbnLeaderboard = new JPanel();
		tabbedPane_1.addTab("Leaderboard", null, tbnLeaderboard, null);
		tbnLeaderboard.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 6, 372, 281);
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		tbnLeaderboard.add(scrollPane);
		txtLeaderBoard.setEditable(false);
		txtLeaderBoard.setForeground(new Color(105, 105, 105));

		scrollPane.setViewportView(txtLeaderBoard);

		JPanel tbnstats = new JPanel();
		tabbedPane_1.addTab("Statistics", null, tbnstats, null);
		tbnstats.setLayout(null);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_1
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setBounds(6, 6, 372, 281);
		tbnstats.add(scrollPane_1);
		txtStats.setEditable(false);
		txtStats.setForeground(new Color(105, 105, 105));

		scrollPane_1.setViewportView(txtStats);

		JPanel tbnGraph = new JPanel();
		tabbedPane_1.addTab("Graph", null, tbnGraph, null);
		tbnGraph.setLayout(null);

		final JButton btnLineChart = new JButton("Line Chart");
		btnLineChart.setEnabled(false);
		btnLineChart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String expIndex[] = { String.valueOf(cmbExpSel
						.getSelectedIndex()) };
				LineChart.main(expIndex);

			}
		});
		btnLineChart.setBounds(28, 111, 117, 66);
		tbnGraph.add(btnLineChart);

		final JButton btnBarChart = new JButton("Bar Chart");
		btnBarChart.setEnabled(false);
		btnBarChart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String expIndex[] = { String.valueOf(cmbExpSel
						.getSelectedIndex()) };
				BarChartx.main(expIndex);
			}
		});
		btnBarChart.setBounds(240, 111, 117, 66);
		tbnGraph.add(btnBarChart);

		final JButton btnAreaChart = new JButton("Area Chart");
		btnAreaChart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String expIndex[] = { String.valueOf(cmbExpSel
						.getSelectedIndex()) };
				AreaChart.main(expIndex);
			}
		});
		btnAreaChart.setEnabled(false);
		btnAreaChart.setBounds(28, 202, 117, 66);
		tbnGraph.add(btnAreaChart);

		cmbExpSel = new JComboBox<Object>();
		cmbExpSel.setEnabled(false);
		cmbExpSel.setBounds(28, 32, 324, 27);
		tbnGraph.add(cmbExpSel);

		JLabel lblSelectExperiment = new JLabel("Select Experiment :");
		lblSelectExperiment.setForeground(Color.DARK_GRAY);
		lblSelectExperiment.setBounds(38, 16, 152, 16);
		tbnGraph.add(lblSelectExperiment);

		tabbedPane.addChangeListener(new ChangeListener() { // add the Listener

					@Override
					public void stateChanged(ChangeEvent e) {
						String info = "";
						if (startSimulation) {
							if (tabbedPane.getSelectedIndex() == 3) // Index
																	// starts at
																	// 0, so
																	// Index 2 =
																	// Tab3
							{
								info = HIM.getExperimentResults(tabbedPane_1
										.getSelectedIndex());
								txtLeaderBoard.setText(info);
							}
						}
					}
				});

		// Actions for tabs under display clicked 
		tabbedPane_1.addChangeListener(new ChangeListener() { 
					@Override
					public void stateChanged(ChangeEvent e) {
						String info = "";
						if (startSimulation) {
							// Get leader board to observe players performance
							if (tabbedPane_1.getSelectedIndex() == 0) 
							{
								// print agents final scores

								info = HIM.getExperimentResults(tabbedPane_1
										.getSelectedIndex());
								txtLeaderBoard.setText(info);
							}

							// Get tournament Statistics
							if (tabbedPane_1.getSelectedIndex() == 1) {
								info = HIM.getExperimentResults(tabbedPane_1
										.getSelectedIndex());
								txtStats.setText(info);

							}

							// Get players performance as a graph
							if (tabbedPane_1.getSelectedIndex() == 2) {
								cmbExpSel.setEnabled(true);
								btnLineChart.setEnabled(true);
								btnBarChart.setEnabled(true);
								btnAreaChart.setEnabled(true);

							}

						}

					}
				});

	}
}
