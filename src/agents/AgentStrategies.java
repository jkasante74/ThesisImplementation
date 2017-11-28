package agents;

import javax.swing.JOptionPane;

import historicalInformationManager.HIM;

public class AgentStrategies {

	// Parameters for Agent strategies
	public static boolean infoAcquired = false;

	// Private Variables
	private static final char COOPERATE = 'C';
	private static final char DEFECT = 'D';
	private static float TEMPT;
	private static float REWARD;
	private static float PUNISH;
	private static char opponentFirstAct;
	@SuppressWarnings("unused")
	private static float SUCKER;
	private static double ALPHA = 0.5, BETA = 0.5,DISCOUNTFACTOR = 0.9;
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
			
			// Make strategic decision if agent is advanced
			else
			//	matchAction = reciprocityAction(opponentCooperateRatio,requestingAgentID, opponentID);
				matchAction = superRationalWithDiscountFactor(opponentCooperateRatio);
		}

		return matchAction;
	}

	
	//advance Defector here
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
			
			// Decide using returns-based beliefs
			else
			//	matchAction = reciprocityAction(opponentCooperateRatio,requestingAgentID, opponentID);
				matchAction = superRationalWithDiscountFactor(opponentCooperateRatio);
		}
		return matchAction;
	}

	
	private static char superRationalWithDiscountFactor(double opponentCooperateRatio) {
		char matchAction; // Set default action

		if(opponentFirstAct == COOPERATE)
			matchAction = COOPERATE;
		
		else{
		
			// Find opponent defect ratio
			double opponentDefectRatio = 1.0 - opponentCooperateRatio;
		
		
			//Calculate opponent cooperate expected function
			double opponentCooperateExpectation = opponentCooperateRatio * (REWARD / (1 - DISCOUNTFACTOR));
			
			//Calculate opponent defect expected function
			double opponentDefectExpectation = opponentDefectRatio * (TEMPT + ((DISCOUNTFACTOR * PUNISH) / (1 - DISCOUNTFACTOR)));
		
			// JOptionPane.showMessageDialog(null, "Opponent Cooperate Expectation : "+opponentCooperateExpectation +"\n"+"Opponent Defect Expectation : "+opponentDefectExpectation);
		
			// Make decision based on opponent Expectations
			if(opponentCooperateExpectation >= opponentDefectExpectation)
				matchAction = COOPERATE;
			else
				matchAction = DEFECT;
		}
		
		return matchAction;
	}

	
	/**
	 * returnAltruisticReciprocityAction method returns the action based on altruism reciprocity
	 * such that the agent cooperate only when the opponent has a higher cooperating index else it defects
	 * 
	 * @param opponentCooperateRatio
	 * @param requestingAgentID
	 * @param opponentID
	 * @return
	 */
	private static char reciprocityAction(
			double opponentCooperateRatio, int requestingAgentID, int opponentID) {
		
		char matchAction;
		if(opponentCooperateRatio >= 0.5)
			matchAction = COOPERATE;
		else
			matchAction = DEFECT;
			
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
			
			if (infoAcquired){
				opponentCooperateRatio = calcOppRating(opponentInformation);
				opponentFirstAct = opponentInformation.charAt(0);
			//	JOptionPane.showMessageDialog(null, opponentFirstAct);

			}
			break;

		// Request all opponent's past actions from a random tournament
		case 3:
			opponentInformation = HIM.requestOppPastInfo(requestingAgentID,
					opponentID, pastInfoTypeIndex);
			if (infoAcquired) {
				opponentCooperateRatio = calcOppRating(opponentInformation);
				opponentFirstAct = opponentInformation.charAt(0);
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
			if (opponentInformation.charAt(i) == DEFECT)
				numOfDefections++;
			else
				numOfCooperations++;
		}

		if ((numOfCooperations + numOfDefections) == 0)
			opponentCooperateRatio = 0;
		else
			opponentCooperateRatio = numOfCooperations
					/ (numOfCooperations + numOfDefections);
		
	//	JOptionPane.showMessageDialog(null, "Agent has C : "+ numOfCooperations +  " D : "+ numOfDefections + " = " + (numOfCooperations/(numOfCooperations + numOfDefections)));

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

	
	
	
	
	public static void setPayOffValues(int[] currentSetupValues) {
		
		TEMPT = currentSetupValues[0];
		REWARD = currentSetupValues[1];
		PUNISH = currentSetupValues[2];
		SUCKER = currentSetupValues[3];
	}
	
	
}

