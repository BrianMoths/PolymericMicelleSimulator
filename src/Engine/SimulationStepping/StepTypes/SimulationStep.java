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
public interface SimulationStep {

    public boolean doStep(PolymerState polymerState, SystemAnalyzer systemAnalyzer);

    public void undoStep(PolymerState polymerState);

    public EnergyEntropyChange getEnergyEntropyChange();

    public StepType getMoveType();

}
