/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping.StepGenerators.ElementaryStepGenerators;

import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SimulationStepping.StepTypes.ReptationStep;
import Engine.SimulationStepping.StepTypes.SimulationStep;
import Engine.SimulationStepping.StepTypes.ZeroStep;
import Engine.SystemAnalyzer;
import java.util.Random;

/**
 *
 * @author bmoths
 */
public class ReptationStepGenerator implements StepGenerator {

    static private Random random;

    static {
        random = new Random();
    }

    static public SimulationStep getReptationMove(SystemAnalyzer systemAnalyzer) {
        if (systemAnalyzer.getNumBeads() == 0) {
            return ZeroStep.getZeroStep();
        }
        final int bead = getBeadNumber(systemAnalyzer);
        final boolean isGoingRight = random.nextBoolean();

        return new ReptationStep(bead, isGoingRight);
    }

    static private int getBeadNumber(SystemAnalyzer systemAnalyzer) {
        return random.nextInt(systemAnalyzer.getNumBeads());
    }

    public ReptationStepGenerator() {
    }

    @Override
    public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
        return getReptationMove(systemAnalyzer);
    }

}
