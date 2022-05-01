package tfm.vrp.runner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.lab.visualization.plot.impl.Plot2D;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.PMXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PermutationSwapMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.permutationproblem.PermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import tfm.algorithm.AlgorithmRunner;
import tfm.vrp.VRPFactory;

/**
 * This class executes a jMetal algorithm against a single problem.
 */
@Data
@RequiredArgsConstructor
public class VRPRunner {
	private final File vrpFile;
	private int populationSize = 100;
	private int maxEvaluations = 1000;
	private float crossoverProbability = (float) 0.9;
	private Float mutationProbability = null;

	public void runVRP() throws IOException {
		Algorithm<List<PermutationSolution<Integer>>> algorithm = getAlgorithm();

		AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
				.execute();

		saveResults(algorithm, algorithmRunner);
	}

	private Algorithm<List<PermutationSolution<Integer>>> getAlgorithm() throws FileNotFoundException {
		PermutationProblem<PermutationSolution<Integer>> problem = VRPFactory.produce(vrpFile);
		CrossoverOperator<PermutationSolution<Integer>> crossover = new PMXCrossover(crossoverProbability);
		SelectionOperator<List<PermutationSolution<Integer>>, PermutationSolution<Integer>> selection = new BinaryTournamentSelection<PermutationSolution<Integer>>(
				new RankingAndCrowdingDistanceComparator<PermutationSolution<Integer>>());
		MutationOperator<PermutationSolution<Integer>> mutation;
		Algorithm<List<PermutationSolution<Integer>>> algorithm;

		if (mutationProbability == null)
			mutationProbability = (float) (1.0 / problem.getNumberOfVariables());

		mutation = new PermutationSwapMutation<Integer>(mutationProbability);

		algorithm = new NSGAIIBuilder<PermutationSolution<Integer>>(
				problem, crossover, mutation, populationSize)
				.setSelectionOperator(selection)
				.setMaxEvaluations(maxEvaluations)
				.build();

		return algorithm;
	}

	private void saveResults(Algorithm<List<PermutationSolution<Integer>>> algorithm, AlgorithmRunner algorithmRunner) {
		List<PermutationSolution<Integer>> population = algorithm.getResult();
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
