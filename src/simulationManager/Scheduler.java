package simulationManager;

import gui.GUI_Simulation;
import historicalInformationManager.HIM;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import setupManager.ParamConfigMgr;
import setupManager.StrategySetupManager;

/**
 * The scheduler as a sub-component of the Simulation Engine plans events for
 * execution and also provide arrangement for agents to compete at their
 * assigned time step while ensuring that the round robin scheduling abstraction
 * adopted for this model is maintained.
 * 
 * @author jonathanasante
 * 
 */
public class Scheduler {

	// Scheduler Parameters
	protected static float tempt = 0;
	protected static float reward = 0;
	protected static float punish = 0;
	protected static float sucker = 0;
	protected static float uLevel = 0;
	protected static float numOfTournament = 0;
	protected static int agentsTotal = 0;
	protected static String[] strategies;

	// Private variables
	private static boolean readySign = false;
	static int experimentIndex = 1;
	private static String TOURNAMENTBOARD = "TB/TB.csv";
	private static String FILENOTFOUND = "File not found";

	/**
	 * Main method is known to be the class' application entry point
	 * 
	 * @param args
	 *            Java main array of command-line arguments whose data type is
	 *            string passed to this method
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		// Begin Scheduler activities
		signalSetUpManager(); 
	}

	
	/**
	 * signalSetUpManager method of the scheduler calls the requestSimParam() to
	 * signal the Setup Manager that the simulation engine is ready to begin
	 * simulation experiment and also request for parameters required to
	 * initiate simulation
	 * 
	 * @throws IOException
	 */

	private static void signalSetUpManager() throws IOException {
		readySign = true;
		JOptionPane.showMessageDialog(null,
				"Scheduler ready to begin simulation");

		requestSimParam(readySign, 1);
	}

	/**
	 * requestSimParam request from the StrategySetupManager simulation agents's
	 * strategies in current experiment and setup parameters from the
	 * ParamConfigMgr
	 * 
	 * 
	 * @param sign
	 *            : signal to indicate scheduler's readiness to begin simulation
	 * 
	 * @throws IOException
	 */
	private static void requestSimParam(boolean sign, int nextExp)
			throws IOException {
		if (sign) {
			
			StrategySetupManager.getSimParam(nextExp);
			ParamConfigMgr.getSimulationParam(nextExp);

		}
	}

	/**
	 * setSetupParam receives setup parameters from the ParamConfigMgr to be
	 * implemented in the current experiment
	 * 
	 * @param param
	 *            : Current experimental setup vaules
	 * 
	 * @throws IOException
	 * 
	 */
	public static void setSetupParam(String[] param, int agentsTotal,
			String[] agentStrategies) throws IOException {

		float[] setupValues = new float[param.length];
		for (int i = 0; i < param.length; i++) {
			setupValues[i] = Float.parseFloat(param[i]);
		}

		initializeParam(setupValues, agentsTotal, agentStrategies);
	}

	/**
	 * initializeParam method received set-up parameters before simulation
	 * begins and initializes them to be used by the Simulation Engine
	 * 
	 * @param setupValues
	 *            : Simulation setup values received from the Paramter Manager
	 * @param numOfAgents
	 *            : Total number of agents in current experiment
	 * @param agentStrategies
	 *            : List of agents' strategies in current experiment
	 * 
	 * @throws IOException
	 */
	private static void initializeParam(float[] setupValues, int numOfAgents,
			String[] agentStrategies) throws IOException {
		tempt = setupValues[0];
		reward = setupValues[1];
		punish = setupValues[2];
		sucker = setupValues[3];
		numOfTournament = setupValues[4];
		agentsTotal = numOfAgents;
		strategies = agentStrategies;

		ArrayList<Object> homeList = new ArrayList<Object>();
		ArrayList<Object> awayList = new ArrayList<Object>();

		startSimulation(homeList, awayList);

	}

	/**
	 * startSimulation method upon invocation from initializeParam method begins
	 * simulation experiment for the current experimental setup
	 * 
	 * @param homeList
	 *            : Group of Agents that forms the first half
	 * @param awayList
	 *            : Group of Agents that forms the last half
	 * @throws IOException
	 */
	public static void startSimulation(ArrayList<Object> homeList,
			ArrayList<Object> awayList) throws IOException {
		printExperiment();
		TournamentHandler.tournHandler(homeList, awayList);
		HIM.displayAgentsTournamentPerformance(experimentIndex);
		experimentIndex++;
		requestSimParam(true, experimentIndex);

	}

	/**
	 * printExperiment method reports on a statistics of current Experiment
	 * index agents' payoffs, strategies and positions .
	 * 
	 * @param experimentIndex
	 *            : Current Tournament index
	 * 
	 */
	private static void printExperiment() {

		// Save to TB and signal HIM
		String experimentTitle = "\n\nEXPERIMENT " + (experimentIndex)
				+ "\n-----------------------\n";
		String experimentTitle2 = "\nEXPERIMENT " + (experimentIndex) + "\n";
		GUI_Simulation.txtSim.append(experimentTitle);

		try {
			Files.write(Paths.get(TOURNAMENTBOARD), experimentTitle2.getBytes());
			HIM.updateLog(experimentTitle2);
			HIM.startExp(experimentIndex);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, FILENOTFOUND);
		}

	}

}
