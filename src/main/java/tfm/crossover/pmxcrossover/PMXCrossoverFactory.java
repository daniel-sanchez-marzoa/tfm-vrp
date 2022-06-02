package tfm.crossover.pmxcrossover;

import java.io.File;
import java.io.FileNotFoundException;

import org.uma.jmetal.util.errorchecking.JMetalException;

import tfm.crossover.CrossoverFactoryUtils;

public class PMXCrossoverFactory {
	public static PMXAreaCoverageCrossover produce(File file) throws FileNotFoundException {
		try {
			return new PMXAreaCoverageCrossover(CrossoverFactoryUtils.getCrossoverProbability(file));
		} catch (Exception e) {
			new JMetalException("PMXCrossoverFactory.produce(file): error when reading data file " + e);

			return null;
		}
	}
}
