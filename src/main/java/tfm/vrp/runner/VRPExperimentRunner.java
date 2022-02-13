package tfm.vrp.runner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.ExperimentBuilder;
import org.uma.jmetal.lab.experiment.component.impl.ComputeQualityIndicators;
import org.uma.jmetal.lab.experiment.component.impl.ExecuteAlgorithms;
import org.uma.jmetal.lab.experiment.component.impl.GenerateBoxplotsWithR;
import org.uma.jmetal.lab.experiment.component.impl.GenerateFriedmanHolmTestTables;
import org.uma.jmetal.lab.experiment.component.impl.GenerateHtmlPages;
import org.uma.jmetal.lab.experiment.component.impl.GenerateLatexTablesWithStatistics;
import org.uma.jmetal.lab.experiment.component.impl.GenerateReferenceParetoSetAndFrontFromDoubleSolutions;
import org.uma.jmetal.lab.experiment.component.impl.GenerateWilcoxonTestTablesWithR;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.lab.visualization.StudyVisualizer;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import tfm.algorithm.AlgorithmFactory;
import tfm.vrp.VRPFactory;

@Data
@RequiredArgsConstructor
public class VRPExperimentRunner {
	private final int numberOfIndependentRuns;
	private final String studyName;
	private final File experimentBaseDirectory;
	private final File problemsDirectory;
	private final File algorithmsDirectory;
	private int cores = 1;

	public void runExperiment() throws IOException {
		if (experimentBaseDirectory.exists())
			FileUtils.deleteDirectory(experimentBaseDirectory);

		List<ExperimentProblem<PermutationSolution<Integer>>> problems = getProblems();

		List<ExperimentAlgorithm<PermutationSolution<Integer>, List<PermutationSolution<Integer>>>> algorithms = getAlgorithms(
				problems);

		Experiment<PermutationSolution<Integer>, List<PermutationSolution<Integer>>> experiment = new ExperimentBuilder<PermutationSolution<Integer>, List<PermutationSolution<Integer>>>(
				studyName)
						.setExperimentBaseDirectory(experimentBaseDirectory.getAbsolutePath())
						.setAlgorithmList(algorithms)
						.setProblemList(problems)
						.setOutputParetoFrontFileName("FUN")
						.setOutputParetoSetFileName("VAR")
						.setReferenceFrontDirectory(
								new File(experimentBaseDirectory, "referenceFront").getAbsolutePath())
						.setIndicatorList(List.of(
								new Epsilon(),
								new Spread(),
								new GenerationalDistance(),
								new PISAHypervolume(),
								new InvertedGenerationalDistancePlus()))
						.setIndependentRuns(numberOfIndependentRuns)
						.setNumberOfCores(cores)
						.build();

		new ExecuteAlgorithms<>(experiment).run();
		new GenerateReferenceParetoSetAndFrontFromDoubleSolutions(experiment).run();
		new ComputeQualityIndicators<>(experiment).run();

		// Generate Latex tables and R files
		new GenerateLatexTablesWithStatistics(experiment).run();
		new GenerateFriedmanHolmTestTables<>(experiment).run();
		new GenerateWilcoxonTestTablesWithR<>(experiment).run();
		new GenerateBoxplotsWithR<>(experiment).setRows(2).setColumns(3).run();

		// Generate HTML
		new GenerateHtmlPages<>(experiment, StudyVisualizer.TYPE_OF_FRONT_TO_SHOW.MEDIAN).run();
	}

	private List<ExperimentProblem<PermutationSolution<Integer>>> getProblems() throws FileNotFoundException {
		List<ExperimentProblem<PermutationSolution<Integer>>> problemList = new ArrayList<>();
		File[] problemFiles = problemsDirectory.listFiles();

		for (File problemFile : problemFiles)
			problemList.add(new ExperimentProblem<>(VRPFactory.produce(problemFile)));
		return problemList;
	}

	private List<ExperimentAlgorithm<PermutationSolution<Integer>, List<PermutationSolution<Integer>>>> getAlgorithms(
			List<ExperimentProblem<PermutationSolution<Integer>>> problems) throws FileNotFoundException {
		List<ExperimentAlgorithm<PermutationSolution<Integer>, List<PermutationSolution<Integer>>>> algorithms = new ArrayList<>();

		for (int run = 0; run < numberOfIndependentRuns; run++)
			for (File algorithmFile : algorithmsDirectory.listFiles())
				for (ExperimentProblem<PermutationSolution<Integer>> problem : problems)
					algorithms.add(AlgorithmFactory.produce(algorithmFile, problem, run));

		return algorithms;
	}
}
