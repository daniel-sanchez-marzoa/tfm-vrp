package tfm.vrp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

/**
 * Defines an implementation of solution composed of a permutation of integers.
 */

public class SweepCoverageSolution extends AbstractSolution<Integer>
		implements PermutationSolution<Integer> {
	private int numberOfObjectives;
	private List<List<Integer>> mandatoryPaths;
	private int numberOfOperators;
	private int depot;
	private int numberOfNodes;

	/** Constructor */
	public SweepCoverageSolution(int permutationLength, int numberOfObjectives, List<List<Integer>> mandatoryPaths,
			int numberOfOperators, int depot, int numberOfNodes) {
		super(permutationLength, numberOfObjectives);

		this.numberOfObjectives = numberOfObjectives;
		this.mandatoryPaths = mandatoryPaths;
		this.numberOfOperators = numberOfOperators;
		this.depot = depot;
		this.numberOfNodes = numberOfNodes;

		List<Integer> randomSequence = new ArrayList<>(permutationLength - 1);

		for (int j = 0; j < permutationLength - 1; j++) {
			randomSequence.add(j);
		}

		java.util.Collections.shuffle(randomSequence);

		randomSequence.add(0, 1);

		for (int i = 0; i < permutationLength; i++)
			variables().set(i, randomSequence.get(i));

		int newNumberOfOperators = (int) ThreadLocalRandom.current().nextInt(1,
				numberOfOperators + 1);
		int numberOfDrones = separateSolutionIntoRoutes().size();

		if (newNumberOfOperators > numberOfDrones)
			newNumberOfOperators = numberOfDrones;

		variables().set(0, newNumberOfOperators);

		fixWithMandatoryPaths();

		// System.out.println("Solution: " + variables());
	}

	/** Copy Constructor */
	public AreaCoverageSolution(AreaCoverageSolution solution) {
		super(solution.getLength(), solution.objectives().length);

		this.numberOfObjectives = solution.numberOfObjectives;
		this.mandatoryPaths = solution.mandatoryPaths;
		this.numberOfOperators = solution.numberOfOperators;
		this.depot = solution.depot;
		this.numberOfNodes = solution.numberOfNodes;

		for (int i = 0; i < objectives().length; i++) {
			objectives()[i] = solution.objectives()[i];
		}

		for (int i = 0; i < variables().size(); i++) {
			variables().set(i, solution.variables().get(i));
		}

		for (int i = 0; i < constraints().length; i++) {
			constraints()[i] = solution.constraints()[i];
		}

		attributes = new HashMap<Object, Object>(solution.attributes);
	}

	@Override
	public AreaCoverageSolution copy() {
		return new AreaCoverageSolution(this);
	}

	@Override
	public int getLength() {
		return variables().size();
	}

	public void fixWithMandatoryPaths() {
		List<Integer> routes = variables().subList(1, variables().size());

		for (List<Integer> mandatoryPath : mandatoryPaths) {
			Integer i = routes.indexOf(mandatoryPath.get(0));
			Integer j = routes.indexOf(mandatoryPath.get(1));

			if (Math.abs(i - j) > 1) {
				List<Integer> newList = new ArrayList<>(routes.subList(0, Math.min(i, j) + 1));

				newList.add(routes.get(Math.max(i, j)));
				newList.addAll(routes.subList(Math.min(i, j) + 1, Math.max(i, j)));
				newList.addAll(routes.subList(Math.max(i, j) + 1, routes.size()));

				// System.out.println("RandomSequence: " + routes);
				// System.out.println("RandomSequence: " + newList);
				routes = newList;
			}
		}

		for (int i = 1; i < variables().size(); i++) {
			variables().set(i, routes.get(i - 1));
		}
	}

	public List<List<Integer>> separateSolutionIntoRoutes() {
		List<Integer> variables = this.variables().subList(1, this.variables().size());
		List<List<Integer>> routes = new ArrayList<>();
		int depotNode = depot - 1;
		routes.add(new ArrayList<>());

		for (Integer solutionVariable : variables) {
			if (isDelimeter(solutionVariable))
				routes.add(new ArrayList<>());
			else if (solutionVariable != depotNode)
				routes.get(routes.size() - 1).add(solutionVariable);
		}

		List<List<Integer>> emptyRoutes = new ArrayList<>();

		for (List<Integer> route : routes) {
			if (route.isEmpty())
				emptyRoutes.add(route);
			else {
				route.add(0, depotNode);
				route.add(depotNode);
			}
		}

		routes.removeAll(emptyRoutes);

		return routes;
	}

	private boolean isDelimeter(Integer i) {
		return i >= numberOfNodes;
	}
}
