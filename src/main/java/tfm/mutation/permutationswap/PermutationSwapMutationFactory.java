package tfm.mutation.permutationswap;

import java.io.File;
import java.io.FileNotFoundException;

import org.uma.jmetal.util.errorchecking.JMetalException;

import tfm.mutation.MutationFactoryUtils;
import tfm.problem.vrp.VRPFactory;

public class PermutationSwapMutationFactory {
	public static PermutationSwapAreaCoverageMutation produce(File file) throws FileNotFoundException {
		try {
			return new PermutationSwapAreaCoverageMutation(MutationFactoryUtils.getMutationProbability(file),
					VRPFactory.getNumberOfOperators(file));
		} catch (Exception e) {
			new JMetalException("PermutationSwapMutationFactory.produce(file): error when reading data file " + e);

			return null;
		}
	}
}
