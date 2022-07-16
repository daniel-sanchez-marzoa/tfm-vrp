package tfm.crossover;

import java.io.File;
import java.io.IOException;
import java.io.StreamTokenizer;

import tfm.utils.FileUtils;

public class CrossoverFactoryUtils {

	public static double getCrossoverProbability(File file) throws IOException {
		StreamTokenizer token = FileUtils.getTokens(file);
		boolean found = false;
		token.nextToken();

		while (!found) {
			if ((token.sval != null) && ((token.sval.compareTo("CROSSOVER_PROBABILITY") == 0)))
				found = true;
			else
				token.nextToken();
		}

		token.nextToken();
		token.nextToken();

		return (double) token.nval;
	}
}
