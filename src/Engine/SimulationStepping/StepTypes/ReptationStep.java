/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping.StepTypes;

import Engine.PolymerPosition;
import Engine.SystemAnalyzer;
import Engine.SystemGeometry.SystemGeometry;

/**
 *
 * @author bmoths
 */
public class ReptationStep implements SimulationStep {

    static public final MoveType moveType = MoveType.REPTATION;
    private double energyChange;
    private final int beadInChain;
    private final boolean isGoingRight;
    private double[] stepVector;

    public ReptationStep(int beadInChain, boolean isGoingRight) {
        this.beadInChain = beadInChain;
        this.isGoingRight = isGoingRight;
    }

    @Override
    public boolean doStep(PolymerPosition polymerPosition, SystemAnalyzer systemAnalyzer) {
        final int movingBead = getMovingBead(polymerPosition);

        final double initialEnergy = systemAnalyzer.beadEnergy(movingBead);

        SystemGeometry systemGeometry = systemAnalyzer.getSystemGeometry();

        stepVector = getStepVector(polymerPosition, movingBead);

        final boolean isMoveSuccessful = polymerPosition.moveBead(movingBead, stepVector);
        if (isMoveSuccessful) {
            polymerPosition.reptateChainOfBead(beadInChain, isGoingRight);
            energyChange = systemAnalyzer.beadEnergy(movingBead) - initialEnergy;
        }
        return isMoveSuccessful;
    }

    private int getMovingBead(PolymerPosition polymerPosition) {
        int movingBead;

        if (isGoingRight) {
            movingBead = polymerPosition.getLeftEndOfChain(beadInChain);
        } else {
            movingBead = polymerPosition.getRightEndOfChain(beadInChain);
        }

        return movingBead;
    }

    private double[] getStepVector(PolymerPosition polymerPosition, int movingBead) {
        double[] localStepVector;

        double[] movingBeadPosition = getMovingBeadPosition(polymerPosition, movingBead);
        double[] initialNeighborPosition = getInitialNeighborPosition(polymerPosition, movingBead);
        double[] finalNeighborPosittion = getFinalNeighborPosition(polymerPosition, movingBead);

        localStepVector = getStepVectorFromPositions(movingBeadPosition, initialNeighborPosition, finalNeighborPosittion);
        return localStepVector;
    }

    private double[] getMovingBeadPosition(PolymerPosition polymerPosition, int movingBead) {
        return polymerPosition.getBeadPositions()[movingBead];
    }

    private double[] getInitialNeighborPosition(PolymerPosition polymerPosition, int movingBead) {
        if (isGoingRight) {
            return polymerPosition.getRightNeighbor(movingBead);
        } else {
            return polymerPosition.getLeftNeighbor(movingBead);
        }
    }

    private double[] getFinalNeighborPosition(PolymerPosition polymerPosition, int movingBead) {
        if (isGoingRight) {
            return polymerPosition.getRightNeighbor(movingBead);
        } else {
            return polymerPosition.getLeftNeighbor(movingBead);
        }
    }

    private double[] getStepVectorFromPositions(double[] movingBeadPosition, double[] initialNeighborPosition, double[] finalNeighborPosittion) {
        final int numDimensions = movingBeadPosition.length;
        double[] localStepVector = new double[numDimensions];

        for (int dimension = 0; dimension < numDimensions; dimension++) {
            localStepVector[dimension] = initialNeighborPosition[dimension] + finalNeighborPosittion[dimension] - 2 * movingBeadPosition[dimension];
        }

        return localStepVector;
    }

    @Override
    public void undoStep(PolymerPosition polymerPosition, SystemAnalyzer systemAnalyzer) {
        polymerPosition.undoStep(beadInChain, stepVector);
        polymerPosition.reptateChainOfBead(beadInChain, !isGoingRight);
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
