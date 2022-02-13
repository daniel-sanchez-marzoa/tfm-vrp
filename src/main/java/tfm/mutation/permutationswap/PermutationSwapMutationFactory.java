package tfm.mutation.permutationswap;

import java.io.File;
import java.io.FileNotFoundException;

import org.uma.jmetal.operator.mutation.impl.PermutationSwapMutation;
import org.uma.jmetal.util.errorchecking.JMetalException;

import tfm.mutation.MutationFactoryUtils;

public class PermutationSwapMutationFactory {
	public static PermutationSwapMutation<Integer> produce(File file) throws FileNotFoundException {
		try {
			return new PermutationSwapMutation<Integer>(MutationFactoryUtils.getMutationProbability(file));
		} catch (Exception e) {
			new JMetalException("PermutationSwapMutationFactory.produce(file): error when reading data file " + e);

			return null;
		}
	}
}
