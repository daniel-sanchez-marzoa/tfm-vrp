package tfm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.impl.DefaultLocalSearch;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.PMXCrossover;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.mutation.impl.PermutationSwapMutation;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.permutationproblem.PermutationProblem;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.problem.singleobjective.TSP;
import org.uma.jmetal.runner.multiobjective.RNSGAIIRunner;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.solution.permutationsolution.impl.IntegerPermutationSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.fileinput.VectorFileUtils;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

/**
 * Hello world!
 */
public final class App {
	private App() {
	}

	/**
	 * Says hello to the world.
	 * 
	 * @param args The arguments of the program.
	 */
	public static void main(String[] args) {
		try {
			tsp();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void tsp() throws IOException {
		PermutationProblem<PermutationSolution<Integer>> problem;
		Algorithm<PermutationSolution<Integer>> algorithm;
		CrossoverOperator<PermutationSolution<Integer>> crossover;
		MutationOperator<PermutationSolution<Integer>> mutation;
		SelectionOperator<List<PermutationSolution<Integer>>, PermutationSolution<Integer>> selection;

		problem = new TSP("resources/tspInstances/kroA100.tsp");

		crossover = new PMXCrossover(0.9);

		double mutationProbability = 1.0 / problem.getNumberOfVariables();
		mutation = new PermutationSwapMutation<Integer>(mutationProbability);

		selection = new BinaryTournamentSelection<PermutationSolution<Integer>>(
				new RankingAndCrowdingDistanceComparator<PermutationSolution<Integer>>());

		algorithm = new GeneticAlgorithmBuilder<>(problem, crossover, mutation)
				.setPopulationSize(100)
				.setMaxEvaluations(250000)
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
