/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping.StepGenerators.CompoundStepGenerators;

import Engine.SimulationStepping.StepGenerators.ElementaryStepGenerators.BeadMoveGenerator;
import Engine.SimulationStepping.StepGenerators.ElementaryStepGenerators.RelativeResizeStepGenerator;
import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SimulationStepping.StepTypes.SimulationStep;
import Engine.SystemAnalyzer;
import java.util.Random;

/**
 *
 * @author bmoths
 */
public class BeadMoveAndRelativeResizeStepGenerator implements StepGenerator {

    static private Random random;

    static {
        random = new Random();
    }

    private final double resizeStepChance;
    private final RelativeResizeStepGenerator relativeResizeStepGenerator;

    public BeadMoveAndRelativeResizeStepGenerator() {
//        this(maxScalingFactor, resizeStepChance);
        this(.01, .0001);//.0001
    }

    public BeadMoveAndRelativeResizeStepGenerator(double maxScalingFactor, double resizeStepChance) {
        this.resizeStepChance = resizeStepChance;

        relativeResizeStepGenerator = new RelativeResizeStepGenerator(maxScalingFactor);
    }

    @Override
    public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
        if (random.nextDouble() < resizeStepChance) {
            return relativeResizeStepGenerator.generateStep(systemAnalyzer);
        } else {
            return BeadMoveGenerator.getBeadMove(systemAnalyzer);
        }
    }

}
