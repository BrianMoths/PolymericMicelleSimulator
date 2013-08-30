/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping;

import Engine.PolymerPosition;
import Engine.SystemAnalyzer;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class SingleChainStep implements SimulationStep {

    private final List<Integer> beads;
    private final double[] stepVector;
    private double energyChange;

    public SingleChainStep(List<Integer> beads, double[] stepVector) {
        this.beads = beads;

        final int length = stepVector.length;
        this.stepVector = new double[length];
        System.arraycopy(stepVector, 0, this.stepVector, 0, length);
    }

    public SingleChainStep(SingleChainStep singleChainStep) {
        beads = singleChainStep.beads;

        final int length = singleChainStep.stepVector.length;
        stepVector = new double[length];
        System.arraycopy(singleChainStep.stepVector, 0, stepVector, 0, length);
    }

    @Override
    public boolean doStep(PolymerPosition polymerPosition, SystemAnalyzer systemAnalyzer) {
        boolean isSuccessful = true;
        int currentBead = -1;
        final int numBeads = beads.size();
        while (currentBead < numBeads && isSuccessful) {
            currentBead++;
            final SingleBeadStep beadStep = new SingleBeadStep(currentBead, stepVector);
            isSuccessful = beadStep.doStep(polymerPosition, systemAnalyzer);
            energyChange += beadStep.getEnergyChange();
        }
        if (!isSuccessful) {
            energyChange = 0;
            for (; currentBead >= 0; currentBead--) {
                final SingleBeadStep beadStep = new SingleBeadStep(currentBead, stepVector);
                beadStep.undoStep(polymerPosition);
            }
        }
        return isSuccessful;
    }

    @Override
    public void undoStep(PolymerPosition polymerPosition) {
        final int numBeads = beads.size();
        for (int currentBead = 0; currentBead < numBeads; currentBead++) {
            final SingleBeadStep beadStep = new SingleBeadStep(currentBead, stepVector);
            beadStep.undoStep(polymerPosition);
        }
    }

    @Override
    public double getEnergyChange() {
        return energyChange;
    }

    public List<Integer> getBeads() {
        return beads;
    }

    public double[] getStepVector() {
        final int length = stepVector.length;
        double[] stepVectorCopy = new double[length];
        System.arraycopy(stepVector, 0, stepVectorCopy, 0, length);
        return stepVectorCopy;
    }
}
