/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.PolymerState;

import Engine.PolymerState.SystemGeometry.Interfaces.ImmutableSystemGeometry;
import Engine.PolymerState.SystemGeometry.Interfaces.SystemGeometry;
import Engine.SystemAnalyzer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class PolymerState implements ImmutablePolymerState {

    private static final long serialVersionUID = 5254646349028284040L;
    private final DiscretePolymerState discretePolymerState;
    private final PolymerPosition polymerPosition;
    private final SystemGeometry systemGeometry;

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
        if (discretePolymerState.getLeftBeadOfChain(bead) == discretePolymerState.getRightBeadOfChain(bead)) {
            return false;
        }
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

        double[] movingBeadPosition = getMovingBeadPosition(movingBead);
        double[] initialNeighborPosition = getInitialNeighborPosition(movingBead, isGoingRight);
        double[] finalNeighborPosittion = getFinalNeighborPosition(movingBead, isGoingRight);

        return getStepVectorFromPositions(movingBeadPosition, initialNeighborPosition, finalNeighborPosittion);
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

    public void reasonableMiddleRandomize() {
        polymerPosition.reasonableMiddleRandomize(discretePolymerState);
    }

    public void reasonableColumnRandomize() {
        polymerPosition.reasonableColumnRandomize(discretePolymerState);
    }

    public void reasonableRandomize() {
        polymerPosition.reasonableRandomize(discretePolymerState);
    }

    public void linearInitialize() {
        polymerPosition.linearInitialize(discretePolymerState);
    }

    public void anneal() {
        polymerPosition.anneal();
    }

    public void rescaleBeadPositions(double rescaleFactor) {
        polymerPosition.rescaleBeadPositionsHorizontally(rescaleFactor);
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

    private double[] getEndToEndDisplacement(final Integer leftBead) {
        class DisplacementIterator implements Iterator<double[]> {

            private int currentBead;
            private int rightNeighbor;
            private double[] currentPosition;
            private double[] neighborPosition;

            public DisplacementIterator() {
                currentBead = leftBead;
                currentPosition = polymerPosition.getBeadPosition(currentBead);
                rightNeighbor = discretePolymerState.getNeighborToRightOfBead(currentBead);
            }

            @Override
            public boolean hasNext() {
                return rightNeighbor != -1;
            }

            @Override
            public double[] next() {
                double[] displacement = calculateNextDisplacement();
                iterateFields();
                return displacement;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            private double[] calculateNextDisplacement() {
                neighborPosition = polymerPosition.getBeadPosition(rightNeighbor);
                final double[] displacement = systemGeometry.getDisplacement(neighborPosition, currentPosition);
                return displacement;
            }

            private void iterateFields() {
                currentBead = rightNeighbor;
                currentPosition = neighborPosition;
                rightNeighbor = discretePolymerState.getNeighborToRightOfBead(currentBead);
            }

        }
        final double[] displacement = new double[systemGeometry.getNumDimensions()];
        final DisplacementIterator displacementIterator = new DisplacementIterator();

        while (displacementIterator.hasNext()) {
            final double[] currentDisplacement = displacementIterator.next();
            for (int i = 0; i < displacement.length; i++) {
                displacement[i] += currentDisplacement[i];
            }
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
