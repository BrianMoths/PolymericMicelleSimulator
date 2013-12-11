/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping.StepTypes;

import Engine.PolymerState.PolymerState;
import Engine.SystemAnalyzer;

/**
 *
 * @author bmoths
 */
public class SingleWallResizeStep implements SimulationStep {

    static public final StepType moveType = StepType.SINGLE_WALL_RESIZE;
    private final int dimension;
    private final double sizeChange;
    private double energyChange;

    public SingleWallResizeStep(int dimension, double sizeChange) {
        this.dimension = dimension;
        this.sizeChange = sizeChange;
    }

    @Override
    public boolean doStep(PolymerState polymerState, SystemAnalyzer systemAnalyzer) {
        final double oldEnergy = systemAnalyzer.computeEnergy(); //optimization: get energy from polymerSimulator.
        polymerState.scaleSystemAlongDimension(sizeChange, dimension);
        final double newEnergy = systemAnalyzer.computeEnergy();
        energyChange = newEnergy - oldEnergy;
        return true;
    }

    @Override
    public void undoStep(PolymerState polymerState) {
        polymerState.scaleSystemAlongDimension(-sizeChange, dimension);
    }

    @Override
    public double getEnergyChange() {
        return energyChange;
    }

    @Override
    public StepType getMoveType() {
        return moveType;
    }

}
