package tfm.algorithm.spea2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamTokenizer;

import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.errorchecking.JMetalException;

import tfm.crossover.CrossoverFactory;
import tfm.mutation.MutationFactory;
import tfm.selection.SelectionFactory;
import tfm.utils.FileUtils;
import tfm.problem.vrp.AreaCoverageSolution;

public class SPEA2Factory {
//	public static SPEA2<AreaCoverageSolution> produce(File file,
//			Problem<AreaCoverageSolution> problem) throws FileNotFoundException {
//		try {
//
//			SPEA2Builder<AreaCoverageSolution> espeaBuilder = new SPEA2Builder<AreaCoverageSolution>(
//					problem,
//					CrossoverFactory.produce(file),
//					MutationFactory.produce(file));
//
//			espeaBuilder.setPopulationSize(getPopulationSize(file));
//			espeaBuilder.setSelectionOperator(SelectionFactory.produce(file));
//			// espeaBuilder.setMaxEvaluations(getMaxEvaluations(file));
//
//			return espeaBuilder.build();
//		} catch (Exception e) {
//			new JMetalException("SPEA2Factory.produce(file): error when reading data file " + e);
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
