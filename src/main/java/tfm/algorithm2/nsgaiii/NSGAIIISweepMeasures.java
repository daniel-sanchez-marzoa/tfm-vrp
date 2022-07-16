package tfm.algorithm2.nsgaiii;

import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIII;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.legacy.front.Front;
import org.uma.jmetal.util.legacy.front.impl.ArrayFront;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.util.measure.Measurable;
import org.uma.jmetal.util.measure.MeasureManager;
import org.uma.jmetal.util.measure.impl.BasicMeasure;
import org.uma.jmetal.util.measure.impl.CountingMeasure;
import org.uma.jmetal.util.measure.impl.DurationMeasure;
import org.uma.jmetal.util.measure.impl.SimpleMeasureManager;
import tfm.problem.sweep.SweepCoverageProblem;
import tfm.problem.sweep.SweepCoverageSolution;

import java.util.ArrayList;
import java.util.List;

public class NSGAIIISweepMeasures extends NSGAIII<SweepCoverageSolution> implements Measurable {
    protected CountingMeasure evaluations;
    protected CountingMeasure iterations;
    protected DurationMeasure durationMeasure;
    protected SimpleMeasureManager measureManager;
    protected BasicMeasure<List<SweepCoverageSolution>> solutionListMeasure;
    protected BasicMeasure<Integer> numberOfNonDominatedSolutionsInPopulation;
    protected BasicMeasure<List<Solution>> nonDominatedFrontMeasure;
    protected BasicMeasure<Double> hypervolumeValue;
    protected Front referenceFront = new ArrayFront();

    private final boolean initializePopulation;

    public NSGAIIISweepMeasures(NSGAIIIBuilder<SweepCoverageSolution> builder,
                                SelectionOperator<List<SweepCoverageSolution>, SweepCoverageSolution> selectionOperator,
                                boolean initializePopulation) {


        super(builder);

        this.selectionOperator = selectionOperator;
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
        return this.evaluations.get() >= (long) this.maxIterations;
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

    public String getName() {
        return "NSGAIIIM";
    }

    public String getDescription() {
        return "Nondominated Sorting Genetic Algorithm version III. Version using measures";
    }
}
