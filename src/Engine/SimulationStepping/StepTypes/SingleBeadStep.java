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
public class SingleBeadStep implements SimulationStep {

    static public final MoveType moveType = MoveType.SINGLE_BEAD;
    private final int bead;
    private final double[] stepVector;
    private double energyChange;

    public SingleBeadStep(int bead, double[] stepVector) {
        this.bead = bead;
        energyChange = 0;

        final int length = stepVector.length;
        this.stepVector = new double[length];
        System.arraycopy(stepVector, 0, this.stepVector, 0, length);
    }

    public SingleBeadStep(SingleBeadStep singleBeadStep) {
        bead = singleBeadStep.bead;
        energyChange = 0;

        final int length = singleBeadStep.stepVector.length;
        stepVector = new double[length];
        System.arraycopy(singleBeadStep.stepVector, 0, this.stepVector, 0, length);
    }

    @Override
    public boolean doStep(PolymerState polymerState, SystemAnalyzer systemAnalyzer) {
        final double initialEnergy = systemAnalyzer.beadEnergy(bead);
        final boolean isMoveSuccessful = polymerState.moveBead(bead, stepVector);
        if (isMoveSuccessful) {
            energyChange = systemAnalyzer.beadEnergy(bead) - initialEnergy;
        }
        return isMoveSuccessful;
    }

    @Override
    public void undoStep(PolymerState polymerState) {
        polymerState.undoStep(bead, stepVector);
    }

    @Override
    public double getEnergyChange() {
        return energyChange;
    }

    public int getBead() {
        return bead;
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
