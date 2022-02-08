package tfm.vrp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.problem.permutationproblem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

import lombok.Getter;

@Getter
public class VRP extends AbstractIntegerPermutationProblem {
	private int numberOfCities;
	private double[][] distanceMatrix;
	private int depot;
	private int numberOfVehicles;

	public VRP(int numberOfCities, double[][] distanceMatrix, int depot, int numberOfVehicles) throws IOException {
		this.numberOfCities = numberOfCities;
		this.distanceMatrix = distanceMatrix;
		this.depot = depot;
		this.numberOfVehicles = numberOfVehicles;

		setNumberOfVariables(numberOfCities);
		setNumberOfObjectives(1);
		setName("VRP");
	}

	@Override
	public int getLength() {
		return numberOfCities + numberOfVehicles - 1;
	}

	@Override
	public PermutationSolution<Integer> evaluate(PermutationSolution<Integer> solution) {
		List<List<Integer>> routes = separateSolutionIntoRoutes(solution);
		double fitness1 = 0.0;

		for (List<Integer> route : routes) {
			for (int i = 0; i < (route.size() - 1); i++) {
				int x = solution.variables().get(i);
				int y = solution.variables().get(i + 1);

				fitness1 += distanceMatrix[x][y];
			}
		}

		solution.objectives()[0] = fitness1;

		return solution;
	}

	private List<List<Integer>> separateSolutionIntoRoutes(PermutationSolution<Integer> solution) {
		List<List<Integer>> routes = new ArrayList<>();

		for (Integer solutionVariable : solution.variables()) {
			if (isDelimeter(solutionVariable))
				routes.add(new ArrayList<>());
			else if (solutionVariable != depot)
				routes.get(routes.size() - 1).add(solutionVariable);
		}

		for (List<Integer> route : routes) {
			route.add(0, depot);
			route.add(depot);
		}

		return routes;
	}

	private boolean isDelimeter(Integer i) {
		return i >= numberOfCities;
	}
}
