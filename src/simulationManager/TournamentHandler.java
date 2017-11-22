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
	private static String TOURNAMENTBOARD = "TB/TB.csv";
	private static String FILENOTFOUND = "File not found";

	/**
	 * tournHandler method generates for each tournament a groups for the ageents.
	 * 
	 * @param homeList
	 *            : Group of Agents that forms the first half
	 * @param awayList
	 *            : Group of Agents that forms the last half
	 * 
	 */
	protected static void tournHandler(ArrayList<Object> homeList,
			ArrayList<Object> awayList) {
		for (int currentTournament = 0; currentTournament < Scheduler.numOfTournament; currentTournament++) {

			printTournamentStats(currentTournament);

			int totalRounds = (Scheduler.agentsTotal - 1); 
			int matchesPerRound = Scheduler.agentsTotal / 2; 

			// Group players into two for fair matching
			for (int j = 0; j < matchesPerRound; j++) {
				homeList.add(new Scheduler());
				awayList.add(new Scheduler());
			}

			RoundHandler.roundMgr(currentTournament, totalRounds,
					matchesPerRound, homeList, awayList);

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

		tournamentStats = getTournamentTitleLog(currentTournamentIndex);
		GUI_Simulation.txtSim.append(tournamentStats);
		String tx = getTournamentTitleForBoard(currentTournamentIndex);
		
		// Store current tournament title in tournament board
		try {
			Files.write(Paths.get(TOURNAMENTBOARD), tx.getBytes());
			HIM.updateLog(tournamentStats);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, FILENOTFOUND);
		}

	}
	
	
	/**
	 * getTournamentTitleForBoard method returns current tournament title to be
	 * stored on the tournament board for pickup by the HIM
	 * @param currentTournamentIndex : current tournament number
	 * @return	log : String of current tournament title
	 */
	private static String getTournamentTitleForBoard(
			float currentTournamentIndex) {
		String log = "\nRound-Robin Tournament " + (currentTournamentIndex + 1)
		+ "\n";
		return log;
	}

	
	/**
	 * getTournamentTitleLog method returns current tournament title to be
	 * displayed on the simulation log
	 * @param currentTournamentIndex : current tournament number
	 * @return	log : String of current tournament title displayed in simulation log
	 */
	private static String getTournamentTitleLog(float currentTournamentIndex) {
		String log = "\nRound-Robin Tournament "
				+ (currentTournamentIndex + 1) + "\n" + "===================\n";
		return log;
	}

}
