package historicalInformationManager;

import gui.AreaChart;
import gui.BarChart;
import gui.LineChart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.JOptionPane;

import agents.Agent;
import agents.AgentStrategies;
import setupManager.StrategySetupManager;

public class HIM {

	/* Private Variables */
	private static float experimentUncertaintyLevel;
	private static int totalNumOfTournament, currentExperimentIndex;
	private static int totalNumOfAgents;
	private static String opponentPastInfo, chartsInfo = "";
	private static String agentsTournamentStatistics = "", agentTournamentStats = "";
	private static double sum = 0, min = 0, max = 0;
	private static String[] experimentPayOff;
	private static String experimentLeaderboard = "", tournamentBoardInfo = "", currentExperimentResults = "",
			requestLimitOptions;

	/**
	 * Initialize method sets up the Historical Information Manager component
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

	public static void initialize(int exp, int numOfTournament, float uLevel,
			int[] agentsRequestLimit, int numOfAgents, String[] param,
			String requestLimitOption) {

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
	 * requestTournamentResults accepts display request from the GUI component and based
	 * on the request returns the appropriate results stored in Historical Information
	 * Repository
	 * 
	 * @param requestOption
	 *            : Query from GUI component
	 * 
	 * @return requestInfo : Response given to GUI component
	 * 
	 */

	public static String getExperimentResults(int requestOption) {
		String requestInfo = "";
		
		switch(requestOption){
		case 0:
			requestInfo = experimentLeaderboard; 
			break;
			
		case 1 :
			requestInfo = agentsTournamentStatistics;
		}
		
		return requestInfo;
	}

	/**
	 * getUncertaintyLimit method applies the uncertainty limit on queried
	 * opponent past information
	 * 
	 * @param opponentPastInfo
	 *            : Requestion Information on opponent's past actions before
	 *            uncertainty limit is applied
	 * @return pastInfoAfterUncertainty : Requested Information on opponent's past actions after
	 *         uncertainty limit application
	 */

	private static String applyUncertaintyLimit(String opponentPastInfo) {

		
		double currentUncertaintyLimit = experimentUncertaintyLevel;

		String pastInfoAfterUncertainty = opponentPastInfo.substring(0,
				(int) ((opponentPastInfo.length()) * currentUncertaintyLimit));

		return pastInfoAfterUncertainty;
	}

	/**
	 * updateAgActionsInReposiory method updates all agents actions after every round of
	 * matches
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
	public static void updateAgActionsInReposiory(int currentTournamentIndex, int requestingAgentID, int opponentID,
			char[] agentsAction) {

		// Initialize Parameters 
		char actionA, actionB;
		actionA = agentsAction[0]; 
		actionB = agentsAction[1]; 
		
		// Store match actions in HIR based on agents IDs for easy response
		HIR.agentActionsDbase[currentTournamentIndex][opponentID][requestingAgentID] = actionA;
		HIR.agentActionsDbase[currentTournamentIndex][requestingAgentID][opponentID] = actionB;

		// Store match actions in HIR in time order for easy query response 
		if (HIR.agentActs[requestingAgentID] == null)
			HIR.agentActs[requestingAgentID] = String.valueOf(actionA);
		else
			HIR.agentActs[requestingAgentID] = HIR.agentActs[requestingAgentID]
					+ String.valueOf(actionA);

		if (HIR.agentActs[opponentID] == null)
			HIR.agentActs[opponentID] = String.valueOf(actionB);

		else
			HIR.agentActs[opponentID] = HIR.agentActs[opponentID]
					+ String.valueOf(actionB);
	}

	/**
	 * getTournamentStand method performs the arrangement of the player's
	 * tournament standing at the end of each tournament in a leader board format
	 * showing the following;
	 * <ul>
	 * <li>player name / id
	 * <li>player's chosen strategy at the start of the tournament
	 * <li>Player's final score </u>
	 * 
	 * @return experimentLeaderboard : Agents actions, strategy and pay-offs
	 * 
	 * @throws IOException
	 * 
	 */

	public static String displayAgentsTournamentPerformance(int ExpNum) throws IOException {
		experimentLeaderboard = experimentLeaderboard + "\nExperiment " + ExpNum
				+ " Simulation Leaderboard\n=========================\n";
		currentExperimentResults = currentExperimentResults + "\nExperiment " + ExpNum
				+ "\n==========================\n";
		currentExperimentResults = currentExperimentResults + "Number of Tournament : ," + totalNumOfTournament + "\n";
		currentExperimentResults = currentExperimentResults + "Payoffs : ,T = " + experimentPayOff[0] + ", R = "
				+ experimentPayOff[1] + ", P = " + experimentPayOff[2] + ", S = "
				+ experimentPayOff[3] + "\n";
		currentExperimentResults = currentExperimentResults + "Uncertainty Level : ," + experimentUncertaintyLevel + "\n";
		currentExperimentResults = currentExperimentResults + "Request Limit : ," + requestLimitOptions + "\n";
		currentExperimentResults = currentExperimentResults
				+ "Simulation Leaderboard\n------------------------\n";

		HIR.data = new String[HIR.agentsRequestLimit.length][3];
		String[] Strategy = StrategySetupManager.Strategies;

		
		 // Create a 3-dimensional array to store experiment performance
		for (int i = 0; i < (Strategy.length); i++) {
			HIR.data[i][0] = "Player " + (i + 1);
			HIR.data[i][1] = Strategy[i];
			HIR.data[i][2] = String.valueOf((Agent.agentScores[i]));
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

		for (int i = HIR.data.length - 1; i >= 0; i--) {
			if (HIR.data[i][1] != "Dummy") {
				System.out.println(HIR.data[i][0] + "\t" + HIR.data[i][1]
						+ "\t" + HIR.data[i][2] + "\t");
				experimentLeaderboard = experimentLeaderboard + HIR.data[i][0] + "\t"
						+ HIR.data[i][1] + "\t" + HIR.data[i][2] + "\n \n";
				currentExperimentResults = currentExperimentResults + HIR.data[i][0] + "," + HIR.data[i][1]
						+ "," + HIR.data[i][2] + "\n \n";
				;
			}
		}

		updateHistoricalRepository(currentExperimentResults);

		return experimentLeaderboard;

	}

	/**
	 * updateHistoricalRepository performs the function of saving the currentExperimentResults from
	 * every experiment into a text file.
	 * 
	 * @param leaderboard2
	 *            : updated leadership board
	 * @throws IOException
	 *             exception
	 * 
	 */
	private static void updateHistoricalRepository(String currentExperimentResults) throws IOException {

		Files.write(Paths.get("HIR/SimLeaderBoard.csv"),
				currentExperimentResults.getBytes());
	}

	/**
	 * displayAgentsTournamentStats methoddevelops a statistics of players performance
	 * after every tournament in the current experiment.Statistics will indicate
	 * the following;
	 * <ul>
	 * <li>player name / id
	 * <li>player's chosen strategy at the start of the tournament
	 * <li>Player's final tournament payoff
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
		agentsTournamentStatistics = agentsTournamentStatistics + "\n\n\nTounament " + (currentTournamentIndex + 1)
				+ "\n-----------------------\n";
		agentTournamentStats = agentsTournamentStatistics + "\n\n\nTounament " + (currentTournamentIndex + 1)
				+ "\n-----------------------\n";
		
		HIR.data = new String[HIR.agentsRequestLimit.length][3];
		String[] Strategy = StrategySetupManager.Strategies;

		 // Create a 3-dimensional array to store agents' tournament performance
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

		// Store the information in descending order
		for (int i = HIR.data.length - 1; i >= 0; i--) {
			if (HIR.data[i][1] != "Dummy") {
				System.out.println(HIR.data[i][0] + "\t" + HIR.data[i][1]
						+ "\t" + HIR.data[i][2] + "\t");
				agentsTournamentStatistics = agentsTournamentStatistics + HIR.data[i][0] + "\t" + HIR.data[i][1] + "\t"
						+ HIR.data[i][2] + "\n";
				agentTournamentStats = agentTournamentStats + HIR.data[i][0] + ","
						+ HIR.data[i][1] + "," + HIR.data[i][2] + "\n";

				sum = sum + Double.parseDouble(HIR.data[i][2]);
				min = Double.parseDouble(HIR.data[0][2]);
				max = Double.parseDouble(HIR.data[(HIR.data.length - 1)][2]);

			}
		}

		agentsTournamentStatistics = agentsTournamentStatistics + "\n" + "Statistics \n Min : " + min + "\nAverage : "
				+ (sum / HIR.data.length) + "\nMax : " + max;
		agentTournamentStats = agentTournamentStats + "\n" + "Statistics \n Min : " + "," + min
				+ "\nAverage : " + "," + (sum / HIR.data.length) + "\nMax : "
				+ "," + max;

		// saveStats(agentsTournamentStatistics);
		writeTournamentStatsToFile(agentTournamentStats);

	}
	
	
	/**
	 * writeChartDataset stores chart dataset to file for later use in making different charts
	 * @param currentTournamentIndex	:	Current tournament number
	 * @param i	: agent ID
	 * @param data :	Stored Agent's id, and total experiment payoffs
	 * @throws IOException
	 */
	private static void writeChartDataset(int currentTournamentIndex, int i,
			String[][] data) throws IOException {
		String x_axis = "T " + (currentTournamentIndex + 1);
		chartsInfo = chartsInfo + String.valueOf(currentExperimentIndex) + "," + HIR.data[i][2]
				+ "," + HIR.data[i][0] + "," + x_axis + "\n";

		Files.write(Paths.get("HIR/chartInfo.csv"), chartsInfo.getBytes());
	}

	
	
	/**
	 * saveStats saves statistcs of agents' performance in a text file.
	 * 
	 * @param leaderboard2
	 *            : updated leadership board
	 *            
	 * @throws IOException
	 *             
	 */

	private static void writeTournamentStatsToFile(String agentTournamentStats) throws IOException {

		Files.write(Paths.get("HIR/SimStats.csv"), agentTournamentStats.getBytes());
	}

	/**
	 * requestOppActions accepts information request about an opponent from the
	 * agent and based on limitations return the requested info
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
	public static String requestOppPastInfo(int requestingAgentID, int opponentID, int requestOption) {
		String opponentPastInfo = "";
		switch (requestOption) {
		case 0:
			opponentPastInfo = getOppFirstAction(requestingAgentID, opponentID);
			break;

		case 1:
			opponentPastInfo = getOpponentFirstDefection(requestingAgentID, opponentID);
			break;

		case 2:
			opponentPastInfo = getOpponentPastInfo(requestingAgentID, opponentID);
			break;

		case 3:
			opponentPastInfo = getOpponentActionsInRandomTournament(requestingAgentID, opponentID);
			break;

		case 4:
			opponentPastInfo = get2ndLevelAction(requestingAgentID, opponentID);
			break;
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
	 * @return pastInfoAfterUncertainty : past actions of all previous opponents against current
	 *         opponent after uncertainty limit has been applied
	 */

	private static String get2ndLevelAction(int requestingAgentID, int opponentID) {

		// Set up Parameters
		String opponentPastInfo = "";
		String pastInfoAfterUncertainty = "";

		
		if (HIR.agentsRequestLimit[requestingAgentID] == 0) {
			pastInfoAfterUncertainty = "";
			AgentStrategies.infoAcquired = false; // Deny info request
			System.out.println("No more Info");

		}

		
		else {

			AgentStrategies.infoAcquired = true;
			
			// Stores past actions of those who played against opponent
			for (int i = 0; i < totalNumOfTournament; i++) {
				for (int j = 0; j < totalNumOfAgents; j++)
					opponentPastInfo = opponentPastInfo
							+ HIR.agentActionsDbase[i][opponentID][j];
			}
			pastInfoAfterUncertainty = applyUncertaintyLimit(opponentPastInfo);
			HIR.agentsRequestLimit[requestingAgentID] = HIR.agentsRequestLimit[requestingAgentID] - 1;

			System.out.println(opponentPastInfo);
			System.out.println(pastInfoAfterUncertainty);
		}

		//return opponentPastInfo;
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
	 * @return pastInfoAfterUncertainty : opponent past actions after uncertainty limit has been
	 *         applied
	 */

	private static String getOppFirstAction(int requestingAgentID, int opponentID) {

		String pastInfoAfterUncertainty = "";

		
		if (HIR.agentsRequestLimit[requestingAgentID] == 0) {
			opponentPastInfo = "";
			AgentStrategies.infoAcquired = false; 
			System.out.println("No more Info");

		}

		
		else {
			AgentStrategies.infoAcquired = true;
			if (HIR.agentActs[opponentID] != null) {
				opponentPastInfo = String.valueOf(HIR.agentActs[opponentID]
						.substring(0, 1));

				pastInfoAfterUncertainty = applyUncertaintyLimit(opponentPastInfo);

				HIR.agentsRequestLimit[requestingAgentID] = HIR.agentsRequestLimit[requestingAgentID] - 1;

				System.out.println(opponentPastInfo);
				System.out.println(pastInfoAfterUncertainty);

			}

		}
		return pastInfoAfterUncertainty;

	}

	/**
	 * getOpponentPastInfo method returns all past actions of the current opponent
	 * 
	 * @param requestingAgentID
	 *            : Experiment identity for the agent
	 * @param opponentID
	 *            : Experiment identity for the opponent
	 * 
	 * @return pastInfoAfterUncertainty : opponent past actions after uncertainty limit has been
	 *         applied
	 */

	private static String getOpponentPastInfo(int requestingAgentID, int opponentID) {
		String pastInfoAfterUncertainty = "";

		if (HIR.agentsRequestLimit[requestingAgentID] == 0) {
			pastInfoAfterUncertainty = "";
			AgentStrategies.infoAcquired = false;
			System.out.println("No more Info");

		}

		else {
			AgentStrategies.infoAcquired = true;
			for (int i = 0; i < totalNumOfTournament; i++) {
				for (int j = 0; j < totalNumOfAgents; j++) {
					if (opponentPastInfo == null)
						opponentPastInfo = String
								.valueOf(HIR.agentActionsDbase[i][j][opponentID]);
					else
						opponentPastInfo = opponentPastInfo
								+ HIR.agentActionsDbase[i][j][opponentID];
				}
			}

			pastInfoAfterUncertainty = applyUncertaintyLimit(opponentPastInfo);
			HIR.agentsRequestLimit[requestingAgentID] = HIR.agentsRequestLimit[requestingAgentID] - 1;
			System.out.println(opponentPastInfo);
			System.out.println(pastInfoAfterUncertainty);

		}

		return pastInfoAfterUncertainty;

	}

	/**
	 * getOpponentFirstDefection method returns the first time current opponent defected
	 * 
	 * @param requestingAgentID
	 *            : Experiment identity for the agent
	 * @param opponentID
	 *            : Experiment identity for the opponent
	 * 
	 * @return pastInfoAfterUncertainty : opponent past actions after uncertainty limit has been
	 *         applied
	 */

	private static String getOpponentFirstDefection(int requestingAgentID, int opponentID) {

		String oppActions = "";
		String pastInfoAfterUncertainty = "";

		if (HIR.agentsRequestLimit[requestingAgentID] == 0) {
			AgentStrategies.infoAcquired = false;
			System.out.println("No more Info");
		}

		else {
			AgentStrategies.infoAcquired = true;
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

			HIR.agentsRequestLimit[requestingAgentID] = HIR.agentsRequestLimit[requestingAgentID] - 1;

			System.out.println(opponentPastInfo);
			System.out.println(pastInfoAfterUncertainty);

		}

		return pastInfoAfterUncertainty;

	}

	/**
	 * getOpponentActionsInRandomTournament method returns past actions of the opponent from a
	 * randomly chosen tournament
	 * 
	 * @param requestingAgentID
	 *            : Experiment identity for the agent
	 * @param opponentID
	 *            : Experiment identity for the opponent
	 * 
	 * @return pastInfoAfterUncertainty opponent past actions after uncertainty limit has been
	 *         applied
	 */

	private static String getOpponentActionsInRandomTournament(int requestingAgentID, int opponentID) {
		String pastInfoAfterUncertainty = "";

		if (HIR.agentsRequestLimit[requestingAgentID] == 0) {
			pastInfoAfterUncertainty = "";
			AgentStrategies.infoAcquired = false;
			System.out.println("No more Info");
		}

		else {
			AgentStrategies.infoAcquired = true;

			// Request agent to submit random tournament index
			int randomTournamentIndex = Agent.requestRandomTournament();
			for (int j = 0; j < totalNumOfAgents; j++) {
				if (opponentPastInfo == null)
					opponentPastInfo = String
							.valueOf(HIR.agentActionsDbase[randomTournamentIndex][j][opponentID]);
				else
					opponentPastInfo = opponentPastInfo
							+ HIR.agentActionsDbase[randomTournamentIndex][j][opponentID];
			}

			pastInfoAfterUncertainty = applyUncertaintyLimit(opponentPastInfo);
			HIR.agentsRequestLimit[requestingAgentID] = HIR.agentsRequestLimit[requestingAgentID] - 1;
			
			System.out.println(opponentPastInfo);
			System.out.println(opponentPastInfo);
			System.out.println(pastInfoAfterUncertainty);

		}
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

		agentsTournamentStatistics = agentsTournamentStatistics + "\n\nEXPERIMENT " + (currentExpIndex)
				+ "\n-----------------------\n";
		agentTournamentStats = agentsTournamentStatistics + "\n\nEXPERIMENT " + (currentExpIndex)
				+ "\n-----------------------\n";
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
		Files.write(Paths.get("HIR/SimLog.csv"), tournamentBoardInfo.getBytes());

	}

	
	/**
	 * readTB method locates the Tournament Board and reads all information
	 * stored on it by the simulation manager.
	 *
	 * Modified from original code
	 * [https://www.mkyong.com/java/how-to-read-file
	 * -from-java-bufferedreader-example/ ]
	 *
	 * @return splited : All information read from TB
	 */
	private static String readTB() {
		String boardInfo = "";
		try {

			File f = new File("TB/TB.csv");
			BufferedReader b = new BufferedReader(new FileReader(f));
			String readLine = "";
			readLine = b.readLine();
			while ((readLine = b.readLine()) != null) {
				boardInfo = readLine;

			}
			b.close();
		} catch (IOException err) {
			JOptionPane.showMessageDialog(null, "File not found");
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

		readLines(selectedExperimentIndex);

	}

	/**
	 * readLines method locates the chartInfo file in HIR and reads all
	 * information for the requested experiment index and then returns to
	 * getDataset method
	 *
	 * Modified from original code
	 * [https://www.mkyong.com/java/how-to-read-file
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

			File f = new File("HIR/chartInfo.csv");

			BufferedReader b = new BufferedReader(new FileReader(f));

			String readLine = "";
			readLine = b.readLine();
			while ((readLine = b.readLine()) != null) {

				chartDataset = readLine.split(",");

				if (chartDataset[0].equalsIgnoreCase(String
						.valueOf((currentExperimentIndex + 1)))) {
					LineChart.dataset.addValue(Float.parseFloat(chartDataset[1]),
							chartDataset[2], chartDataset[3]);
					BarChart.dataset.addValue(Float.parseFloat(chartDataset[1]),
							chartDataset[2], chartDataset[3]);
					AreaChart.dataset.addValue(Float.parseFloat(chartDataset[1]),
							chartDataset[2], chartDataset[3]);

				}
			}

			b.close();
		} catch (IOException err) {
			JOptionPane.showMessageDialog(null, "File not found");
		}

		return null;
	}

}
