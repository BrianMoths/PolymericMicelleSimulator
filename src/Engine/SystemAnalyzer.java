/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.Energetics.EnergeticsConstants;
import Engine.Energetics.TwoBeadOverlap;
import Engine.BeadBinning.BeadBinner;
import Engine.Energetics.EnergyEntropyChange;
import Engine.PolymerState.ImmutableDiscretePolymerState;
import Engine.PolymerState.ImmutablePolymerState;
import Engine.PolymerState.SystemGeometry.AreaOverlap;
import Engine.PolymerState.SystemGeometry.Interfaces.ImmutableSystemGeometry;
import SystemAnalysis.AreaPerimeter.BeadRectangle;
import SystemAnalysis.GeometryAnalyzer;
import SystemAnalysis.GeometryAnalyzer.AreaPerimeter;
import SystemAnalysis.AreaPerimeter.RectangleSplitting.RectanglesAndGluedPerimeter;
import SystemAnalysis.SimulationHistory;
import SystemAnalysis.SimulationHistory.TrackedVariable;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class SystemAnalyzer implements Serializable {

    static public final class BeadPositionsGetter {

        private double[][] temporaryBeadPositions;

        private BeadPositionsGetter() {
        }

        public void setBeadPositions(double[][] beadPositions) {
            this.temporaryBeadPositions = beadPositions;
        }

    }

    public class AnalyzerListener {

        private AnalyzerListener() {
        }

        public void rebinBeads() {
            beadBinner = new BeadBinner(beadPositions, systemGeometry);
        }

        public void updateBinOfBead(int stepBead) {
            beadBinner.updateBeadPosition(stepBead, beadPositions[stepBead]);
        }

        public void resetHistory() {
            simulationHistory.clearAll();
        }

    }

    static private final int statisticsWindow = 1000;
    private final ImmutableDiscretePolymerState immutableDiscretePolymerState;
    private final ImmutableSystemGeometry systemGeometry;
    private final double[][] beadPositions;
    private final EnergeticsConstants energeticsConstants;
    private final int numBeads;
    private BeadBinner beadBinner;
    private SimulationHistory simulationHistory;

    //<editor-fold defaultstate="collapsed" desc="constructors">
    public SystemAnalyzer(ImmutablePolymerState immutablePolymerState,
            EnergeticsConstants energeticsConstants) {
        systemGeometry = immutablePolymerState.getImmutableSystemGeometry();
        immutableDiscretePolymerState = immutablePolymerState.getImmutableDiscretePolymerState();
        numBeads = immutableDiscretePolymerState.getNumBeads();
        this.energeticsConstants = energeticsConstants;
        BeadPositionsGetter beadPositionsGetter = new BeadPositionsGetter();
        immutablePolymerState.acceptBeadPositionGetter(beadPositionsGetter);
        beadPositions = beadPositionsGetter.temporaryBeadPositions;
        AnalyzerListener analyzerListener = new AnalyzerListener();
        immutablePolymerState.acceptAnalyzerListener(analyzerListener);
        beadBinner = new BeadBinner(beadPositions, systemGeometry);
        simulationHistory = new SimulationHistory(statisticsWindow);
    }

    public SystemAnalyzer(SystemAnalyzer systemAnalyzer) {
        systemGeometry = systemAnalyzer.systemGeometry;
        immutableDiscretePolymerState = systemAnalyzer.immutableDiscretePolymerState;
        numBeads = systemAnalyzer.numBeads;
        energeticsConstants = systemAnalyzer.energeticsConstants;
        beadPositions = systemAnalyzer.beadPositions;
        beadBinner = new BeadBinner(systemAnalyzer.beadBinner); //check this
//        analyzerListener = systemAnalyzer.analyzerListener; //this copying doesn't work well
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="area and perimeter">
    /**
     *
     * @return The amount of area occupied by the beads (any area covered
     * multiple times is still only counted once).
     */
    public double findArea() { //produces bad output
        List<BeadRectangle> beadRectangles = systemGeometry.getRectanglesFromPositions(beadPositions);
        return GeometryAnalyzer.findArea(beadRectangles);
    }

    public AreaPerimeter findAreaAndPerimeter() {
        RectanglesAndGluedPerimeter rectanglesAndGluedPerimeter;
        rectanglesAndGluedPerimeter = systemGeometry.getRectanglesAndPerimeterFromPositions(beadPositions);
        AreaPerimeter areaPerimeter;
        areaPerimeter = GeometryAnalyzer.findAreaAndPerimeter(rectanglesAndGluedPerimeter.beadRectangles);
        areaPerimeter.perimeter -= rectanglesAndGluedPerimeter.gluedPerimeter * 2;
        return areaPerimeter;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="simulation history">
    public void addPerimeterAreaEnergySnapshot(double perimeter, double area, double energy) {
        simulationHistory.addValue(SimulationHistory.TrackedVariable.PERIMETER, perimeter);
        simulationHistory.addValue(SimulationHistory.TrackedVariable.AREA, area);
        simulationHistory.addValue(SimulationHistory.TrackedVariable.ENERGY, energy);
    }

    public double getAverage(TrackedVariable trackedVariable) {
        return simulationHistory.getAverage(trackedVariable);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="overlap and stretching">
    public double totalSpringStretching() {
        double sqLength = 0;

        for (int bead = 0; bead < numBeads; bead++) {
            sqLength += beadStretching(bead);
        }

        return sqLength / 2; //divide by two since double counting
    }

    public double beadStretching(int bead) {
        double sqLength = 0;
        final int leftNeighborIndex = immutableDiscretePolymerState.getNeighborToLeftOfBead(bead);
        sqLength += getBeadStretchingEnergyWithNeighborIndex(bead, leftNeighborIndex);
        final int rightNeighborIndex = immutableDiscretePolymerState.getNeighborToRightOfBead(bead);
        sqLength += getBeadStretchingEnergyWithNeighborIndex(bead, rightNeighborIndex);
        return sqLength;
    }

    private double getBeadStretchingEnergyWithNeighborIndex(int bead, int neighborIndex) {
        if (neighborIndex >= 0) {
            return systemGeometry.sqDist(beadPositions[bead], beadPositions[neighborIndex]);
        } else {
            return 0;
        }
    }

    public AreaOverlap totalOverlap() {
        AreaOverlap overlap = new AreaOverlap();

        for (int i = 0; i < numBeads; i++) {
            overlap.incrementBy(beadOverlap(i));
        }

        overlap.halve(); //divide by two since double counting

        return overlap;
    }

    public AreaOverlap beadOverlap(int bead) {
        TwoBeadOverlap AOverlap = new TwoBeadOverlap(), BOverlap = new TwoBeadOverlap();
        final double[] beadPosition = beadPositions[bead];
        Iterator<Integer> nearbyBeadIterator = beadBinner.getNearbyBeadIterator(bead);
        while (nearbyBeadIterator.hasNext()) {
            final int currentBead = nearbyBeadIterator.next();
            if (isTypeA(currentBead)) {
                AOverlap.increment(systemGeometry.twoBeadOverlap(beadPosition, beadPositions[currentBead]));
            } else {
                BOverlap.increment(systemGeometry.twoBeadOverlap(beadPosition, beadPositions[currentBead]));
            }
        }

        return AreaOverlap.overlapOfBead(isTypeA(bead), AOverlap, BOverlap);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="computing energy">
    //<editor-fold defaultstate="collapsed" desc="total Energy">
    public double computeEnergy() {
        return springEnergy() + densityEnergy() + externalEnergy();
    }

    public double springEnergy() {
        double sqLength = totalSpringStretching();

        return energeticsConstants.springEnergy(sqLength);
    }

    public double densityEnergy() {
        AreaOverlap overlap = totalOverlap();

        return energeticsConstants.densityEnergy(overlap);
    }

    public double externalEnergy() {
        return energeticsConstants.externalEnergy(systemGeometry);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="bead Energy">
    public double beadEnergy(int bead) {
        return beadSpringEnergy(bead) + beadDensityEnergy(bead);
    }

    public double beadSpringEnergy(int bead) {
        final double beadStretching = beadStretching(bead);

        return energeticsConstants.springEnergy(beadStretching);
    }

    public double beadDensityEnergy(int bead) {
        final AreaOverlap overlap = beadOverlap(bead);

        return energeticsConstants.densityEnergy(overlap);
    }
    //</editor-fold>
    //</editor-fold>

    public List<Integer> getChainOfBead(int bead) {
        return immutableDiscretePolymerState.getChainOfBead(bead);
    }

    //<editor-fold defaultstate="collapsed" desc="getters">
    public boolean isEquilibrated() {
        return simulationHistory.isEquilibrated();
    }

    public ImmutableSystemGeometry getSystemGeometry() {
        return systemGeometry;
    }

    public EnergeticsConstants getEnergeticsConstants() {
        return energeticsConstants;
    }

    public boolean isEnergeticallyAllowed(double energyChange) {
        return energeticsConstants.isEnergeticallyAllowed(energyChange);
    }

    public boolean isEnergeticallyAllowed(EnergyEntropyChange energyEntropyChange) {
        return energeticsConstants.isEnergeticallyAllowed(energyEntropyChange);
    }

    public int getNumBeads() {
        return numBeads;
    }

    public int getNumABeads() {
        return immutableDiscretePolymerState.getNumABeads();
    }

    public int getNeighbor(int bead, int direction) {
        if (direction == 0) {
            return immutableDiscretePolymerState.getNeighborToLeftOfBead(bead);
        } else if (direction == 1) {
            return immutableDiscretePolymerState.getNeighborToRightOfBead(bead);
        }
        throw new IllegalArgumentException("direction must be 0 or 1");
    }

    public double getBeadPositionComponent(int bead, int component) {
        return beadPositions[bead][component];
    }

    public SimulationHistory getSimulationHistory() {
        return simulationHistory;
    }

    public boolean isTypeA(int bead) {
        return immutableDiscretePolymerState.isTypeA(bead);
    }
    //</editor-fold>
}
