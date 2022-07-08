package tfm.problem.sweep.runner;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.lab.visualization.plot.impl.Plot2D;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.PMXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PermutationSwapMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.permutationproblem.PermutationProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.measure.MeasureManager;
import org.uma.jmetal.util.measure.impl.BasicMeasure;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import tfm.algorithm.AlgorithmRunner;
import tfm.algorithm.ngsaii.NSGAIISweepMeasures;
import tfm.problem.sweep.SweepCoverageProblem;
import tfm.problem.sweep.SweepCoverageProblemFactory;
import tfm.problem.sweep.SweepCoverageSolution;
import tfm.selection.comparators.AreaCoverageDominanceComparator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * This class executes a jMetal algorithm against a single problem.
 */
@Data
@RequiredArgsConstructor
public class SweepCoverageRunner {
    private final File sweepCoverageProblemFile;
    private boolean initializePopulation;
    private int populationSize = 50;
    private int maxIterations = 10000;
    private float crossoverProbability = (float) 0.9;
    private Float mutationProbability = (float) 0.05;

    public void run() throws IOException {
        Algorithm<List<SweepCoverageSolution>> algorithm = getAlgorithm();

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

        saveResults(algorithm, algorithmRunner);
    }

    private Algorithm<List<SweepCoverageSolution>> getAlgorithm() throws IOException {
        SweepCoverageProblem problem = SweepCoverageProblemFactory.produce(sweepCoverageProblemFile);
        CrossoverOperator crossover = new PMXCrossover(crossoverProbability);
        SelectionOperator<List<SweepCoverageSolution>, SweepCoverageSolution> selection = new BinaryTournamentSelection<>(
            new AreaCoverageDominanceComparator<>());
        MutationOperator<SweepCoverageSolution> mutation;
        NSGAIISweepMeasures algorithm;

        createConvergencyFile(problem);

        if (mutationProbability == null)
            mutationProbability = (float) (1.0 / problem.getNumberOfVariables());

        mutation = new PermutationSwapMutation(mutationProbability);

        algorithm = new NSGAIISweepMeasures(problem,
            maxIterations * populationSize,
            populationSize,
            populationSize,
            populationSize,
            crossover,
            mutation,
            selection,
            new DominanceComparator<>(),
            new SequentialSolutionListEvaluator<>(),
            initializePopulation);

        MeasureManager measureManager = algorithm.getMeasureManager();
        BasicMeasure<List<SweepCoverageSolution>> solutionListMeasure = (BasicMeasure<List<SweepCoverageSolution>>)
            measureManager.<List<SweepCoverageSolution>>getPushMeasure("nonDominatedFront");
//        CountingMeasure iterationMeasure = (CountingMeasure) measureManager.<Long>getPushMeasure("currentIteration");

        solutionListMeasure.register(new SolutionListener(problem.getNumberOfDrones()));
//        iterationMeasure.register(new IterationListener());

//        new NSGAIIBuilder<SweepCoverageSolution>(problem, crossover, mutation, populationSize)
//            .setSelectionOperator(selection)
//            .setMaxEvaluations(maxEvaluations)
//            .setVariant(NSGAIIBuilder.NSGAIIVariant.Measures)
//            .build();

        return algorithm;
    }

    private void createConvergencyFile(SweepCoverageProblem problem) throws IOException {
        File csv = new File("convergencia.csv");

        if (csv.exists()) csv.delete();

        StringBuilder str = new StringBuilder("Iteración");

        for (int i = 1; i <= problem.getNumberOfDrones(); i++)
            str.append(",").append(i);

        BufferedWriter writer = new BufferedWriter(new FileWriter(csv));
        writer.write(str.toString());

        writer.close();
    }

    private void saveResults(Algorithm<List<SweepCoverageSolution>> algorithm, AlgorithmRunner algorithmRunner) {
        List<SweepCoverageSolution> population = algorithm.getResult();
        long computingTime = algorithmRunner.getComputingTime();

        double[][] a = new double[][]{{1, 2}, {2, 2}};

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
