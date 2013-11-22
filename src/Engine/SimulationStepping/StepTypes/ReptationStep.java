/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping.StepTypes;

import Engine.PolymerPosition;
import Engine.SystemAnalyzer;

/**
 *
 * @author bmoths
 */
public class ReptationStep implements SimulationStep {

    @Override
    public boolean doStep(PolymerPosition polymerPosition, SystemAnalyzer systemAnalyzer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void undoStep(PolymerPosition polymerPosition, SystemAnalyzer systemAnalyzer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getEnergyChange() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MoveType getMoveType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
