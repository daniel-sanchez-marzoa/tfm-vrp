package tfm.algorithm.mombi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamTokenizer;

import org.uma.jmetal.algorithm.multiobjective.mombi.MOMBI;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import tfm.crossover.CrossoverFactory;
import tfm.mutation.MutationFactory;
import tfm.selection.SelectionFactory;
import tfm.utils.FileUtils;
import tfm.problem.vrp.AreaCoverageSolution;

public class MOMBIFactory {
//	public static MOMBI<AreaCoverageSolution> produce(File file,
//			Problem<AreaCoverageSolution> problem) throws FileNotFoundException {
//		try {
//
//			return new MOMBI<>(problem,
//					getMaxEvaluations(file),
//					CrossoverFactory.produce(file),
//					MutationFactory.produce(file),
//					SelectionFactory.produce(file),
//					new SequentialSolutionListEvaluator<AreaCoverageSolution>(),
//					"");
//		} catch (Exception e) {
//			new JMetalException("MOMBIFactory.produce(file): error when reading data file " + e);
//
//			return null;
//		}
//	}
//
//	private static int getMaxEvaluations(File file) throws IOException {
//		StreamTokenizer token = FileUtils.getTokens(file);
//		boolean found = false;
//		token.nextToken();
//
//		while (!found) {
//			if ((token.sval != null) && ((token.sval.compareTo("MAX_EVALUATIONS") == 0)))
//				found = true;
//			else
//				token.nextToken();
//		}
//
//		token.nextToken();
//		token.nextToken();
//
//		return (int) token.nval;
//	}
}
