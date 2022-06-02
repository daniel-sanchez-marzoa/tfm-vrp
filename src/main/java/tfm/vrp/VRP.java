package tfm.vrp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.problem.AbstractGenericProblem;
import org.uma.jmetal.problem.permutationproblem.PermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

import lombok.Getter;

@Getter
public class VRP extends AbstractGenericProblem<PermutationSolution<Integer>>
		implements PermutationProblem<PermutationSolution<Integer>> {
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
	public PermutationSolution<Integer> createSolution() {
		return new AreaCoverageSolution(getLength(), getNumberOfObjectives(), mandatoryPaths, numberOfOperators);
	}

	@Override
	public int getLength() {
		return numberOfNodes + numberOfVehicles;
	}

	@Override
	public PermutationSolution<Integer> evaluate(PermutationSolution<Integer> solution) {
		List<List<Integer>> routes = separateSolutionIntoRoutes(solution);
		List<Double> traveledDistances = new ArrayList<>();

		for (List<Integer> route : routes) {
			traveledDistances.add(0.0);

			for (int i = 0; i < (route.size() - 1); i++) {
				int x = route.get(i);
				int y = route.get(i + 1);

				traveledDistances.set(traveledDistances.size() - 1,
						traveledDistances.get(traveledDistances.size() - 1) + distanceMatrix[x][y]);
			}
		}

		double maxDistance = traveledDistances.stream().max((d1, d2) -> Double.compare(d1, d2)).get();

		solution.objectives()[0] = maxDistance / droneSpeed + getSetupTime(solution, traveledDistances, maxDistance);
		solution.objectives()[1] = routes.size();
		solution.objectives()[2] = getNumberOfOperators(solution);

		return solution;
	}

	private Integer getNumberOfOperators(PermutationSolution<Integer> solution) {
		return Math.abs(solution.variables().get(0));
	}

	private double getSetupTime(PermutationSolution<Integer> solution, List<Double> traveledDistances,
			double maxDistance) {
		int operators = getNumberOfOperators(solution);

		return setupTime + setupTime
				* Math.floor((traveledDistances.lastIndexOf(maxDistance)) / operators);
	}

	private List<List<Integer>> separateSolutionIntoRoutes(PermutationSolution<Integer> solution) {
		List<Integer> variables = solution.variables().subList(1, solution.variables().size() - 1);
		List<List<Integer>> routes = new ArrayList<>();
		routes.add(new ArrayList<>());

		for (Integer solutionVariable : variables) {
			if (isDelimeter(solutionVariable))
				routes.add(new ArrayList<>());
			else if (solutionVariable != depot)
				routes.get(routes.size() - 1).add(solutionVariable);
		}

		List<List<Integer>> emptyRoutes = new ArrayList<>();

		for (List<Integer> route : routes) {
			if (route.isEmpty())
				emptyRoutes.add(route);
			else {
				route.add(0, depot);
				route.add(depot);
			}
		}

		routes.removeAll(emptyRoutes);

		return routes;
	}

	private boolean isDelimeter(Integer i) {
		return i >= numberOfNodes;
	}
}
