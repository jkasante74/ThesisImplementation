package agents;

import javax.swing.JOptionPane;

import historicalInformationManager.HIM;

public class AgentStrategies {

	// Parameters for Agent strategies
	public static boolean infoAcquired = false;

	// Private Variables
	private static final char COOPERATE = 'C';
	private static final char DEFECT = 'D';
	private static final double HIGH_RATING = 1.00;

	/**
	 * DefectAll strategy defects at all times no matter what the opponent does
	 * 
	 * @return 'D' : Match Action associated with the DefectAll strategy
	 * 
	 * The algorithm and code structure for the DefectAll Strategy was
	 * taken and modified from
	 * http://www.prisoners-dilemma.com/java/ipdlx/ipdlx_javadocs/
	 * 
	 */

	static char defectAll() {

		return DEFECT;
	}

	/**
	 * CooperateAll strategy cooperates at all times no matter what the opponent
	 * does
	 * 
	 * @return 'C' : Match Action associated with the CooperateAll strategy
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

	protected static char advanceCooperator(int agentID, int opponentID,
			int currentTournament, int currentRound) {

		char matchAction = COOPERATE; // Set default action

		if ((currentTournament == 0) && (currentRound == 0)) {
			matchAction = COOPERATE; // First action in a new experiment
		}

		else {

			double opponentRating = getOpponentPastInfo(agentID, opponentID);
			updateBelief(agentID, opponentID, opponentRating);
				
			if(Agent.readOpponentRating(agentID,
					opponentID) >= 0.9)
				matchAction = COOPERATE; // promote cooperation with Naive_C
			
			else
				matchAction = DEFECT; // protect against exploiters if unsure  					

		}

		return matchAction;
	}

	/**
	 * advancedDefect strategy cooperates or defects based on a strategic
	 * analysis of acquired past information and updated belief on opponent with
	 * a higher inclination to defect
	 * 
	 * @return matchAction : Action taken after strategic decision
	 * 
	 */

	static char advanceDefector(int agentID, int opponentID,
			int currentTournament, int currentRound) {

		char matchAction = DEFECT; // Set default action

		if ((currentTournament == 0) && (currentRound == 0)) {
			matchAction = DEFECT; // First action in a new experiment
		}

		else {

			double opponentRating = getOpponentPastInfo(agentID, opponentID);

			updateBelief(agentID, opponentID, opponentRating);

			if ((Agent.readOpponentRating(agentID,
					opponentID) <= 0.1) || (Agent.readOpponentRating(agentID,
							opponentID) >= 0.9)) {
				matchAction = DEFECT; // Exploit naive cooperators and protect
										// against exploiters
			}

			else {
				if (Math.random() > 0.4)
					matchAction = DEFECT; // Defect 60% of time when unsure
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
	 * @param agentID : Requesting agent ID
	 * 
	 * @param opponentID : Opponent ID
	 * 
	 * 
	 */

	private static double getOpponentPastInfo(int agentID, int opponentID) {

		String opponentInformation = "";
		int pastInfoTypeIndex = Agent.expInfoRequestOption;
		double opponentRating = 0;
	//	JOptionPane.showMessageDialog(null, pastInfoTypeIndex);
		switch (pastInfoTypeIndex) {
		
		case 0:
			opponentInformation = HIM.requestOppPastInfo(agentID, opponentID,
					pastInfoTypeIndex); //Request opponent's first action
			if (infoAcquired) {
				if (opponentInformation.equalsIgnoreCase("D"))
					opponentRating = 0.0; // first time defectors get low rating
				else
					opponentRating = 1.0; // first time cooperators get high rating

			}
			break;
		
		case 1:
			opponentInformation = HIM.requestOppPastInfo(agentID, opponentID,
					pastInfoTypeIndex); // Request opponent's first defection
			if (infoAcquired) {
				int pastInfo = Integer.parseInt(opponentInformation);
				opponentRating = pastInfo / 10;
			}
			break;
		 
		case 2:
				opponentInformation = HIM.requestOppPastInfo(agentID, opponentID,
				pastInfoTypeIndex); //Request all opponent's past actions
			//	JOptionPane.showMessageDialog(null, (opponentID + 1) + " " + opponentInformation);
			if (infoAcquired) 
				opponentRating = calcOppRating(opponentInformation);
			
			break;

			
		case 3:

			opponentInformation = HIM.requestOppPastInfo(agentID, opponentID,
					pastInfoTypeIndex); // Request random past opponent info

			if (infoAcquired) {

				opponentRating = calcOppRating(opponentInformation);

			}
			break;

		
		case 4:

			opponentInformation = HIM.requestOppPastInfo(agentID, opponentID,
					pastInfoTypeIndex); //Request secondary opponents past info

			
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
			opponentRating = 0; // no info acquired
		else
			opponentRating = numOfCooperations / (numOfCooperations + numOfDefections); 

		return opponentRating;
	}

	/**
	 * updateBelief upon invocation by an agent updates the opponent's current
	 * rating in the requesting agent's belief base
	 * 
	 * 
	 * @param agentID : Requesting agent ID
	 * 
	 * @param opponentID : Opponent ID
	 * 
	 * @param opponentRating
	 *            : agent's calculated rating of the opponent.
	 * 
	 */
	private static void updateBelief(int agentID, int opponentID,
			double opponentRating) {
	/*	if (opponentRating > 0.0){
			Agent.agentBeliefs[agentID][opponentID] = (Agent.agentBeliefs[agentID][opponentID] + opponentRating) / 2;
			JOptionPane.showMessageDialog(null, Agent.agentBeliefs[agentID][opponentID] );
		}
		else
			Agent.agentBeliefs[agentID][opponentID] = Agent.agentBeliefs[agentID][opponentID];
	*/
		Agent.agentBeliefs[agentID][opponentID] = opponentRating;
	}

}
