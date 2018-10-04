import logist.task.Task;
import logist.topology.Topology.City;
import logist.task.Task;

/**
 * This Class represents a State
 *
 */
public class State {
	private City currentCity;
	private City destinationCity;
	
	public State(City currentCity, City destinationCity) {
		this.currentCity = currentCity;
		this.destinationCity = destinationCity;
	}
	
	//TODO: Check if this works. There might be an issue with Cities not being equal
	 @Override
	 public boolean equals(Object that) {
	 return (this.currentCity == ((State) that).currentCity) && (this.destinationCity == ((State) that).destinationCity);
	 }
}
