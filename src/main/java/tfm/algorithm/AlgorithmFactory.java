package tfm.algorithm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.List;

import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

import tfm.algorithm.ngsaii.NGSAIIFactory;
import tfm.utils.FileUtils;

public class AlgorithmFactory {
	public static ExperimentAlgorithm<PermutationSolution<Integer>, List<PermutationSolution<Integer>>> produce(
			File file, ExperimentProblem<PermutationSolution<Integer>> experimentProblem, int run)
			throws FileNotFoundException {
		try {
			switch (getAlgorithmType(file)) {
				case NGSAII:
					return new ExperimentAlgorithm<>(NGSAIIFactory.produce(file, experimentProblem.getProblem()),
							getAlgorithmName(file), experimentProblem, run);
				default:
					new JMetalException(
							"AlgorithmFactory.produce(file): unrecognized algorithm type. Check parameter ALGORITHM_TYPE to ensure it has one of the following values: "
									+ AlgorithmType.values());

					return null;
			}
		} catch (Exception e) {
			new JMetalException("AlgorithmFactory.produce(file): error when reading data file " + e);

			return null;
		}
	}

	public static AlgorithmType getAlgorithmType(File file) throws IOException {
		StreamTokenizer token = FileUtils.getTokens(file);
		boolean found = false;
		token.nextToken();

		while (!found) {
			if ((token.sval != null) && ((token.sval.compareTo("ALGORITHM_TYPE") == 0)))
				found = true;
			else
				token.nextToken();
		}

		token.nextToken();
		token.nextToken();

		return AlgorithmType.valueOf(token.sval);
	}

	public static String getAlgorithmName(File file) throws IOException {
		StreamTokenizer token = FileUtils.getTokens(file);
		boolean found = false;
		token.nextToken();

		while (!found) {
			if ((token.sval != null) && ((token.sval.compareTo("ALGORITHM_NAME") == 0)))
				found = true;
			else
				token.nextToken();
		}

		token.nextToken();
		token.nextToken();

		return token.sval;
	}
}
