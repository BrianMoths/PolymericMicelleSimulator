/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.PolymerState;

import Engine.PolymerState.SystemGeometry.Interfaces.SystemGeometry;
import Engine.SystemAnalyzer;
import Engine.SystemAnalyzer.AnalyzerListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class PolymerPosition implements ImmutablePolymerPosition {

    private final int numBeads;
    private List<AnalyzerListener> registeredAnalyzerListeners;
    private final SystemGeometry systemGeometry;
    private final double[][] beadPositions;

    public PolymerPosition(int numBeads, SystemGeometry systemGeometry) {
        this.numBeads = numBeads;
        registeredAnalyzerListeners = new ArrayList<>();
        this.systemGeometry = systemGeometry;
        beadPositions = new double[numBeads][systemGeometry.getNumDimensions()];
        randomizePrivate();
    }

    public PolymerPosition(PolymerPosition polymerPosition) {
        numBeads = polymerPosition.numBeads;
        registeredAnalyzerListeners = new ArrayList<>();
        systemGeometry = polymerPosition.systemGeometry;
        beadPositions = polymerPosition.getBeadPositions();
    }

    //<editor-fold defaultstate="collapsed" desc="toString">
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Number of Beads: ").append(Integer.toString(numBeads)).append("\n");
        stringBuilder.append("List of bead positions: \n ").append(beadPositionString(beadPositions)).append("\n");
        stringBuilder.append("Geometry: \n").append(systemGeometry.toString()).append("\n");
        return stringBuilder.toString();
    }

    public String beadPositionString(double[][] beadPositions) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < beadPositions.length; i++) {
            stringBuilder.append("Position of bead ").append(i).append(": ").append(Arrays.toString(beadPositions[i])).append("\n");
        }
        return stringBuilder.toString();
    }

    public String neighborString(int[][] neighbors) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < beadPositions.length; i++) {
            stringBuilder.append("neighbors of bead ").append(i).append(": ").append(Arrays.toString(neighbors[i])).append("\n");
        }
        return stringBuilder.toString();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="randomizePositions">
    public void randomize() {
        randomizePrivate();
    }

    public void columnRandomize() {
        setBeadPositions(systemGeometry.randomColumnPositions(numBeads));
    }

    public void reasonableRandomize(ImmutableDiscretePolymerState immutableDiscretePolymerState, PositionGenerator positionGenerator) {
        setBeadPositions(reasonableRandomPositions(immutableDiscretePolymerState, positionGenerator));
    }

    @Override
    public double[][] reasonableRandomPositions(ImmutableDiscretePolymerState immutableDiscretePolymerState, PositionGenerator positionGenerator) {
        List<Boolean> isRandomized = new ArrayList<>(numBeads);
        for (int bead = 0; bead < numBeads; bead++) {
            isRandomized.add(false);
        }
        final double[][] randomPositions = new double[numBeads][systemGeometry.getNumDimensions()];
        for (int bead = 0; bead < numBeads; bead++) {
            if (!isRandomized.get(bead)) {
                List<Integer> chainOfBead = immutableDiscretePolymerState.getChainOfBead(bead);
                reasonableChainRandomize(chainOfBead, positionGenerator, randomPositions);
                for (Integer randomizedBead : chainOfBead) {
                    isRandomized.set(randomizedBead, true);
                }
            }

        }
        return randomPositions;
    }

    @Override
    public void reasonableChainRandomize(List<Integer> chainOfBead, PositionGenerator positionGenerator, double[][] randomPositions) {
        double[] currentPosition = positionGenerator.generatePosition();
        reasonableChainRandomizeAtPosition(chainOfBead, currentPosition, randomPositions);
    }

    private void reasonableChainRandomizeAtPosition(List<Integer> chainOfBead, double[] currentPosition, double[][] randomPositions) {
        for (int currentBead : chainOfBead) {
            movePositionByStep(currentPosition);
            systemGeometry.checkedCopyPosition(currentPosition, randomPositions[currentBead]);
        }
    }

    public void reasonableColumnRandomize(ImmutableDiscretePolymerState immutableDiscretePolymerState) {
        PositionGenerator positionGenerator = new PositionGenerator() {

            @Override
            public double[] generatePosition() {
                return systemGeometry.randomColumnPosition(.15);
            }

        };
        reasonableRandomize(immutableDiscretePolymerState, positionGenerator);
    }

    public void reasonableMiddleRandomize(ImmutableDiscretePolymerState immutableDiscretePolymerState) {
        PositionGenerator positionGenerator = new PositionGenerator() {

            @Override
            public double[] generatePosition() {
                return systemGeometry.randomMiddlePosition();
            }

        };
        reasonableRandomize(immutableDiscretePolymerState, positionGenerator);
    }

    public void reasonableRandomize(ImmutableDiscretePolymerState immutableDiscretePolymerState) {
        PositionGenerator positionGenerator = new PositionGenerator() {

            @Override
            public double[] generatePosition() {
                return systemGeometry.randomPosition();
            }

        };
        reasonableRandomize(immutableDiscretePolymerState, positionGenerator);
    }

    public void reasonableBoxRandomize(ImmutableDiscretePolymerState immutableDiscretePolymerState, double[] lowerLimits, double[] upperLimits) {
        PositionGenerator positionGenerator = new BoxPositionGenerator(lowerLimits, upperLimits, systemGeometry);
        reasonableRandomize(immutableDiscretePolymerState, positionGenerator);
    }

    private void movePositionByStep(double[] currentPosition) {
        boolean isStepped = false;
        while (!isStepped) {
            isStepped = systemGeometry.incrementFirstVector(currentPosition, systemGeometry.randomGaussian());
        }
    }

    private void randomizePrivate() {
        setBeadPositions(systemGeometry.randomMiddlePositions(numBeads));
    }

    public void anneal() {
        for (int bead = 0; bead < numBeads; bead++) {
            double[] beadPosition = beadPositions[bead];
            double[] stepVector = systemGeometry.randomGaussian(.5);//.5
            systemGeometry.incrementFirstVector(beadPosition, stepVector);
        }
        analyzersRebinBeads();
    }
    //</editor-fold>

    public void rescaleBeadPositionsHorizontally(double rescaleFactor) {
        systemGeometry.rescaleVectorsHorizontally(beadPositions, rescaleFactor);
    }

    //<editor-fold defaultstate="collapsed" desc="recenter">
    public void recenter() {
        double[] averagePosition = getAveragePosition();
        double[] displacementFromMiddle = new double[]{averagePosition[0] - systemGeometry.getSizeOfDimension(0) / 2, averagePosition[1] - systemGeometry.getSizeOfDimension(1) / 2};
        subtractFromAllPositions(displacementFromMiddle);
        analyzersRebinBeads();
    }

    private double[] getAveragePosition() {
        double[] averagePosition = {0, 0, 0};
        final int numDimensions = systemGeometry.getNumDimensions();
        for (int bead = 0; bead < numBeads; bead++) {
            for (int dimension = 0; dimension < numDimensions; dimension++) {
                averagePosition[dimension] += beadPositions[bead][dimension];
            }
        }
        for (int dimension = 0; dimension < numDimensions; dimension++) {
            averagePosition[dimension] /= numBeads;
        }
        return averagePosition;
    }

    private void subtractFromAllPositions(double[] subtrahend) {
        for (int i = 0; i < subtrahend.length; i++) {
            subtrahend[i] = -subtrahend[i];
        }
        systemGeometry.incrementVectors(beadPositions, subtrahend);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="deal with analyzers">
    public void acceptBeadPositionsGetter(SystemAnalyzer.BeadPositionsGetter beadPositionsGetter) {
        beadPositionsGetter.setBeadPositions(beadPositions);
    }

    public void acceptAnalyzerListener(AnalyzerListener analyzerListener) {
        registeredAnalyzerListeners.add(analyzerListener);
    }

    private void analyzersRebinBeads() {
        for (AnalyzerListener analyzerListener : registeredAnalyzerListeners) {
            analyzerListener.recomputeInternalState();
        }
    }

    private void signalMoveToAnalyzers(int stepBead) {
        for (AnalyzerListener analyzerListener : registeredAnalyzerListeners) {
            analyzerListener.updateBinOfBead(stepBead);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="move beads">    
    public boolean moveBead(int stepBead, double[] stepVector) {
        final boolean isSuccessful = systemGeometry.incrementFirstVector(beadPositions[stepBead], stepVector);
        if (isSuccessful) {
            signalMoveToAnalyzers(stepBead);
        }
        return isSuccessful;
    }

    public void undoStep(int stepBead, double[] stepVector) {
        systemGeometry.decrementFirstVector(beadPositions[stepBead], stepVector);
        signalMoveToAnalyzers(stepBead);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="setters">
    public void setBeadPositions(double[][] beadPositions) {
        systemGeometry.checkedCopyPositions(beadPositions, this.beadPositions);
        analyzersRebinBeads();
    }

    private void setBeadPositionNoRebin(int bead, double[] beadPosition) {
        systemGeometry.checkedCopyPosition(beadPosition, this.beadPositions[bead]);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getters">
    @Override
    public int getNumBeads() {
        return numBeads;
    }

    @Override
    public double[] getBeadPosition(int bead) {
        double[] beadPositionCopy = new double[systemGeometry.getNumDimensions()];
        System.arraycopy(beadPositions[bead], 0, beadPositionCopy, 0, systemGeometry.getNumDimensions());
        return beadPositionCopy;
    }

    @Override
    public double[][] getBeadPositions() {
        double[][] beadPositionsCopy = new double[numBeads][systemGeometry.getNumDimensions()];
        for (int bead = 0; bead < numBeads; bead++) {
            System.arraycopy(beadPositions[bead], 0, beadPositionsCopy[bead], 0, systemGeometry.getNumDimensions());
        }
        return beadPositionsCopy;
    }
    //</editor-fold>

}
