package tfm;

import java.util.Comparator;
import java.util.List;

import org.uma.jmetal.algorithm.impl.DefaultLocalSearch;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.DominanceComparator;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        BinaryProblem problem = new OneMax(10) ;

        MutationOperator<BinarySolution> mutationOperator =
            new BitFlipMutation(1.0 / problem.getBitsFromVariable(0)) ;
    
        int improvementRounds = 1000 ;
    
        Comparator<BinarySolution> comparator = new DominanceComparator<>() ;
    
        DefaultLocalSearch<BinarySolution> localSearch = new DefaultLocalSearch<BinarySolution>(
                improvementRounds,
                problem,
                mutationOperator,
                comparator) ;
    
        localSearch.run();
    
        BinarySolution newSolution = localSearch.getResult() ;
    
        JMetalLogger.logger.info("Fitness: " + newSolution.objectives()[0]) ;
        JMetalLogger.logger.info("Solution: " + newSolution.variables().get(0)) ;
    }
}
