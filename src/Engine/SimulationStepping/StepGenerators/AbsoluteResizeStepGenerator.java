/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping.StepGenerators;

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

    private final double resizeStepChance;
    private final double maxDisplacement;

    public AbsoluteResizeStepGenerator() {
//        this(maxScalingFactor, resizeStepChance);
        this(1., .0001);//.0001
    }

    public AbsoluteResizeStepGenerator(double maxDisplacement, double resizeStepChance) {
        this.resizeStepChance = resizeStepChance;
        this.maxDisplacement = maxDisplacement;
    }

    @Override
    public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
        if (random.nextDouble() < resizeStepChance) {
            return generateResizeMove(systemAnalyzer);
        } else {
            return BeadMoveGenerator.getBeadMove(systemAnalyzer);
        }
    }

    public SimulationStep generateResizeMove(SystemAnalyzer systemAnalyzer) {
        final double randomNumber = 2 * random.nextDouble() - 1.;
        final double sizeChange = maxDisplacement * randomNumber;
        return new SingleWallResizeStep(resizeDimension, sizeChange);
    }

}
