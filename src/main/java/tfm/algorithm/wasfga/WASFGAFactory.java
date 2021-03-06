package tfm.algorithm.wasfga;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;

import org.uma.jmetal.algorithm.multiobjective.wasfga.WASFGA;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import tfm.crossover.CrossoverFactory;
import tfm.mutation.MutationFactory;
import tfm.selection.SelectionFactory;
import tfm.utils.FileUtils;
import tfm.problem.vrp.AreaCoverageSolution;

public class WASFGAFactory {
//	public static WASFGA<AreaCoverageSolution> produce(File file,
//			Problem<AreaCoverageSolution> problem) throws FileNotFoundException {
//		try {
//			return new WASFGA<AreaCoverageSolution>(
//					problem,
//					getPopulationSize(file),
//					getMaxEvaluations(file),
//					CrossoverFactory.produce(file),
//					MutationFactory.produce(file),
//					SelectionFactory.produce(file),
//					new SequentialSolutionListEvaluator<AreaCoverageSolution>(),
//					0.0,
//					new ArrayList<Double>());
//		} catch (Exception e) {
//			new JMetalException("WASFGAFactory.produce(file): error when reading data file " + e);
//
//			return null;
//		}
//	}
//
//	private static int getPopulationSize(File file) throws IOException {
//		StreamTokenizer token = FileUtils.getTokens(file);
//		boolean found = false;
//		token.nextToken();
//
//		while (!found) {
//			if ((token.sval != null) && ((token.sval.compareTo("POPULATION_SIZE") == 0)))
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
