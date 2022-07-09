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

import tfm.problem.sweep.runner.SweepCoverageRunner;
import tfm.problem.vrp.runner.VRPExperimentRunner;
import tfm.problem.vrp.runner.VRPExperimentRunnerFactory;
import tfm.problem.vrp.runner.VRPRunner;

public final class App {
    public static void main(String[] args) {
        Options options = getProgramOptions();

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("vrpe")) {
                System.out.println("App - vrpe");
                VRPExperimentRunner runner = VRPExperimentRunnerFactory
                    .produce((File) cmd.getParsedOptionValue("vrpe"));

                runner.runExperiment();
            } else if (cmd.hasOption("vrp")) {
                VRPRunner runner = setupVRPRunner(options, args);

                runner.run();
            } else if (cmd.hasOption("sweep")) {
                SweepCoverageRunner runner = setupSweepCoverageProblemRunner(options, args);

                runner.run();
            } else {
                String header = "VRP problem solver using jMetal\n\n";
                String footer = "";

                new HelpFormatter().printHelp("tfm-vrp", header, options, footer, true);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the parameters passed in the command-line to setup a runner for a VRP
     * problem
     *
     * @param options options of the VRP problem and used algorithm
     * @param args    arguments passed in the command-line for the given options
     * @return a runner for the VRP problem
     */
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

    /**
     * Reads the parameters passed in the command-line to setup a runner for a Sweep Coverage
     * problem
     *
     * @param options options of the Sweep Coverage problem and used algorithm
     * @param args    arguments passed in the command-line for the given options
     * @return a runner for the VRP problem
     */
    private static SweepCoverageRunner setupSweepCoverageProblemRunner(Options options, String[] args) {
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            SweepCoverageRunner runner = new SweepCoverageRunner((File) cmd.getParsedOptionValue("sweep"));

            Integer populationSize = (Integer) cmd.getParsedOptionValue("p");
            Integer maxEvaluations = (Integer) cmd.getParsedOptionValue("e");
            Float crossoverProbability = (Float) cmd.getParsedOptionValue("cp");
            Integer lengthModificationPercentage = cmd.getParsedOptionValue("l") != null ?
                ((Number) cmd.getParsedOptionValue("l")).intValue()
                : null;

            if (populationSize != null)
                runner.setPopulationSize(populationSize);
            else
                populationSize = 100;

            if (maxEvaluations != null)
                runner.setMaxIterations(maxEvaluations / populationSize);

            if (crossoverProbability != null)
                runner.setCrossoverProbability(crossoverProbability);

            if (lengthModificationPercentage != null)
                runner.setLengthModificationPercentage(lengthModificationPercentage);

            runner.setInitializePopulation(cmd.hasOption("i"));
            runner.setShuffleSweeps(cmd.hasOption("s"));

            return runner;
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

        return null;
    }

    /**
     * get the avaiable command-line options for the current application
     *
     * @return the avaiable command-line options
     */
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
            .option("sweep")
            .longOpt("sweep-file")
            .hasArg()
            .desc("File containing a sweep coverage intance.")
            .type(File.class)
            .build());

        options.addOption(Option.builder()
            .option("i")
            .longOpt("initialize-population")
            .desc("Initializes the population with one individual per number of drones and the rest of the population is random")
            .build());

        options.addOption(Option.builder()
            .option("s")
            .longOpt("shuffle-sweeps")
            .desc("Shuffles the sweeps to get a new instance of the problem. The new sweeps are saved in the shuffledSweeps.csv")
            .build());

        options.addOption(Option.builder()
            .option("l")
            .longOpt("modify-length")
            .hasArg()
            .desc("Modifies the length of the sweeps randomly inside the given percentage.")
            .type(Number.class)
            .build());

        options.addOption(Option.builder()
            .option("p")
            .longOpt("population-size")
            .hasArg()
            .desc("Size of the genetic algorithm's population. Default = 100")
            .type(Number.class)
            .build());

        options.addOption(Option.builder()
            .option("e")
            .longOpt("max-evaluations")
            .hasArg()
            .desc("Maximum number of evaluations (iterations) for the algorithm. Default = 1000")
            .type(Number.class)
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
