import java.util.Map;

import logist.plan.Action;
import logist.task.TaskDistribution;

/**
 * This class provides you with the best action to do given a certain state
 *
 */
public class RLA {
	private Map stateBestAction;
	
	public void RLA(TaskDistribution td) {
		//TODO : Make it so that it runs RLA upon construction and population the stateBestActionMap
	}
	
	/**
	 * Returns the best action from the State state according to the RLA
	 * @param state
	 * @return
	 */
	public Action Best(State state) {
		return (Action) stateBestAction.get(state);
	}
}
