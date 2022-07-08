package tfm.problem.sweep.runner;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.ExperimentBuilder;
import org.uma.jmetal.lab.experiment.component.impl.*;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.lab.visualization.StudyVisualizer;
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import tfm.algorithm.AlgorithmFactory;
import tfm.problem.sweep.SweepCoverageSolution;
import tfm.problem.sweep.SweepCoverageProblemFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class executes jMetal experiments, running multiple algorithms against
 * multiple problems and comparing the results
 */
@Data
@RequiredArgsConstructor
public class SweepCoverageExperimentRunner {
//	private final int numberOfIndependentRuns;
//	private final String studyName;
//	private final File experimentBaseDirectory;
//	private final File problemsDirectory;
//	private final File algorithmsDirectory;
//	private int cores = 1;
//
//	/**
//	 * Runs a jMetal experiment. These experiments execute multiple algorithms
//	 * against multiple problems and calculates a number of metrics to compare the
//	 * results.
//	 * <br>
//	 * <br>
//	 * These results are stored in a directory with the following subdirectories:
//	 * <ul>
//	 * <li><b>referenceFronts</b>: contains the reference fronts that will be used
//	 * to
//	 * calculate the metrics.
//	 * <li><b>[experimentName]</b>: folder containing the results of the experiment
//	 * <ul>
//	 * <li><b>data</b>: folder containing a folder for each algorithm and inside of
//	 * it a
//	 * folder for each problem, containing the result of each run and the best
//	 * results for each metric
//	 * <li><b>html</b>: html files presenting the results
//	 * <li><b>latex</b>: latex files presenting the results
//	 * <li><b>R</b>: R files presenting the result
//	 * <li><b>QualityIndicatorSummary.csv</b>: csv file containing a summary of the
//	 * results
//	 * </ul>
//	 * </ul>
//	 *
//	 * @throws IOException
//	 */
//	public void runExperiment() throws IOException {
//		if (experimentBaseDirectory.exists())
//			FileUtils.deleteDirectory(experimentBaseDirectory);
//
//		List<ExperimentProblem<SweepCoverageSolution>> problems = getProblems();
//
//		List<ExperimentAlgorithm<SweepCoverageSolution, List<SweepCoverageSolution>>> algorithms = getAlgorithms(
//				problems);
//
//		Experiment<SweepCoverageSolution, List<SweepCoverageSolution>> experiment = getExperiment(
//				problems, algorithms);
//
//		// Execute the algorithm
//		new ExecuteAlgorithms<>(experiment).run();
//		new GenerateReferenceParetoSetAndFrontFromDoubleSolutions(experiment).run();
//		new ComputeQualityIndicators<>(experiment).run();
//
//		// Generate Latex tables
//		new GenerateLatexTablesWithStatistics(experiment).run();
//		new GenerateFriedmanHolmTestTables<>(experiment).run();
//
//		// Generate R files
//		new GenerateWilcoxonTestTablesWithR<>(experiment).run();
//		new GenerateBoxplotsWithR<>(experiment).setRows(2).setColumns(3).run();
//
//		// Generate HTML
//		new GenerateHtmlPages<>(experiment, StudyVisualizer.TYPE_OF_FRONT_TO_SHOW.MEDIAN).run();
//	}
//
//	private Experiment<SweepCoverageSolution, List<SweepCoverageSolution>> getExperiment(
//			List<ExperimentProblem<SweepCoverageSolution>> problems,
//			List<ExperimentAlgorithm<SweepCoverageSolution, List<SweepCoverageSolution>>> algorithms) {
//		Experiment<SweepCoverageSolution, List<SweepCoverageSolution>> experiment = new ExperimentBuilder<SweepCoverageSolution, List<SweepCoverageSolution>>(
//				studyName)
//				.setExperimentBaseDirectory(experimentBaseDirectory.getAbsolutePath())
//				.setAlgorithmList(algorithms)
//				.setProblemList(problems)
//				.setOutputParetoFrontFileName("FUN")
//				.setOutputParetoSetFileName("VAR")
//				.setReferenceFrontDirectory(
//						new File(experimentBaseDirectory, "referenceFronts").getAbsolutePath())
//				.setIndicatorList(List.of(
//						// new Epsilon(),
//						new Spread(),
//						new GenerationalDistance(),
//						new PISAHypervolume())
//				// new InvertedGenerationalDistancePlus())
//				)
//				.setIndependentRuns(numberOfIndependentRuns)
//				.setNumberOfCores(cores)
//				.build();
//		return experiment;
//	}
//
//	private List<ExperimentProblem<SweepCoverageSolution>> getProblems() throws FileNotFoundException {
//		List<ExperimentProblem<SweepCoverageSolution>> problemList = new ArrayList<>();
//		File[] problemFiles = problemsDirectory.listFiles();
//
//		for (File problemFile : problemFiles)
//			problemList.add(new ExperimentProblem<>(SweepCoverageProblemFactory.produce(problemFile)));
//		return problemList;
//	}
//
//	private List<ExperimentAlgorithm<SweepCoverageSolution, List<SweepCoverageSolution>>> getAlgorithms(
//			List<ExperimentProblem<SweepCoverageSolution>> problems) throws FileNotFoundException {
//		List<ExperimentAlgorithm<SweepCoverageSolution, List<SweepCoverageSolution>>> algorithms = new ArrayList<>();
//
//		for (int run = 0; run < numberOfIndependentRuns; run++)
//			for (File algorithmFile : algorithmsDirectory.listFiles())
//				for (ExperimentProblem<SweepCoverageSolution> problem : problems)
//					algorithms.add(AlgorithmFactory.produce(algorithmFile, problem, run));
//
//		return algorithms;
//	}
}
