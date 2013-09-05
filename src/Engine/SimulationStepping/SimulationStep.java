/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping;

import Engine.PolymerPosition;
import Engine.SystemAnalyzer;

/**
 *
 * @author bmoths
 */
public interface SimulationStep {

    public boolean doStep(PolymerPosition polymerPosition, SystemAnalyzer systemAnalyzer);

    public void undoStep(PolymerPosition polymerPosition);

    public double getEnergyChange();

    public MoveType getMoveType();
}
