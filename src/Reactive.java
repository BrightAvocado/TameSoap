import logist.simulation.Vehicle;
import logist.agent.Agent;
import logist.behavior.ReactiveBehavior;
import logist.plan.Action;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;


public class Reactive implements ReactiveBehavior {

	private double discount;
	private int numActions;
	private Agent myAgent;
	private StateActionTable stateActionTable;

	@Override
	public void setup(Topology topology, TaskDistribution td, Agent agent) {

		// Reads the discount factor from the agents.xml file.
		// If the property is not present it defaults to 0.95
		Double discount = agent.readProperty("discount-factor", Double.class,
				0.95);

		this.discount = discount;
		this.numActions = 0;
		this.myAgent = agent;
		this.stateActionTable = new StateActionTable(topology, td);
	}


	/**
	 * Determine the best Action according to the stateActionTable of this Agent and do that.
	 * Then log how many actions have been made so far and print it to the user.
	 */
	@Override
	public Action act(Vehicle vehicle, Task availableTask) {
		Action action = null;
		
		City currentCity = vehicle.getCurrentCity();
		
		action = this.stateActionTable.getAction(currentCity, availableTask);		
		
		if (numActions >= 1) {
			System.out.println("The total profit after "+numActions+" actions is "+myAgent.getTotalProfit()+" (average profit: "+(myAgent.getTotalProfit() / (double)numActions)+")");
		}
		numActions++;
		
		return action;
	}
}