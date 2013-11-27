/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.PolymerState;

import Engine.PolymerState.SystemGeometry.Interfaces.ImmutableSystemGeometry;
import Engine.PolymerState.SystemGeometry.Interfaces.SystemGeometry;
import Engine.SystemAnalyzer;

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
        return false;
    }

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

    public void anneal() {
        polymerPosition.anneal();
    }

    public boolean moveBead(int stepBead, double[] stepVector) {
        return polymerPosition.moveBead(stepBead, stepVector);
    }

    public void undoStep(int stepBead, double[] stepVector) {
        polymerPosition.undoStep(stepBead, stepVector);
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
