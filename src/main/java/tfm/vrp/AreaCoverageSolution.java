package tfm.vrp;

import java.security.InvalidParameterException;
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

	/** Constructor */
	public AreaCoverageSolution(int permutationLength, int numberOfObjectives, List<List<Integer>> mandatoryPaths,
			int numberOfOperators) {
		super(permutationLength, numberOfObjectives);

		List<Integer> randomSequence = new ArrayList<>(permutationLength - 1);

		for (int j = 0; j < permutationLength - 1; j++) {
			randomSequence.add(j);
		}

		java.util.Collections.shuffle(randomSequence);

		randomSequence = fixWithMandatoryPaths(permutationLength - 1, mandatoryPaths, randomSequence);

		randomSequence.add(0, 100 + (int) ((Math.random() * (numberOfOperators - 1)) + 1));

		for (int i = 0; i < permutationLength; i++) {
			variables().set(i, randomSequence.get(i));
		}
	}

	/** Copy Constructor */
	public AreaCoverageSolution(AreaCoverageSolution solution) {
		super(solution.getLength(), solution.objectives().length);

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

	private List<Integer> fixWithMandatoryPaths(int permutationLength, List<List<Integer>> mandatoryPaths,
			List<Integer> randomSequence) {
		for (List<Integer> mandatoryPath : mandatoryPaths) {
			validateMandatoryPath(permutationLength, mandatoryPath);

			Integer i = randomSequence.indexOf(mandatoryPath.get(0));
			Integer j = randomSequence.indexOf(mandatoryPath.get(1));

			if (Math.abs(i) - Math.abs(j) > 1) {
				List<Integer> newList = new ArrayList<>(randomSequence.subList(0, Math.min(i, j) + 1));

				newList.add(randomSequence.get(Math.max(i, j)));
				newList.addAll(randomSequence.subList(Math.min(i, j) + 1, Math.max(i, j)));
				newList.addAll(randomSequence.subList(Math.max(i, j) + 1, randomSequence.size()));

				randomSequence = newList;
			}

		}
		return randomSequence;
	}

	private void validateMandatoryPath(int permutationLength, List<Integer> mandatoryPath) {
		if (mandatoryPath.size() != 2)
			throw new InvalidParameterException("The mandatory paths must be of length 2");

		if (mandatoryPath.get(0) == null || mandatoryPath.get(0) < 0 || mandatoryPath.get(0) > permutationLength)
			throw new InvalidParameterException(
					"The mandatory paths must contain values between 0 and " + permutationLength);

		if (mandatoryPath.get(1) == null || mandatoryPath.get(1) < 0 || mandatoryPath.get(1) > permutationLength)
			throw new InvalidParameterException(
					"The mandatory paths must contain values between 0 and " + permutationLength);
	}
}
