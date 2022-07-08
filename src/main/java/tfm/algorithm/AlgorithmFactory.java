package tfm.algorithm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.List;

import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.util.errorchecking.JMetalException;

import tfm.algorithm.espea.ESPEAFactory;
import tfm.algorithm.gwasfga.GWASFGAFactory;
import tfm.algorithm.mocell.MOCellFactory;
import tfm.algorithm.mombi.MOMBIFactory;
import tfm.algorithm.ngsaii.NGSAIIFactory;
import tfm.algorithm.pesa2.PESA2Factory;
import tfm.algorithm.smsemoa.SMSEMOAFactory;
import tfm.algorithm.spea2.SPEA2Factory;
import tfm.algorithm.wasfga.WASFGAFactory;
import tfm.utils.FileUtils;
import tfm.problem.vrp.AreaCoverageSolution;

public class AlgorithmFactory {
	public static ExperimentAlgorithm<AreaCoverageSolution, List<AreaCoverageSolution>> produce(
			File file, ExperimentProblem<AreaCoverageSolution> experimentProblem, int run)
			throws FileNotFoundException {
		try {
			switch (getAlgorithmType(file)) {
				case NGSAII:
					return new ExperimentAlgorithm<>(NGSAIIFactory.produce(
							file,
							experimentProblem.getProblem()),
							getAlgorithmName(file),
							experimentProblem,
							run);
				case ESPEA:
					return new ExperimentAlgorithm<>(ESPEAFactory.produce(
							file,
							experimentProblem.getProblem()),
							getAlgorithmName(file),
							experimentProblem,
							run);
				case WASFGA:
					// TODO check parameters of WASFGA
					return new ExperimentAlgorithm<>(WASFGAFactory.produce(
							file,
							experimentProblem.getProblem()),
							getAlgorithmName(file),
							experimentProblem,
							run);
				case MOCell:
					return new ExperimentAlgorithm<>(MOCellFactory.produce(
							file,
							experimentProblem.getProblem()),
							getAlgorithmName(file),
							experimentProblem,
							run);
				case MOMBI:
					// TODO check parameters of MOMBI
					return new ExperimentAlgorithm<>(MOMBIFactory.produce(
							file,
							experimentProblem.getProblem()),
							getAlgorithmName(file),
							experimentProblem,
							run);
				case PESA2:
					// TODO check parameters of PESA2
					return new ExperimentAlgorithm<>(PESA2Factory.produce(
							file,
							experimentProblem.getProblem()),
							getAlgorithmName(file),
							experimentProblem,
							run);
				case SMSEMOA:
					return new ExperimentAlgorithm<>(SMSEMOAFactory.produce(
							file,
							experimentProblem.getProblem()),
							getAlgorithmName(file),
							experimentProblem,
							run);
				case SPEA2:
					return new ExperimentAlgorithm<>(SPEA2Factory.produce(
							file,
							experimentProblem.getProblem()),
							getAlgorithmName(file),
							experimentProblem,
							run);
				case GWASFGA:
					return new ExperimentAlgorithm<>(GWASFGAFactory.produce(
							file,
							experimentProblem.getProblem()),
							getAlgorithmName(file),
							experimentProblem,
							run);
				default:
					new JMetalException(
							"AlgorithmFactory.produce(file): unrecognized algorithm type. Check parameter ALGORITHM_TYPE to ensure it has one of the following values: "
									+ AlgorithmType.values());

					return null;
			}
		} catch (Exception e) {
			new JMetalException("AlgorithmFactory.produce(file): error when reading data file " + e);

			return null;
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
