package setupManager;

import gui.GUI;
import historicalInformationManager.HIM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import javax.swing.JOptionPane;

import simulationManager.Scheduler;

/**
 * ParamConfigMgr validates other simulation inputs such as Payoff matrix,
 * information acquisition uncertainties, information request limits and number
 * of tournaments that are stored in the setup repositories. These validated
 * inputs and assigned strategies are then sent to the simulation manager at the
 * begining of the simulation.
 * 
 * @author jonathanasante
 * 
 */
public class ParamConfigMgr {

	/* ParamConfigMgr Parameters */
	private static boolean startSim = false;
	private static int[] currentSetupValues;
	private static float uncertaintyLevel;
	private static int experimentCounter = 0;
	private static int[] agentRequestLimit;
	private static String requestLimitOption;
	private static final String ADVANCECOOPERATOR = "ADVANCECOOPERATOR";
	private static final String ADVANCEDEFECTOR = "ADVANCEDEFECTOR";
	public static void initiate() throws IOException {

		
		readSimulationParameters("SR/SetupParam.csv");

		if (!startSim) {
			updateChartingOption();
			simulationManager.Scheduler.main(null); //Setup validation complete begin simulation
		}
	}

	private static Object makeObj(final String item) {
		return new Object() {
			@Override
			public String toString() {
				return item;
			}
		};
	}

	@SuppressWarnings("unchecked")
	
	/**
	 * updateChartingOption method provides a quick update on 
	 * experiment charting option for HIM to use during charting 
	 */
	private static void updateChartingOption() {
		for (int i = 0; i < experimentCounter; i++) {
			String name = "Experiment " + (i + 1);
			GUI.cmbExpSel.addItem(makeObj(name));
		}
	}

	
	/**
	 * readSimulationParameters method reads experimental parameters stored in the setup
	 * repository file to be validated.
	 * Modified from original code
	 * [https://www.mkyong.com/java/how-to-read-file
	 * -from-java-bufferedreader-example/ ]
	 * 
	 * @param filename
	 *            : directory of setup file
	 */
	private static void readSimulationParameters(String filename) {

		String[] currentExperimentSetup = null;
		try {
			File f = new File(filename);

			BufferedReader b = new BufferedReader(new FileReader(f));
			String readLine = "";
			readLine = b.readLine();
			while ((readLine = b.readLine()) != null) {
				experimentCounter++;
				currentExperimentSetup = readLine.split(",");
				ValidateSetupValues(currentExperimentSetup);
			}

			// close the file reader 
			b.close();
		} catch (IOException err) {
			JOptionPane.showMessageDialog(null, "File not found");
		}
	}

	
	/**
	 * ValidateInputs method validates the parameters setup values
	 * 
	 * @param setupParameters
	 *            : setup values necessary to setup an experiment
	 */
	private static void ValidateSetupValues(String[] setupParameters) {
		currentSetupValues = new int[setupParameters.length];

		for (int i = 0; i < setupParameters.length; i++) {
			try {
				currentSetupValues[i] = Math.round(Float.parseFloat(setupParameters[i]));
				startSim = false;
			} catch (NumberFormatException ef) {
				JOptionPane.showMessageDialog(null,
						"PayOff inputs must be Integer.");
				startSim = true;
				return;
			}

		}

		// Validate conditions  for cooperation
		if ((2 * currentSetupValues[1]) <= ((currentSetupValues[0]) + (currentSetupValues[3]))) {
			JOptionPane.showMessageDialog(null,
					"PayOff inputs must follow (2 * R) > (T + S)");
			startSim = true;
			return;
		}

		if (((currentSetupValues[0]) <= (currentSetupValues[1]))
				|| ((currentSetupValues[1]) <= (currentSetupValues[2]) || ((currentSetupValues[2]) <= (currentSetupValues[3])))) {
			JOptionPane.showMessageDialog(null,
					"PayOff inputs must follow T > R > P > S");
			startSim = true;
			return;
		}

		// Validate Number of tournaments input and info. request limit
		try {
			Math.round(Float.parseFloat(setupParameters[4]));
			uncertaintyLevel = Float.parseFloat(setupParameters[5]);
			startSim = false;

		} catch (NumberFormatException ef) {
			JOptionPane.showMessageDialog(null,
					"Tournament number must enter a number.");
			startSim = true;
			return;
		}

		// Validate Uncertainty limit as a probability value 
		if ((Float.parseFloat(setupParameters[5]) < 0)
				&& (Float.parseFloat(setupParameters[5]) > 1)) {
			JOptionPane.showMessageDialog(null,
					"Uncertainty Level must be a probability value");
			startSim = true;
			return;
		}

	}

	/**
	 * getSimParam method upon request from the simulation manager retrieves experiment
	 * parameters
	 * 
	 * @param i
	 *            : index of current experiment
	 * @throws IOException
	 */
	public static void getSimulationParam(int i) throws IOException {

		if (i <= experimentCounter) {
			String lineParam = Files.readAllLines(
					Paths.get("SR/SetupParam.csv")).get(i);

			String[] setupParam = lineParam.split(",");

			
			int NumOfTournament = Integer.parseInt(setupParam[4]);
			float uncertaintyLevel = Float.parseFloat(setupParam[5]);
			int numOfAgents = StrategySetupManager.agents;
			String[] Strategies = StrategySetupManager.Strategies;
			int[] agentsRequestLimit = getRequestLimit();

			HIM.initialize(i, NumOfTournament, uncertaintyLevel, agentsRequestLimit, numOfAgents,
					setupParam, requestLimitOption);

			Scheduler.getSetupParam(setupParam, numOfAgents, Strategies);
		} else
			return;

	}

	/**
	 * getRequestLimit() returns the current experiment request limits for agents
	 * to Historical Information Manager upon request.
	 * 
	 * @return agentRequestLimit : Request limit if all agents in the current
	 *         experiment
	 */
	public static int[] getRequestLimit() {

		agentRequestLimit = new int[StrategySetupManager.Strategies.length];

		if (GUI.radomRequest) {
			Random r = new Random();
			requestLimitOption = "Random"; 

			for (int i = 0; i < agentRequestLimit.length; i++)
				agentRequestLimit[i] = r.nextInt((4000 - 0) + 1) + 0;
		}
		else {
			requestLimitOption = "Assigned";

			for (int i = 0; i < agentRequestLimit.length; i++) {
				if (StrategySetupManager.Strategies[i]
						.equalsIgnoreCase("ADVANCECOOPERATOR")) {
					agentRequestLimit[i] = currentSetupValues[6];
				}

				else if (StrategySetupManager.Strategies[i]
						.equalsIgnoreCase("ADVANCEDEFECTOR")) {
					agentRequestLimit[i] = currentSetupValues[7];
				}

				else {
					agentRequestLimit[i] = 0;
				}
			}

		}
		return agentRequestLimit;

	}

	/**
	 * getUncertaintyLevel : Send the current experiment uncertainty level to
	 * Historical Information Manager upon request.
	 * 
	 * @return uncertaintyLevel : Uncertainty level for current experiment
	 * 
	 */
	public static float getUncertaintyLevel() {
		return uncertaintyLevel;
	}
// commit
}
