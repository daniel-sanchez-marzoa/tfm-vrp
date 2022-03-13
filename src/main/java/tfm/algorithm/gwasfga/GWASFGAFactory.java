package tfm.algorithm.gwasfga;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamTokenizer;

import org.uma.jmetal.algorithm.multiobjective.gwasfga.GWASFGA;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import tfm.crossover.CrossoverFactory;
import tfm.mutation.MutationFactory;
import tfm.selection.SelectionFactory;
import tfm.utils.FileUtils;

public class GWASFGAFactory {
	public static GWASFGA<PermutationSolution<Integer>> produce(File file,
			Problem<PermutationSolution<Integer>> problem) throws FileNotFoundException {
		try {
			return new GWASFGA<>(
					problem,
					getPopulationSize(file),
					getMaxEvaluations(file),
					CrossoverFactory.produce(file),
					MutationFactory.produce(file),
					SelectionFactory.produce(file),
					new SequentialSolutionListEvaluator<PermutationSolution<Integer>>(),
					0.0);
		} catch (Exception e) {
			new JMetalException("GWASFGAFactory.produce(file): error when reading data file " + e);

			return null;
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
