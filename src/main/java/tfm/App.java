package tfm;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import tfm.vrp.runner.VRPExperimentRunner;
import tfm.vrp.runner.VRPExperimentRunnerFactory;
import tfm.vrp.runner.VRPRunner;

public final class App {
	public static void main(String[] args) {
		Options options = getProgramOptions();

		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption("vrpe")) {
				VRPExperimentRunner runner = VRPExperimentRunnerFactory
						.produce((File) cmd.getParsedOptionValue("vrpe"));

				runner.runExperiment();
			} else {
				VRPRunner runner = setupVRPRunner(options, args);

				runner.runVRP();
			}

		} catch (IOException | ParseException e) {
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
				.option("vrpe")
				.longOpt("vrp-experiment")
				.hasArg()
				.desc("File containing a vrp experiment.")
				.type(File.class)
				.build());

		options.addOption(Option.builder()
				.option("vrp")
				.longOpt("vrp-file")
				.hasArg()
				.desc("File containing a vrp intance. These files can be created from tsp instances from TSPLIB, by adding two parameters: 'DEPOT' (index of the depot location) and 'NUMBER_OF_VEHICLES' (integer indicating the maximum number of vehicles to be used)\n'http://comopt.ifi.uni-heidelberg.de/software/TSPLIB95/'")
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
}
