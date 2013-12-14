/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping.StepTypes;

import Engine.Energetics.EnergyEntropyChange;
import Engine.PolymerState.PolymerState;
import Engine.SystemAnalyzer;

/**
 *
 * @author bmoths
 */
public class ZeroStep implements SimulationStep {

    static public SimulationStep getZeroStep() {
        return new ZeroStep();
    }

    @Override
    public boolean doStep(PolymerState polymerState, SystemAnalyzer systemAnalyzer) {
        return false;
    }

    @Override
    public void undoStep(PolymerState polymerState) {
    }

    @Override
    public EnergyEntropyChange getEnergyEntropyChange() {
        return new EnergyEntropyChange(0, 0);
    }

    @Override
    public StepType getMoveType() {
        return StepType.ZERO_STEP;
    }

}
