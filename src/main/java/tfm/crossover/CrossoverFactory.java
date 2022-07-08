package tfm.crossover;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamTokenizer;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.util.errorchecking.JMetalException;

import tfm.crossover.pmxcrossover.PMXCrossoverFactory;
import tfm.utils.FileUtils;
import tfm.problem.vrp.AreaCoverageSolution;

public class CrossoverFactory {
	public static CrossoverOperator<AreaCoverageSolution> produce(File file) throws FileNotFoundException {
		try {
			switch (getCrossoverType(file)) {
				case PMX:
					return PMXCrossoverFactory.produce(file);
				default:
					new JMetalException(
							"CrossoverFactory.produce(file): unrecognized crossover type. Check parameter CROSSOVER_TYPE to ensure it has one of the following values: "
									+ CrossoverType.values());

					return null;
			}
		} catch (Exception e) {
			new JMetalException("CrossoverFactory.produce(file): error when reading data file " + e);

			return null;
		}
	}

	public static CrossoverType getCrossoverType(File file) throws IOException {
		StreamTokenizer token = FileUtils.getTokens(file);
		boolean found = false;
		token.nextToken();

		while (!found) {
			if ((token.sval != null) && ((token.sval.compareTo("CROSSOVER_TYPE") == 0)))
				found = true;
			else
				token.nextToken();
		}

		token.nextToken();
		token.nextToken();

		return CrossoverType.valueOf(token.sval);
	}
}
