package tfm.vrp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;

import org.uma.jmetal.util.errorchecking.JMetalException;

public class VRPFactory {
	public VRP produce(String file) throws FileNotFoundException {
		// TODO check if the order of the methods affects the reading because of
		// token.nextToken
		StreamTokenizer token = getTokens(file);

		try {
			int numberOfCities = getNumberOfCities(token);
			int depot = getDepot(token);
			int numberOfVehicles = getNumberOfVehicles(token);
			double[][] distanceMatrix = getDistanceMatrix(token, numberOfCities);

			return new VRP(numberOfCities, distanceMatrix, depot, numberOfVehicles);
		} catch (Exception e) {
			new JMetalException("VRPFactory.produce(file): error when reading data file " + e);

			return null;
		}
	}

	private double[][] getDistanceMatrix(StreamTokenizer token, int matrixSize) throws IOException {
		double[][] distanceMatrix = new double[matrixSize][matrixSize];

		// Find the string SECTION
		boolean found = false;
		token.nextToken();
		while (!found) {
			if ((token.sval != null) &&
					((token.sval.compareTo("SECTION") == 0)))
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

	private int getNumberOfCities(StreamTokenizer token) throws IOException {
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

	private int getDepot(StreamTokenizer token) throws IOException {
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

	private int getNumberOfVehicles(StreamTokenizer token) throws IOException {
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

	private StreamTokenizer getTokens(String file) throws FileNotFoundException {
		InputStream in = getClass().getResourceAsStream(file);

		if (in == null)
			in = new FileInputStream(file);

		InputStreamReader isr = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(isr);
		StreamTokenizer token = new StreamTokenizer(br);
		return token;
	}
}
