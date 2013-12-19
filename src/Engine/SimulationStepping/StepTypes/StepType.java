/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping.StepTypes;

import Engine.SimulationStepping.StepGenerators.ElementaryStepGenerators.BeadMoveGenerator;
import Engine.SimulationStepping.StepGenerators.ElementaryStepGenerators.ChainMoveStepGenerator;
import Engine.SimulationStepping.StepGenerators.ElementaryStepGenerators.NoStretchWallStepGenerator;
import Engine.SimulationStepping.StepGenerators.ElementaryStepGenerators.RelativeResizeStepGenerator;
import Engine.SimulationStepping.StepGenerators.ElementaryStepGenerators.ReptationStepGenerator;
import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SystemAnalyzer;

/**
 *
 * @author bmoths
 */
public enum StepType {

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
    NO_STRETCH_WALL(new StepGenerator() {
        @Override
        public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
            return NoStretchWallStepGenerator.getNoStretchWallStep(systemAnalyzer);
        }

    }),
    REPTATION(new StepGenerator() {
        @Override
        public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
            return ReptationStepGenerator.getReptationMove(systemAnalyzer);
        }

    }),
    ZERO_STEP(new StepGenerator() {
        @Override
        public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
            return ZeroStep.getZeroStep();
        }

    });
    private final StepGenerator stepGenerator;

    private StepType(StepGenerator stepGenerator) {
        this.stepGenerator = stepGenerator;
    }

    public SimulationStep getSimulationStep(SystemAnalyzer systemAnalyzer) {
        return stepGenerator.generateStep(systemAnalyzer);
    }

}
