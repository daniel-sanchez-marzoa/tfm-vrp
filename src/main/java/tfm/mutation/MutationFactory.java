package tfm.mutation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.Arrays;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

import tfm.mutation.permutationswap.PermutationSwapMutationFactory;
import tfm.problem.sweep.SweepCoverageSolution;
import tfm.utils.FileUtils;
import tfm.problem.vrp.AreaCoverageSolution;

public class MutationFactory {
	public static MutationOperator<SweepCoverageSolution> produce(File file) throws FileNotFoundException {
		try {
			switch (getMutationType(file)) {
				case PermutationSwap:
					return PermutationSwapMutationFactory.produce(file);
				default:
                    throw new JMetalException(
							"MutationFactory.produce(file): unrecognized mutation type. Check parameter MUTATION_TYPE to ensure it has one of the following values: "
									+ Arrays.toString(MutationType.values()));
			}
		} catch (Exception e) {
            throw new JMetalException("MutationFactory.produce(file): error when reading data file " + e);
		}
	}

	public static MutationType getMutationType(File file) throws IOException {
		StreamTokenizer token = FileUtils.getTokens(file);
		boolean found = false;
		token.nextToken();

		while (!found) {
			if ((token.sval != null) && ((token.sval.compareTo("MUTATION_TYPE") == 0)))
				found = true;
			else
				token.nextToken();
		}

		token.nextToken();
		token.nextToken();

		return MutationType.valueOf(token.sval);
	}
}
