import logist.task.Task;
import logist.topology.Topology.City;
import logist.task.Task;

/**
 * This Class represents a State
 *
 */
public class State {
	private City fromCity;
	private City toCity;

	public State(City fromCity, City toCity) {
		this.fromCity = fromCity;
		this.toCity = toCity;
	}

	// TODO: Check if this works. There might be an issue with Cities not being
	// equal
	@Override
	public boolean equals(Object that) {
		return (this.fromCity == ((State) that).fromCity)
				&& (this.toCity == ((State) that).toCity);
	}

	public City getFromCity() {
		return this.fromCity;
	}
	
	public City getToCity() {
		return this.toCity;
	}
}
