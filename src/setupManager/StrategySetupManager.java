package setupManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.JOptionPane;

import agents.Agent;

/**
 * StrategySetupManager validates the number of agents in the experiment and
 * ensures that there is an accurate assignment of strategies as specified by
 * the experimenter in the setup repository.
 * 
 * @author jonathanasante
 * 
 */
public class StrategySetupManager {

	/* Variable declaration and initialization */
	private static boolean startSim = false;
	public static String[] Strategies;
	protected static int[] AgentNum;
	protected static int agents;
	private static int expCounter = 0;
	protected static boolean simEnd = false;

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

		/* Read and Verify Agents Strategy set-up */
		readStrategyAssign("SR/AgentNum.csv");

		/* Signal ParamConfigMgr to also perform its validation */
		if (!startSim) {
			ParamConfigMgr.initiate();
		}
	}

	/**
	 * readStrategyAssign method reads assignment of strategies to agents from
	 * the input file to be verified before simulation begins
	 * 
	 * @param filename
	 *            : Name of file with agents strategies information
	 * 
	 */

	private static void readStrategyAssign(String filename) {

		/* Read agents strategy setup component of Setup Repository */

		/**
		 * Modified from original code
		 * [https://www.mkyong.com/java/how-to-read-file
		 * -from-java-bufferedreader-example/ ]
		 **/

		String[] splited = null;
		try {

			File f = new File(filename);

			BufferedReader b = new BufferedReader(new FileReader(f));

			String readLine = "";
			readLine = b.readLine();
			while ((readLine = b.readLine()) != null) {
				splited = readLine.split(",");
				expCounter++;
				validate(splited);
				if (!startSim)
					getAgentStrategies(splited);
			}

			/* close the file reader */
			b.close();
		} catch (IOException err) {
			JOptionPane.showMessageDialog(null, "File not found");
			startSim = true;
		}

	}

	/**
	 * validateStrategies method is called after each line read of the number of
	 * agents for each strategy. And this method performs the function of
	 * ensuring that user input is accurate and is a number
	 * 
	 * @param strategyInput
	 *            : List of all agent strategies in current experiment
	 */
	private static void validate(String[] strategyInput) {

		/* Store number of agents for each Strategy into an array */
		int[] AgentNum = new int[strategyInput.length];

		/* Validate that payoffs are integer values */
		for (int i = 0; i < (strategyInput.length - 1); i++) {

			try {
				AgentNum[i] = Math.round(Float.parseFloat(strategyInput[i]));
				startSim = false;
			} catch (NumberFormatException ef) {
				JOptionPane.showMessageDialog(null,
						"Must input a number for agents playing strategy "
								+ (i + 1));
				startSim = true;
				return;
			}

		}
		try {
			AgentNum[(strategyInput.length - 1)] = Integer
					.parseInt((strategyInput[(strategyInput.length - 1)]));
			startSim = false;
		} catch (NumberFormatException ef) {
			JOptionPane.showMessageDialog(null,
					"Specify option index for information acquisition ");
			startSim = true;
			return;
		}

	}

	/**
	 * getSimParam is responsible for sending to the agent component the
	 * verified numbers of agents for each strategy in the current simulation
	 * experiment.
	 * 
	 * @param i
	 *            : index of experiment for which information is requested
	 * 
	 * @throws IOException
	 * 
	 */

	public static void getSimParam(int i) throws IOException {

		/* Read requested numbers of agents for each strategy from file */

		if (i <= expCounter) {

			String lineParam = Files.readAllLines(Paths.get("SR/AgentNum.csv"))
					.get(i);

			/* Put them in an array */
			String[] numOfAgentStrategies = lineParam.split(",");

			/* Get info request option */
			int infoApproachOption = Integer
					.parseInt(numOfAgentStrategies[(numOfAgentStrategies.length - 1)]);

			String[] AgentsStrategies = getAgentStrategies(numOfAgentStrategies);

			/* Send number of Agent strategies to agent */
			Agent.setVariable(agents, AgentsStrategies, infoApproachOption);
		} else
			return;

	}

	/**
	 * getAgentStrategies Sets up the strategy of each competing player n the
	 * tournament.
	 * 
	 * @param strategiesNum
	 *            : Agents strategies in the current experiment
	 * 
	 * 
	 */

	public static String[] getAgentStrategies(String[] strategiesNum) {

		agents = 0;

		/* Initialize variables */

		/* Get total number of agents */
		for (int i = 0; i < (strategiesNum.length - 1); i++) {
			agents = agents + Integer.parseInt(strategiesNum[i]);

		}

		/* Set allocation for odd number of agents */
		if (agents % 2 != 0) {
			agents = agents + 1;
			Strategies = new String[agents];
			Strategies[(agents - 1)] = "Dummy"; // Add dummy array to last
												// location
		}

		/* Set allocation for even number of agents */
		else {

			Strategies = new String[agents];

		}

		int count = 0;

		/* Generate agent strategies and store in array */
		for (int i = 0; i < 4; i++) {

			for (int j = 0; j < Integer.parseInt(strategiesNum[i]); j++) {

				if (i == 0) {
					Strategies[(count)] = "Naive_C";
				}

				else if (i == 1) {
					Strategies[(count)] = "Naive_D";
				}

				else if (i == 2) {
					Strategies[(count)] = "Advanced_C";
				}

				else if (i == 3) {
					Strategies[(count)] = "Advanced_D";
				}

				else if (i == 4) {
					Strategies[(count)] = "Rater";
				}

				count++;
			}
		}

		/*
		 * Shuffle agent strategies so that position doesn't favor outcome in
		 * first round
		 */
		Collections.shuffle(Arrays.asList(Strategies));

		return Strategies;
	}

}
