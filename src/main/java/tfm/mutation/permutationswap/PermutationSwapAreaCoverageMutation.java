package tfm.mutation.permutationswap;

import java.util.concurrent.ThreadLocalRandom;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import tfm.vrp.AreaCoverageSolution;

public class PermutationSwapAreaCoverageMutation implements MutationOperator<AreaCoverageSolution> {
	private double mutationProbability;
	private RandomGenerator<Double> mutationRandomGenerator;
	private BoundedRandomGenerator<Integer> positionRandomGenerator;
	private int maxNumberOfOperators;

	/** Constructor */
	public PermutationSwapAreaCoverageMutation(double mutationProbability, int maxNumberOfOperators) {
		this(
				mutationProbability,
				() -> JMetalRandom.getInstance().nextDouble(),
				(a, b) -> JMetalRandom.getInstance().nextInt(a, b), maxNumberOfOperators);
	}

	/** Constructor */
	public PermutationSwapAreaCoverageMutation(
			double mutationProbability, RandomGenerator<Double> randomGenerator, int maxNumberOfOperators) {
		this(
				mutationProbability,
				randomGenerator,
				BoundedRandomGenerator.fromDoubleToInteger(randomGenerator), maxNumberOfOperators);
	}

	/** Constructor */
	public PermutationSwapAreaCoverageMutation(
			double mutationProbability,
			RandomGenerator<Double> mutationRandomGenerator,
			BoundedRandomGenerator<Integer> positionRandomGenerator, int maxNumberOfOperators) {
		Check.probabilityIsValid(mutationProbability);
		this.mutationProbability = mutationProbability;
		this.mutationRandomGenerator = mutationRandomGenerator;
		this.positionRandomGenerator = positionRandomGenerator;
		this.maxNumberOfOperators = maxNumberOfOperators;
	}

	/* Getters */
	@Override
	public double getMutationProbability() {
		return mutationProbability;
	}

	/* Setters */
	public void setMutationProbability(double mutationProbability) {
		this.mutationProbability = mutationProbability;
	}

	@Override
	public AreaCoverageSolution execute(AreaCoverageSolution solution) {
		Check.notNull(solution);

		doMutation(solution);
		return solution;
	}

	/** Performs the operation */
	public void doMutation(AreaCoverageSolution solution) {
		int permutationLength;
		permutationLength = solution.variables().size();

		if ((permutationLength != 0) && (permutationLength != 1)) {
			if (mutationRandomGenerator.getRandomValue() < mutationProbability) {
				if (mutationRandomGenerator.getRandomValue() > 0.5) {
					int pos1 = positionRandomGenerator.getRandomValue(1, permutationLength - 1);
					int pos2 = positionRandomGenerator.getRandomValue(1, permutationLength - 1);

					while (pos1 == pos2) {
						if (pos1 == (permutationLength - 1))
							pos2 = positionRandomGenerator.getRandomValue(1, permutationLength - 2);
						else
							pos2 = positionRandomGenerator.getRandomValue(pos1, permutationLength - 1);
					}

					Integer temp = solution.variables().get(pos1);
					solution.variables().set(pos1, solution.variables().get(pos2));
					solution.variables().set(pos2, temp);

					solution.fixWithMandatoryPaths();
				} else {
					int newNumberOfOperators = (int) ThreadLocalRandom.current().nextInt(1,
							maxNumberOfOperators + 1);
					int numberOfDrones = solution.separateSolutionIntoRoutes().size();

					if (newNumberOfOperators > numberOfDrones)
						newNumberOfOperators = numberOfDrones;

					solution.variables().set(0, newNumberOfOperators);
				}
			}
		}
	}
}
