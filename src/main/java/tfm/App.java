package tfm;

import org.apache.commons.cli.*;
import tfm.problem.sweep.SweepManager;
import tfm.problem.sweep.runner.SweepCoverageExperimentRunner;
import tfm.problem.sweep.runner.SweepCoverageExperimentRunnerFactory;
import tfm.problem.sweep.runner.SweepCoverageRunner;
import tfm.problem.vrp.runner.VRPExperimentRunner;
import tfm.problem.vrp.runner.VRPExperimentRunnerFactory;
import tfm.problem.vrp.runner.VRPRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
            } else if (cmd.hasOption("vrp")) {
                VRPRunner runner = setupVRPRunner(options, args);

                runner.run();
            } else if (cmd.hasOption("sweep")) {
                List<SweepCoverageRunner> runners = SweepManager.setupSweepCoverageProblemRunner(options, args);

                int numberOfRuns = SweepManager.getNumberOfRuns(cmd);

                SweepManager.runAlgorithm(runners, numberOfRuns);
                SweepManager.analyzeResults(runners, numberOfRuns);
            } else if (cmd.hasOption("sweepe")) {
                SweepCoverageExperimentRunner runner = SweepCoverageExperimentRunnerFactory
                    .produce((File) cmd.getParsedOptionValue("sweepe"));

                runner.runExperiment();
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
            .option("sweepe")
            .longOpt("sweep-experiment")
            .hasArg()
            .desc("File containing a sweep experiment.")
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
            .desc("Size of the genetic algorithm's population. It's given as an array of sizes to have the algorithm executed wit. Default = [100]")
            .type(String.class)
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

        options.addOption(Option.builder()
            .option("nr")
            .longOpt("number-of-runs")
            .hasArg()
            .desc("Number of times that the problem should be run. It saves one convergency file for each run. Default = 1")
            .type(Number.class)
            .build());

        return options;
    }
}
