package agents;

import java.util.Random;

public class Agent {

	// Parameters for Agent
	protected static String[] agentStrategies;
	protected static double agentBeliefs[][], gameValue[][];
	public static float[] agentScores;
	protected static int expInfoRequestOption;

	// Private variables
	private static int currentTournament;
	private static final String ADVANCE_COOPERATOR = "Advanced_C";
	private static final String ADVANCE_DEFECTOR = "Advanced_D";
	private static final String NAIVE_DEFECTOR = "Naive_D";
	private static final String NAIVE_COOPERATOR = "Naive_C";

	/**
	 * setVariable method sets up the variables and data structures used by the
	 * Agent component.
	 * 
	 * @param numOfAgents
	 *            : number of agents in current experiment
	 * @param agentStrategies
	 *            : Array of different agents' strategies for the current
	 *            experiment
	 * @param infoRequestsOption
	 *            : Approach to information request
	 * 
	 */
	public static void setVariable(int numOfAgents, String[] agentsStrategies,
			int infoRequestsOption) {

		// Set Parameter variables
		agentScores = new float[numOfAgents];
		agentStrategies = agentsStrategies;
		expInfoRequestOption = infoRequestsOption;

		createAgentsBeliefs(numOfAgents);
	}

	/**
	 * readOpponentRating method returns the updated belief of an opponent
	 * stored by an agent in its internal memory.
	 * 
	 * @param requestingAgentID
	 *            : Requesting agent ID
	 * 
	 * @param opponentID
	 *            : Opponent ID
	 * 
	 * @return agentBeliefs : return updated Agent's current belief about
	 *         opponent
	 * 
	 */
	protected static double readOpponentRating(int requestingAgentID,
			int opponentID) {

		return agentBeliefs[requestingAgentID][opponentID];

	}

	/**
	 * createAgBeliefs creates and initializes the beliefs for both advanced
	 * cooperators and defectors about their ooponents.
	 * 
	 * @param numOfAgents
	 *            : Number of agents in current Experiment
	 * 
	 */
	private static void createAgentsBeliefs(int numOfAgents) {

		agentBeliefs = new double[numOfAgents][numOfAgents];
		gameValue = new double[numOfAgents][numOfAgents];

		// Advanced agents assigns belief and game values about other agents
		for (int i = 0; i < agentStrategies.length; i++) {
			if ((agentStrategies[i] == ADVANCE_COOPERATOR)
					|| (agentStrategies[i] == ADVANCE_DEFECTOR)) {
				for (int j = 0; j < agentStrategies.length; j++) {
					agentBeliefs[i][j] = 0.0;
					gameValue[i][j] = 0.0;
				}
			}

		}
	}

	/**
	 * getStrategies returns the different strategies of agents
	 * 
	 * @return agentStrategies : All competing agents' strategies
	 */
	public static String[] getStrategies() {

		return agentStrategies;
	}

	/**
	 * getMatchedAgentActions method returns the chosen actions of paired agents
	 * 
	 * @param requestingAgentID
	 *            : Requesting agent ID
	 * @param opponentID
	 *            : Opponent ID
	 * @param currentTournament
	 *            : Current Tournament
	 * @param currentRound
	 *            : Current Round
	 * 
	 * @return actions : Selected actions of both agent and opponent
	 * 
	 */
	public static char[] getMatchedAgentActions(int requestingAgentID,
			int opponentID, int presentTournament, int currentRound) {

		char[] actions = new char[2];
		char agentAction = 0, OpponentAction = 0;
		currentTournament = presentTournament;

		String agentStrategi = agentStrategies[requestingAgentID];
		String opponentStrategi = agentStrategies[opponentID];

		// Get action of requesting agent
		if (agentStrategi.equalsIgnoreCase(NAIVE_COOPERATOR))
			agentAction = AgentStrategies.cooperateAll();
		if (agentStrategi.equalsIgnoreCase(NAIVE_DEFECTOR))
			agentAction = AgentStrategies.defectAll();
		if (agentStrategi.equalsIgnoreCase(ADVANCE_COOPERATOR))
			agentAction = AgentStrategies.advanceCooperator(requestingAgentID,
					opponentID, currentTournament, currentRound);
		if (agentStrategi.equalsIgnoreCase(ADVANCE_DEFECTOR))
			agentAction = AgentStrategies.advanceDefector(requestingAgentID,
					opponentID, currentTournament, currentRound);
		
		// Get action of opponent
		if (opponentStrategi.equalsIgnoreCase(NAIVE_COOPERATOR))
			OpponentAction = AgentStrategies.cooperateAll();
		if (opponentStrategi.equalsIgnoreCase(NAIVE_DEFECTOR))
			OpponentAction = AgentStrategies.defectAll();
		if (opponentStrategi.equalsIgnoreCase(ADVANCE_COOPERATOR))
			OpponentAction = AgentStrategies.advanceCooperator(opponentID,
					requestingAgentID, currentTournament, currentRound);
		if (opponentStrategi.equalsIgnoreCase(ADVANCE_DEFECTOR))
			OpponentAction = AgentStrategies.advanceDefector(requestingAgentID,
					opponentID, currentTournament, currentRound);

		actions[0] = agentAction;
		actions[1] = OpponentAction;

		return actions;

	}

	/**
	 * updateMatchedAgentsScores method updates the score for each agent
	 * participating in the current match
	 * 
	 * @param requestingAgentID
	 *            : Requesting agent ID
	 * 
	 * @param opponentID
	 *            : Opponent ID
	 * 
	 * @param matchScores
	 *            : Scores of both agents and opponents
	 */
	public static void updateMatchedAgentsScores(int requestingAgentID,
			int opponentID, float[] matchScores) {
		agentScores[requestingAgentID] += matchScores[0];
		agentScores[opponentID] += matchScores[1];
	}

	/**
	 * requestRandomTournament method provides the mechanism for advanced agents
	 * to randomly choose a tournament and request opponent's past information.
	 * 
	 * @return getRandomTournament : randomly selected tournament
	 */
	public static int requestRandomTournament() {

		// Generate Random tournament
		Random rand = new Random();
		int randomTournamentNumber = rand.nextInt((currentTournament - 0) + 1);

		return randomTournamentNumber;
	}

	

}
