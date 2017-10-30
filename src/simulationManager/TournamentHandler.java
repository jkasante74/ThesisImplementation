package simulationManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import gui.GUI_Simulation;
import historicalInformationManager.HIM;

/**
 * TournamentHandler class performs the functions of controlling the activities
 * of agents for every tournament in an experiment. After that the tournament
 * handler reports on a statistics of agents payoff for every tournament.
 * 
 * @author jonathanasante
 * 
 */
public class TournamentHandler {

	// Private Variable
	private static String tournamentStats;

	/**
	 * tournHandler method generates for each tournament a groups for the agents
	 * and splits up players into groupds of pairs.
	 * 
	 * @param myHomeList
	 *            : Group of Agents that forms the first half
	 * @param myAwayList
	 *            : Group of Agents that forms the last half
	 * 
	 */
	protected static void tournHandler(ArrayList<Object> myHomeList,
			ArrayList<Object> myAwayList) {
		for (int currentTournament = 0; currentTournament < Scheduler.NumOfTournament; currentTournament++) {

			
			printTournamentStats(currentTournament);

			// Set total rounds in a tournament
			int totalRounds = (Scheduler.agentsTotal - 1); 
			int matchesPerRound = Scheduler.agentsTotal / 2; 

			// Group players into two for fair matching
			for (int j = 0; j < matchesPerRound; j++) {
				myHomeList.add(new Scheduler());
				myAwayList.add(new Scheduler());
			}

			
			RoundHandler.roundMgr(currentTournament, totalRounds, matchesPerRound,
					myHomeList, myAwayList);

			try {
				HIM.displayAgentsTournamentStats(currentTournament);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * printTournamentStats method reports on a statistics of agents' payoffs,
	 * strategies and positions for every tournament.
	 * 
	 * @param currentTournamentIndex
	 *            : Current Tournament index
	 * 
	 */
	private static void printTournamentStats(float currentTournamentIndex) {

		System.out.println("\nRound-Robin Tournament " + (currentTournamentIndex + 1)
				+ "\n====================================="); 
		tournamentStats = "\nRound-Robin Tournament " + (currentTournamentIndex + 1) + "\n"
				+ "===================\n";
		GUI_Simulation.txtSim.append(tournamentStats);
		String tx = "\nRound-Robin Tournament " + (currentTournamentIndex + 1) + "\n";

		
		try {
			Files.write(Paths.get("TB/TB.csv"), tx.getBytes());
			HIM.updateLog(tournamentStats);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "File not found");
		}

	}

}
