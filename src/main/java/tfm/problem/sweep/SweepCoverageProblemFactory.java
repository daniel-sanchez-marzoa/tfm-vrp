package tfm.problem.sweep;

import org.uma.jmetal.util.errorchecking.JMetalException;
import tfm.utils.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SweepCoverageProblemFactory {
    public static SweepCoverageProblem produce(File file) throws FileNotFoundException {
        try {
            String name = getName(file);
            List<Sweep> sweeps = getSweeps(file);
            Coordinate depot = getDepot(file);
            int numberOfDrones = getNumberOfVehicles(file);


            return new SweepCoverageProblem(name, sweeps, depot, numberOfDrones);
        } catch (Exception e) {
            throw new JMetalException("VRPFactory.produce(file): error when reading data file " + e);
        }
    }

    private static double[][] getDistanceMatrix(File file, int numberOfVertices, int dronRadius, int depot)
        throws IOException {
        double[] nodes = getNodes(file, numberOfVertices, dronRadius, depot);
        double[][] distanceMatrix = new double[nodes.length / 2][nodes.length / 2];

        double dist;
        for (int k = 0; k < nodes.length / 2; k++) {
            distanceMatrix[k][k] = 0;
            for (int j = k + 1; j < nodes.length / 2; j++) {
                dist = Math.sqrt(Math.pow((nodes[k * 2] - nodes[j * 2]), 2.0) +
                    Math.pow((nodes[k * 2 + 1] - nodes[j * 2 + 1]), 2));
                dist = (int) (dist + .5);
                distanceMatrix[k][j] = dist;
                distanceMatrix[j][k] = dist;
            }
        }

        return distanceMatrix;
    }

    private static double[] getNodes(File file, int matrixSize, int dronRadius, int depot)
        throws FileNotFoundException, IOException {
        double[] v = getVertices(file, matrixSize);

        double[] minAndMaxHeight = getMinAndMaxHeightForArea(v);

        double rowHeight = minAndMaxHeight[0] + dronRadius;
        List<Integer> verticesAboveRow = new ArrayList<>();
        List<Integer> verticesBelowRow = new ArrayList<>();

        for (int i = 0; i < v.length; i += 2) {
            if (i == (depot - 1) * 2)
                continue;

            if (v[i + 1] > rowHeight)
                verticesAboveRow.add(i);
            else
                verticesBelowRow.add(i);
        }

        int numberOfCoverageRows = (int) Math.floor((minAndMaxHeight[1] - minAndMaxHeight[0]) / (dronRadius * 2));
        double[] nodes = new double[4 * numberOfCoverageRows + 2];

        nodes[0] = v[depot - 1];
        nodes[1] = v[depot];

        for (int i = 2; i < 4 * numberOfCoverageRows + 2; i += 4) {
            double[] nodePair = getCoverageRowIntersectionWithArea(v, rowHeight, verticesAboveRow, verticesBelowRow);

            nodes[i] = nodePair[0];
            nodes[i + 1] = rowHeight;
            nodes[i + 2] = nodePair[1];
            nodes[i + 3] = rowHeight;
            rowHeight += dronRadius * 2;
        }

        return nodes;
    }

    private static double[] getCoverageRowIntersectionWithArea(double[] v, double rowHeight,
                                                               List<Integer> verticesAboveRow,
                                                               List<Integer> verticesBelowRow) {
        double[] nodePair = new double[]{Double.MAX_VALUE, 0};

        for (Integer vertexAbove : verticesAboveRow) {
            for (Integer vertexBelow : verticesBelowRow) {
                double x1 = v[vertexAbove];
                double y1 = v[vertexAbove + 1];
                double x2 = v[vertexBelow];
                double y2 = v[vertexBelow + 1];

                double intersectionX;

                if (x2 == x1) {
                    intersectionX = x1;
                } else {
                    double a = (y2 - y1) / (x2 - x1);
                    double b = -((y2 - y1) / (x2 - x1)) * x1 + y1;

                    if (a == 0)
                        continue;

                    intersectionX = (b - rowHeight) / (-a);
                }

                if (intersectionX < nodePair[0])
                    nodePair[0] = intersectionX;
                if (intersectionX > nodePair[1])
                    nodePair[1] = intersectionX;
            }
        }
        return nodePair;
    }

    private static double[] getMinAndMaxHeightForArea(double[] v) {
        double[] minAndMaxHeight = new double[]{Double.MAX_VALUE, 0};

        for (int i = 1; i < v.length; i += 2) {
            if (v[i] < minAndMaxHeight[0])
                minAndMaxHeight[0] = v[i];

            if (v[i] > minAndMaxHeight[1])
                minAndMaxHeight[1] = v[i];
        }
        return minAndMaxHeight;
    }

    private static double[] getVertices(File file, int matrixSize) throws FileNotFoundException, IOException {
        StreamTokenizer token = FileUtils.getTokens(file);

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
        }
        return c;
    }

    private static List<List<Integer>> getMandatoryPaths(int numberOfNodes) throws IOException {
        List<List<Integer>> mandatoryPaths = new ArrayList<>();

        for (int i = 2; i <= numberOfNodes; i += 2) {
            mandatoryPaths.add(Arrays.asList(i, i + 1));
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

    private static Coordinate getDepot(File file) throws IOException {
        StreamTokenizer token = FileUtils.getTokens(file);
        boolean found = false;
        token.nextToken();

        while (!found) {
            if ((token.sval != null) && ((token.sval.compareTo("DEPOT_FILE") == 0)))
                found = true;
            else
                token.nextToken();
        }

        token.nextToken();
        token.nextToken();

        File depotFile = new File(token.sval);

        BufferedReader reader = new BufferedReader(new FileReader(depotFile));
        String line;
        Coordinate depot;

        reader.readLine();
        line = reader.readLine();

        String[] coordinates = line.split("\t");

        depot = Coordinate.builder()
            .x(Float.parseFloat(coordinates[0]))
            .y(Float.parseFloat(coordinates[1]))
            .build();

        reader.close();

        return depot;
    }

    private static List<Sweep> getSweeps(File file) throws IOException {
        StreamTokenizer token = FileUtils.getTokens(file);
        boolean found = false;
        token.nextToken();

        while (!found) {
            if ((token.sval != null) && ((token.sval.compareTo("SWEEPS_FILE") == 0)))
                found = true;
            else
                token.nextToken();
        }

        token.nextToken();
        token.nextToken();

        File depotFile = new File(token.sval);

        BufferedReader reader = new BufferedReader(new FileReader(depotFile));
        String line;
        List<Sweep> sweeps = new ArrayList<>();

        reader.readLine();
        line = reader.readLine();

        while (line != null) {
            String[] coordinates = line.split("\t");

            Coordinate a = Coordinate.builder()
                .x(Float.parseFloat(coordinates[0]))
                .y(Float.parseFloat(coordinates[1]))
                .build();

            Coordinate b = Coordinate.builder()
                .x(Float.parseFloat(coordinates[2]))
                .y(Float.parseFloat(coordinates[3]))
                .build();

            sweeps.add(Sweep.builder()
                .a(a)
                .b(b)
                .build());

            line = reader.readLine();
        }

        reader.close();

        return sweeps;
    }

    public static int getNumberOfOperators(File file) throws IOException {
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

    private static int getDroneRadius(File file) throws IOException {
        StreamTokenizer token = FileUtils.getTokens(file);
        boolean found = false;
        token.nextToken();

        while (!found) {
            if ((token.sval != null) && ((token.sval.compareTo("DRONE_CAMERA_RADIUS") == 0)))
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
