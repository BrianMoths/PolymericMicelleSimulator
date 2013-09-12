/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping;

import Engine.PolymerPosition;
import Engine.SystemAnalyzer;
import Engine.SystemGeometry.SystemGeometry;

/**
 *
 * @author bmoths
 */
public class SingleWallResizeStep implements SimulationStep {

    static public final MoveType moveType = MoveType.SINGLE_WALL_RESIZE;
    private final int dimension;
    private final double sizeChange;
    private double energyChange;

    public SingleWallResizeStep(int dimension, double sizeChange) {
        this.dimension = dimension;
        this.sizeChange = sizeChange;
    }

    @Override
    public boolean doStep(PolymerPosition polymerPosition, SystemAnalyzer systemAnalyzer) {
        final double oldEnergy = systemAnalyzer.computeEnergy();
        scaleSystem(polymerPosition, systemAnalyzer);
        final double newEnergy = systemAnalyzer.computeEnergy();
        energyChange = newEnergy - oldEnergy;
        return true;
    }
    //<editor-fold defaultstate="collapsed" desc="helpers">

    private void scaleSystem(PolymerPosition polymerPosition, SystemAnalyzer systemAnalyzer) {
        SystemGeometry systemGeometry = systemAnalyzer.getSystemGeometry();
        final double oldSize = getSizeOfDimension(systemGeometry);
        changeGeometry(systemGeometry);
        final double newSize = getSizeOfDimension(systemGeometry);
        final double scaleFactor = newSize / oldSize;
        scalePositions(polymerPosition, scaleFactor);
    }

    private double getSizeOfDimension(SystemGeometry systemGeometry) {
        return systemGeometry.getRMax()[dimension];
    }

    private void changeGeometry(SystemGeometry systemGeometry) {
        final double oldSize = systemGeometry.getRMax()[dimension];
        final double newSize = oldSize + sizeChange;
        systemGeometry.setRMax(dimension, newSize);
    }

    private void scalePositions(PolymerPosition polymerPosition, double scaleFactor) {
        final double[][] beadPositions = polymerPosition.getBeadPositions();
        final int numBeads = polymerPosition.getNumBeads();
        for (int bead = 0; bead < numBeads; bead++) {
            beadPositions[bead][dimension] *= scaleFactor;
        }
        polymerPosition.setBeadPositions(beadPositions);
    }
    //</editor-fold>

    @Override
    public void undoStep(PolymerPosition polymerPosition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
