package tfm.vrp.runner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamTokenizer;

import org.uma.jmetal.util.errorchecking.JMetalException;

import tfm.utils.FileUtils;

public class VRPExperimentRunnerFactory {
	public static VRPExperimentRunner produce(File file) throws FileNotFoundException {
		try {
			VRPExperimentRunner runner = new VRPExperimentRunner(getNumerOfIndependentRuns(file), getStudyName(file),
					getExperimentBaseDirectory(file), getProblemsDirectory(file), getAlgorithmsDirectory(file));

			runner.setCores(getCores(file));

			System.out.println("VRPExperimentRunner - produce" + runner);

			return runner;
		} catch (Exception e) {
			System.out.println("VRPExperimentRunner - produce - exception" + e.getMessage());
			e.printStackTrace();
			new JMetalException("AlgorithmFactory.produce(file): error when reading data file " + e);

			return null;
		}
	}

	private static String getStudyName(File file) throws IOException {
		StreamTokenizer token = FileUtils.getTokens(file);
		boolean found = false;
		token.nextToken();

		while (!found && !token.sval.equals("EOF")) {
			if ((token.sval != null) && ((token.sval.compareTo("STUDY_NAME") == 0)))
				found = true;
			else
				token.nextToken();
		}

		token.nextToken();
		token.nextToken();

		return token.sval;
	}

	private static File getExperimentBaseDirectory(File file) throws IOException {
		StreamTokenizer token = FileUtils.getTokens(file);
		boolean found = false;
		token.nextToken();

		while (!found) {
			if ((token.sval != null) && ((token.sval.compareTo("EXPERIMENT_BASE_DIRECTORY") == 0)))
				found = true;
			else
				token.nextToken();
		}

		token.nextToken();
		token.nextToken();

		return new File(token.sval);
	}

	private static File getProblemsDirectory(File file) throws IOException {
		StreamTokenizer token = FileUtils.getTokens(file);
		boolean found = false;
		token.nextToken();

		while (!found) {
			if ((token.sval != null) && ((token.sval.compareTo("PROBLEMS_DIRECTORY") == 0)))
				found = true;
			else
				token.nextToken();
		}

		token.nextToken();
		token.nextToken();

		return new File(token.sval);
	}

	private static File getAlgorithmsDirectory(File file) throws IOException {
		StreamTokenizer token = FileUtils.getTokens(file);
		boolean found = false;
		token.nextToken();

		while (!found) {
			if ((token.sval != null) && ((token.sval.compareTo("ALGORITHMS_DIRECTORY") == 0)))
				found = true;
			else
				token.nextToken();
		}

		token.nextToken();
		token.nextToken();

		return new File(token.sval);
	}

	private static int getNumerOfIndependentRuns(File file) throws IOException {
		StreamTokenizer token = FileUtils.getTokens(file);
		boolean found = false;
		token.nextToken();

		while (!found) {
			if ((token.sval != null) && ((token.sval.compareTo("NUMBER_OF_INDEPENDENT_RUNS") == 0)))
				found = true;
			else
				token.nextToken();
		}

		token.nextToken();
		token.nextToken();

		return (int) token.nval;
	}

	private static int getCores(File file) throws IOException {
		StreamTokenizer token = FileUtils.getTokens(file);
		boolean found = false;
		token.nextToken();

		while (!found) {
			if ((token.sval != null) && ((token.sval.compareTo("CORES") == 0)))
				found = true;
			else
				token.nextToken();
		}

		token.nextToken();
		token.nextToken();

		return (int) token.nval;
	}
}
