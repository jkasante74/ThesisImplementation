package simulationManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

import gui.GUI_Simulation;
import historicalInformationManager.HIM;
import agents.Agent;

/**
 * The MatchManger provides the capacity for matches scheduled in each round to
 * be executed independent of one another. Also, it ensures that the right
 * scores are assigned to each participant based on their actions and the payoff
 * matrix stored in SR and accessed through interaction with SeM. After each
 * match, the match manager stores information on strategies, actions and
 * payoffs on the Tournament Board to be accessed by the Historical Information
 * Manager.
 * 
 * @author jonathanasante
 * 
 */
public class MatchManager {

	// Private variables 
	private static char[] agentsAction;
	private static final char COOPERATE = 'C';
	private static final char DEFECT = 'D';
	private static final char DUMMY = 'A';
	private static String TOURNAMENTBOARD = "TB/TB.csv";
	private static String FILENOTFOUND = "File not found";
	/**
	 * matchMgr method initiates the function of managing all agents activities
	 * in various matches in current round
	 * 
	 * @param a
	 *            : Strategy of agent in current Experiment
	 * @param b
	 *            : Strategy of opponent in current Experiment
	 * @param agentID
	 *            : Agent id in current experiment
	 * @param opponentID
	 *            : Opponent id in current experiment
	 * @param currentTournament
	 *            : current Tournament
	 * @param currentRound
	 *            : Current Round
	 */
	protected static void matchMgr(String agentStrategy, String opponentStrategy, int agentID, int opponentID, int currentTournament,
			int currentRound) {

		
		agentsAction = Agent.getMatchedAgentActions(agentID, opponentID, currentTournament, currentRound);

		//System.out.println(agentsAction[0] + "\t \t vrs \t " + agentsAction[1]);

		String text = "\n" + agentsAction[0] + "\t \t vrs \t "
				+ agentsAction[1] + "\n";
		GUI_Simulation.txtSim.append(text);

		// Store actions in TB for HIM to pick up
		try {
			Files.write(Paths.get(TOURNAMENTBOARD), text.getBytes());
			HIM.updateLog(text);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, FILENOTFOUND);
		}

		float[] matchScores = MatchManager.calcMatchedAgentsScores(agentsAction);

		Agent.agentsScores(agentID, opponentID, matchScores);

		HIM.updateAgActionsInReposiory(currentTournament, agentID, opponentID, agentsAction);

	}

	/**
	 * calcMatchedAgentsScores method calculates the payoffs associated with the
	 * selected actions of the players in customized simulation experiment of
	 * the iterated round robin tourment of the PD Game.
	 * 
	 * @param agentsActions
	 *            : Selected actions of both player and his opponent
	 * 
	 * @return matchScores : Scores for both player and his opponent based on
	 *         their actions
	 * 
	 */

	private static float[] calcMatchedAgentsScores(char[] agentsActions) {

		float scoreA = 0, scoreB = 0;
		float[] matchScores = new float[2];

		
		if ((agentsActions[0] == COOPERATE) && (agentsActions[1] == COOPERATE)) {
			scoreA = (Scheduler.reward);
			scoreB = (Scheduler.reward);
		}

		if ((agentsActions[0] == COOPERATE) && (agentsActions[1] == DEFECT)) {
			scoreA = (Scheduler.sucker);
			scoreB = (Scheduler.tempt);
		}

		if ((agentsActions[0] == DEFECT) && (agentsActions[1] == COOPERATE)) {
			scoreA = (Scheduler.tempt);
			scoreB = (Scheduler.sucker);
		}

		if ((agentsActions[0] == DEFECT) && (agentsActions[1] == DEFECT)) {
			scoreA = (Scheduler.punish);
			scoreB = (Scheduler.punish);
		}

		if (agentsActions[1] == DUMMY) {
			scoreA = 0;
			scoreB = 0;
		}

		matchScores[0] = scoreA;
		matchScores[1] = scoreB;

		if (agentsActions[1] != DUMMY) {
		//	System.out.println(matchScores[0] + "\t \t vrs \t "
		//			+ matchScores[1] + "\n");
			String calculatedScores = matchScores[0] + "\t \t vrs \t " + matchScores[1]
					+ "\n\n";
			GUI_Simulation.txtSim.append(calculatedScores);

			
			try {
				Files.write(Paths.get(TOURNAMENTBOARD), calculatedScores.getBytes());
				HIM.updateLog(calculatedScores);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, FILENOTFOUND);
			}

		}

		return matchScores;

	}
}
