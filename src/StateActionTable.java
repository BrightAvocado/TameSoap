import java.util.ArrayList;
import java.util.List;

import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;

/**
 * This class provides you with the best action to do given a certain state
 *
 */
public class StateActionTable {

	private TaskDistribution td;
	private List<City> cityList;
	private int numCities;
	private int numActions;
	private ArrayList<ArrayList<ArrayList<Double>>> T;


	public StateActionTable(Topology topology, TaskDistribution td) {
		// TODO : Make it so that it runs RLA upon construction and population the
		// state2BestActionMap
		this.td = td;
		this.cityList = topology.cities();
		this.numCities = this.cityList.size();
		this.numActions = this.numCities + 1;
		this.T = computeStateTransitionProbability();
	}

	private ArrayList<ArrayList<ArrayList<Double>>> computeStateTransitionProbability() {

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
		
		//SO FAR SO GOOD

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
										
										//Compute sum
										double sum = 0;
										for (int city_to = 0; city_to < this.numCities; city_to++) {
											sum += td.probability(this.cityList.get(future_from), this.cityList.get(city_to));
										}
										
										T.get(state).get(action).set(future_state, 1- sum);
									} else {
										double p = td.probability(this.cityList.get(future_from), this.cityList.get(future_to));
										T.get(state).get(action).set(future_state, p);
									}
								} else {
									if ((action != current_from)
											&& (action == future_from)) {
										if (future_to == this.numCities) {
											
											//Compute sum
											double sum = 0;
											for (int city_to = 0; city_to < this.numCities; city_to++) {
												sum += td.probability(this.cityList.get(future_from), this.cityList.get(city_to));
											}
											
											T.get(state).get(action).set(future_state, 1- sum);
											
										} else {
											double p = td.probability(this.cityList.get(future_from), this.cityList.get(future_to));
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
		return T;
	}
	
}
