/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping.StepTypes;

import Engine.SimulationStepping.StepGenerators.ElementaryStepGenerators.BeadMoveGenerator;
import Engine.SimulationStepping.StepGenerators.ElementaryStepGenerators.ChainMoveStepGenerator;
import Engine.SimulationStepping.StepGenerators.ElementaryStepGenerators.RelativeResizeStepGenerator;
import Engine.SimulationStepping.StepGenerators.ElementaryStepGenerators.ReptationStepGenerator;
import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SystemAnalyzer;

/**
 *
 * @author bmoths
 */
public enum MoveType {

    SINGLE_BEAD(new StepGenerator() {
        @Override
        public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
            return BeadMoveGenerator.getBeadMove(systemAnalyzer);
        }

    }),
    SINGLE_CHAIN(new StepGenerator() {
        @Override
        public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
            return ChainMoveStepGenerator.getChainMove(systemAnalyzer);
        }

    }),
    SINGLE_WALL_RESIZE(new StepGenerator() {
        @Override
        public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
            return RelativeResizeStepGenerator.getRelativeResizeStep(systemAnalyzer);
        }

    }),
    REPTATION(new StepGenerator() {
        @Override
        public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
            return ReptationStepGenerator.getReptationMove(systemAnalyzer);
        }

    });
    private final StepGenerator stepGenerator;

    private MoveType(StepGenerator stepGenerator) {
        this.stepGenerator = stepGenerator;
    }

    public SimulationStep getSimulationStep(SystemAnalyzer systemAnalyzer) {
        return stepGenerator.generateStep(systemAnalyzer);
    }

}
