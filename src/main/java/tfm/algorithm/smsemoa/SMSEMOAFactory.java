package tfm.algorithm.smsemoa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamTokenizer;

import org.uma.jmetal.algorithm.multiobjective.smsemoa.SMSEMOA;
import org.uma.jmetal.algorithm.multiobjective.smsemoa.SMSEMOABuilder;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.errorchecking.JMetalException;

import tfm.crossover.CrossoverFactory;
import tfm.mutation.MutationFactory;
import tfm.selection.SelectionFactory;
import tfm.utils.FileUtils;
import tfm.vrp.AreaCoverageSolution;

public class SMSEMOAFactory {
	public static SMSEMOA<AreaCoverageSolution> produce(File file,
			Problem<AreaCoverageSolution> problem) throws FileNotFoundException {
		try {

			SMSEMOABuilder<AreaCoverageSolution> espeaBuilder = new SMSEMOABuilder<AreaCoverageSolution>(
					problem, CrossoverFactory.produce(file), MutationFactory.produce(file));

			espeaBuilder.setPopulationSize(getPopulationSize(file));
			espeaBuilder.setSelectionOperator(SelectionFactory.produce(file));
			espeaBuilder.setMaxEvaluations(getMaxEvaluations(file));

			return espeaBuilder.build();
		} catch (Exception e) {
			new JMetalException("SMSEMOAFactory.produce(file): error when reading data file " + e);

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
