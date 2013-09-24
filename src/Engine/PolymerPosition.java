/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.SimulationStepping.StepTypes.SimulationStep;
import Engine.SimulationStepping.StepTypes.SingleBeadStep;
import Engine.SystemGeometry.SystemGeometry;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class PolymerPosition implements Serializable {

    private final int numBeads;
    private List<SystemAnalyzer> registeredSystemAnalyzers;
    private final SystemGeometry systemGeometry;
    private double[][] beadPositions;

    public PolymerPosition(PolymerCluster polymerCluster, SystemGeometry systemGeometry) {
        numBeads = polymerCluster.getNumBeads();
        registeredSystemAnalyzers = new ArrayList<>();
        this.systemGeometry = systemGeometry;
        randomizePrivate();
    }

    public PolymerPosition(PolymerPosition polymerPosition) {
        numBeads = polymerPosition.numBeads;
        registeredSystemAnalyzers = new ArrayList<>();
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
        setBeadPositionsPrivate(systemGeometry.randomColumnPositions(numBeads));
        resetAnalyzersHistory();
    }

    private void randomizePrivate() {
        setBeadPositionsPrivate(systemGeometry.randomPositions(numBeads));
        resetAnalyzersHistory();
    }

    public void anneal() {
        for (int bead = 0; bead < numBeads; bead++) {
            double[] beadPosition = beadPositions[bead];
            double[] stepVector = systemGeometry.randomGaussian(5);
            systemGeometry.incrementFirstVector(beadPosition, stepVector);
        }
        resetAnalyzersHistory();
        syncAnalyzers();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="deal with analyzers">
    public void registerAnalyzer(SystemAnalyzer systemAnalyzer) {
        registeredSystemAnalyzers.add(systemAnalyzer);
        systemAnalyzer.setBeadPositions(beadPositions);
    }

    private void syncAnalyzers() {
        for (SystemAnalyzer systemAnalyzer : registeredSystemAnalyzers) {
            systemAnalyzer.setBeadPositions(beadPositions);
        }
    }

    private void signalMoveToAnalyzers(int stepBead) {
        for (SystemAnalyzer systemAnalyzer : registeredSystemAnalyzers) {
            systemAnalyzer.updateBinOfBead(stepBead);
        }
    }

    private void resetAnalyzersHistory() {
        for (SystemAnalyzer systemAnalyzer : registeredSystemAnalyzers) {
            systemAnalyzer.resetHistory();
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
        syncAnalyzers();
    }

    private void setBeadPositionsPrivate(double[][] newBeadPositions) {
        beadPositions = newBeadPositions;
        syncAnalyzers();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getters">
    public int getNumBeads() {
        return numBeads;
    }

    public double[][] getBeadPositions() {
        double[][] beadPositionsCopy = new double[numBeads][systemGeometry.getDimension()];
        for (int bead = 0; bead < numBeads; bead++) {
            System.arraycopy(beadPositions[bead], 0, beadPositionsCopy[bead], 0, systemGeometry.getDimension());
        }
        return beadPositionsCopy;
    }
    //</editor-fold>

}
