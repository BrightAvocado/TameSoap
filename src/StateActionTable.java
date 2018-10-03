import java.util.Map;

import logist.plan.Action;
import logist.task.TaskDistribution;

/**
 * This class provides you with the best action to do given a certain state
 *
 */
public class StateActionTable {
	private Map<State, Action> state2BestActionMap;
	
	public StateActionTable(TaskDistribution td) {
		//TODO : Make it so that it runs RLA upon construction and population the state2BestActionMap
	}
	
	/**
	 * Returns the best action from the State state according to the RLA
	 * @param state
	 * @return
	 */
	public Action best(State state) {
		return (Action) state2BestActionMap.get(state);
	}
}
