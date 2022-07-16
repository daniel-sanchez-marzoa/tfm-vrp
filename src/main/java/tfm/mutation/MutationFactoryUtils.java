package tfm.mutation;

import java.io.File;
import java.io.IOException;
import java.io.StreamTokenizer;

import tfm.utils.FileUtils;

public class MutationFactoryUtils {

	public static double getMutationProbability(File file) throws IOException {
		StreamTokenizer token = FileUtils.getTokens(file);
		boolean found = false;
		token.nextToken();

		while (!found) {
			if ((token.sval != null) && ((token.sval.compareTo("MUTATION_PROBABILITY") == 0)))
				found = true;
			else
				token.nextToken();
		}

		token.nextToken();
		token.nextToken();

		return (double) token.nval;
	}
}
