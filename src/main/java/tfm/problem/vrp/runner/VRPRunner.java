package tfm.problem.vrp.runner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.lab.visualization.plot.impl.Plot2D;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.permutationproblem.PermutationProblem;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import tfm.algorithm.AlgorithmRunner;
import tfm.crossover.pmxcrossover.PMXAreaCoverageCrossover;
import tfm.mutation.permutationswap.PermutationSwapAreaCoverageMutation;
import tfm.selection.comparators.AreaCoverageDominanceComparator;
import tfm.problem.vrp.AreaCoverageSolution;
import tfm.problem.vrp.VRPFactory;

/**
 * This class executes a jMetal algorithm against a single problem.
 */
@Data
@RequiredArgsConstructor
public class VRPRunner {
	private final File vrpFile;
	private int populationSize = 5000;
	private int maxEvaluations = 500000;
	private float crossoverProbability = (float) 0.9;
	private Float mutationProbability = (float) 0.05;

	public void run() throws IOException {
		Algorithm<List<AreaCoverageSolution>> algorithm = getAlgorithm();

		AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
				.execute();

		saveResults(algorithm, algorithmRunner);
	}

	private Algorithm<List<AreaCoverageSolution>> getAlgorithm() throws IOException {
		PermutationProblem<AreaCoverageSolution> problem = VRPFactory.produce(vrpFile);
		CrossoverOperator<AreaCoverageSolution> crossover = new PMXAreaCoverageCrossover(crossoverProbability);
		SelectionOperator<List<AreaCoverageSolution>, AreaCoverageSolution> selection = new BinaryTournamentSelection<AreaCoverageSolution>(
				new AreaCoverageDominanceComparator<AreaCoverageSolution>());
		MutationOperator<AreaCoverageSolution> mutation;
		Algorithm<List<AreaCoverageSolution>> algorithm;

		if (mutationProbability == null)
			mutationProbability = (float) (1.0 / problem.getNumberOfVariables());

		mutation = new PermutationSwapAreaCoverageMutation(mutationProbability,
				VRPFactory.getNumberOfOperators(vrpFile));

		algorithm = new NSGAIIBuilder<AreaCoverageSolution>(
				problem, crossover, mutation, populationSize)
				.setSelectionOperator(selection)
				.setMaxEvaluations(maxEvaluations)
				.build();

		return algorithm;
	}

	private void saveResults(Algorithm<List<AreaCoverageSolution>> algorithm, AlgorithmRunner algorithmRunner) {
		List<AreaCoverageSolution> population = algorithm.getResult();
		long computingTime = algorithmRunner.getComputingTime();

		double[][] a = new double[][] { { 1, 2 }, { 2, 2 } };

		Plot2D plot = new Plot2D(a, algorithm.getName());
		plot.plot();

		new SolutionListOutput(population)
				.setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
				.setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
				.print();

		JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
		JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
		JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
		JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
	}
}
