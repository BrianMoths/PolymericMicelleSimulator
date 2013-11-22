/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping.StepGenerators.CompoundStepGenerators;

import Engine.SimulationStepping.StepGenerators.ElementaryStepGenerators.BeadMoveGenerator;
import Engine.SimulationStepping.StepGenerators.ElementaryStepGenerators.AbsoluteResizeStepGenerator;
import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SimulationStepping.StepTypes.SimulationStep;
import Engine.SystemAnalyzer;
import java.util.Random;

/**
 *
 * @author bmoths
 */
public class BeadMoveAndAbsoluteResizeStepGenerator implements StepGenerator {

    static private Random random;

    static {
        random = new Random();
    }

    private final double resizeStepChance;
    private final AbsoluteResizeStepGenerator resizeStepGenerator;

    public BeadMoveAndAbsoluteResizeStepGenerator() {
//        this(maxScalingFactor, resizeStepChance);
        this(1., .0001);//.0001
    }

    public BeadMoveAndAbsoluteResizeStepGenerator(double maxDisplacement, double resizeStepChance) {
        this.resizeStepChance = resizeStepChance;
        resizeStepGenerator = new AbsoluteResizeStepGenerator(maxDisplacement);
    }

    @Override
    public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
        if (random.nextDouble() < resizeStepChance) {
            return resizeStepGenerator.generateStep(systemAnalyzer);
        } else {
            return BeadMoveGenerator.getBeadMove(systemAnalyzer);
        }
    }

}
