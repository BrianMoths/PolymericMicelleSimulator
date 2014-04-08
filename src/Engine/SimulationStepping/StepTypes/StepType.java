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
import java.io.Serializable;

/**
 *
 * @author bmoths
 */
public enum StepType implements Serializable {

    SINGLE_BEAD(new StepGenerator() {
        @Override
        public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
            return BeadMoveGenerator.getBeadMove(systemAnalyzer);
        }

    },
    "single bead"),
    SINGLE_CHAIN(new StepGenerator() {
        @Override
        public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
            return ChainMoveStepGenerator.getChainMove(systemAnalyzer);
        }

    },
    "chain"),
    SINGLE_WALL_RESIZE(new StepGenerator() {
        @Override
        public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
            return RelativeResizeStepGenerator.getRelativeResizeStep(systemAnalyzer);
        }

    },
    "wall scaling"),
    NO_STRETCH_WALL(new StepGenerator() {
        @Override
        public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
            return NoStretchWallStepGenerator.getNoStretchWallStep(systemAnalyzer);
        }

    },
    "wall move"),
    REPTATION(new StepGenerator() {
        @Override
        public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
            return ReptationStepGenerator.getReptationMove(systemAnalyzer);
        }

    },
    "reptation"),
    ZERO_STEP(new StepGenerator() {
        @Override
        public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
            return ZeroStep.getZeroStep();
        }

    },
    "trivial");
    private static final long serialVersionUID = 0L;
    private final StepGenerator stepGenerator;
    private final String name;

    private StepType(StepGenerator stepGenerator, String name) {
        this.stepGenerator = stepGenerator;
        this.name = name;
    }

    public SimulationStep getSimulationStep(SystemAnalyzer systemAnalyzer) {
        return stepGenerator.generateStep(systemAnalyzer);
    }

    public String getName() {
        return name;
    }

}
