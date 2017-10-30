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
	protected static float NumOfTournament = 0;
	protected static int agentsTotal = 0;
	
	//Private variables
	//private static boolean flags = false;
	//private static float[] reqLimit = new float[3];
	protected static String[] Strategies;
	private static boolean readySign = false;
	static int ExperimentIndex = 1;

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

		
		signalSetUpManager(); //Begin Scheduler activities
	}

	/**
	 * signalSetUpManager method of the scheduler calls the requestSimParam() to signal the
	 * Setup Manager that the simulation engine is ready to begin simulation
	 * experiment and also request for parameters required to initiate
	 * simulation
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
	 * requestSimParam request from the StrategySetupManager simulation agents
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
	 * getSetupParam receives setup parameters from the ParamConfigMgr to be implemented in
	 * the current experiment
	 * 
	 * @param param
	 *            : Current experimental setup vaules
	 * 
	 * @throws IOException
	 * 
	 */
	public static void getSetupParam(String[] param, int agentsTotal,
			String[] AgentStrategies) throws IOException {

		float[] setupValues = new float[param.length];
		for (int i = 0; i < param.length; i++) {
			setupValues[i] = Float.parseFloat(param[i]);
		}

		initializeParam(setupValues, agentsTotal, AgentStrategies);
	}

	/**
	 * initializeParam method received set-up parameters before simulation begins and
	 * initializes them to be used by the Simulation Engine
	 * 
	 * @param setupValues
	 *            : Simulation setup values received from the Paramter Manager
	 * @param numOfAgents
	 *            : Total number of agents in current experiment
	 * @param AgentStrategies
	 *            : List of agents' strategies in current experiment
	 * 
	 * @throws IOException
	 */
	private static void initializeParam(float[] setupValues, int numOfAgents,
			String[] AgentStrategies) throws IOException {
		tempt = setupValues[0];
		reward = setupValues[1];
		punish = setupValues[2];
		sucker = setupValues[3];
		NumOfTournament = setupValues[4];
		agentsTotal = numOfAgents;
		Strategies = AgentStrategies;

		ArrayList<Object> myHomeList = new ArrayList<Object>();
		ArrayList<Object> myAwayList = new ArrayList<Object>();

		
		startSimulation(myHomeList, myAwayList);

	}

	/**
	 * simulationStart after receiving invocation from initializeParam begins
	 * simulation experiment for the current experimental setup
	 * 
	 * @param myHomeList
	 *            : Group of Agents that forms the first half
	 * @param myAwayList
	 *            : Group of Agents that forms the last half
	 * @throws IOException
	 */
	public static void startSimulation(ArrayList<Object> myHomeList,
			ArrayList<Object> myAwayList) throws IOException {
		printExperiment();
		TournamentHandler.tournHandler(myHomeList, myAwayList);
		HIM.displayAgentsTournamentPerformance(ExperimentIndex);
		ExperimentIndex++;
		requestSimParam(true, ExperimentIndex);

	}

	/**
	 * printExperiment method reports on a statistics of current Experiment
	 * index agents' payoffs, strategies and positions .
	 * 
	 * @param ExperimentIndex
	 *            : Current Tournament index
	 * 
	 */
	private static void printExperiment() {

		// Save to TB and signal HIM
		String experimentTitle = "\n\nEXPERIMENT " + (ExperimentIndex)
				+ "\n-----------------------\n";
		String experimentTitle2 = "\nEXPERIMENT " + (ExperimentIndex) + "\n";
		GUI_Simulation.txtSim.append(experimentTitle);

		try {
			Files.write(Paths.get("TB/TB.csv"), experimentTitle2.getBytes());
			HIM.updateLog(experimentTitle2);
			HIM.startExp(ExperimentIndex);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "File not found");
		}

	}

}
