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
	private int numberOfCities;
	private double[][] distanceMatrix;
	private int depot;
	private int numberOfVehicles;
	private List<List<Integer>> mandatoryPaths;

	public VRP(String name, int numberOfCities, double[][] distanceMatrix, int depot, int numberOfVehicles,
			List<List<Integer>> mandatoryPaths)
			throws IOException {
		this.numberOfCities = numberOfCities;
		this.distanceMatrix = distanceMatrix;
		this.depot = depot;
		this.numberOfVehicles = numberOfVehicles;
		this.mandatoryPaths = mandatoryPaths;

		setNumberOfVariables(numberOfCities + numberOfVehicles - 1);
		setNumberOfObjectives(2);
		setName(name);
	}

	@Override
	public PermutationSolution<Integer> createSolution() {
		return new AreaCoverageSolution(getLength(), getNumberOfObjectives(), mandatoryPaths);
	}

	@Override
	public int getLength() {
		return numberOfCities + numberOfVehicles - 1;
	}

	@Override
	public PermutationSolution<Integer> evaluate(PermutationSolution<Integer> solution) {
		List<List<Integer>> routes = separateSolutionIntoRoutes(solution);
		double traveledDistance = 0.0;

		for (List<Integer> route : routes) {
			for (int i = 0; i < (route.size() - 1); i++) {
				int x = route.get(i);
				int y = route.get(i + 1);

				traveledDistance += distanceMatrix[x][y];
			}
		}

		solution.objectives()[0] = traveledDistance;
		solution.objectives()[1] = routes.size();

		return solution;
	}

	private List<List<Integer>> separateSolutionIntoRoutes(PermutationSolution<Integer> solution) {
		List<List<Integer>> routes = new ArrayList<>();
		routes.add(new ArrayList<>());

		for (Integer solutionVariable : solution.variables()) {
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
		return i >= numberOfCities;
	}
}
