package tfm.selection.binarytournament;

import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import tfm.problem.sweep.SweepCoverageSolution;

import java.io.File;
import java.io.FileNotFoundException;

public class BinaryTournamentSelectionFactory {
    public static BinaryTournamentSelection<SweepCoverageSolution> produce(File file)
        throws FileNotFoundException {
        try {
            return new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>());
        } catch (Exception e) {
            throw new JMetalException("BinaryTournamentSelectionFactory.produce(file): error when reading data file " + e);
        }
    }
}
