package historicalInformationManager;

import gui.AreaChart;
import gui.BarChart;
import gui.BarChart3DDemo4;
import gui.BarChartx;
import gui.LineChart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.JOptionPane;

import agents.Agent;
import agents.AgentStrategies;
import setupManager.StrategySetupManager;

public class HIM {

	// Private Variables
	private static float experimentUncertaintyLevel;
	private static int totalNumOfTournament, currentExperimentIndex,
			totalNumOfAgents;
	private static String opponentPastInfo, chartsInfo = "",
			agentsTournamentStatistics = "", agentTournamentStats = "";
	private static double sum = 0, min = 0, max = 0;
	private static String[] experimentPayOff;
	private static String SIMULATION_LEADERBOARD_FILE = "HIR/SimLeaderBoard.csv";
	private static String SIMULATION_STATS_FILE = "HIR/SimStats.csv";
	private static String CHARTS_FILE = "HIR/chartInfo.csv";
	private static String TOURNAMENT_BOARD_FILE = "TB/TB.csv";
	private static String SIMULATION_LOG_FILE = "HIR/SimLog.csv";
	private static String FILE_NOT_FOUND = "File not found";
	private static String DUMMY = "Dummy";
	private static String experimentLeaderboard = "", tournamentBoardInfo = "",
			currentExperimentResults = "", requestLimitOptions;

	/**
	 * initialize method sets up the Historical Information Manager component
	 * and makes it ready to respond to request from other components in the
	 * software simulator
	 * 
	 * @param numOfTournament
	 *            : Number of tournaments in current experiment
	 * @param exp
	 *            : current experiment index
	 * @param uLevel
	 *            : experiment's uncertainty level
	 * @param agentsRequestLimit
	 *            : current experiment's request limit for agents
	 * @param numOfAgents
	 *            : Number of agents in current experiment
	 * 
	 */
	public static void initializeParameters(int exp, int numOfTournament,
			float uLevel, int[] agentsRequestLimit, int numOfAgents,
			String[] param, String requestLimitOption) {

		// Set up Parameters
		totalNumOfAgents = numOfAgents;
		HIR.agentsRequestLimit = agentsRequestLimit;
		requestLimitOptions = requestLimitOption;
		experimentUncertaintyLevel = uLevel;
		totalNumOfTournament = numOfTournament;
		currentExperimentIndex = exp;
		sum = 0;
		min = 0;
		max = 0;
		experimentPayOff = param;
		HIR.agentActionsDbase = new char[numOfTournament][totalNumOfAgents][totalNumOfAgents];
		HIR.agentActs = new String[totalNumOfAgents];

	}

	/**
	 * getExperimentResults accepts display request from the GUI component and
	 * based on the request returns the appropriate results stored in Historical
	 * Information Repository
	 * 
	 * @param requestOption
	 *            : Query from GUI component
	 * 
	 * @return requestInfo : Response given to GUI component
	 * 
	 */

	public static String getExperimentResults(int requestOption) {
		String requestInfo = "";

		switch (requestOption) {
		case 0:
			requestInfo = experimentLeaderboard;
			break;

		case 1:
			requestInfo = agentsTournamentStatistics;
			break;
		}

		return requestInfo;
	}

	/**
	 * applyUncertaintyLimit method applies the uncertainty limit on queried
	 * opponent past information
	 * 
	 * @param opponentPastInfo
	 *            : Information on opponent's past actions before
	 *            uncertainty limit is applied
	 * @return pastInfoAfterUncertainty : Requested Information on opponent's
	 *         past actions after uncertainty limit application
	 */
	private static String applyUncertaintyLimit(String opponentPastInfo) {
		double currentUncertaintyLimit = (double) experimentUncertaintyLevel;
		String pastInfoAfterUncertainty = opponentPastInfo.substring(0,
				(int) ((opponentPastInfo.length()) * currentUncertaintyLimit));

		return pastInfoAfterUncertainty;
	}

	/**
	 * updateAgActionsInReposiory method updates all agents actions after every
	 * round of matches after invocation by round manager
	 * 
	 * @param currentTournamentIndex
	 *            : Current Tournament
	 * @param requestingAgentID
	 *            : Agent id
	 * @param opponentID
	 *            : Opponent Agent id
	 * @param agentsAction
	 *            : Actions of both agent and opponent
	 */
	public static void updateAgActionsInReposiory(int currentTournamentIndex,
			int requestingAgentID, int opponentID, char[] agentsAction) {

		// Parameter variables
		char agentMatchAct, opponentMatchAct;
		agentMatchAct = agentsAction[0];
		opponentMatchAct = agentsAction[1];

		// Store agents' actions in HIR based on ID
		HIR.agentActionsDbase[currentTournamentIndex][opponentID][requestingAgentID] = agentMatchAct;
		HIR.agentActionsDbase[currentTournamentIndex][requestingAgentID][opponentID] = opponentMatchAct;

		// Store agents' actions in HIR based on time taken
		if (HIR.agentActs[requestingAgentID] == null)
			HIR.agentActs[requestingAgentID] = String.valueOf(agentMatchAct);
		else
			HIR.agentActs[requestingAgentID] = HIR.agentActs[requestingAgentID]
					+ String.valueOf(agentMatchAct);

		if (HIR.agentActs[opponentID] == null)
			HIR.agentActs[opponentID] = String.valueOf(opponentMatchAct);

		else
			HIR.agentActs[opponentID] = HIR.agentActs[opponentID]
					+ String.valueOf(opponentMatchAct);
	}

	/**
	 * displayAgentsTournamentPerformance method performs the arrangement of the
	 * player's tournament standing at the end of each tournament in a leader
	 * board format showing the following;
	 * <ul>
	 * <li>player name / id
	 * <li>player's chosen strategy at the start of the tournament
	 * <li>Agent's final score </u>
	 * 
	 * @return experimentLeaderboard : Agents actions, strategy and pay-offs
	 * 
	 * @throws IOException
	 * 
	 */
	public static String displayAgentsTournamentPerformance(int currentExpIndex)
			throws IOException {
		experimentLeaderboard = experimentLeaderboard + "\nExperiment "
				+ currentExpIndex
				+ " Simulation Leaderboard\n=========================\n";
		currentExperimentResults = currentExperimentResults + "\nExperiment "
				+ currentExpIndex + "\n==========================\n";
		currentExperimentResults = currentExperimentResults
				+ "Number of Tournament : ," + totalNumOfTournament + "\n";
		currentExperimentResults = currentExperimentResults + "Payoffs : ,T = "
				+ experimentPayOff[0] + ", R = " + experimentPayOff[1]
				+ ", P = " + experimentPayOff[2] + ", S = "
				+ experimentPayOff[3] + "\n";
		currentExperimentResults = currentExperimentResults
				+ "Uncertainty Level : ," + experimentUncertaintyLevel + "\n";
		currentExperimentResults = currentExperimentResults
				+ "Request Limit : ," + requestLimitOptions + "\n";
		currentExperimentResults = currentExperimentResults
				+ "Simulation Leaderboard\n------------------------\n";

		HIR.data = new String[HIR.agentsRequestLimit.length][3];
		String[] Strategy = StrategySetupManager.Strategies;

		for (int i = 0; i < (Strategy.length); i++) {
			HIR.data[i][0] = "Agent " + (i + 1);
			HIR.data[i][1] = Strategy[i];
			HIR.data[i][2] = String.valueOf((Agent.agentScores[i]));
		}

		// Arrange performance based on total scores
		Arrays.sort(HIR.data, new Comparator<String[]>() {
			@Override
			public int compare(final String[] entry1, final String[] entry2) {
				final String time1 = entry1[2];
				final String time2 = entry2[2];
				return Float.valueOf(time1).compareTo(Float.valueOf(time2));
			}
		});

		for (int i = HIR.data.length - 1; i >= 0; i--) {
			if (HIR.data[i][1] != DUMMY) {
				experimentLeaderboard = experimentLeaderboard + HIR.data[i][0]
						+ "\t" + HIR.data[i][1] + "\t" + HIR.data[i][2]
						+ "\n \n";
				currentExperimentResults = currentExperimentResults
						+ HIR.data[i][0] + "," + HIR.data[i][1] + ","
						+ HIR.data[i][2] + "\n \n";
				;
			}
		}

		updateHistoricalRepository(currentExperimentResults);

		return experimentLeaderboard;

	}

	/**
	 * updateHistoricalRepository performs the function of saving the
	 * currentExperimentResults from every experiment into a text file.
	 * 
	 * @param leaderboard2
	 *            : updated leadership board
	 * @throws IOException
	 *             exception
	 * 
	 */
	private static void updateHistoricalRepository(
			String currentExperimentResults) throws IOException {

		Files.write(Paths.get(SIMULATION_LEADERBOARD_FILE),
				currentExperimentResults.getBytes());
	}

	/**
	 * displayAgentsTournamentStats method develops a statistics of players
	 * performance after every tournament in the current experiment.Statistics
	 * will indicate the following;
	 * <ul>
	 * <li>player name / id
	 * <li>player's chosen strategy at the start of the tournament
	 * <li>Agent's final tournament payoff
	 * <li>Tournament Statistics [Maximum, Average, Minimum payoff]
	 * </ul>
	 * 
	 * @throws IOException
	 * 
	 * 
	 */
	public static void displayAgentsTournamentStats(int currentTournamentIndex)
			throws IOException {

		sum = 0;
		agentsTournamentStatistics = agentsTournamentStatistics
				+ "\n\n\nTounament " + (currentTournamentIndex + 1)
				+ "\n-----------------------\n";
		agentTournamentStats = agentsTournamentStatistics + "\n\n\nTounament "
				+ (currentTournamentIndex + 1) + "\n-----------------------\n";
		HIR.data = new String[HIR.agentsRequestLimit.length][3];
		String[] Strategy = StrategySetupManager.Strategies;

		// update agents' tournament information
		for (int i = 0; i < (Strategy.length); i++) {
			HIR.data[i][0] = "Agent " + (i + 1);
			HIR.data[i][1] = Strategy[i];
			HIR.data[i][2] = String.valueOf((Agent.agentScores[i]));

			// store parameters in chart for later display
			writeChartDataset(currentTournamentIndex, i, HIR.data);
		}

		// Arrange performance based on total Experiment/Tournament scores
		Arrays.sort(HIR.data, new Comparator<String[]>() {
			@Override
			public int compare(final String[] entry1, final String[] entry2) {
				final String time1 = entry1[2];
				final String time2 = entry2[2];
				return Float.valueOf(time1).compareTo(Float.valueOf(time2));
			}
		});

		// Arrange information in descending order and calculate statistics
		for (int i = HIR.data.length - 1; i >= 0; i--) {
			if (HIR.data[i][1] != DUMMY) {
				agentsTournamentStatistics = agentsTournamentStatistics
						+ HIR.data[i][0] + "\t" + HIR.data[i][1] + "\t"
						+ HIR.data[i][2] + "\n";
				agentTournamentStats = agentTournamentStats + HIR.data[i][0]
						+ "," + HIR.data[i][1] + "," + HIR.data[i][2] + "\n";

				sum = sum + Double.parseDouble(HIR.data[i][2]);
				min = Double.parseDouble(HIR.data[0][2]);
				max = Double.parseDouble(HIR.data[(HIR.data.length - 1)][2]);

			}
		}

		agentsTournamentStatistics = agentsTournamentStatistics + "\n"
				+ "Statistics \n Min : " + min + "\nAverage : "
				+ (sum / HIR.data.length) + "\nMax : " + max;
		agentTournamentStats = agentTournamentStats + "\n"
				+ "Statistics \n Min : " + "," + min + "\nAverage : " + ","
				+ (sum / HIR.data.length) + "\nMax : " + "," + max;

		// save Stats
		writeTournamentStatsToFile(agentTournamentStats);
	}

	/**
	 * writeChartDataset stores chart dataset to file for later use in making
	 * different charts
	 * 
	 * @param currentTournamentIndex
	 *            : Current tournament number
	 * @param i
	 *            : agent ID
	 * @param data
	 *            : Stored Agent's id, and total experiment payoffs
	 * @throws IOException
	 */
	private static void writeChartDataset(int currentTournamentIndex, int i,
			String[][] data) throws IOException {
		String x_axis = "T " + (currentTournamentIndex + 1);
		chartsInfo = chartsInfo + String.valueOf(currentExperimentIndex) + ","
				+ HIR.data[i][2] + "," + HIR.data[i][0] + "("+ HIR.data[i][1] + ")" +" ," + HIR.data[i][1] + "," + x_axis + "\n";

		Files.write(Paths.get(CHARTS_FILE), chartsInfo.getBytes());
	}

	/**
	 * writeTournamentStatsToFil saves statistcs of agents' performance in a
	 * text file.
	 * 
	 * @param leaderboard2
	 *            : updated leadership board
	 * 
	 * @throws IOException
	 * 
	 */
	private static void writeTournamentStatsToFile(String agentTournamentStats)
			throws IOException {

		Files.write(Paths.get(SIMULATION_STATS_FILE),
				agentTournamentStats.getBytes());
	}

	/**
	 * requestOppPastInfo accepts information request about an opponent from the
	 * agent and based on limitations return the requested info after applying uncertainty
	 * limit
	 * 
	 * @param requestingAgentID
	 *            : Experiment identity for the agent
	 * @param opponentID
	 *            : Experiment identity for the opponent
	 * @param option
	 *            : Approach for information request
	 * 
	 * @return opponentPastInfo : Retrieved information on opponent past action
	 * 
	 */
	public static String requestOppPastInfo(int requestingAgentID,
			int opponentID, int requestOption) {
		String opponentPastInfo = "";

		// If agent has exceeded limit
		if (HIR.agentsRequestLimit[requestingAgentID] == 0) {
			opponentPastInfo = "";
			AgentStrategies.infoAcquired = false;
		}

		// Honour request based on type of info reqired
		else {
			AgentStrategies.infoAcquired = true;
			switch (requestOption) {

			case 0:
				opponentPastInfo = getOppFirstAction(requestingAgentID,
						opponentID);
				break;

			case 1:
				opponentPastInfo = getOpponentFirstDefection(requestingAgentID,
						opponentID);
				break;

			case 2:
				opponentPastInfo = getOpponentPastInfo(requestingAgentID,
						opponentID);
				break;

			case 3:
				opponentPastInfo = getOpponentActionsInRandomTournament(
						requestingAgentID, opponentID);
				break;

			case 4:
				opponentPastInfo = get2ndLevelAction(requestingAgentID,
						opponentID);
				break;
			}

			HIR.agentsRequestLimit[requestingAgentID] = HIR.agentsRequestLimit[requestingAgentID] - 1;
		}
		return opponentPastInfo;
	}

	/**
	 * get2ndLevelAction method returns past actions of the secondary level
	 * opponents that were taken against current opponent.
	 * 
	 * @param requestingAgentID
	 *            : Experiment identity for the agent
	 * @param opponentID
	 *            : Experiment identity for the opponent
	 * @return pastInfoAfterUncertainty : past actions of all previous opponents
	 *         against current opponent after uncertainty limit has been applied
	 */
	private static String get2ndLevelAction(int requestingAgentID,
			int opponentID) {

		String opponentPastInfo = "", pastInfoAfterUncertainty = "";

		for (int i = 0; i < totalNumOfTournament; i++) {
			for (int j = 0; j < totalNumOfAgents; j++)
				opponentPastInfo = opponentPastInfo
						+ HIR.agentActionsDbase[i][opponentID][j];
		}
		pastInfoAfterUncertainty = applyUncertaintyLimit(opponentPastInfo);
		return pastInfoAfterUncertainty;
	}

	/**
	 * getOppFirstAction method returns the first action of current opponent
	 * 
	 * @param requestingAgentID
	 *            : Experiment identity for the agent
	 * @param opponentID
	 *            : Experiment identity for the opponent
	 * 
	 * @return pastInfoAfterUncertainty : opponent past actions after
	 *         uncertainty limit has been applied
	 */
	private static String getOppFirstAction(int requestingAgentID,
			int opponentID) {

		String pastInfoAfterUncertainty = "";

		if (HIR.agentActs[opponentID] != null) {
			opponentPastInfo = String.valueOf(HIR.agentActs[opponentID]
					.substring(0, 1));
			pastInfoAfterUncertainty = applyUncertaintyLimit(opponentPastInfo);

		}
		return pastInfoAfterUncertainty;
	}

	
	/**
	 * getOpponentPastInfo method returns all past actions of the current
	 * opponent
	 * 
	 * @param requestingAgentID
	 *            : Experiment identity for the agent
	 * @param opponentID
	 *            : Experiment identity for the opponent
	 * 
	 * @return pastInfoAfterUncertainty : opponent past actions after
	 *         uncertainty limit has been applied
	 */
	private static String getOpponentPastInfo(int requestingAgentID,
			int opponentID) {
		String pastInfoAfterUncertainty = "";
		if (HIR.agentActs[opponentID] != null) {
			opponentPastInfo = String.valueOf(HIR.agentActs[opponentID]);
			//JOptionPane.showMessageDialog(null,(opponentID + 1) + " has played : "
				//	+ opponentPastInfo);
			pastInfoAfterUncertainty = applyUncertaintyLimit(opponentPastInfo);
		}

		return pastInfoAfterUncertainty;

	}

	/**
	 * getOpponentFirstDefection method returns the first time it defected
	 * 
	 * @param requestingAgentID
	 *            : Experiment identity for the agent
	 * @param opponentID
	 *            : Experiment identity for the opponent
	 * 
	 * @return pastInfoAfterUncertainty : opponent past actions after
	 *         uncertainty limit has been applied
	 */
	private static String getOpponentFirstDefection(int requestingAgentID,
			int opponentID) {

		String oppActions = "";
		String pastInfoAfterUncertainty = "";

		oppActions = HIR.agentActs[opponentID];

		// Determine the first time opponent defected
		for (int i = 0; i < oppActions.length(); i++) {

			if (oppActions.charAt(i) == 'D') {
				opponentPastInfo = String.valueOf(i);
				break;
			}

			else {
				opponentPastInfo = "0";
			}

		}
		pastInfoAfterUncertainty = applyUncertaintyLimit(opponentPastInfo);

		return pastInfoAfterUncertainty;
	}

	
	/**
	 * getOpponentActionsInRandomTournament method returns past actions of the
	 * opponent from a randomly chosen tournament
	 * 
	 * @param requestingAgentID
	 *            : Experiment identity for the agent
	 * @param opponentID
	 *            : Experiment identity for the opponent
	 * 
	 * @return pastInfoAfterUncertainty opponent past actions after uncertainty
	 *         limit has been applied
	 */
	private static String getOpponentActionsInRandomTournament(
			int requestingAgentID, int opponentID) {
		String pastInfoAfterUncertainty = "";

		// Request agent to submit random tournament index
		int randomTournamentIndex = Agent.requestRandomTournament();
		
		// Get past action of opponent in tournament
		for (int j = 0; j < totalNumOfAgents; j++) {
			if (opponentPastInfo == null)
				opponentPastInfo = String
						.valueOf(HIR.agentActionsDbase[randomTournamentIndex][j][opponentID]);
			else
				opponentPastInfo = opponentPastInfo
						+ HIR.agentActionsDbase[randomTournamentIndex][j][opponentID];
		}

		pastInfoAfterUncertainty = applyUncertaintyLimit(opponentPastInfo);

		return pastInfoAfterUncertainty;
	}

	
	/**
	 * startExp method creates a heading for the current experiment to be used
	 * for reporting in the statistics log
	 * 
	 * @param currentExpIndex
	 *            : current Experiment index for statistical reporting
	 */
	public static void startExp(int currentExpIndex) {

		agentsTournamentStatistics = agentsTournamentStatistics
				+ "\n\nEXPERIMENT " + (currentExpIndex)
				+ "\n-----------------------\n";
		agentTournamentStats = agentsTournamentStatistics + "\n\nEXPERIMENT "
				+ (currentExpIndex) + "\n-----------------------\n";
	}

	
	/**
	 * updateLog method after every match reads from the Tournament Board and
	 * updates the SimLog data structure in HIR
	 * 
	 * @param tournamentBoardInfo
	 *            : Current information stored on TB by the Simulation Manager
	 * 
	 * @throws IOException
	 *             : Throw an input output exception when SimLog file is not
	 *             found
	 */
	public static void updateLog(String tournamentBoardInfo) throws IOException {

		String expTitle = readTB();

		tournamentBoardInfo = tournamentBoardInfo + expTitle + "\n";
		Files.write(Paths.get(SIMULATION_LOG_FILE),
				tournamentBoardInfo.getBytes());

	}

	/**
	 * readTB method locates the Tournament Board and reads all information
	 * stored on it by the simulation manager.
	 * 
	 * Modified from original code [https://www.mkyong.com/java/how-to-read-file
	 * -from-java-bufferedreader-example/ ]
	 * 
	 * @return splited : All information read from TB
	 */
	private static String readTB() {
		String boardInfo = "";
		
		try {
			File f = new File(TOURNAMENT_BOARD_FILE);
			BufferedReader b = new BufferedReader(new FileReader(f));
			String readLine = "";
			readLine = b.readLine();
			while ((readLine = b.readLine()) != null) {
				boardInfo = readLine;

			}
			b.close();
		} catch (IOException err) {
			JOptionPane.showMessageDialog(null, FILE_NOT_FOUND);
		}
		return boardInfo;

	}

	/**
	 * getDataset reads all the information necessary to make a graph depending
	 * on the experiment query from GUI component
	 * 
	 * @param selectedExperimentIndex
	 *            : index for experiment for which dataset are required to make
	 *            a graph.
	 */
	public static void getDataset(int selectedExperimentIndex) {
		
		ArrayList<String> myAgentStrategies = new ArrayList<String>(Arrays.asList(StrategySetupManager.Strategies));
		BarChart.agentStrategies = myAgentStrategies;
		BarChart3DDemo4.agentStrategies = myAgentStrategies;

		readLines(selectedExperimentIndex);

	}
	


	/**
	 * readLines method locates the chartInfo file in HIR and reads all
	 * information for the requested experiment index and then returns to
	 * getDataset method
	 * 
	 * Modified from original code [https://www.mkyong.com/java/how-to-read-file
	 * -from-java-bufferedreader-example/ ]
	 * 
	 * @param currentExperimentIndex
	 *            : Dataset info of requested experiment stored in HIR
	 * @return splited : All information read from HIR/chartInfo required for
	 *         making graphs
	 */
	private static String[][] readLines(int currentExperimentIndex) {

		String[] chartDataset = null;
		try {

			File f = new File(CHARTS_FILE);

			BufferedReader b = new BufferedReader(new FileReader(f));

			String readLine = "";
		//	readLine = b.readLine();
			while ((readLine = b.readLine()) != null) {

				chartDataset = readLine.split(",");

				if (chartDataset[0].equalsIgnoreCase(String
						.valueOf((currentExperimentIndex + 1)))) {
					LineChart.dataset.addValue(
							Float.parseFloat(chartDataset[1]), chartDataset[2],
							chartDataset[4]);
					BarChart.dataset.addValue(
							Float.parseFloat(chartDataset[1]), chartDataset[2],
							chartDataset[4]);

					BarChart3DDemo4.dataset.addValue(
							Float.parseFloat(chartDataset[1]), chartDataset[2],
							chartDataset[4]);
					AreaChart.dataset.addValue(
							Float.parseFloat(chartDataset[1]), chartDataset[2],
							chartDataset[4]);

				}
			}
			
			b.close();
		} catch (IOException err) {
			JOptionPane.showMessageDialog(null, FILE_NOT_FOUND);
		}

		return null;
	}
	
	
	/**
	 * The clarChartInfo method Cleans up the chartsInfo file 
	 * to begin a new set of experiments
	 * @throws FileNotFoundException 
	 * 
	 */
	public static void clearChartInfo() throws FileNotFoundException {
		chartsInfo = "";
		PrintWriter writer = new PrintWriter(CHARTS_FILE);
		writer.print("");
		writer.close();
		
		
	}

}

