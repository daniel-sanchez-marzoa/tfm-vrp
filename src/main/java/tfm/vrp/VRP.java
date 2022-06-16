package tfm.vrp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.problem.AbstractGenericProblem;
import org.uma.jmetal.problem.permutationproblem.PermutationProblem;

import lombok.Getter;

@Getter
public class VRP extends AbstractGenericProblem<AreaCoverageSolution>
		implements PermutationProblem<AreaCoverageSolution> {
	private int numberOfNodes;
	/**
	 * Given in meters
	 */
	private double[][] distanceMatrix;
	private int depot;
	private int numberOfVehicles;
	private int numberOfOperators;
	private int setupTime;
	/**
	 * Given in meters / second
	 */
	private float droneSpeed;
	/**
	 * Set of edges that must be traveled for the solution to be valid
	 */
	private List<List<Integer>> mandatoryPaths;

	public VRP(String name, int numberOfCities, double[][] distanceMatrix, int depot, int numberOfVehicles,
			int numberOfOperators, int setupTime, float droneSpeed, List<List<Integer>> mandatoryPaths)
			throws IOException {
		this.numberOfNodes = numberOfCities;
		this.distanceMatrix = distanceMatrix;
		this.depot = depot;
		this.numberOfVehicles = numberOfVehicles;
		this.numberOfOperators = numberOfOperators;
		this.setupTime = setupTime;
		this.droneSpeed = droneSpeed;
		this.mandatoryPaths = mandatoryPaths;

		setNumberOfVariables(getLength());
		setNumberOfObjectives(3);
		setName(name);
	}

	@Override
	public AreaCoverageSolution createSolution() {
		return new AreaCoverageSolution(getLength(), getNumberOfObjectives(), mandatoryPaths, numberOfOperators, depot,
				this.numberOfNodes);
	}

	@Override
	public int getLength() {
		return numberOfNodes + numberOfVehicles;
	}

	@Override
	public AreaCoverageSolution evaluate(AreaCoverageSolution solution) {
		List<List<Integer>> routes = solution.separateSolutionIntoRoutes();
		List<Double> traveledDistances = new ArrayList<>();

		// System.out.println("----" + solution.variables());
		for (List<Integer> route : routes) {
			traveledDistances.add(0.0);

			// System.out.println("Route: " + route);

			for (int i = 0; i < (route.size() - 1); i++) {
				int x = route.get(i);
				int y = route.get(i + 1);

				traveledDistances.set(traveledDistances.size() - 1,
						traveledDistances.get(traveledDistances.size() - 1) + distanceMatrix[x][y]);
			}
		}

		List<Double> travelTimes = new ArrayList<>();

		for (Double travelDistance : traveledDistances) {
			travelTimes.add(travelDistance / droneSpeed + getSetupTime(solution, traveledDistances, travelDistance));
		}

		solution.objectives()[0] = travelTimes.stream().max((d1, d2) -> Double.compare(d1, d2)).get();
		solution.objectives()[1] = routes.size();
		solution.objectives()[2] = getNumberOfOperators(solution);

		// System.out.println("Objective 1: " + solution.objectives()[0]);
		// System.out.println("Objective 2: " + solution.objectives()[1]);
		// System.out.println("Objective 3: " + solution.objectives()[2]);

		return solution;
	}

	private Integer getNumberOfOperators(AreaCoverageSolution solution) {
		return Math.abs(solution.variables().get(0));
	}

	private double getSetupTime(AreaCoverageSolution solution, List<Double> traveledDistances,
			double maxDistance) {
		int operators = getNumberOfOperators(solution);

		// if (operators != 1)
		// System.out.println("Operators " + operators + " | Index " +
		// traveledDistances.lastIndexOf(maxDistance)
		// + " | Div " + Math.floor((traveledDistances.lastIndexOf(maxDistance)) /
		// operators));

		return setupTime + setupTime
				* Math.floor((traveledDistances.lastIndexOf(maxDistance)) / operators);
	}
}
