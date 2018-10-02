package template;

import java.util.List;
import java.util.Random;

import logist.simulation.Vehicle;
import logist.agent.Agent;
import logist.behavior.ReactiveBehavior;
import logist.plan.Action;
import logist.plan.Action.Move;
import logist.plan.Action.Pickup;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;

public class ReactiveTemplate implements ReactiveBehavior {

	private Random random;
	private double pPickup;
	private int numActions;
	private Agent myAgent;

	@Override
	public void setup(Topology topology, TaskDistribution td, Agent agent) {

		// Reads the discount factor from the agents.xml file.
		// If the property is not present it defaults to 0.95
		Double discount = agent.readProperty("discount-factor", Double.class,
				0.95);

		this.random = new Random();
		this.pPickup = discount;
		this.numActions = 0;
		this.myAgent = agent;
		
		printTables(topology, td);
	}

	@Override
	public Action act(Vehicle vehicle, Task availableTask) {
		Action action;

		if (availableTask == null || random.nextDouble() > pPickup) {
			City currentCity = vehicle.getCurrentCity();
			action = new Move(currentCity.randomNeighbor(random));
		} else {
			action = new Pickup(availableTask);
		}
		
		if (numActions >= 1) {
			System.out.println("The total profit after "+numActions+" actions is "+myAgent.getTotalProfit()+" (average profit: "+(myAgent.getTotalProfit() / (double)numActions)+")");
		}
		numActions++;
		
		return action;
	}
	
	public void printTables(Topology topology, TaskDistribution td){

		int numCities = topology.size();

		//print city names
		List<City> cityList = topology.cities();
		for (int i = 0; i<numCities; i++)
		{			
			for (int j = 0; j<numCities; j++)
			{				
				System.out.print(cityList.get(i).name+" "+cityList.get(j).name+", ");
			}
			System.out.println("");
		}
		System.out.println("");

		//print probabilities of having tasks
		for (int i = 0; i<numCities; i++)
		{			
			for (int j = 0; j<numCities; j++)
			{
				double probability = td.probability(cityList.get(i), cityList.get(j));
				System.out.print(probability+", ");
			}
			System.out.println("");
		}
		System.out.println("");
		
		//print rewards for tasks
		for (int i = 0; i<numCities; i++)
		{			
			for (int j = 0; j<numCities; j++)
			{
				int reward = td.reward(cityList.get(i), cityList.get(j));
				System.out.print(reward+", ");
			}
			System.out.println("");
		}		
		System.out.println("");
		
		//print costs of travel
		for (int i = 0; i<numCities; i++)
		{			
			for (int j = 0; j<numCities; j++)
			{
				double distance = cityList.get(i).distanceTo(cityList.get(j));
				System.out.print(distance+", ");
			}
			System.out.println("");
		}		
		System.out.println("");
	}
}
