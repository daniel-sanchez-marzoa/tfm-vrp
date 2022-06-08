package tfm.selection.binarytournament;

import java.io.File;
import java.io.FileNotFoundException;

import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;

import tfm.vrp.AreaCoverageSolution;

public class BinaryTournamentSelectionFactory {
	public static BinaryTournamentSelection<AreaCoverageSolution> produce(File file)
			throws FileNotFoundException {
		try {
			return new BinaryTournamentSelection<AreaCoverageSolution>(
					new RankingAndCrowdingDistanceComparator<AreaCoverageSolution>());
		} catch (Exception e) {
			new JMetalException("BinaryTournamentSelectionFactory.produce(file): error when reading data file " + e);

			return null;
		}
	}
}
