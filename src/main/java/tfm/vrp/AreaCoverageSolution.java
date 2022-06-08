package tfm.vrp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

/**
 * Defines an implementation of solution composed of a permutation of integers.
 */

public class AreaCoverageSolution extends AbstractSolution<Integer>
		implements PermutationSolution<Integer> {
	int numberOfObjectives;
	List<List<Integer>> mandatoryPaths;
	int numberOfOperators;

	/** Constructor */
	public AreaCoverageSolution(int permutationLength, int numberOfObjectives, List<List<Integer>> mandatoryPaths,
			int numberOfOperators) {
		super(permutationLength, numberOfObjectives);

		this.numberOfObjectives = numberOfObjectives;
		this.mandatoryPaths = mandatoryPaths;
		this.numberOfOperators = numberOfOperators;

		List<Integer> randomSequence = new ArrayList<>(permutationLength - 1);

		for (int j = 0; j < permutationLength - 1; j++) {
			randomSequence.add(j);
		}

		java.util.Collections.shuffle(randomSequence);

		randomSequence.add(0, (int) ((Math.random() * (numberOfOperators - 1)) + 1));

		for (int i = 0; i < permutationLength; i++)
			variables().set(i, randomSequence.get(i));

		fixWithMandatoryPaths();

		System.out.println("Solution: " + variables());
	}

	/** Copy Constructor */
	public AreaCoverageSolution(AreaCoverageSolution solution) {
		super(solution.getLength(), solution.objectives().length);

		this.numberOfObjectives = solution.numberOfObjectives;
		this.mandatoryPaths = solution.mandatoryPaths;
		this.numberOfOperators = solution.numberOfOperators;

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

				System.out.println("RandomSequence: " + routes);
				System.out.println("RandomSequence: " + newList);
				routes = newList;
			}
		}

		for (int i = 1; i < variables().size(); i++) {
			variables().set(i, routes.get(i - 1));
		}
	}
}
