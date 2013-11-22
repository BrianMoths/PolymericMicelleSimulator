/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping.StepGenerators.ElementaryStepGenerators;

import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SimulationStepping.StepTypes.SimulationStep;
import Engine.SimulationStepping.StepTypes.SingleWallResizeStep;
import Engine.SystemAnalyzer;
import java.util.Random;

/**
 *
 * @author bmoths
 */
public class AbsoluteResizeStepGenerator implements StepGenerator {

    static private Random random;
    static private final int resizeDimension = 0;

    static {
        random = new Random();
    }

    private final double maxDisplacement;

    public AbsoluteResizeStepGenerator() {
        this(1.);
    }

    public AbsoluteResizeStepGenerator(double maxDisplacement) {
        this.maxDisplacement = maxDisplacement;
    }

    @Override
    public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {

        final double randomNumber = 2 * random.nextDouble() - 1.;
        final double sizeChange = maxDisplacement * randomNumber;
        return new SingleWallResizeStep(resizeDimension, sizeChange);
    }

}
