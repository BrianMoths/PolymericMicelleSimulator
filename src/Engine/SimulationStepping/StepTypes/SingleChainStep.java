/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping.StepTypes;

import Engine.PolymerState.PolymerState;
import Engine.SystemAnalyzer;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class SingleChainStep implements SimulationStep {

    static public final MoveType moveType = MoveType.SINGLE_CHAIN;
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
    public boolean doStep(PolymerState polymerState, SystemAnalyzer systemAnalyzer) {
        boolean isSuccessful = true;
        energyChange = 0;
        int currentBeadInChain = 0;
        final int numBeads = beads.size();
        while (currentBeadInChain < numBeads && isSuccessful) {
            final int currentBead = beads.get(currentBeadInChain);
            final SingleBeadStep beadStep = new SingleBeadStep(currentBead, stepVector);
            isSuccessful = beadStep.doStep(polymerState, systemAnalyzer);
            energyChange += beadStep.getEnergyChange();
            currentBeadInChain++;
        }
        if (!isSuccessful) {
            energyChange = 0;
            currentBeadInChain--;
            for (; currentBeadInChain >= 0; currentBeadInChain--) {
                final int currentBead = beads.get(currentBeadInChain);
                final SingleBeadStep beadStep = new SingleBeadStep(currentBead, stepVector);
                beadStep.undoStep(polymerState);
            }
        }
        return isSuccessful;
    }

    @Override
    public void undoStep(PolymerState polymerState) {
        final int numBeads = beads.size();
        for (int currentBeadInChain = 0; currentBeadInChain < numBeads; currentBeadInChain++) {
            final int currentBead = beads.get(currentBeadInChain);
            final SingleBeadStep beadStep = new SingleBeadStep(currentBead, stepVector);
            beadStep.undoStep(polymerState);
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

    @Override
    public MoveType getMoveType() {
        return moveType;
    }

}
