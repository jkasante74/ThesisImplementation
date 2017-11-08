package agents;

import historicalInformationManager.HIM;

public class AgentStrategies {

	// Parameters for Agent strategies
	public static boolean infoAcquired = false;

	// Private Variables
	private static final char COOPERATE = 'C';
	private static final char DEFECT = 'D';
	private static float tempt;
	private static float reward;
	private static float punish;
	private static float sucker;
	/**
	 * DefectAll strategy defects at all times no matter what the opponent does
	 * 
	 * @return DEFECT : 'D'
	 * 
	 *         The algorithm and code structure for the DefectAll Strategy was
	 *         taken and modified from
	 *         http://www.prisoners-dilemma.com/java/ipdlx/ipdlx_javadocs/
	 * 
	 */
	static char defectAll() {

		return DEFECT;
	}

	/**
	 * CooperateAll strategy cooperates at all times no matter what the opponent
	 * does
	 * 
	 * @return COOPERATE : 'C'
	 * 
	 *         The algorithm and code structure for the CooperateAll Strategy
	 *         could be located here
	 *         http://www.prisoners-dilemma.com/java/ipdlx/ipdlx_javadocs/
	 * 
	 */
	static char cooperateAll() {

		return COOPERATE;
	}

	/**
	 * advanceCooeprator strategy cooperates or defect based on acquired past
	 * information on opponents.
	 * 
	 * @return matchAction : Action taken after strategic decision
	 * 
	 */

	protected static char advanceCooperator(int requestingAgentID,
			int opponentID, int currentTournament, int currentRound) {

		char matchAction = COOPERATE; // Set default action

		// First action in a new experiment
		if ((currentTournament == 0) && (currentRound == 0)) {
			matchAction = COOPERATE;
		}

		// Act based on updated beliefs from past information
		else {

			double opponentCooperateRatio = getOpponentPastInfo(requestingAgentID,
					opponentID);
			updateBelief(requestingAgentID, opponentID, opponentCooperateRatio);
			//double opponentCooperatingRating = Agent.readOpponentRating(requestingAgentID, opponentID);
			
			// Promote cooperation
			if (opponentCooperateRatio >= 0.9)
				matchAction = COOPERATE;
			 
			// Protect against exploiters
			else if(opponentCooperateRatio <= 0.1)
				matchAction = DEFECT;
			
			// Decide using Returns-based belief
			else
				matchAction = returnBasedBeliefAction(opponentCooperateRatio,requestingAgentID, opponentID);

		}

		return matchAction;
	}


	/**
	 * advancedDefector strategy cooperates or defects based on a strategic
	 * analysis of acquired past information and updated belief on opponent with
	 * a higher inclination to defect
	 * 
	 * @return matchAction : Action taken after strategic decision
	 * 
	 */
	static char advanceDefector(int requestingAgentID, int opponentID,
			int currentTournament, int currentRound) {

		char matchAction = DEFECT; // Set default action

		// First action in a new experiment
		if ((currentTournament == 0) && (currentRound == 0)) {
			matchAction = DEFECT;
		}

		// Act based on updated beliefs from past information
		else {
			double opponentCooperateRatio = getOpponentPastInfo(requestingAgentID,
					opponentID);

			updateBelief(requestingAgentID, opponentID, opponentCooperateRatio);

			// Exploit naive cooperators and protect from defectors
			if ((Agent.readOpponentRating(requestingAgentID, opponentID) <= 0.1)
					|| (Agent.readOpponentRating(requestingAgentID, opponentID) >= 0.9)) {
				matchAction = DEFECT;
			}
			
			// Decide using Returns-based beliefs
			else
				matchAction = returnBasedBeliefAction(opponentCooperateRatio,requestingAgentID, opponentID);
		}
		return matchAction;
	}

	/**
	 * getOpponentPastInfo request for past opponent action from the HIM and
	 * calculate the cooperating level of opponent which is stored in the
	 * agent's belief base
	 * 
	 * @param requestingAgentID
	 *            : Requesting agent ID
	 * 
	 * @param opponentID
	 *            : Opponent ID
	 * 
	 * 
	 */
	private static double getOpponentPastInfo(int requestingAgentID,
			int opponentID) {

		String opponentInformation = "";
		int pastInfoTypeIndex = Agent.expInfoRequestOption;
		double opponentCooperateRatio = 0;

		switch (pastInfoTypeIndex) {

		// Get opponent's first action
		case 0:
			opponentInformation = HIM.requestOppPastInfo(requestingAgentID,
					opponentID, pastInfoTypeIndex);
			if (infoAcquired) {
				if (opponentInformation.equalsIgnoreCase("D"))
					opponentCooperateRatio = 0.0;
				else
					opponentCooperateRatio = 1.0;

			}
			break;

		// Get opponent's first defection
		case 1:
			opponentInformation = HIM.requestOppPastInfo(requestingAgentID,
					opponentID, pastInfoTypeIndex);
			if (infoAcquired) {
				int pastInfo = Integer.parseInt(opponentInformation);
				opponentCooperateRatio = pastInfo / 10;
			}
			break;

		// Request all opponent's past actions
		case 2:
			opponentInformation = HIM.requestOppPastInfo(requestingAgentID,
					opponentID, pastInfoTypeIndex);
			if (infoAcquired)
				opponentCooperateRatio = calcOppRating(opponentInformation);
			break;

		// Request all opponent's past actions from a random tournament
		case 3:
			opponentInformation = HIM.requestOppPastInfo(requestingAgentID,
					opponentID, pastInfoTypeIndex);
			if (infoAcquired) {
				opponentCooperateRatio = calcOppRating(opponentInformation);
			}
			break;

		// Request past actions of those who played against opponent
		case 4:
			opponentInformation = HIM.requestOppPastInfo(requestingAgentID,
					opponentID, pastInfoTypeIndex);
			if (infoAcquired)
				opponentCooperateRatio = calcOppRating(opponentInformation);
			break;
		}
		return opponentCooperateRatio;
	}

	/**
	 * calcOppRating method calculates the current rating of the opponent based
	 * on the past information received from HIM component
	 * 
	 * @param opponentInformation
	 *            : list of past actions of opponent or secondary opponents
	 *            received from HIM.
	 * @return opponentCooperateRatio : agent's calculated rating of the opponent.
	 * 
	 */
	private static double calcOppRating(String opponentInformation) {

		double numOfCooperations = 0.0, numOfDefections = 0.0;
		double opponentCooperateRatio = 0.0;

		for (int i = 0; i < opponentInformation.length(); i++) {
			if (opponentInformation.charAt(i) == COOPERATE)
				numOfCooperations++;

			if (opponentInformation.charAt(i) == DEFECT)
				numOfDefections++;
		}

		if ((numOfCooperations + numOfDefections) == 0)
			opponentCooperateRatio = 0;
		else
			opponentCooperateRatio = numOfCooperations
					/ (numOfCooperations + numOfDefections);
		
		return opponentCooperateRatio;
	}

	/**
	 * updateBelief upon invocation by an agent updates the opponent's current
	 * rating in the requesting agent's belief base
	 * 
	 * @param requestingAgentID
	 *            : Requesting agent ID
	 * 
	 * @param opponentID
	 *            : Opponent ID
	 * 
	 * @param opponentCooperateRatio
	 *            : agent's calculated rating of the opponent.
	 * 
	 */
	private static void updateBelief(int requestingAgentID, int opponentID,
			double opponentCooperateRatio) {

		Agent.agentBeliefs[requestingAgentID][opponentID] = opponentCooperateRatio;
	}

	/**
	 * returnBasedBeliefAction returns the requesting agent's action based on returns-based belief about the agent
	 * @param opponentCooperateRatio
	 * @param requestingAgentID
	 * @param opponentID
	 * @return
	 */
	private static char returnBasedBeliefAction(double opponentCooperateRatio,
			int requestingAgentID, int opponentID) {
		
		double gameValue = 0.0;
		
		//Calculate opponent defect ratio
		double opponentDefectRatio = (1 - opponentCooperateRatio);
		
		// Calculate sum of rewards
		double agentCooperateReward = ((reward + Agent.gameValue[requestingAgentID][opponentID]) * opponentCooperateRatio) 
										+ ((sucker + Agent.gameValue[requestingAgentID][opponentID]) * opponentDefectRatio);
		
		double agentDefectionReward = ((tempt  + Agent.gameValue[requestingAgentID][opponentID]) * opponentCooperateRatio)  
										+ ((punish  + Agent.gameValue[requestingAgentID][opponentID]) * opponentDefectRatio);
		
		// Calculate agent's probability of playing a strategy
		double agentCooperateProbability = (agentCooperateReward / (agentCooperateReward + agentDefectionReward));
		double agentDefectProbability =    (agentDefectionReward / (agentCooperateReward + agentDefectionReward));
		
		double agentCProbability = (Math.round(agentCooperateProbability * 100));
		double agentDProbability = (Math.round(agentDefectProbability * 100));
		
		
		// Calculate the game value
		gameValue = (agentCooperateProbability* (agentCooperateProbability * reward) + (agentDefectProbability * sucker)) 
				+ (agentDefectProbability* (agentCooperateProbability * tempt) + (agentDefectProbability * punish));
		double gameValueInSig = Math.round(gameValue * 100);
		
		// Update game value with opponent
		Agent.gameValue[requestingAgentID][opponentID] = (gameValueInSig/100); 
		
		// Return Action based on highest probability
		if((agentDProbability/100) > (agentCProbability/100))
			return DEFECT;
		else
			return COOPERATE;
	}
	
	
	
	public static void setPayOffValues(int[] currentSetupValues) {
		
		tempt = currentSetupValues[0];
		reward = currentSetupValues[1];
		punish = currentSetupValues[2];
		sucker = currentSetupValues[3];
	}
	
	
}
