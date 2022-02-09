package tfm.vrp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
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

import lombok.Data;
import lombok.RequiredArgsConstructor;
import tfm.AlgorithmRunner;

@Data
@RequiredArgsConstructor
public class VRPRunner {
	private final File vrpFile;
	private int populationSize = 100;
	private int maxEvaluations = 1000;
	private float crossoverProbability = (float) 0.9;
	private Float mutationProbability = (float) 0.9;

	public void runVRP() throws IOException {
		PermutationProblem<PermutationSolution<Integer>> problem;
		Algorithm<PermutationSolution<Integer>> algorithm;
		CrossoverOperator<PermutationSolution<Integer>> crossover;
		MutationOperator<PermutationSolution<Integer>> mutation;
		SelectionOperator<List<PermutationSolution<Integer>>, PermutationSolution<Integer>> selection;

		problem = VRPFactory.produce(vrpFile);

		crossover = new PMXCrossover(crossoverProbability);

		if (mutationProbability == null)
			mutationProbability = (float) (1.0 / problem.getNumberOfVariables());

		mutation = new PermutationSwapMutation<Integer>(mutationProbability);

		selection = new BinaryTournamentSelection<PermutationSolution<Integer>>(
				new RankingAndCrowdingDistanceComparator<PermutationSolution<Integer>>());

		algorithm = new GeneticAlgorithmBuilder<>(problem, crossover, mutation)
				.setPopulationSize(populationSize)
				.setMaxEvaluations(maxEvaluations)
				.setSelectionOperator(selection)
				.build();

		AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
				.execute();

		PermutationSolution<Integer> solution = algorithm.getResult();
		List<PermutationSolution<Integer>> population = new ArrayList<>(1);
		population.add(solution);

		long computingTime = algorithmRunner.getComputingTime();

		new SolutionListOutput(population)
				.setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
				.setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
				.print();

		JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
		JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
		JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
	}
}
