import logist.task.Task;
import logist.topology.Topology.City;
import logist.task.Task;

/**
 * This Class represents a State
 *
 */
public class State {
	private City currentCity;
	private Task availableTask;

	public State(City currentCity, Task availableTask) {
		this.currentCity = currentCity;
		this.availableTask = availableTask;
	}
}
