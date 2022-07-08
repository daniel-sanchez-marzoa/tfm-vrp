package tfm.problem.sweep;

import lombok.*;
import org.uma.jmetal.problem.AbstractGenericProblem;
import org.uma.jmetal.problem.permutationproblem.PermutationProblem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class SweepCoverageProblem extends AbstractGenericProblem<SweepCoverageSolution>
    implements PermutationProblem<SweepCoverageSolution> {

    /**
     * Given in meters
     */
    private List<Sweep> sweeps;
    private Coordinate depot;
    private int numberOfDrones;
//    private int numberOfOperators;
//    private int setupTime;

    /**
     * Given in meters / second
     */
//    private float droneSpeed;
    public SweepCoverageProblem(String name, List<Sweep> sweeps, Coordinate depot, int numberOfDrones/*,
                                int numberOfOperators, int setupTime, float droneSpeed,*/)
        throws IOException {
        this.sweeps = sweeps;
        this.depot = depot;
        this.numberOfDrones = numberOfDrones;
//        this.numberOfOperators = numberOfOperators;
//        this.setupTime = setupTime;
//        this.droneSpeed = droneSpeed;

        setNumberOfVariables(getLength());
        setNumberOfObjectives(2);
        setName(name);
    }

    @Override
    public SweepCoverageSolution createSolution() {
        return new SweepCoverageSolution(sweeps.size(), numberOfDrones, getNumberOfObjectives(), false, 0);
    }

    public SweepCoverageSolution createSolution(boolean spreadSweepsAmongDrones, int numberOfDronesToSpread) {
        return new SweepCoverageSolution(sweeps.size(), numberOfDrones, getNumberOfObjectives(), spreadSweepsAmongDrones, numberOfDronesToSpread);
    }

    @Override
    public int getLength() {
        return sweeps.size() + numberOfDrones - 1;
    }

    @Override
    public SweepCoverageSolution evaluate(SweepCoverageSolution solution) {
        List<List<Integer>> routes = solution.separateSolutionIntoRoutes();
        List<Double> traveledDistances = new ArrayList<>();

        // System.out.println("----" + solution.variables());
        for (List<Integer> route : routes)
            traveledDistances.add(calculateRouteDistance(route));

//        List<Double> travelTimes = new ArrayList<>();

//        for (Double travelDistance : traveledDistances) {
//            travelTimes.add(travelDistance / droneSpeed + getSetupTime(solution, traveledDistances, travelDistance));
//        }

//        solution.objectives()[0] = travelTimes.stream().max((d1, d2) -> Double.compare(d1, d2)).get();
        solution.objectives()[0] = traveledDistances.stream().max(Double::compare).get();
        solution.objectives()[1] = routes.size();
//        solution.objectives()[2] = getNumberOfOperators(solution);

//         System.out.println("Objective 1: " + solution.objectives()[0]);
//         System.out.println("Objective 2: " + solution.objectives()[1]);
        // System.out.println("Objective 3: " + solution.objectives()[2]);

        return solution;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    static class DistanceDTO {
        private boolean lastSweepUsedPointWasA;
        private double distance;
    }

    private double calculateRouteDistance(List<Integer> route) {
        DistanceDTO distanceDTO = DistanceDTO.builder()
            .distance(0)
            .lastSweepUsedPointWasA(true)
            .build();

        if (route.size() == 1) {
            Sweep sweep1 = sweeps.get(route.get(0));

            addStartDistance(distanceDTO, sweep1);

            if (distanceDTO.isLastSweepUsedPointWasA())
                distanceDTO.setDistance(distanceDTO.getDistance() + sweep1.getB().euclideanDistanceTo(depot));
            else
                distanceDTO.setDistance(distanceDTO.getDistance() + sweep1.getA().euclideanDistanceTo(depot));

            return distanceDTO.getDistance();
        }

        for (int i = 0; i < route.size() - 1; i++) {
            Sweep sweep1 = sweeps.get(route.get(i));
            Sweep sweep2 = sweeps.get(route.get(i + 1));

            if (i == 0)
                addStartDistance(distanceDTO, sweep1);

            if (distanceDTO.isLastSweepUsedPointWasA())
                addSweepDistance(sweep1.getB(), sweep2, distanceDTO);
            else
                addSweepDistance(sweep1.getA(), sweep2, distanceDTO);

            if (i == route.size() - 2) {
                if (distanceDTO.isLastSweepUsedPointWasA())
                    distanceDTO.setDistance(distanceDTO.getDistance() + sweep2.getB().euclideanDistanceTo(depot));
                else
                    distanceDTO.setDistance(distanceDTO.getDistance() + sweep2.getA().euclideanDistanceTo(depot));
            }
        }

        return distanceDTO.getDistance();
    }

    private void addSweepDistance(Coordinate sweep1, Sweep sweep2, DistanceDTO distance) {
        float distanceToA = sweep1.euclideanDistanceTo(sweep2.getA());
        float distanceToB = sweep1.euclideanDistanceTo(sweep2.getB());

        if (distanceToA < distanceToB) {
            distance.setLastSweepUsedPointWasA(true);
            distance.setDistance(distance.getDistance() + distanceToA + sweep2.getA().euclideanDistanceTo(sweep2.getB()));
        } else {
            distance.setLastSweepUsedPointWasA(false);
            distance.setDistance(distance.getDistance() + distanceToB + sweep2.getA().euclideanDistanceTo(sweep2.getB()));
        }

    }

    private void addStartDistance(DistanceDTO distance, Sweep sweep1) {
        float distanceToA = depot.euclideanDistanceTo(sweep1.getA());
        float distanceToB = depot.euclideanDistanceTo(sweep1.getB());

        if (distanceToA < distanceToB) {
            distance.setLastSweepUsedPointWasA(true);
            distance.setDistance(distance.getDistance() + distanceToA + sweep1.getA().euclideanDistanceTo(sweep1.getB()));
        } else {
            distance.setLastSweepUsedPointWasA(false);
            distance.setDistance(distance.getDistance() + distanceToB + sweep1.getA().euclideanDistanceTo(sweep1.getB()));
        }

    }

//    private Integer getNumberOfOperators(SweepCoverageSolution solution) {
//        return Math.abs(solution.variables().get(0));
//    }

//    private double getSetupTime(SweepCoverageSolution solution, List<Double> traveledDistances,
//                                double maxDistance) {
//        int operators = getNumberOfOperators(solution);
//
//        // if (operators != 1)
//        // System.out.println("Operators " + operators + " | Index " +
//        // traveledDistances.lastIndexOf(maxDistance)
//        // + " | Div " + Math.floor((traveledDistances.lastIndexOf(maxDistance)) /
//        // operators));
//
//        return setupTime + setupTime
//            * Math.floor((traveledDistances.lastIndexOf(maxDistance)) / operators);
//    }
}
