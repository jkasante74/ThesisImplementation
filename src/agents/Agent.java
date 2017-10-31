package agents;

import java.util.Random;

public class Agent {

	// Parameters for Agent 
	protected static String[] agentStrategies; // change
	protected static double agentBeliefs[][];
	public static float[] agentScores;
	protected static int expInfoRequestOption;
	
	//Private variables
	private static int currentTournament;
	private static final String ADVANCE_COOPERATOR = "Advanced_C";
	private static final String ADVANCE_DEFECTOR = "Advanced_D";
	private static final String NAIVE_DEFECTOR = "Naive_D";
	private static final String NAIVE_COOPERATOR = "Naive_C";

	/**
	 * initialize method sets up the variables and data structures used by the
	 * Agent component.
	 * 
	 * @param numOfAgents
	 *             : number of agents in current experiment
	 * @param agentStrategies
	 *            : Array of different agents' strategies for the current
	 *            experiment
	 * @param infoRequestsOption
	 *            : Past information advanced agents can request about current
	 *            opponent
	 * 
	 */
	public static void setVariable(int numOfAgents, String[] agentsStrategies,
			int infoRequestsOption) {

		// Initialize variables
		agentScores = new float[numOfAgents]; 
		agentStrategies = agentsStrategies; 
		expInfoRequestOption = infoRequestsOption; 

		createAgentsBeliefs(numOfAgents);
	}

	/**
	 * readOpponentRating method returns the updated belief of an opponent stored
	 * by an agent in its internal memory.
	 * 
	 * @param agentID : Requesting agent ID
	 * 
	 * @param opponentID : Opponent ID
	 *  
	 * @return agentBeliefs : return updated Agent's current belief about opponent
	 * 
	 */
	protected static double readOpponentRating(int agentID, int opponentID) {

		return agentBeliefs[agentID][opponentID];

	}

	/**
	 * createAgBeliefs creates and initializes the beliefs for both advance
	 * cooperators and defectors about their ooponents.
	 * 
	 * @param numOfAgents
	 *            : Number of agents in current Experiment
	 * 
	 */
	private static void createAgentsBeliefs(int numOfAgents) {

		
		agentBeliefs = new double[numOfAgents][numOfAgents];
		
		// Advance agents create beliefs as summary information about opponent
		for (int i = 0; i < agentStrategies.length; i++) {
			if ((agentStrategies[i] == ADVANCE_COOPERATOR)||(agentStrategies[i] == ADVANCE_DEFECTOR)) {
				for (int j = 0; j < agentStrategies.length; j++) {
					agentBeliefs[i][j] = 0.0; 
				}
			}

		}
	}

	/**
	 * getStrategies returns the different strategies of agents stored as a
	 * string array
	 * 
	 * @return agentStrategies : All competing agents' strategies
	 */
	public static String[] getStrategies() {

		return agentStrategies;
	}

	/**
	 * getAgentActions method returns the chosen action of paired agent and its
	 * opponent for the current Tournament, current Round,
	 * 
	 * @param agentID : Requesting agent ID
	 * 
	 * @param opponentID : Opponent ID
	 * @param currentTournament	: Current Tournament
	 * @param currentRound : Current Round
	 * 
	 * @return actions : Selected actions of both agent and opponent
	 * 
	 */
	public static char[] getMatchedAgentActions(int agentID, int opponentID,
			int presentTournament, int currentRound) {

		
		char[] actions = new char[2];
		char agentAction = 0, OpponentAction = 0;
		currentTournament = presentTournament;

		String a = agentStrategies[agentID];
		String b = agentStrategies[opponentID];

		// Get action of agent 
		if (a.equalsIgnoreCase(NAIVE_COOPERATOR))
			agentAction = AgentStrategies.cooperateAll();
		if (a.equalsIgnoreCase(NAIVE_DEFECTOR))
			agentAction = AgentStrategies.defectAll();
		if (a.equalsIgnoreCase(ADVANCE_COOPERATOR))
			agentAction = AgentStrategies.advanceCooperator(agentID,
					opponentID, currentTournament, currentRound);
		if (a.equalsIgnoreCase(ADVANCE_DEFECTOR))
			agentAction = AgentStrategies.advanceDefector(agentID, opponentID,
					currentTournament, currentRound);

		// Get  action of opponent 
		if (b.equalsIgnoreCase(NAIVE_COOPERATOR))
			OpponentAction = AgentStrategies.cooperateAll();
		if (b.equalsIgnoreCase(NAIVE_DEFECTOR))
			OpponentAction = AgentStrategies.defectAll();
		if (b.equalsIgnoreCase(ADVANCE_COOPERATOR))
			OpponentAction = AgentStrategies.advanceCooperator(opponentID,
					agentID, currentTournament, currentRound);
		if (b.equalsIgnoreCase(ADVANCE_DEFECTOR))
			OpponentAction = AgentStrategies.advanceDefector(agentID,
					opponentID, currentTournament, currentRound);
		
		actions[0] = agentAction;
		actions[1] = OpponentAction;

		return actions;

	}

	/**
	 * agentScores method Updates the score for each agent participating in the simulation
	 * experiment
	 * 
	 * @param agentID : Requesting agent ID
	 * 
	 * @param opponentID : Opponent ID
 	 *
	 * @param matchScores : Scores of both agents and opponents 
	 */
	public static void agentsScores(int agentID, int opponentID,
			float[] matchScores) {
		agentScores[agentID] = agentScores[agentID] + matchScores[0];
		agentScores[opponentID] = agentScores[opponentID] + matchScores[1];
	}

	/**
	 * requestRandomTournament method provides the mechanism for advanced agents
	 * to randomly choose a tournament and request opponent's past information.
	 * 
	 * @return getRandomTournament : randomly selected tournament
	 */
	public static int requestRandomTournament() {

		
		Random rand = new Random();
		int randomTournamentNumber = rand.nextInt((currentTournament - 0) + 1);

		return randomTournamentNumber;
	}

}
