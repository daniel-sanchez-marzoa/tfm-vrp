package tfm.problem.sweep.runner;

import org.uma.jmetal.util.measure.MeasureListener;

public class IterationListener implements MeasureListener<Long> {
    public IterationListener() {
    }

    @Override
    synchronized public void measureGenerated(Long iteration) {
        System.out.println("Iteración: " + iteration);
    }
}
