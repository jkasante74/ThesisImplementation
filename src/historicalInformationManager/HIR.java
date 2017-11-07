package historicalInformationManager;

/**
 * The Historical Information Repository (HIR) is basically a storage unit for
 * all request limits, strategies, scores, and actions of agents in the
 * simulation experiment. This passive component is updated at the end of every
 * round by HIM.
 * 
 * @author jonathanasante
 * 
 */
abstract class HIR {

	// HIR Parameters
	protected static int[] agentsRequestLimit;
	protected static String[][] data;
	protected static String[][][][] chartsData;
	protected static char[][][] agentActionsDbase;
	protected static String[] agentActs;
}
