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
public class NoStretchWallStep implements SimulationStep {

    static public final StepType moveType = StepType.NO_STRETCH_WALL;
    private final int dimension;
    private final double sizeChange;
    private double energyChange;
    private double entropyChange;

    public NoStretchWallStep(int dimension, double sizeChange) {
        this.dimension = dimension;
        this.sizeChange = sizeChange;
    }

    @Override
    public boolean doStep(PolymerState polymerState, SystemAnalyzer systemAnalyzer) {
        if (!isMoveAllowed(systemAnalyzer)) {
            return false;
        } else {
            final double intialEnergy = systemAnalyzer.computeEnergy();
            polymerState.getSystemGeometry().setRMax(dimension, polymerState.getSystemGeometry().getSizeOfDimension(dimension) + sizeChange);
            entropyChange = computeEntropyChange(systemAnalyzer);
            energyChange = systemAnalyzer.computeEnergy() - intialEnergy;
            return true;
        }
    }

    private boolean isMoveAllowed(SystemAnalyzer systemAnalyzer) {
        final int numBeads = systemAnalyzer.getNumBeads();
        final double threshold = systemAnalyzer.getSystemGeometry().getSizeOfDimension(dimension) + sizeChange;
        for (int bead = 0; bead < numBeads; bead++) {
            if (systemAnalyzer.getBeadPositionComponent(bead, dimension) > threshold) {
                return false;
            }
        }
        return true;
    }

    private double computeEntropyChange(SystemAnalyzer systemAnalyzer) {
        double entropyChangeLocal;

        final int numBeads = systemAnalyzer.getNumBeads();
        final double fractionalSizeChange = 1. + sizeChange / systemAnalyzer.getSystemGeometry().getSizeOfDimension(dimension);

        entropyChangeLocal = numBeads / 2 * Math.log(fractionalSizeChange);

        return entropyChangeLocal;
    }

    @Override
    public void undoStep(PolymerState polymerState) {
        polymerState.getSystemGeometry().setRMax(dimension, polymerState.getSystemGeometry().getSizeOfDimension(dimension) - sizeChange);
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
