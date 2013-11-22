/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping.StepGenerators.CompoundStepGenerators;

import Engine.SimulationStepping.StepGenerators.ElementaryStepGenerators.BeadMoveGenerator;
import Engine.SimulationStepping.StepGenerators.ElementaryStepGenerators.ChainMoveStepGenerator;
import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SimulationStepping.StepTypes.SimulationStep;
import Engine.SystemAnalyzer;
import java.util.Random;

/**
 *
 * @author bmoths
 */
public class BeadAndChainMoveStepGenerator implements StepGenerator {

    static private Random random;

    static {
        random = new Random();
    }

    final private double chainMoveChance;

    public BeadAndChainMoveStepGenerator() {
        chainMoveChance = 0;
    }

    public BeadAndChainMoveStepGenerator(double chainMoveChance) {
        this.chainMoveChance = chainMoveChance;
    }

    @Override
    public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
        if (random.nextDouble() < chainMoveChance) {
            return ChainMoveStepGenerator.getChainMove(systemAnalyzer);
        } else {
            return BeadMoveGenerator.getBeadMove(systemAnalyzer);
        }
    }

}
