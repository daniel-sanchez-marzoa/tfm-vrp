package tfm.vrp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamTokenizer;

import org.uma.jmetal.util.errorchecking.JMetalException;

import tfm.utils.FileUtils;

public class VRPFactory {
	public static VRP produce(File file) throws FileNotFoundException {
		try {
			int numberOfCities = getNumberOfCities(file);
			int depot = getDepot(file);
			int numberOfVehicles = getNumberOfVehicles(file);
			double[][] distanceMatrix = getDistanceMatrix(file, numberOfCities);

			return new VRP(numberOfCities, distanceMatrix, depot, numberOfVehicles);
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
