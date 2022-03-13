package tfm.algorithm.ngsaii;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamTokenizer;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

import tfm.crossover.CrossoverFactory;
import tfm.mutation.MutationFactory;
import tfm.selection.SelectionFactory;
import tfm.utils.FileUtils;

public class NGSAIIFactory {
	public static NSGAII<PermutationSolution<Integer>> produce(File file,
			Problem<PermutationSolution<Integer>> problem) throws FileNotFoundException {
		try {
			return new NSGAIIBuilder<PermutationSolution<Integer>>(
					problem, CrossoverFactory.produce(file), MutationFactory.produce(file), getPopulationSize(file))
					.setSelectionOperator(SelectionFactory.produce(file))
					.setMaxEvaluations(getMaxEvaluations(file))
					.build();
		} catch (Exception e) {
			new JMetalException("NGSAIIFactory.produce(file): error when reading data file " + e);

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
