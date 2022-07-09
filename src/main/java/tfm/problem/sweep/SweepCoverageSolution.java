package tfm.problem.sweep;

import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Defines an implementation of solution composed of a permutation of integers.
 */

public class SweepCoverageSolution extends AbstractSolution<Integer>
    implements PermutationSolution<Integer> {
    private int numberOfObjectives;
    private int numberOfSweeps;
    private int maxNumberOfDrones;
    //	private int numberOfOperators;

    /**
     * Constructor
     */
    public SweepCoverageSolution(int numberOfSweeps,
                                 int maxNumberOfDrones,
                                 int numberOfObjectives,
                                 boolean spreadSweepsAmongDrones,
                                 int numberOfDronesToSpread
        /*int numberOfOperators,*/) {
        super(numberOfSweeps + maxNumberOfDrones - 1, numberOfObjectives);
        int permutationLength = numberOfSweeps + maxNumberOfDrones - 1;

        this.numberOfSweeps = numberOfSweeps;
        this.maxNumberOfDrones = maxNumberOfDrones;
        this.numberOfObjectives = numberOfObjectives;
//		this.numberOfOperators = numberOfOperators;

        List<Integer> randomSequence = initializePermutation(numberOfSweeps,
            spreadSweepsAmongDrones,
            numberOfDronesToSpread,
            permutationLength);

        for (int i = 0; i < permutationLength; i++)
            variables().set(i, randomSequence.get(i));

        // System.out.println("Solution: " + variables());
    }

    private List<Integer> initializePermutation(int numberOfSweeps, boolean spreadSweepsAmongDrones, int numberOfDronesToSpread, int permutationLength) {
        List<Integer> randomSequence = new ArrayList<>(permutationLength);

        for (int i = 0; i < permutationLength; i++)
            randomSequence.add(i);

        if (spreadSweepsAmongDrones) {
            int numberOfSweepsAux = numberOfSweeps;
            int positionToPlaceDrone = 0;
            int routeSize = (int) Math.ceil((float) numberOfSweepsAux / numberOfDronesToSpread);

            for (int i = numberOfSweeps; i < numberOfSweeps + numberOfDronesToSpread - 1; i++) {
                positionToPlaceDrone += routeSize;

                randomSequence.remove(i);
                randomSequence.add(positionToPlaceDrone, i);

                positionToPlaceDrone++;

                routeSize = (int) Math.ceil((float) --numberOfSweepsAux / numberOfDronesToSpread);
            }
        } else
            java.util.Collections.shuffle(randomSequence);

        return randomSequence;
    }

    /**
     * Copy Constructor
     */
    public SweepCoverageSolution(SweepCoverageSolution solution) {
        super(solution.getLength(), solution.objectives().length);

        this.numberOfObjectives = solution.numberOfObjectives;
        this.numberOfSweeps = solution.numberOfSweeps;
        this.maxNumberOfDrones = solution.maxNumberOfDrones;
//		this.numberOfOperators =solution. numberOfOperators;

        for (int i = 0; i < objectives().length; i++)
            objectives()[i] = solution.objectives()[i];

        for (int i = 0; i < variables().size(); i++)
            variables().set(i, solution.variables().get(i));

        for (int i = 0; i < constraints().length; i++)
            constraints()[i] = solution.constraints()[i];

        attributes = new HashMap<>(solution.attributes);
    }

    @Override
    public SweepCoverageSolution copy() {
        return new SweepCoverageSolution(this);
    }

    @Override
    public int getLength() {
        return variables().size();
    }

    public List<List<Integer>> separateSolutionIntoRoutes() {
        List<List<Integer>> routes = new ArrayList<>();
        routes.add(new ArrayList<>());

        for (Integer solutionVariable : this.variables()) {
            if (isDelimeter(solutionVariable))
                routes.add(new ArrayList<>());
            else
                routes.get(routes.size() - 1).add(solutionVariable);
        }

        routes.removeIf(List::isEmpty);

        return routes;
    }

    private boolean isDelimeter(Integer i) {
        return i >= numberOfSweeps;
    }
}
