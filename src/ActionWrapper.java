import logist.plan.Action;
import logist.topology.Topology.City;

/**
 * Class whose purpose is to act as an action but who also stores the destination of the action
 *
 */
public class ActionWrapper {
	private Action action;
	private City toCity;
	
	public ActionWrapper(Action action, City toCity) {
		this.action = action;
		this.toCity = toCity;
	}
	
	public City getToCity() {
		return this.toCity;
	}
	
	public Action getAction() {
		return this.action;
	}
}
