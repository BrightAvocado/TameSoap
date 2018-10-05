import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import logist.plan.Action;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;
import logist.plan.Action.Move;
import logist.plan.Action.Delivery;
import logist.plan.Action.Pickup;

/**
 * This class provides you with the best action to do given a certain state
 *
 */
public class StateActionTable {

	private static final double COST_PER_KM = 1;

	private List<City> cityList;
	private int numCities;
	private int numActions;
	private ArrayList<ArrayList<ArrayList<Double>>> T;
	private ArrayList<ArrayList<Double>> P = new ArrayList<ArrayList<Double>>();
	private ArrayList<Integer> best;
	private double gamma;// discount factor
	
	public StateActionTable(Topology topology, TaskDistribution td) {
		// TODO : Make it so that it runs RLA upon construction and population the
		// state2BestActionMap
		this.cityList = topology.cities();
		System.out.println(cityList);//Debug
		this.numCities = this.cityList.size();
		this.numActions = this.numCities + 1;
		this.gamma = 0.95;
		computeStateTransitionProbability(td);
		computeProfitMatrix(topology, td);
		computeBest();

	}

	private void computeStateTransitionProbability(TaskDistribution td) {

		// Initialize the T as full of zeros
		ArrayList<ArrayList<ArrayList<Double>>> T = new ArrayList<ArrayList<ArrayList<Double>>>();

		for (int i = 0; i < this.numCities * (this.numCities + 1); i++) {
			ArrayList<ArrayList<Double>> zero_array = new ArrayList<ArrayList<Double>>();
			for (int j = 0; j < this.numActions; j++) {
				ArrayList<Double> zero_column = new ArrayList<Double>();
				for (int k = 0; k < this.numCities * (this.numCities + 1); k++) {
					zero_column.add(0.0);
				}
				zero_array.add(zero_column);
			}
			T.add(zero_array);
		}

		// SO FAR SO GOOD

		int valueToEncodeState = this.numCities + 1;// TODO: I think this works; not sure

		for (int current_from = 0; current_from < this.numCities; current_from++) {
			for (int current_to = 0; current_to < this.numCities + 1; current_to++) {
				int state = current_from * valueToEncodeState + current_to;

				for (int future_from = 0; future_from < this.numCities; future_from++) {
					for (int future_to = 0; future_to < this.numCities + 1; future_to++) {
						int future_state = future_from * valueToEncodeState + future_to;

						/*
						 * Action are encoded such that : if it's a number corresponding to a city, it
						 * means you have a task and you need to go to that city. It its value isn't the
						 * value of a city, then you pick up (right Simon ? TODO : ASK)
						 */
						for (int action = 0; action < this.numActions; action++) {
							if ((current_to != current_from) && (future_to != future_from)
									&& (current_from != future_from)) {
								if ((action == this.numActions - 1) && (current_to == future_from)) {
									if (future_to == this.numCities) {

										// Compute sum
										double sum = 0;
										for (int city_to = 0; city_to < this.numCities; city_to++) {
											sum += td.probability(this.cityList.get(future_from),
													this.cityList.get(city_to));
										}

										T.get(state).get(action).set(future_state, 1 - sum);
									} else {
										double p = td.probability(this.cityList.get(future_from),
												this.cityList.get(future_to));
										T.get(state).get(action).set(future_state, p);
									}
								} else {
									if ((action != current_from) && (action == future_from)) {
										if (future_to == this.numCities) {

											// Compute sum
											double sum = 0;
											for (int city_to = 0; city_to < this.numCities; city_to++) {
												sum += td.probability(this.cityList.get(future_from),
														this.cityList.get(city_to));
											}

											T.get(state).get(action).set(future_state, 1 - sum);

										} else {
											double p = td.probability(this.cityList.get(future_from),
													this.cityList.get(future_to));
											T.get(state).get(action).set(future_state, p);
										}
									}
								}
							}
						}

					}
				}
			}
		}
		this.T = T;
	}

	private void computeProfitMatrix(Topology topology, TaskDistribution td) {
		// print city names
		List<City> cityList = topology.cities();

		// initialize maps for p, r and d
		for (int current_from = 0; current_from < numCities; current_from++) {

			for (int current_to = 0; current_to < numCities + 1; current_to++) {

				int s = (current_from) * (numCities + 1) + current_to;

				ArrayList<Double> nullList = new ArrayList<Double>();
				P.add(nullList);

				for (int a = 0; a < numActions; a++) {

					P.get(s).add(a, (double) 0); // initialize values as 0

					City fromCity = cityList.get(current_from);

					if (a < numCities) // no task
					{
						City toCityAction = cityList.get(a);
						P.get(s).set(a, -1 * fromCity.distanceTo(toCityAction) * COST_PER_KM);
					} else // if there is a task
					{
						if (current_to < numCities) {
							City toCity = cityList.get(current_to);
							double reward = (double) td.reward(fromCity, toCity);
							double distanceCost = fromCity.distanceTo(toCity) * COST_PER_KM;
							double profit = reward - distanceCost;

							P.get(s).set(a, profit);

						}
					}
				}
			}
		}
	}

	// DO THIS
	private void computeBest() {
		// Create and initialize statesConverged
		ArrayList<Boolean> statesConverged = new ArrayList<Boolean>();
		for (int i = 0; i < this.numCities * (this.numCities + 1); i++) {
			statesConverged.add(false);
		}

		boolean converged = false;

		// Create and initialize Q
		ArrayList<ArrayList<Double>> Q = new ArrayList<ArrayList<Double>>();

		for (int i = 0; i < this.numCities * (this.numCities + 1); i++) {
			ArrayList<Double> zero_column = new ArrayList<Double>();
			for (int k = 0; k < this.numCities + 1; k++) {
				zero_column.add(0.0);
			}
			Q.add(zero_column);
		}

		// Create and initialize V, which will eventually store the best values...
		ArrayList<Double> V = new ArrayList<Double>();
		for (int i = 0; i < this.numCities * (this.numCities + 1); i++) {
			V.add(0.0);
		}

		// Create and initialize VTemp
		ArrayList<Double> VTemp = new ArrayList<Double>();
		for (int i = 0; i < this.numCities * (this.numCities + 1); i++) {
			VTemp.add(0.0);
		}
		// Create and initialize best
		ArrayList<Integer> best = new ArrayList<Integer>();
		for (int i = 0; i < this.numCities * (this.numCities + 1); i++) {
			best.add(0);
		}

		int valueToEncodeState = this.numCities + 1;// TODO: I think this works; not sure

		// Start actual algorithm
		while (!converged) {
			for (int current_from = 0; current_from < this.numCities; current_from++) {
				for (int current_to = 0; current_to < this.numCities + 1; current_to++) {

					int state = current_from * valueToEncodeState + current_to;

					for (int action = 0; action < this.numActions; action++) {

						double discounted_future = 0;
						for (int i = 0; i < this.numCities * (this.numCities + 1); i++) {
							discounted_future = discounted_future + this.T.get(state).get(action).get(i) * V.get(i);
						}
						Q.get(state).set(action, this.P.get(state).get(action) + this.gamma * discounted_future);// update
																													// value
					}
					VTemp.set(state, Collections.max(Q.get(state)));
					best.set(state, Q.get(state).indexOf(VTemp.get(state)));
					double error = 0.1;
					if (VTemp.get(state) - V.get(state) < error) {
						statesConverged.set(state, true);
					}
					V.set(state, VTemp.get(state));
				}
			}
			if (statesConverged.indexOf(false) == -1) {
				converged = true;
			}
		}
		this.best = best;
	}
	
	public Action getBestAction(City fromCity, Task availableTask) {
		Action action = null;
		
		int valueToEncodeState = this.numCities + 1;// TODO: I think this works; not sure
		
		//find the numerical values associated with each city
		int current_from = this.cityList.indexOf(fromCity);
		int current_to;
		
		if (availableTask != null) {//If there was a task in the from city
			 current_to = this.cityList.indexOf(availableTask.deliveryCity);
		} else { //TODO: Simon, we do agree that if there's no task, then the "current_to" is this.numCities, right ?
			current_to = this.numCities;
		}

		//Compute the state
		int state = current_from * valueToEncodeState + current_to;//TODO: Isn't there an issue here ?
		
		//START TO DEBUG BY FIXING THIS PART OF THE FUNCTION
		//THIS IS BROKEN
		//THIS DOESN'T WORK 
		//What is the best action ?
		//Take the task
		if (this.best.get(state) < this.numCities) {//Go to that place
			City toCity = this.cityList.get(this.best.get(state));
			action = new Move(fromCity.pathTo(toCity).get(0));
		} else { //Deliver the package
			action = new Delivery(availableTask);
		}
		return action;
	}
}
