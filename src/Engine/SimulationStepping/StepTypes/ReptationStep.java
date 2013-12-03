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
public class ReptationStep implements SimulationStep {

    static public final MoveType moveType = MoveType.REPTATION;
    private double energyChange;
    private final int beadInChain;
    private final boolean isGoingRight;

    public ReptationStep(int beadInChain, boolean isGoingRight) {
        this.beadInChain = beadInChain;
        this.isGoingRight = isGoingRight;
    }

    @Override
    public boolean doStep(PolymerState polymerState, SystemAnalyzer systemAnalyzer) {
        final int movingBead = polymerState.getDiscretePolymerState().getReptatingBead(beadInChain, isGoingRight);
        final double initialEnergy = systemAnalyzer.beadEnergy(movingBead);

        final boolean isMoveSuccessful = polymerState.reptate(beadInChain, isGoingRight);

        if (isMoveSuccessful) {
            energyChange = systemAnalyzer.beadEnergy(movingBead) - initialEnergy;
        }

        return isMoveSuccessful;
    }

    @Override
    public void undoStep(PolymerState polymerState) {
        polymerState.reptate(beadInChain, !isGoingRight);
    }

    @Override
    public double getEnergyChange() {
        return energyChange;
    }

    @Override
    public MoveType getMoveType() {
        return moveType;
    }

}
