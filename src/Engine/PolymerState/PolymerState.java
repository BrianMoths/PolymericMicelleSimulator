/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.PolymerState;

import Engine.PolymerState.SystemGeometry.Interfaces.ImmutableSystemGeometry;
import Engine.PolymerState.SystemGeometry.Interfaces.SystemGeometry;
import Engine.SystemAnalyzer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class PolymerState implements ImmutablePolymerState {

    private DiscretePolymerState discretePolymerState;
    private PolymerPosition polymerPosition;
    private SystemGeometry systemGeometry;

    public PolymerState(DiscretePolymerState discretePolymerState, PolymerPosition polymerPosition, SystemGeometry systemGeometry) {
        this.discretePolymerState = discretePolymerState;
        this.polymerPosition = polymerPosition;
        this.systemGeometry = systemGeometry;
    }

    public void scaleSystemAlongDimension(double sizeChange, int dimension) {
        final double oldSize = systemGeometry.getSizeOfDimension(dimension);
        scaleGeometry(sizeChange, dimension);
        final double newSize = systemGeometry.getSizeOfDimension(dimension);
        final double scaleFactor = newSize / oldSize;
        scalePositions(scaleFactor, dimension);
    }

    //<editor-fold defaultstate="collapsed" desc="helpers">
    private void scaleGeometry(double sizeChange, int dimension) {
        final double oldSize = systemGeometry.getSizeOfDimension(dimension);
        final double newSize = oldSize + sizeChange;
        systemGeometry.setRMax(dimension, newSize);
    }

    private void scalePositions(double scaleFactor, int dimension) {
        final double[][] beadPositions = polymerPosition.getBeadPositions();
        final int numBeads = polymerPosition.getNumBeads();
        for (int bead = 0; bead < numBeads; bead++) {
            beadPositions[bead][dimension] *= scaleFactor;
        }
        polymerPosition.setBeadPositions(beadPositions);
    }
//</editor-fold>

    public boolean reptate(int bead, boolean isGoingRight) {
        int movingBead = discretePolymerState.getReptatingBead(bead, isGoingRight);
        double[] stepVector = getStepVector(movingBead, isGoingRight);
        boolean isSuccessful = polymerPosition.moveBead(movingBead, stepVector);
        if (isSuccessful) {
            discretePolymerState.reptateChainOfBead(bead, isGoingRight);
        }
        return isSuccessful;
    }

    //<editor-fold defaultstate="collapsed" desc="reptate helpers">
    private double[] getStepVector(int movingBead, boolean isGoingRight) {
        double[] stepVector;

        double[] movingBeadPosition = getMovingBeadPosition(movingBead);
        double[] initialNeighborPosition = getInitialNeighborPosition(movingBead, isGoingRight);
        double[] finalNeighborPosittion = getFinalNeighborPosition(movingBead, isGoingRight);

        stepVector = getStepVectorFromPositions(movingBeadPosition, initialNeighborPosition, finalNeighborPosittion);
        return stepVector;
    }

    private double[] getStepVectorFromPositions(double[] movingBeadPosition, double[] initialNeighborPosition, double[] finalNeighborPosittion) {
        final int numDimensions = movingBeadPosition.length;
        double[] stepVector = new double[numDimensions];

        for (int dimension = 0; dimension < numDimensions; dimension++) {
            stepVector[dimension] = initialNeighborPosition[dimension] + finalNeighborPosittion[dimension] - 2 * movingBeadPosition[dimension];
        }

        return stepVector;
    }

    private double[] getMovingBeadPosition(int movingBead) {
        return polymerPosition.getBeadPosition(movingBead);
    }

    private double[] getInitialNeighborPosition(int movingBead, boolean isGoingRight) {
        return polymerPosition.getBeadPosition(getInitialNeighbor(movingBead, isGoingRight));
    }

    private int getInitialNeighbor(int movingBead, boolean isGoingRight) {
        int initialNeighbor;
        if (isGoingRight) {
            initialNeighbor = discretePolymerState.getNeighborToRightOfBead(movingBead);
        } else {
            initialNeighbor = discretePolymerState.getNeighborToLeftOfBead(movingBead);
        }
        return initialNeighbor;
    }

    private double[] getFinalNeighborPosition(int movingBead, boolean isGoingRight) {
        return polymerPosition.getBeadPosition(getFinalNeighbor(movingBead, isGoingRight));
    }

    private int getFinalNeighbor(int movingBead, boolean isGoingRight) {
        return discretePolymerState.getReptatingBeadDestination(movingBead, isGoingRight);
    }
    //</editor-fold>

    @Override
    public ImmutableSystemGeometry getImmutableSystemGeometry() {
        return systemGeometry;
    }

    @Override
    public ImmutablePolymerPosition getImmutablePolymerPosition() {
        return polymerPosition;
    }

    @Override
    public ImmutableDiscretePolymerState getImmutableDiscretePolymerState() {
        return discretePolymerState;
    }

    @Override
    public void acceptBeadPositionGetter(SystemAnalyzer.BeadPositionsGetter beadPositionsGetter) {
        polymerPosition.acceptBeadPositionsGetter(beadPositionsGetter);
    }

    @Override
    public void acceptAnalyzerListener(SystemAnalyzer.AnalyzerListener analyzerListener) {
        polymerPosition.acceptAnalyzerListener(analyzerListener);
    }

    public void randomize() {
        polymerPosition.randomize();
    }

    public void columnRandomize() {
        polymerPosition.columnRandomize();
    }

    public void reasonableColumnRandomize() {
        polymerPosition.reasonableColumnRandomize(discretePolymerState);
    }

    public void anneal() {
        polymerPosition.anneal();
    }

    public boolean moveBead(int stepBead, double[] stepVector) {
        return polymerPosition.moveBead(stepBead, stepVector);
    }

    public void undoStep(int stepBead, double[] stepVector) {
        polymerPosition.undoStep(stepBead, stepVector);
    }

    @Override
    public List<double[]> getEndToEndDisplacements() {
        List<Integer> leftBeads = discretePolymerState.getLeftmostBeads();
        List<double[]> displacements = new ArrayList<>(leftBeads.size());
        for (Integer leftBead : leftBeads) {
            double[] displacement = getEndToEndDisplacement(leftBead);
            displacements.add(displacement);
        }
        return displacements;
    }

    private double[] getEndToEndDisplacement(Integer leftBead) {
        final double[] displacement = new double[systemGeometry.getNumDimensions()];
        int currentBead = leftBead;
        double[] currentPosition = polymerPosition.getBeadPosition(currentBead);
        int rightNeighbor = discretePolymerState.getNeighborToRightOfBead(currentBead);
        double[] neighborPosition;
        while (rightNeighbor != -1) {
            neighborPosition = polymerPosition.getBeadPosition(rightNeighbor);
            final double[] currentDisplacement = systemGeometry.getDisplacement(neighborPosition, currentPosition);
            for (int i = 0; i < displacement.length; i++) {
                displacement[i] += currentDisplacement[i];
            }
            currentPosition = neighborPosition;
            rightNeighbor = discretePolymerState.getNeighborToRightOfBead(currentBead);
        }
        return displacement;
    }

    public DiscretePolymerState getDiscretePolymerState() {
        return discretePolymerState;
    }

    public PolymerPosition getPolymerPosition() {
        return polymerPosition;
    }

    public SystemGeometry getSystemGeometry() {
        return systemGeometry;
    }

}
