package tfm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.PMXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PermutationSwapMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.permutationproblem.PermutationProblem;
import org.uma.jmetal.problem.singleobjective.TSP;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import tfm.vrp.VRPRunner;

public final class App {
	public static void main(String[] args) {
		Options options = getProgramOptions();

		try {
			VRPRunner runner = setupVRPRunner(options, args);

			runner.runVRP();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static VRPRunner setupVRPRunner(Options options, String[] args) {
		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options, args);
			VRPRunner runner = new VRPRunner((File) cmd.getParsedOptionValue("vrp"));

			Integer populationSize = (Integer) cmd.getParsedOptionValue("p");
			Integer maxEvaluations = (Integer) cmd.getParsedOptionValue("e");
			Float crossoverProbability = (Float) cmd.getParsedOptionValue("cp");

			if (populationSize != null)
				runner.setPopulationSize(populationSize);

			if (maxEvaluations != null)
				runner.setMaxEvaluations(maxEvaluations);

			if (crossoverProbability != null)
				runner.setCrossoverProbability(crossoverProbability);

			return runner;
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("utility-name", options);

			System.exit(1);
		}

		return null;
	}

	private static Options getProgramOptions() {
		Options options = new Options();

		options.addOption(Option.builder()
				.option("vrp")
				.longOpt("vrp-file")
				.hasArg()
				.desc("File containing a vrp intance. These files can be created from tsp instances from TSPLIB, by adding two parameters: 'DEPOT' (index of the depot location) and 'NUMBER_OF_VEHICLES' (integer indicating the maximum number of vehicles to be used)\n'http://comopt.ifi.uni-heidelberg.de/software/TSPLIB95/'")
				.required()
				.type(File.class)
				.build());

		options.addOption(Option.builder()
				.option("p")
				.longOpt("population-size")
				.hasArg()
				.desc("Size of the genetic algorithm's population. Default = 100")
				.type(Integer.class)
				.build());

		options.addOption(Option.builder()
				.option("e")
				.longOpt("max-evaluations")
				.hasArg()
				.desc("Maximum number of evaluations (iterations) for the algorithm. Default = 1000")
				.type(Integer.class)
				.build());

		options.addOption(Option.builder()
				.option("cp")
				.longOpt("crossover-probability")
				.hasArg()
				.desc("Probability of applying crossover operation to a pair of individuals. Default = 0.9")
				.type(Float.class)
				.build());

		options.addOption(Option.builder()
				.option("mp")
				.longOpt("mutation-probability")
				.hasArg()
				.desc("Probability of applying mutation operation to an individual. Default = 1/number_of_variables_in_solution")
				.type(Float.class)
				.build());
		return options;
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
