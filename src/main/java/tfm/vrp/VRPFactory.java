package tfm.vrp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.util.errorchecking.JMetalException;

import tfm.utils.FileUtils;

public class VRPFactory {
	public static VRP produce(File file) throws FileNotFoundException {
		try {
			String name = getName(file);
			int numberOfCities = getNumberOfCities(file);
			int depot = getDepot(file);
			int numberOfVehicles = getNumberOfVehicles(file);
			int numberOfOperators = getNumberOfOperators(file);
			int setupTime = getSetupTime(file);
			float droneSpeed = getDroneSpped(file);
			double[][] distanceMatrix = getDistanceMatrix(file, numberOfCities);

			List<List<Integer>> mandatoryPaths = getMandatoryPaths(file);

			return new VRP(name, numberOfCities, distanceMatrix, depot, numberOfVehicles, numberOfOperators, setupTime,
					droneSpeed, mandatoryPaths);
		} catch (Exception e) {
			new JMetalException("VRPFactory.produce(file): error when reading data file " + e);

			return null;
		}
	}

	private static double[][] getDistanceMatrix(File file, int matrixSize) throws IOException {
		StreamTokenizer token = FileUtils.getTokens(file);
		double[][] distanceMatrix = new double[matrixSize][matrixSize];

		// Find the string NODE_COORD_SECTION
		boolean found = false;
		token.nextToken();
		while (!found) {
			if ((token.sval != null) &&
					((token.sval.compareTo("NODE_COORD_SECTION") == 0)))
				found = true;
			else
				token.nextToken();
		}

		double[] c = new double[2 * matrixSize];

		for (int i = 0; i < matrixSize; i++) {
			token.nextToken();
			int j = (int) token.nval;

			token.nextToken();
			c[2 * (j - 1)] = token.nval;
			token.nextToken();
			c[2 * (j - 1) + 1] = token.nval;
		} // for

		double dist;
		for (int k = 0; k < matrixSize; k++) {
			distanceMatrix[k][k] = 0;
			for (int j = k + 1; j < matrixSize; j++) {
				dist = Math.sqrt(Math.pow((c[k * 2] - c[j * 2]), 2.0) +
						Math.pow((c[k * 2 + 1] - c[j * 2 + 1]), 2));
				dist = (int) (dist + .5);
				distanceMatrix[k][j] = dist;
				distanceMatrix[j][k] = dist;
			}
		}

		return distanceMatrix;
	}

	private static List<List<Integer>> getMandatoryPaths(File file) throws IOException {
		StreamTokenizer token = FileUtils.getTokens(file);
		List<List<Integer>> mandatoryPaths = new ArrayList<>();

		// Find the string MANDATORY_PATHS
		boolean found = false;
		token.nextToken();
		while (!found) {
			if ((token.sval != null) &&
					((token.sval.compareTo("MANDATORY_PATHS") == 0)))
				found = true;
			else
				token.nextToken();
		}

		token.nextToken();
		int i = 0;

		while (token.sval == null) {
			if (i++ % 2 == 0)
				mandatoryPaths.add(new ArrayList<>());

			mandatoryPaths.get(mandatoryPaths.size() - 1).add((int) token.nval);

			token.nextToken();
		}

		return mandatoryPaths;
	}

	private static int getNumberOfCities(File file) throws IOException {
		StreamTokenizer token = FileUtils.getTokens(file);
		boolean found = false;
		token.nextToken();

		while (!found) {
			if ((token.sval != null) && ((token.sval.compareTo("DIMENSION") == 0)))
				found = true;
			else
				token.nextToken();
		}

		token.nextToken();
		token.nextToken();

		return (int) token.nval;
	}

	private static String getName(File file) throws IOException {
		StreamTokenizer token = FileUtils.getTokens(file);
		boolean found = false;
		token.nextToken();

		while (!found) {
			if ((token.sval != null) && ((token.sval.compareTo("NAME") == 0)))
				found = true;
			else
				token.nextToken();
		}

		token.nextToken();
		token.nextToken();

		return token.sval;
	}

	private static int getDepot(File file) throws IOException {
		StreamTokenizer token = FileUtils.getTokens(file);
		boolean found = false;
		token.nextToken();

		while (!found) {
			if ((token.sval != null) && ((token.sval.compareTo("DEPOT") == 0)))
				found = true;
			else
				token.nextToken();
		}

		token.nextToken();
		token.nextToken();

		return (int) token.nval;
	}

	private static int getNumberOfOperators(File file) throws IOException {
		StreamTokenizer token = FileUtils.getTokens(file);
		boolean found = false;
		token.nextToken();

		while (!found) {
			if ((token.sval != null) && ((token.sval.compareTo("NUMBER_OF_OPERATORS") == 0)))
				found = true;
			else
				token.nextToken();
		}

		token.nextToken();
		token.nextToken();

		return (int) token.nval;
	}

	private static int getSetupTime(File file) throws IOException {
		StreamTokenizer token = FileUtils.getTokens(file);
		boolean found = false;
		token.nextToken();

		while (!found) {
			if ((token.sval != null) && ((token.sval.compareTo("SETUP_TIME") == 0)))
				found = true;
			else
				token.nextToken();
		}

		token.nextToken();
		token.nextToken();

		return (int) token.nval;
	}

	private static float getDroneSpped(File file) throws IOException {
		StreamTokenizer token = FileUtils.getTokens(file);
		boolean found = false;
		token.nextToken();

		while (!found) {
			if ((token.sval != null) && ((token.sval.compareTo("DRONE_SPEED") == 0)))
				found = true;
			else
				token.nextToken();
		}

		token.nextToken();
		token.nextToken();

		return (int) token.nval;
	}

	private static int getNumberOfVehicles(File file) throws IOException {
		StreamTokenizer token = FileUtils.getTokens(file);
		boolean found = false;
		token.nextToken();

		while (!found) {
			if ((token.sval != null) && ((token.sval.compareTo("NUMBER_OF_VEHICLES") == 0)))
				found = true;
			else
				token.nextToken();
		}

		token.nextToken();
		token.nextToken();

		return (int) token.nval;
	}
}
