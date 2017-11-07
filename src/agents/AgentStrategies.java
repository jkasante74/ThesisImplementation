package agents;

import historicalInformationManager.HIM;

public class AgentStrategies {

	// Parameters for Agent strategies
	public static boolean infoAcquired = false;

	// Private Variables
	private static final char COOPERATE = 'C';
	private static final char DEFECT = 'D';

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

			double opponentRating = getOpponentPastInfo(requestingAgentID,
					opponentID);
			updateBelief(requestingAgentID, opponentID, opponentRating);

			if (Agent.readOpponentRating(requestingAgentID, opponentID) >= 0.9)
				matchAction = COOPERATE;

			else
				matchAction = DEFECT;

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
			double opponentRating = getOpponentPastInfo(requestingAgentID,
					opponentID);

			updateBelief(requestingAgentID, opponentID, opponentRating);

			// Exploit naive cooperators and protect from defectors
			if ((Agent.readOpponentRating(requestingAgentID, opponentID) <= 0.1)
					|| (Agent.readOpponentRating(requestingAgentID, opponentID) >= 0.9)) {
				matchAction = DEFECT;
			}

			// Defect 60% of time if unsure
			else {
				if (Math.random() > 0.4)
					matchAction = DEFECT;
				else
					matchAction = COOPERATE;

			}

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
		double opponentRating = 0;

		switch (pastInfoTypeIndex) {

		// Get opponent's first action
		case 0:
			opponentInformation = HIM.requestOppPastInfo(requestingAgentID,
					opponentID, pastInfoTypeIndex);
			if (infoAcquired) {
				if (opponentInformation.equalsIgnoreCase("D"))
					opponentRating = 0.0;
				else
					opponentRating = 1.0;

			}
			break;

		// Get opponent's first defection
		case 1:
			opponentInformation = HIM.requestOppPastInfo(requestingAgentID,
					opponentID, pastInfoTypeIndex);
			if (infoAcquired) {
				int pastInfo = Integer.parseInt(opponentInformation);
				opponentRating = pastInfo / 10;
			}
			break;

		// Request all opponent's past actions
		case 2:
			opponentInformation = HIM.requestOppPastInfo(requestingAgentID,
					opponentID, pastInfoTypeIndex);
			if (infoAcquired)
				opponentRating = calcOppRating(opponentInformation);
			break;

		// Request all opponent's past actions from a random tournament
		case 3:
			opponentInformation = HIM.requestOppPastInfo(requestingAgentID,
					opponentID, pastInfoTypeIndex);
			if (infoAcquired) {
				opponentRating = calcOppRating(opponentInformation);
			}
			break;

		// Request past actions of those who played against opponent
		case 4:
			opponentInformation = HIM.requestOppPastInfo(requestingAgentID,
					opponentID, pastInfoTypeIndex);
			if (infoAcquired)
				opponentRating = calcOppRating(opponentInformation);
			break;
		}
		return opponentRating;
	}

	/**
	 * calcOppRating method calculates the current rating of the opponent based
	 * on the past information received from HIM component
	 * 
	 * @param opponentInformation
	 *            : list of past actions of opponent or secondary opponents
	 *            received from HIM.
	 * @return opponentRating : agent's calculated rating of the opponent.
	 * 
	 */
	private static double calcOppRating(String opponentInformation) {

		int numOfCooperations = 0, numOfDefections = 0;
		double opponentRating = 0.0;

		for (int i = 0; i < opponentInformation.length(); i++) {
			if (opponentInformation.charAt(i) == COOPERATE)
				numOfCooperations++;

			if (opponentInformation.charAt(i) == DEFECT)
				numOfDefections++;
		}

		if ((numOfCooperations + numOfDefections) == 0)
			opponentRating = 0;
		else
			opponentRating = numOfCooperations
					/ (numOfCooperations + numOfDefections);

		return opponentRating;
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
	 * @param opponentRating
	 *            : agent's calculated rating of the opponent.
	 * 
	 */
	private static void updateBelief(int requestingAgentID, int opponentID,
			double opponentRating) {

		Agent.agentBeliefs[requestingAgentID][opponentID] = opponentRating;
	}

}
