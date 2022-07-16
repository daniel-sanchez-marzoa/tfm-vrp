package tfm.mutation.permutationswap;

import java.io.File;
import java.io.FileNotFoundException;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PermutationSwapMutation;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

import tfm.mutation.MutationFactoryUtils;
import tfm.problem.sweep.SweepCoverageSolution;
import tfm.problem.vrp.VRPFactory;

public class PermutationSwapMutationFactory {
	public static MutationOperator<SweepCoverageSolution> produce(File file) throws FileNotFoundException {
		try {
			return  new PermutationSwapMutation(MutationFactoryUtils.getMutationProbability(file));
//            return new PermutationSwapAreaCoverageMutation(MutationFactoryUtils.getMutationProbability(file),
//					VRPFactory.getNumberOfOperators(file));
		} catch (Exception e) {
		throw	new JMetalException("PermutationSwapMutationFactory.produce(file): error when reading data file " + e);
		}
	}
}
