package tfm.problem.sweep;

import org.uma.jmetal.util.errorchecking.JMetalException;
import tfm.utils.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SweepCoverageProblemFactory {
    public static SweepCoverageProblem produce(File file, boolean shuffleSweeps, Integer lengthModificationPercentage) throws FileNotFoundException {
        try {
            String name = getName(file);
            List<Sweep> sweeps = getSweeps(file);
            Coordinate depot = getDepot(file);
            int numberOfDrones = getNumberOfVehicles(file);

            if (shuffleSweeps) {
                java.util.Collections.shuffle(sweeps);

                saveSweeps("shuffledSweeps.csv", sweeps);
            }

            if (lengthModificationPercentage != null) {
                int percentageModifier = ThreadLocalRandom.current().nextInt(
                    lengthModificationPercentage > 99 ? -99 : -lengthModificationPercentage,
                    lengthModificationPercentage + 1);

                for (Sweep sweep : sweeps) {
                    sweep.getB().setX(sweep.getB().getX() + (sweep.getB().getX() * percentageModifier / 100));
                    sweep.getB().setY(sweep.getB().getY() + (sweep.getB().getY() * percentageModifier / 100));
                }

                saveSweeps("modifiedLengthSweeps.csv", sweeps);
            }

            return new SweepCoverageProblem(name, sweeps, depot, numberOfDrones);
        } catch (Exception e) {
            throw new JMetalException("VRPFactory.produce(file): error when reading data file " + e);
        }
    }

    private static void saveSweeps(String fileName, List<Sweep> sweeps) {
        try {
            File sweepsFile = new File(fileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(sweepsFile));

            writer.write("X1\tY1\tX2\tY2\n");

            for (Sweep sweep : sweeps) {
                writer.write(sweep.getA().getX() + "\t" + sweep.getA().getY() + "\t" + sweep.getB().getX() + "\t" + sweep.getB().getY() + "\n");
            }

            writer.close();

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
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

        File sweepsFile = new File(token.sval);

        BufferedReader reader = new BufferedReader(new FileReader(sweepsFile));
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
