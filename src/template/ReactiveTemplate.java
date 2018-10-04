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

import java.util.ArrayList;

public class ReactiveTemplate implements ReactiveBehavior {

	private static final double COST_PER_KM = 1;
	private Random random;
	private double pPickup;
	private int numActions;
	private Agent myAgent;
	private int numCities;	
	private int numStates;
	
	private ArrayList<ArrayList<Double>> P = new ArrayList<ArrayList<Double>>();

	@Override
	public void setup(Topology topology, TaskDistribution td, Agent agent) {

		// Reads the discount factor from the agents.xml file.
		// If the property is not present it defaults to 0.95
		Double discount = agent.readProperty("discount-factor", Double.class,
				0.95);

		this.random = new Random();
		this.pPickup = discount;
		this.myAgent = agent;

		this.numCities = topology.size();
		this.numStates = numCities*(numCities+1);
		this.numActions = numCities + 1;

		computeProfitMatrix(topology,td);
		//testProfitMatrix(topology);
		//printTables(topology, td);
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


	public void computeProfitMatrix(Topology topology, TaskDistribution td)
	{
		//print city names
		List<City> cityList = topology.cities();
				
		//initialize maps for p, r and d
		for(int current_from = 0; current_from<numCities; current_from++){
			
			for(int current_to = 0; current_to<numCities+1; current_to++){
				
				int s = (current_from)*(numCities+1)+current_to;

				ArrayList<Double> nullList = new ArrayList<Double>();
				P.add(nullList);
				
				for(int a = 0; a<numActions; a++){
				
					P.get(s).add(a, (double)0); //initialize values as 0
			
					City fromCity = cityList.get(current_from);
					
					if (a < numCities) //no task
					{		
						City toCityAction = cityList.get(a);				
						P.get(s).set(a, -1*fromCity.distanceTo(toCityAction)*COST_PER_KM);
					}				
					else //if there is a task
					{
						if (current_to < numCities){
							City toCity = cityList.get(current_to);
							double reward = (double) td.reward(fromCity, toCity);
							double distanceCost = fromCity.distanceTo(toCity)*COST_PER_KM;
							double profit = reward - distanceCost;
				
							P.get(s).set(a, profit);

						}
					}				
				}	
			}
		}
		System.out.println("Look Here:"+P.get(2).get(9));
	}
	
	public void printTables(Topology topology, TaskDistribution td){

		//print city names
		List<City> cityList = topology.cities();
		
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
	
	public void testProfitMatrix(Topology topology){
				
		System.out.println("             Action:   0     1     2      3        4        5       6       7       8     9");
		for(int i=0;i<numStates;i++){
			System.out.print("State = "+i+"; Profits = ");
			for(int a=0;a<numActions;a++){
				
				System.out.print(P.get(i).get(a) + " ");

			}
			System.out.println("");
		}
	}
}
