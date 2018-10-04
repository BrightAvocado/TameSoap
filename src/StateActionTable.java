import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import logist.plan.Action;
import logist.task.TaskDistribution;
import logist.topology.Topology.City;
import logist.plan.Action.Move;
import logist.plan.Action.Pickup;

/**
 * This class provides you with the best action to do given a certain state
 *
 */
public class StateActionTable {
	private Map<State, Action> state2BestActionMap;
	
	private Map<State, Map<State, Double>> stateTransitionProbability; //T(S,a,S')
	
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
	
	//TODO: Change this to private
	public Map<State, Map<State, Double>> computeStateTransitionProbability(
			Map<City, Map<City, Double>> probabilities,
			Map<City, Map<City, Double>> rewards,
			Map<City, Map<City, Double>> distances) {
		
		Map<State, Map<State, Double>> T = new HashMap<State, Map<State, Double>>();
		
		//Create list of ALL possible State
		City[] cityList = probabilities.keySet().toArray(new City[0]);
		ArrayList<State> stateList = new ArrayList<State>();
		for (City fromCity: cityList) {
			for (City toCity: cityList) {
				stateList.add(new State(toCity, fromCity));
			}
			/*
			 * Add a state where the toCity is null, which means there is not task in the fromCity
			 */
			stateList.add(new State(fromCity, null));
		}
		
		//Create list of ALL possible Action
		ArrayList<ActionWrapper> actionWrapperList = new ArrayList<ActionWrapper>();
		
		//Add the actions of moving to all cities 
		for (City city: cityList) {
			ActionWrapper actionWrapper = new ActionWrapper(new Move(city), city);
			actionWrapperList.add(actionWrapper);//The action is "Go to *city*, I'm not taking the cast"
		}
		//Add the action of pick up
		ActionWrapper pickupWrapper = new ActionWrapper(new Pickup(null), null);
		actionWrapperList.add(pickupWrapper);
		
		//Step 1
		for (State currentState: stateList) {
			for (State futureState: stateList) {
				for (ActionWrapper actionWrapper: actionWrapperList) {
					if ((currentState.getToCity() != currentState.getFromCity())
							&& (futureState.getToCity() != futureState.getFromCity())
							&& (currentState.getFromCity() != futureState.getFromCity())) {
						
					} else {
						
					}
				}
			}
		}
		return T;
	}
}