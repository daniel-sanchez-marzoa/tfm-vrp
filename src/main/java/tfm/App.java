package tfm;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import tfm.problem.sweep.runner.SweepCoverageRunner;
import tfm.problem.vrp.runner.VRPExperimentRunner;
import tfm.problem.vrp.runner.VRPExperimentRunnerFactory;
import tfm.problem.vrp.runner.VRPRunner;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
                List<SweepCoverageRunner> runners = setupSweepCoverageProblemRunner(options, args);

                int numberOfRuns = getNumberOfRuns(cmd);

                runAlgorithm(runners, numberOfRuns);

                for (SweepCoverageRunner runner : runners) {
                    HashMap<Integer, List<BigDecimal>> convergenceSum = new HashMap<>();

                    System.out.println("Analyzing results with population: " + runner.getPopulationSize());

                    for (int i = 1; i <= numberOfRuns; i++) {
                        readConvergencyCsv(runner, convergenceSum, i);
                        System.out.print("\r" + (int) (((float) i / numberOfRuns) * 100) + "%");
                    }

                    writeToSummaryCsv(runner, convergenceSum, numberOfRuns);

                    System.out.println();
                }
            } else {
                String header = "VRP problem solver using jMetal\n\n";
                String footer = "";

                new HelpFormatter().printHelp("tfm-vrp", header, options, footer, true);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static void writeToSummaryCsv(SweepCoverageRunner runner, HashMap<Integer, List<BigDecimal>> convergenceSum, int numberOfRuns) throws IOException {
        int iteration = 1;

        StringBuilder str = new StringBuilder("Iteración");

        for (int h = 1; h <= convergenceSum.get(1).size(); h++)
            str.append(",").append(h);

        File csv = new File("results\\resumen-pop-" + runner.getPopulationSize() + ".csv");
        BufferedWriter writer = new BufferedWriter(new FileWriter(csv, true));
        writer.write(str.toString());

        while (convergenceSum.containsKey(iteration)) {
            try {
                writer.append("\n").append(String.valueOf(iteration)).append(",");

                for (int j = 0; j < convergenceSum.get(iteration).size(); j++) {
                    if (convergenceSum.containsKey(iteration))
                        writer.append(String.valueOf(convergenceSum.get(iteration).get(j).divide(BigDecimal.valueOf(numberOfRuns), RoundingMode.UP).floatValue()));

                    if (j+1 != convergenceSum.get(iteration).size())
                        writer.append(",");
                }

//                                written = true;
            } catch (Throwable e) {
                e.printStackTrace();
            }

            iteration++;
        }


        writer.close();
    }

    private static void readConvergencyCsv(SweepCoverageRunner runner, HashMap<Integer, List<BigDecimal>> convergenceSum, int i) throws IOException {
        File convergenceFile = new File("results\\run-" + i + "\\pop-" + runner.getPopulationSize(), "convergencia.csv");

        BufferedReader reader = new BufferedReader(new FileReader(convergenceFile));
        String line;

        reader.readLine();
        line = reader.readLine();

        while (line != null) {
            String[] columns = line.split(",");

            if (convergenceSum.containsKey(Integer.valueOf(columns[0]))) {
                List<BigDecimal> distances = convergenceSum.get(Integer.valueOf(columns[0]));

                for (int d = 0; d < distances.size(); d++) {
                    distances.set(d, distances.get(d).add(new BigDecimal(columns[d + 1])));
                }
            } else {
                List<BigDecimal> distances = new ArrayList<>();

                for (int d = 1; d < columns.length; d++) {
                    distances.add(new BigDecimal(columns[d]));
                }

                convergenceSum.put(Integer.valueOf(columns[0]), distances);
            }

            line = reader.readLine();
        }

        reader.close();
    }

    private static void runAlgorithm(List<SweepCoverageRunner> runners, Integer numberOfRuns) throws IOException {
        for (int i = 1; i <= numberOfRuns; i++) {
            for (SweepCoverageRunner runner : runners) {
                runner.setResultsDirectory(new File("results\\run-" + i + "\\pop-" + runner.getPopulationSize()));
                System.out.println("Executing run " + i + " with population: " + runner.getPopulationSize());
                runner.run();
                System.out.println();
            }
        }
    }

    private static Integer getNumberOfRuns(CommandLine cmd) throws ParseException {
        Integer numberOfRuns = cmd.getParsedOptionValue("nr") != null ?
            ((Number) cmd.getParsedOptionValue("nr")).intValue()
            : null;

        if (numberOfRuns == null)
            numberOfRuns = 1;
        return numberOfRuns;
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
    private static List<SweepCoverageRunner> setupSweepCoverageProblemRunner(Options options, String[] args) {
        List<SweepCoverageRunner> runners = new ArrayList<>();

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            Integer maxEvaluations = (Integer) cmd.getParsedOptionValue("e");
            Float crossoverProbability = (Float) cmd.getParsedOptionValue("cp");
            Integer lengthModificationPercentage = cmd.getParsedOptionValue("l") != null ?
                ((Number) cmd.getParsedOptionValue("l")).intValue()
                : null;

            List<Integer> populationSizes = getPopulationSizes(cmd);

            File resultsDirectory = new File("results");

            if (resultsDirectory.exists()) FileUtils.deleteDirectory(resultsDirectory);

            for (Integer size : populationSizes) {
                SweepCoverageRunner runner = new SweepCoverageRunner((File) cmd.getParsedOptionValue("sweep"));
                runner.setPopulationSize(size);

                if (maxEvaluations != null)
                    runner.setMaxIterations(maxEvaluations / size);

                if (crossoverProbability != null)
                    runner.setCrossoverProbability(crossoverProbability);

                if (lengthModificationPercentage != null)
                    runner.setLengthModificationPercentage(lengthModificationPercentage);

                runner.setInitializePopulation(cmd.hasOption("i"));
                runner.setShuffleSweeps(cmd.hasOption("s"));
                runners.add(runner);
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("utility-name", options);

            System.exit(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return runners;
    }

    private static List<Integer> getPopulationSizes(CommandLine cmd) throws ParseException {
        String populationSizeString = (String) cmd.getParsedOptionValue("p");
        if (populationSizeString == null)
            populationSizeString = "[100]";

        List<Integer> populationSizes = new ArrayList<>();
        populationSizeString = populationSizeString.replace("[", "").replace("]", "");

        for (String size : populationSizeString.split(",")) {
            populationSizes.add(Integer.valueOf(size));
        }
        return populationSizes;
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
