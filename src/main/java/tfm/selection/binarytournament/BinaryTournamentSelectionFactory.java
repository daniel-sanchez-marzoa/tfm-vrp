package tfm.selection.binarytournament;

import java.io.File;
import java.io.FileNotFoundException;

import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class BinaryTournamentSelectionFactory {
	public static BinaryTournamentSelection<PermutationSolution<Integer>> produce(File file)
			throws FileNotFoundException {
		try {
			return new BinaryTournamentSelection<PermutationSolution<Integer>>(
					new RankingAndCrowdingDistanceComparator<PermutationSolution<Integer>>());
		} catch (Exception e) {
			new JMetalException("BinaryTournamentSelectionFactory.produce(file): error when reading data file " + e);

			return null;
		}
	}
}
