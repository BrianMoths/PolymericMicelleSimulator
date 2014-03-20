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
public class SingleWallResizeStep implements SimulationStep {

    static public final StepType moveType = StepType.SINGLE_WALL_RESIZE;
    private final int dimension;
    private final double sizeChange;
    private double energyChange;
    private double entropyChange;

    public SingleWallResizeStep(int dimension, double sizeChange) {
        this.dimension = dimension;
        this.sizeChange = sizeChange;
    }

    @Override
    public boolean doStep(PolymerState polymerState, SystemAnalyzer systemAnalyzer) {
        final double oldEnergy = systemAnalyzer.computeEnergy(); //optimization: get energy from polymerSimulator.
        entropyChange = computeEntropyChange(systemAnalyzer);
        polymerState.scaleSystemAlongDimension(sizeChange, dimension);
        final double newEnergy = systemAnalyzer.computeEnergy();
        energyChange = newEnergy - oldEnergy;
        return true;
    }

    private double computeEntropyChange(SystemAnalyzer systemAnalyzer) {
        double entropyChangeLocal;

        final int numBeads = systemAnalyzer.getNumBeads();
        final double fractionalSizeChange = 1. + sizeChange / systemAnalyzer.getSystemGeometry().getSizeOfDimension(dimension);

        entropyChangeLocal = numBeads * Math.log(fractionalSizeChange);

        return entropyChangeLocal;
    }

    @Override
    public void undoStep(PolymerState polymerState) {
        polymerState.scaleSystemAlongDimension(-sizeChange, dimension);
    }

    @Override
    public EnergyEntropyChange getEnergyEntropyChange() {
        return new EnergyEntropyChange(energyChange, entropyChange);
    }

    @Override
    public StepType getMoveType() {
        return moveType;
    }

}
