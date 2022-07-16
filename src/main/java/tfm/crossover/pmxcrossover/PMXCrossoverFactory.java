package tfm.crossover.pmxcrossover;

import org.uma.jmetal.operator.crossover.impl.PMXCrossover;
import org.uma.jmetal.util.errorchecking.JMetalException;
import tfm.crossover.CrossoverFactoryUtils;

import java.io.File;
import java.io.FileNotFoundException;

public class PMXCrossoverFactory {
    public static PMXCrossover produce(File file) throws FileNotFoundException {
        try {
            return new PMXCrossover(CrossoverFactoryUtils.getCrossoverProbability(file));
//			return new PMXAreaCoverageCrossover(CrossoverFactoryUtils.getCrossoverProbability(file));
        } catch (Exception e) {
            throw new JMetalException("PMXCrossoverFactory.produce(file): error when reading data file " + e);
        }
    }
}
