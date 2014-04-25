/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.PolymerState;

import Engine.PolymerState.SystemGeometry.Interfaces.SystemGeometry;
import Engine.SystemAnalyzer;
import Engine.SystemAnalyzer.AnalyzerListener;
import java.io.Serializable;
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
        resetAnalyzersHistory();
    }

    public void reasonableColumnRandomize(ImmutableDiscretePolymerState immutableDiscretePolymerState) {
        List<Boolean> isRandomized = new ArrayList<>(numBeads);
        for (int bead = 0; bead < numBeads; bead++) {
            isRandomized.add(false);
        }
        for (int bead = 0; bead < numBeads; bead++) {
            if (!isRandomized.get(bead)) {
                List<Integer> chainOfBead = immutableDiscretePolymerState.getChainOfBead(bead);
                reasonableColumnChainRandomize(chainOfBead);
                for (Integer randomizedBead : chainOfBead) {
                    isRandomized.set(randomizedBead, true);
                }
            }

        }

    }

    private void reasonableColumnChainRandomize(List<Integer> chainOfBead) {
        double[] currentPosition = systemGeometry.randomColumnPosition(.15);
        for (int currentBead : chainOfBead) {
            movePositionByStep(currentPosition);
            setBeadPositionNoRebin(currentBead, currentPosition);
        }
    }

    public void reasonableRandomize(ImmutableDiscretePolymerState immutableDiscretePolymerState) {
        List<Boolean> isRandomized = new ArrayList<>(numBeads);
        for (int bead = 0; bead < numBeads; bead++) {
            isRandomized.add(false);
        }
        for (int bead = 0; bead < numBeads; bead++) {
            if (!isRandomized.get(bead)) {
                List<Integer> chainOfBead = immutableDiscretePolymerState.getChainOfBead(bead);
                reasonableChainRandomize(chainOfBead);
                for (Integer randomizedBead : chainOfBead) {
                    isRandomized.set(randomizedBead, true);
                }
            }

        }

    }

    private void reasonableChainRandomize(List<Integer> chainOfBead) {
        double[] currentPosition = systemGeometry.randomPosition();
        for (int currentBead : chainOfBead) {
            movePositionByStep(currentPosition);
            setBeadPositionNoRebin(currentBead, currentPosition);
        }
    }

    private void movePositionByStep(double[] currentPosition) {
        boolean isStepped = false;
        while (!isStepped) {
            isStepped = systemGeometry.incrementFirstVector(currentPosition, systemGeometry.randomGaussian());
        }
    }

    private void randomizePrivate() {
        setBeadPositions(systemGeometry.randomMiddlePositions(numBeads));
        resetAnalyzersHistory();
    }

    public void anneal() {
        for (int bead = 0; bead < numBeads; bead++) {
            double[] beadPosition = beadPositions[bead];
            double[] stepVector = systemGeometry.randomGaussian(.5);//.5
            systemGeometry.incrementFirstVector(beadPosition, stepVector);
        }
        resetAnalyzersHistory();
        analyzersRebinBeads();
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
            analyzerListener.rebinBeads();
        }
    }

    private void signalMoveToAnalyzers(int stepBead) {
        for (AnalyzerListener analyzerListener : registeredAnalyzerListeners) {
            analyzerListener.updateBinOfBead(stepBead);
        }
    }

    private void resetAnalyzersHistory() {
        for (AnalyzerListener analyzerListener : registeredAnalyzerListeners) {
            analyzerListener.resetHistory();
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
