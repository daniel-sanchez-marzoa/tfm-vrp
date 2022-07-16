package tfm.selection;

import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import tfm.problem.sweep.SweepCoverageSolution;
import tfm.selection.binarytournament.BinaryTournamentSelectionFactory;
import tfm.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.Arrays;
import java.util.List;

public class SelectionFactory {
    public static SelectionOperator<List<SweepCoverageSolution>, SweepCoverageSolution> produce(File file)
        throws FileNotFoundException {
        try {
            switch (getSelectionType(file)) {
                case BinaryTournamentSelection:
                    return BinaryTournamentSelectionFactory.produce(file);
                default:
                    throw new JMetalException(
                        "SelectionFactory.produce(file): unrecognized selection type. Check parameter SELECTION_TYPE to ensure it has one of the following values: "
                            + Arrays.toString(SelectionType.values()));
            }
        } catch (Exception e) {
            throw new JMetalException("SelectionFactory.produce(file): error when reading data file " + e);
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
