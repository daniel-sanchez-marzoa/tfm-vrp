package tfm.crossover.pmxcrossover;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import tfm.problem.vrp.AreaCoverageSolution;

@SuppressWarnings("serial")
public class PMXAreaCoverageCrossover implements
		CrossoverOperator<AreaCoverageSolution> {
	private double crossoverProbability = 1.0;
	private BoundedRandomGenerator<Integer> cuttingPointRandomGenerator;
	private RandomGenerator<Double> crossoverRandomGenerator;

	/**
	 * Constructor
	 */
	public PMXAreaCoverageCrossover(double crossoverProbability) {
		this(crossoverProbability, () -> JMetalRandom.getInstance().nextDouble(),
				(a, b) -> JMetalRandom.getInstance().nextInt(a, b));
	}

	/**
	 * Constructor
	 */
	public PMXAreaCoverageCrossover(double crossoverProbability, RandomGenerator<Double> randomGenerator) {
		this(crossoverProbability, randomGenerator, BoundedRandomGenerator.fromDoubleToInteger(randomGenerator));
	}

	/**
	 * Constructor
	 */
	public PMXAreaCoverageCrossover(double crossoverProbability, RandomGenerator<Double> crossoverRandomGenerator,
			BoundedRandomGenerator<Integer> cuttingPointRandomGenerator) {
		if ((crossoverProbability < 0) || (crossoverProbability > 1)) {
			throw new JMetalException("Crossover probability value invalid: " + crossoverProbability);
		}
		this.crossoverProbability = crossoverProbability;
		this.crossoverRandomGenerator = crossoverRandomGenerator;
		this.cuttingPointRandomGenerator = cuttingPointRandomGenerator;
	}

	/* Getters */
	@Override
	public double getCrossoverProbability() {
		return crossoverProbability;
	}

	/* Setters */
	public void setCrossoverProbability(double crossoverProbability) {
		this.crossoverProbability = crossoverProbability;
	}

	/**
	 * Executes the operation
	 *
	 * @param parents An object containing an array of two solutions
	 */
	public List<AreaCoverageSolution> execute(List<AreaCoverageSolution> parents) {
		if (null == parents) {
			throw new JMetalException("Null parameter");
		} else if (parents.size() != 2) {
			throw new JMetalException("There must be two parents instead of " + parents.size());
		}

		return doCrossover(crossoverProbability, parents);
	}

	/**
	 * Perform the crossover operation
	 *
	 * @param probability Crossover probability
	 * @param parents     Parents
	 * @return An array containing the two offspring
	 */
	public List<AreaCoverageSolution> doCrossover(double probability,
			List<AreaCoverageSolution> parents) {
		List<AreaCoverageSolution> routeParents = new ArrayList<>(2);

		// System.out.println("Parent 0: " + parents.get(0).variables());
		// System.out.println("Parent 1: " + parents.get(1).variables());

		routeParents.add((AreaCoverageSolution) parents.get(0).copy());
		routeParents.add((AreaCoverageSolution) parents.get(1).copy());

		routeParents.get(0).variables().remove(0);
		routeParents.get(1).variables().remove(0);

		List<AreaCoverageSolution> offspring = new ArrayList<>(2);

		offspring.add((AreaCoverageSolution) routeParents.get(0).copy());
		offspring.add((AreaCoverageSolution) routeParents.get(1).copy());

		int permutationLength = routeParents.get(0).variables().size();

		if (crossoverRandomGenerator.getRandomValue() < probability) {
			int cuttingPoint1;
			int cuttingPoint2;

			// STEP 1: Get two cutting points
			cuttingPoint1 = cuttingPointRandomGenerator.getRandomValue(0, permutationLength - 1);
			cuttingPoint2 = cuttingPointRandomGenerator.getRandomValue(0, permutationLength - 1);
			while (cuttingPoint2 == cuttingPoint1)
				cuttingPoint2 = cuttingPointRandomGenerator.getRandomValue(0, permutationLength - 1);

			if (cuttingPoint1 > cuttingPoint2) {
				int swap;
				swap = cuttingPoint1;
				cuttingPoint1 = cuttingPoint2;
				cuttingPoint2 = swap;
			}

			// STEP 2: Get the subchains to interchange
			int replacement1[] = new int[permutationLength];
			int replacement2[] = new int[permutationLength];
			for (int i = 0; i < permutationLength; i++)
				replacement1[i] = replacement2[i] = -1;

			// STEP 3: Interchange
			for (int i = cuttingPoint1; i <= cuttingPoint2; i++) {
				offspring.get(0).variables().set(i, routeParents.get(1).variables().get(i));
				offspring.get(1).variables().set(i, routeParents.get(0).variables().get(i));

				replacement1[routeParents.get(1).variables().get(i)] = routeParents.get(0).variables().get(i);
				replacement2[routeParents.get(0).variables().get(i)] = routeParents.get(1).variables().get(i);
			}

			// STEP 4: Repair offspring
			for (int i = 0; i < permutationLength; i++) {
				if ((i >= cuttingPoint1) && (i <= cuttingPoint2))
					continue;

				int n1 = routeParents.get(0).variables().get(i);
				int m1 = replacement1[n1];

				int n2 = routeParents.get(1).variables().get(i);
				int m2 = replacement2[n2];

				while (m1 != -1) {
					n1 = m1;
					m1 = replacement1[m1];
				}

				while (m2 != -1) {
					n2 = m2;
					m2 = replacement2[m2];
				}

				offspring.get(0).variables().set(i, n1);
				offspring.get(1).variables().set(i, n2);
			}
		}

		int operatorsOffspring = (parents.get(0).variables().get(0) + parents.get(1).variables().get(0)) / 2;

		int numberOfDrones = offspring.get(0).separateSolutionIntoRoutes().size();

		if (operatorsOffspring > numberOfDrones)
			offspring.get(0).variables().add(0, numberOfDrones);
		else
			offspring.get(0).variables().add(0, operatorsOffspring);

		numberOfDrones = offspring.get(1).separateSolutionIntoRoutes().size();

		if (operatorsOffspring > numberOfDrones)
			offspring.get(1).variables().add(0, numberOfDrones);
		else
			offspring.get(1).variables().add(0, operatorsOffspring);

		offspring.get(0).fixWithMandatoryPaths();
		offspring.get(1).fixWithMandatoryPaths();

		return offspring;
	}

	@Override
	public int getNumberOfRequiredParents() {
		return 2;
	}

	@Override
	public int getNumberOfGeneratedChildren() {
		return 2;
	}
}
