package tfm.algorithm2.ngsaii;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.legacy.front.Front;
import org.uma.jmetal.util.legacy.front.impl.ArrayFront;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.util.measure.Measurable;
import org.uma.jmetal.util.measure.MeasureManager;
import org.uma.jmetal.util.measure.impl.BasicMeasure;
import org.uma.jmetal.util.measure.impl.CountingMeasure;
import org.uma.jmetal.util.measure.impl.DurationMeasure;
import org.uma.jmetal.util.measure.impl.SimpleMeasureManager;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import tfm.problem.sweep.SweepCoverageProblem;
import tfm.problem.sweep.SweepCoverageSolution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NSGAIISweepMeasures extends NSGAII<SweepCoverageSolution> implements Measurable {
    protected CountingMeasure evaluations;
    protected CountingMeasure iterations;
    protected DurationMeasure durationMeasure;
    protected SimpleMeasureManager measureManager;
    protected BasicMeasure<List<SweepCoverageSolution>> solutionListMeasure;
    protected BasicMeasure<Integer> numberOfNonDominatedSolutionsInPopulation;
    protected BasicMeasure<List<Solution>> nonDominatedFrontMeasure;
    protected BasicMeasure<Double> hypervolumeValue;
    protected Front referenceFront = new ArrayFront();

    private boolean initializePopulation;

    public NSGAIISweepMeasures(Problem<SweepCoverageSolution> problem,
                               int maxIterations,
                               int populationSize,
                               int matingPoolSize,
                               int offspringPopulationSize,
                               CrossoverOperator<SweepCoverageSolution> crossoverOperator,
                               MutationOperator<SweepCoverageSolution> mutationOperator,
                               SelectionOperator<List<SweepCoverageSolution>, SweepCoverageSolution> selectionOperator,
                               Comparator<SweepCoverageSolution> dominanceComparator,
                               SolutionListEvaluator<SweepCoverageSolution> evaluator,
                               boolean initializePopulation) {
        super(problem,
            maxIterations,
            populationSize,
            matingPoolSize,
            offspringPopulationSize,
            crossoverOperator,
            mutationOperator,
            selectionOperator,
            dominanceComparator,
            evaluator);

        this.initializePopulation = initializePopulation;

        this.initMeasures();
    }

    protected void initProgress() {
        this.evaluations.reset(this.getMaxPopulationSize());
    }

    protected void updateProgress() {
        this.evaluations.increment(this.getMaxPopulationSize());
        this.iterations.increment(1);
        this.solutionListMeasure.push(this.getPopulation());
        this.getPopulation().get(0).objectives();
        if (this.referenceFront.getNumberOfPoints() > 0) {
            this.hypervolumeValue.push((new PISAHypervolume(this.referenceFront)).evaluate(SolutionListUtils.getNonDominatedSolutions(this.getPopulation())));
        }

    }

    protected boolean isStoppingConditionReached() {
        return this.evaluations.get() >= (long) this.maxEvaluations;
    }

    public void run() {
        this.durationMeasure.reset();
        this.durationMeasure.start();
        this.population = createInitialPopulation();
        this.population = this.evaluatePopulation(this.population);
        this.initProgress();

        while (!this.isStoppingConditionReached()) {
            List<SweepCoverageSolution> matingPopulation = this.selection(this.population);
            List<SweepCoverageSolution> offspringPopulation = this.reproduction(matingPopulation);
            offspringPopulation = this.evaluatePopulation(offspringPopulation);
            this.population = this.replacement(this.population, offspringPopulation);
            this.updateProgress();
        }
        this.durationMeasure.stop();
    }

    protected List<SweepCoverageSolution> createInitialPopulation() {
        List<SweepCoverageSolution> population = new ArrayList<>(this.getMaxPopulationSize());
        int start = 0;

        if (initializePopulation) {
            int numberOfDrones = ((SweepCoverageProblem) problem).getNumberOfDrones();

            for (int i = 1; i <= numberOfDrones; i++)
                population.add(((SweepCoverageProblem) problem).createSolution(true, i));

            start = numberOfDrones;
        }

        for (int i = start; i < this.getMaxPopulationSize(); ++i)
            population.add(((SweepCoverageProblem) problem).createSolution(false, 0));

        return population;
    }

    private void initMeasures() {
        this.durationMeasure = new DurationMeasure();
        this.evaluations = new CountingMeasure(0L);
        this.iterations = new CountingMeasure(0L);
        this.numberOfNonDominatedSolutionsInPopulation = new BasicMeasure();
        this.nonDominatedFrontMeasure = new BasicMeasure<>();
        this.solutionListMeasure = new BasicMeasure();
        this.hypervolumeValue = new BasicMeasure();
        this.measureManager = new SimpleMeasureManager();
        this.measureManager.setPullMeasure("currentExecutionTime", this.durationMeasure);
        this.measureManager.setPullMeasure("currentEvaluation", this.evaluations);
        this.measureManager.setPullMeasure("currentIteration", this.iterations);
        this.measureManager.setPullMeasure("nonDominatedFront", this.nonDominatedFrontMeasure);
        this.measureManager.setPullMeasure("numberOfNonDominatedSolutionsInPopulation", this.numberOfNonDominatedSolutionsInPopulation);
        this.measureManager.setPushMeasure("currentPopulation", this.solutionListMeasure);
        this.measureManager.setPushMeasure("currentEvaluation", this.evaluations);
        this.measureManager.setPushMeasure("currentIteration", this.iterations);
        this.measureManager.setPushMeasure("nonDominatedFront", this.nonDominatedFrontMeasure);
        this.measureManager.setPushMeasure("hypervolume", this.hypervolumeValue);
    }

    public MeasureManager getMeasureManager() {
        return this.measureManager;
    }

    protected List<SweepCoverageSolution> replacement(List<SweepCoverageSolution> population, List<SweepCoverageSolution> offspringPopulation) {
        List<SweepCoverageSolution> pop = super.replacement(population, offspringPopulation);
        Ranking<SweepCoverageSolution> ranking = new FastNonDominatedSortRanking(this.dominanceComparator);
        ranking.compute(population);
        this.numberOfNonDominatedSolutionsInPopulation.set(ranking.getSubFront(0).size());
        this.nonDominatedFrontMeasure.push(ranking.getSubFront(0).stream().map(s -> (Solution) s).collect(Collectors.toList()));
        return pop;
    }

    public CountingMeasure getEvaluations() {
        return this.evaluations;
    }

    public String getName() {
        return "NSGAIIM";
    }

    public String getDescription() {
        return "Nondominated Sorting Genetic Algorithm version II. Version using measures";
    }

    public void setReferenceFront(Front referenceFront) {
        this.referenceFront = referenceFront;
    }
}
