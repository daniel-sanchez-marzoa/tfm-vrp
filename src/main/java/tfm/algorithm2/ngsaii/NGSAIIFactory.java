package tfm.algorithm2.ngsaii;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.measure.MeasureManager;
import org.uma.jmetal.util.measure.impl.BasicMeasure;
import tfm.algorithm.ngsaii.NSGAIISweepMeasures;
import tfm.crossover.CrossoverFactory;
import tfm.mutation.MutationFactory;
import tfm.problem.sweep.SweepCoverageProblem;
import tfm.problem.sweep.SweepCoverageSolution;
import tfm.problem.sweep.runner.SolutionListener;
import tfm.selection.SelectionFactory;
import tfm.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.List;

public class NGSAIIFactory {
    public static NSGAII<SweepCoverageSolution> produce(File file,
                                                        SweepCoverageProblem problem) throws FileNotFoundException {
        try {
//            return new NSGAIIBuilder<>(
//                problem, CrossoverFactory.produce(file), MutationFactory.produce(file), getPopulationSize(file))
//                .setSelectionOperator(SelectionFactory.produce(file))
//                .setMaxEvaluations(getMaxEvaluations(file))
//                .build();

             int maxIterations =getMaxEvaluations(file);

            NSGAIISweepMeasures algorithm =      new NSGAIISweepMeasures(problem,
                maxIterations  * getPopulationSize(file),
                getPopulationSize(file),
                getPopulationSize(file),
                getPopulationSize(file),
                CrossoverFactory.produce(file),
                MutationFactory.produce(file),
                SelectionFactory.produce(file),
                new DominanceComparator<>(),
                new SequentialSolutionListEvaluator<>(),
                true);

            MeasureManager measureManager = algorithm.getMeasureManager();
            BasicMeasure<List<SweepCoverageSolution>> solutionListMeasure = (BasicMeasure<List<SweepCoverageSolution>>)
                measureManager.<List<SweepCoverageSolution>>getPushMeasure("nonDominatedFront");

            solutionListMeasure.register(new SolutionListener(maxIterations - 1, null, problem.getNumberOfDrones()));

            return algorithm;
        } catch (Exception e) {
            throw new JMetalException("NGSAIIFactory.produce(file): error when reading data file " + e);
        }
    }

    private static int getPopulationSize(File file) throws IOException {
        StreamTokenizer token = FileUtils.getTokens(file);
        boolean found = false;
        token.nextToken();

        while (!found) {
            if ((token.sval != null) && ((token.sval.compareTo("POPULATION_SIZE") == 0)))
                found = true;
            else
                token.nextToken();
        }

        token.nextToken();
        token.nextToken();

        return (int) token.nval;
    }

    private static int getMaxEvaluations(File file) throws IOException {
        StreamTokenizer token = FileUtils.getTokens(file);
        boolean found = false;
        token.nextToken();

        while (!found) {
            if ((token.sval != null) && ((token.sval.compareTo("MAX_EVALUATIONS") == 0)))
                found = true;
            else
                token.nextToken();
        }

        token.nextToken();
        token.nextToken();

        return (int) token.nval;
    }
}
