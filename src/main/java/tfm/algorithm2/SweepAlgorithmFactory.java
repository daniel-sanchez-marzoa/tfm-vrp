package tfm.algorithm2;

import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import tfm.algorithm.AlgorithmType;
import tfm.algorithm2.ngsaii.NGSAIIFactory;
import tfm.algorithm2.nsgaiii.NGSAIIIFactory;
import tfm.problem.sweep.SweepCoverageProblem;
import tfm.problem.sweep.SweepCoverageSolution;
import tfm.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.Arrays;
import java.util.List;

public class SweepAlgorithmFactory {
    public static ExperimentAlgorithm<SweepCoverageSolution, List<SweepCoverageSolution>> produce(
        File file, ExperimentProblem<SweepCoverageSolution> experimentProblem, int run)
        throws FileNotFoundException {
        try {
            switch (getAlgorithmType(file)) {
                case NGSAII:
                    return new ExperimentAlgorithm<>(NGSAIIFactory.produce(
                        file,
                        (SweepCoverageProblem) experimentProblem.getProblem()),
                        getAlgorithmName(file),
                        experimentProblem,
                        run);
                case NGSAIII:
                    return new ExperimentAlgorithm<>(NGSAIIIFactory.produce(
                        file,
                        (SweepCoverageProblem) experimentProblem.getProblem()),
                        getAlgorithmName(file),
                        experimentProblem,
                        run);
//				case ESPEA:
//					return new ExperimentAlgorithm<>(ESPEAFactory.produce(
//							file,
//							experimentProblem.getProblem()),
//							getAlgorithmName(file),
//							experimentProblem,
//							run);
//				case WASFGA:
//					// TODO check parameters of WASFGA
//					return new ExperimentAlgorithm<>(WASFGAFactory.produce(
//							file,
//							experimentProblem.getProblem()),
//							getAlgorithmName(file),
//							experimentProblem,
//							run);
//				case MOCell:
//					return new ExperimentAlgorithm<>(MOCellFactory.produce(
//							file,
//							experimentProblem.getProblem()),
//							getAlgorithmName(file),
//							experimentProblem,
//							run);
//				case MOMBI:
//					// TODO check parameters of MOMBI
//					return new ExperimentAlgorithm<>(MOMBIFactory.produce(
//							file,
//							experimentProblem.getProblem()),
//							getAlgorithmName(file),
//							experimentProblem,
//							run);
//				case PESA2:
//					// TODO check parameters of PESA2
//					return new ExperimentAlgorithm<>(PESA2Factory.produce(
//							file,
//							experimentProblem.getProblem()),
//							getAlgorithmName(file),
//							experimentProblem,
//							run);
//				case SMSEMOA:
//					return new ExperimentAlgorithm<>(SMSEMOAFactory.produce(
//							file,
//							experimentProblem.getProblem()),
//							getAlgorithmName(file),
//							experimentProblem,
//							run);
//				case SPEA2:
//					return new ExperimentAlgorithm<>(SPEA2Factory.produce(
//							file,
//							experimentProblem.getProblem()),
//							getAlgorithmName(file),
//							experimentProblem,
//							run);
//				case GWASFGA:
//					return new ExperimentAlgorithm<>(GWASFGAFactory.produce(
//							file,
//							experimentProblem.getProblem()),
//							getAlgorithmName(file),
//							experimentProblem,
//							run);
                default:
                    throw new JMetalException(
                        "AlgorithmFactory.produce(file): unrecognized algorithm type. Check parameter ALGORITHM_TYPE to ensure it has one of the following values: "
                            + Arrays.toString(AlgorithmType.values()));
            }
        } catch (Exception e) {
            throw new JMetalException("AlgorithmFactory.produce(file): error when reading data file " + e);
        }
    }

    public static AlgorithmType getAlgorithmType(File file) throws IOException {
        StreamTokenizer token = FileUtils.getTokens(file);
        boolean found = false;
        token.nextToken();

        while (!found) {
            if ((token.sval != null) && ((token.sval.compareTo("ALGORITHM_TYPE") == 0)))
                found = true;
            else
                token.nextToken();
        }

        token.nextToken();
        token.nextToken();

        return AlgorithmType.valueOf(token.sval);
    }

    public static String getAlgorithmName(File file) throws IOException {
        StreamTokenizer token = FileUtils.getTokens(file);
        boolean found = false;
        token.nextToken();

        while (!found) {
            if ((token.sval != null) && ((token.sval.compareTo("ALGORITHM_NAME") == 0)))
                found = true;
            else
                token.nextToken();
        }

        token.nextToken();
        token.nextToken();

        return token.sval;
    }
}
