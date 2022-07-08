package tfm.problem.sweep.runner;

import org.uma.jmetal.util.measure.MeasureListener;
import tfm.problem.sweep.SweepCoverageSolution;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolutionListener implements MeasureListener<List<SweepCoverageSolution>> {

    private int iteration = 0;
    private int numberOfDrones;
    private List<SweepCoverageSolution> previousNonDominatedFrontObjectives = new ArrayList<>();

    public SolutionListener(int numberOfDrones) {
        this.numberOfDrones = numberOfDrones;
    }

    @Override
    synchronized public void measureGenerated(List<SweepCoverageSolution> solutions) {
//        List<double[]> nonDominatedFrontObjectives = solutions.stream().map(Solution::objectives).collect(Collectors.toList());

        Map<Double, Double> frontValues = new HashMap<>();
        iteration++;
        if (iteration % 100 == 0 || iteration == 9999)
            System.out.println("Iteración " + iteration);

        for (SweepCoverageSolution solution : solutions) {
//            System.out.println("Distancia: " + objectives[0]);
//            System.out.println("Num. drones: " + objectives[1]);

            frontValues.put(solution.objectives()[1], solution.objectives()[0]);

//            for (Solution previousSolution : previousNonDominatedFrontObjectives) {
//                if (previousSolution.objectives()[0] == solution.objectives()[0]
//                    && previousSolution.objectives()[1] != solution.objectives()[1]) {
//                    System.out.println("Previous solution: [");
//
//                    for (Object i : previousSolution.variables()) {
//                        System.out.print(i + ",");
//                    }
//                    System.out.println("\n]\nNew solution: [");
//
//                    for (Object i : solution.variables()) {
//                        System.out.print(i + ",");
//                    }
//
//                    System.out.println("\n]");
//
//                    System.out.println("Error generating csv");
//                    System.out.println("Previous objective: [");
//                    for (Solution o : previousNonDominatedFrontObjectives) {
//                        System.out.print("[" + o.objectives()[1] + " " + o.objectives()[0] + "]");
//                    }
//                    System.out.println("\n]\nNew objective: [");
//                    for (double[] o : nonDominatedFrontObjectives) {
//                        System.out.print("[" + o[1] + " " + o[0] + "]");
//                    }
//                    break;
//                }
//            }
        }

        boolean written = false;

        while (!written) {
            try {
                File csv = new File("convergencia.csv");
                BufferedWriter writer = new BufferedWriter(new FileWriter(csv, true));
                writer.append("\n").append(String.valueOf(iteration)).append(",");

                for (double i = 1; i < numberOfDrones + 1; i++) {
                    if (frontValues.containsKey(i))
                        writer.append(String.valueOf(frontValues.get(i)));

                    if (i != numberOfDrones)
                        writer.append(",");
                }

                writer.close();
                written = true;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        previousNonDominatedFrontObjectives = solutions;
    }
}
