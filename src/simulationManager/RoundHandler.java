package simulationManager;

import gui.GUI_Simulation;
import historicalInformationManager.HIM;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * RoundHandler is responsible for ensuring that for each round in a specific
 * tournament participants are paired with different opponents. Also, Round
 * Handler manages agentsTotal' performance and pay-off profiles for each round.
 * 
 * 
 * @author jonathanasante
 * 
 * 
 */
public class RoundHandler {
	/**
	 * roundMgr method initiates the function of managing all agentsTotal activities
	 * in the current round
	 * 
	 * @param currentTournament
	 *            : Current Tournament index
	 * @param totalRounds
	 *            : Total number of rounds in current Tounrament
	 * @param matchesPerRound
	 *            : Total number of matches to be played in every round
	 * @param homeList
	 *            : Group of Agents that forms the first half
	 * @param awayList
	 *            : Group of Agents that forms the last half
	 * 
	 */
	private static String DUMMY = "Dummy";
	private static String TOURNAMENTBOARD = "TB/TB.csv";
	private static String FILENOTFOUND = "File not found";

	public static void roundMgr(int currentTournament, int totalRounds,
			int matchesPerRound, ArrayList<Object> homeList,
			ArrayList<Object> awayList) {

		for (int round = 0; round < totalRounds; round++) {

			// Re-Shuffle the players to play one on one 
			for (int match = 0; match < matchesPerRound; match++) {
				int home = (round + match) % (Scheduler.agentsTotal - 1);
				int away = (Scheduler.agentsTotal - 1 - match + round)
						% (Scheduler.agentsTotal - 1);

				
				if (match == 0)
					away = Scheduler.agentsTotal - 1;

				homeList.set(match, String.valueOf(home + 1));
				awayList.set(match, String.valueOf(away + 1));
			}

			
			//System.out.println();
			//System.out.println("\nRound " + (round + 1));
			//System.out.println("---------------------------------");
			String text = "\n\nRound " + (round + 1) + "\n"
					+ "---------------------------------\n";
			String textx = "\nRound " + (round + 1) + "\n";
			GUI_Simulation.txtSim.append(text);

			try {
				Files.write(Paths.get(TOURNAMENTBOARD), textx.getBytes());
				HIM.updateLog(text);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, FILENOTFOUND);
			}

			
			for (int j = 0; j < matchesPerRound; j++) {

				int agentID = Integer.parseInt((String) (homeList.get(j))) - 1;
				int oppnentID = Integer.parseInt((String) (awayList.get(j))) - 1;

				String agentStrategy = Scheduler.strategies[Integer
						.parseInt((String) (homeList.get(j))) - 1];
				String   opponentStrategy = Scheduler.strategies[Integer
						.parseInt((String) (awayList.get(j))) - 1];

				
				if (!opponentStrategy.contains(DUMMY)) {
					//System.out.println("Agent " + homeList.get(j)
					//		+ "\t vrs \t " + "Agent " + awayList.get(j));
					//System.out.println(agentStrategy + "\t vrs \t " + opponentStrategy);

					String matchedAgentID = "Agent " + homeList.get(j)
							+ "\t \t vrs \t Agent " + awayList.get(j) + "\n"
							+ agentStrategy + "\t \t vrs \t " + opponentStrategy;
					GUI_Simulation.txtSim.append(matchedAgentID);
					String matchedAgentID2 = "\nAgent " + homeList.get(j)
							+ "\t \t vrs \t Agent " + awayList.get(j) + "\n";
					String matchedAgentStrategies = "\n" + agentStrategy + "\t \t vrs \t " + opponentStrategy + "\n";
					
					try {
						Files.write(Paths.get(TOURNAMENTBOARD), matchedAgentID2.getBytes());
						HIM.updateLog(matchedAgentID);
						
						Files.write(Paths.get(TOURNAMENTBOARD), matchedAgentStrategies.getBytes());
						HIM.updateLog(matchedAgentStrategies);
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, FILENOTFOUND);
					}
					
					// Transfer control to MatchManager
					MatchManager.matchMgr(agentStrategy, opponentStrategy, agentID, oppnentID, currentTournament, round);
				}

			}
		}
	}
}
