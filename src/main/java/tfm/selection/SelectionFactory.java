package tfm.selection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.List;

import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

import tfm.selection.binarytournament.BinaryTournamentSelectionFactory;
import tfm.utils.FileUtils;

public class SelectionFactory {
	public static SelectionOperator<List<PermutationSolution<Integer>>, PermutationSolution<Integer>> produce(File file)
			throws FileNotFoundException {
		try {
			switch (getSelectionType(file)) {
				case BinaryTournamentSelection:
					return BinaryTournamentSelectionFactory.produce(file);
				default:
					new JMetalException(
							"SelectionFactory.produce(file): unrecognized selection type. Check parameter SELECTION_TYPE to ensure it has one of the following values: "
									+ SelectionType.values());

					return null;
			}
		} catch (Exception e) {
			new JMetalException("SelectionFactory.produce(file): error when reading data file " + e);

			return null;
		}
	}

	public static SelectionType getSelectionType(File file) throws IOException {
		StreamTokenizer token = FileUtils.getTokens(file);
		boolean found = false;
		token.nextToken();

		while (!found) {
			if ((token.sval != null) && ((token.sval.compareTo("SELECTION_TYPE") == 0)))
				found = true;
			else
				token.nextToken();
		}

		token.nextToken();
		token.nextToken();

		return SelectionType.valueOf(token.sval);
	}
}
