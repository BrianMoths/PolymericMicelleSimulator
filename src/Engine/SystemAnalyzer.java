/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.BeadBinning.BeadBinner;
import Engine.Energetics.EnergeticsConstants;
import Engine.Energetics.EnergyEntropyChange;
import Engine.Energetics.TwoBeadOverlap;
import Engine.PolymerState.ImmutableDiscretePolymerState;
import Engine.PolymerState.ImmutablePolymerState;
import Engine.PolymerState.SystemGeometry.AreaOverlap;
import Engine.PolymerState.SystemGeometry.GeometricalParameters;
import Engine.PolymerState.SystemGeometry.Interfaces.ImmutableSystemGeometry;
import SystemAnalysis.AreaPerimeter.AreaPerimeter;
import SystemAnalysis.AreaPerimeter.circleareaperimeter.Circle;
import SystemAnalysis.AreaPerimeter.circleareaperimeter.CircleAreaFinder;
import SystemAnalysis.AreaPerimeter.circleareaperimeter.CircleAreaPerimeterFinder;
import SystemAnalysis.AreaPerimeter.circleareaperimeter.CirclesAndClippedPerimeter;
import SystemAnalysis.AreaPerimeter.rectangleareaperimeter.BeadRectangle;
import SystemAnalysis.AreaPerimeter.rectangleareaperimeter.RectangleSplitting.RectanglesAndGluedPerimeter;
import SystemAnalysis.GeometryAnalyzer;
import SystemAnalysis.SimulationHistory;
import SystemAnalysis.SimulationHistory.TrackedVariable;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * This class is used to analyze the properties of the state of a polymer
 * simulator. This class is mainly concerned with calculating the area and
 * perimeter of the region occupied by polymer, the amount of overlap a single
 * bead or the entire ensemble makes, the amount of polymer spring stretching a
 * single bead or the entire ensemble makes, and computing the energy of a
 * single bead or the whole ensemble of beads.
 *
 * @author bmoths
 */
public class SystemAnalyzer implements Serializable {

    /**
     * Used to allow a safe mode of communication between a system analyzer and
     * a class containing the bead positions to be analyzed. The system analyzer
     * gives an object of this class to another object, a bead position source
     * object, by a system analyzer. Then the bead position source object calls
     * the
     * <tt>setBeadPositions</tt> method. This will cause the bead position
     * getter object to remember the reference to the beadPositions array, and
     * this data may be seen by the system analyzer.
     */
    static public final class BeadPositionsGetter {

        private double[][] temporaryBeadPositions;

        private BeadPositionsGetter() {
        }

        /**
         * Causes this object to remember the bead positions which have been
         * given to it.
         *
         * @param beadPositions an array containing the position of beads.
         */
        public void setBeadPositions(double[][] beadPositions) {
            this.temporaryBeadPositions = beadPositions;
        }

    }

    /**
     * Listens for changes occurring in the bead positions so that the system
     * analyzer can update its internal state accordingly. It may be that only
     * one bead has moved or that many beads have moved. There are two different
     * methods, one for each case.
     */
    public final class AnalyzerListener implements Serializable {

        private static final long serialVersionUID = 0L;

        private AnalyzerListener() {
        }

        /**
         * If the geometry of the simulation has changed it is necessary for the
         * system analyzer to recompute its internal state. If many beads have
         * changed position, it may be more efficient to recompute internal
         * state from scratch rather than doing a series of updates.
         */
        public void recomputeInternalState() {
            beadBinner = new BeadBinner(beadPositions, systemGeometry);
        }

        /**
         * Updates the system analyzer to reflect the new position of the step
         * bead..
         *
         * @param stepBead The bead whose position has changed.
         */
        public void updateBinOfBead(int stepBead) {
            beadBinner.updateBeadPosition(stepBead, beadPositions[stepBead]);
        }

    }

    private static final long serialVersionUID = 0L;
    private final ImmutablePolymerState immutablePolymerState;
    private final ImmutableDiscretePolymerState immutableDiscretePolymerState;
    private final ImmutableSystemGeometry systemGeometry;
    private final double[][] beadPositions;
    private final EnergeticsConstants energeticsConstants;
    private final int numBeads;
    private BeadBinner beadBinner;

    //<editor-fold defaultstate="collapsed" desc="constructors">
    /**
     * Constructs a system analyzer which has a reference to an immutable view
     * of the mutable state of the polymer simulation as wells as the energetics
     * constants of the simulation, which is immutable.
     *
     * @param immutablePolymerState an immutable view of the mutable state of a
     * simulation
     * @param energeticsConstants the constants describing the interactions
     * between monomers in the simulation.
     */
    public SystemAnalyzer(ImmutablePolymerState immutablePolymerState,
            EnergeticsConstants energeticsConstants) {
        systemGeometry = immutablePolymerState.getImmutableSystemGeometry();
        this.immutablePolymerState = immutablePolymerState;
        immutableDiscretePolymerState = immutablePolymerState.getImmutableDiscretePolymerState();
        numBeads = immutableDiscretePolymerState.getNumBeads();
        this.energeticsConstants = energeticsConstants;
        BeadPositionsGetter beadPositionsGetter = new BeadPositionsGetter();
        immutablePolymerState.acceptBeadPositionGetter(beadPositionsGetter);
        beadPositions = beadPositionsGetter.temporaryBeadPositions;
        AnalyzerListener analyzerListener = new AnalyzerListener();
        immutablePolymerState.acceptAnalyzerListener(analyzerListener);
        beadBinner = new BeadBinner(beadPositions, systemGeometry);
    }

    /**
     * A copy constructor.
     *
     * @param systemAnalyzer the system analyzer to be copied.
     */
    public SystemAnalyzer(SystemAnalyzer systemAnalyzer) {
        systemGeometry = systemAnalyzer.systemGeometry;
        immutablePolymerState = systemAnalyzer.immutablePolymerState;
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
    public double findArea() {
        if (systemGeometry.getGeometricalParameters().getShape() == GeometricalParameters.Shape.SQUARE) {
            List<BeadRectangle> beadRectangles = systemGeometry.getRectanglesFromPositions(beadPositions);
            return GeometryAnalyzer.findAreaOfRectangles(beadRectangles);
        } else if (systemGeometry.getGeometricalParameters().getShape() == GeometricalParameters.Shape.CIRCLE) {
            Iterable<Circle> circles = systemGeometry.getCirclesFromPositions(beadPositions);
            return CircleAreaFinder.findAreaOfCircles(circles, systemGeometry.getBoundaryRectangle());
        } else {
            throw new AssertionError();
        }
    }

    public AreaPerimeter findAreaAndPerimeter() {
        if (systemGeometry.getGeometricalParameters().getShape() == GeometricalParameters.Shape.SQUARE) {
            RectanglesAndGluedPerimeter rectanglesAndGluedPerimeter;
            rectanglesAndGluedPerimeter = systemGeometry.getRectanglesAndPerimeterFromPositions(beadPositions);
            AreaPerimeter areaPerimeter;
            areaPerimeter = GeometryAnalyzer.findAreaAndPerimeterOfRectangles(rectanglesAndGluedPerimeter.beadRectangles);
            areaPerimeter.perimeter -= rectanglesAndGluedPerimeter.gluedPerimeter * 2;
            return areaPerimeter;
        } else if (systemGeometry.getGeometricalParameters().getShape() == GeometricalParameters.Shape.CIRCLE) {
            CirclesAndClippedPerimeter circlesAndClippedPerimeter = systemGeometry.getCirclesAndBoundaryPerimeterFromPosition(beadPositions);
            AreaPerimeter areaPerimeter = CircleAreaPerimeterFinder.findAreaPerimeterOfCirclesNoClipPerimeter(circlesAndClippedPerimeter.getCircles(), systemGeometry.getBoundaryRectangle());
            areaPerimeter.perimeter += circlesAndClippedPerimeter.getClippedPerimeter();
            return areaPerimeter;
        } else {
            throw new AssertionError();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="overlap and stretching">
    //<editor-fold defaultstate="collapsed" desc="stretching">
    /**
     * returns the total square distance resulting from stretched bonds between
     * adjacent monomers. This quantity is half the sum of all pairwise square
     * distances between adjacent monomers.
     *
     * @return the sum of square distances between adjacent monomers
     */
    public double totalSpringStretching() {
        double sqLength = 0;

        for (int bead = 0; bead < numBeads; bead++) {
            sqLength += beadStretching(bead);
        }

        return sqLength / 2; //divide by two since double counting
    }

    /**
     * returns the sum of square distances between a given bead and its
     * neighbors. For the purposes of this function. Summing the result of this
     * function over all beads would give twice the total spring stretching
     * between all pairs of beads, since each bond would be counted twice.
     *
     * @param bead the bead whose bonds' square length is to be determined
     * @return the sum of square lengths of all bonds attached to the given
     * bead.
     */
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
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="overlap">
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
//                AOverlap.increment(systemGeometry.twoBeadRectangularOverlap(beadPosition, beadPositions[currentBead]));
                AOverlap.increment(systemGeometry.twoBeadCircularOverlap(beadPosition, beadPositions[currentBead]));
            } else {
//                BOverlap.increment(systemGeometry.twoBeadRectangularOverlap(beadPosition, beadPositions[currentBead]));
                BOverlap.increment(systemGeometry.twoBeadCircularOverlap(beadPosition, beadPositions[currentBead]));
            }
        }

        return AreaOverlap.overlapOfBead(isTypeA(bead), AOverlap, BOverlap);
    }

    /**
     * Returns the area overlap between two given beads.
     *
     * @see TwoBeadOverlap
     * @param firstBead the first bead of the pair whose area overlap is to be
     * calculated
     * @param secondBead the second bead of the pair whose area overlap is to be
     * calculated
     * @return the area overlap between the two given beads
     */
    public AreaOverlap beadOverlap(int firstBead, int secondBead) {
        final TwoBeadOverlap AOverlap = new TwoBeadOverlap();
        final TwoBeadOverlap BOverlap = new TwoBeadOverlap();
        final double[] firstBeadPosition = beadPositions[firstBead];
        final double[] secondBeadPosition = beadPositions[secondBead];
        if (isTypeA(secondBead)) {
            AOverlap.increment(systemGeometry.twoBeadCircularOverlap(firstBeadPosition, secondBeadPosition));
        } else {
            BOverlap.increment(systemGeometry.twoBeadCircularOverlap(firstBeadPosition, secondBeadPosition));
        }
        return AreaOverlap.overlapOfBead(isTypeA(firstBead), AOverlap, BOverlap);
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="computing energy and entropy">
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

    public double beadDensityEnergy(int firstBead, int secondBead) {
        final AreaOverlap areaOverlap = beadOverlap(firstBead, secondBead);
        return energeticsConstants.densityEnergy(areaOverlap);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="entropy and energy entropy">
    public double computeEntropy() {
        return getNumBeads() * Math.log(systemGeometry.getVolume() / getNumBeads());
    }

    public EnergyEntropyChange computeEnergyEntropy() {
        return new EnergyEntropyChange(computeEnergy(), computeEntropy());
    }
    //</editor-fold>
    //</editor-fold>

    public List<Integer> getChainOfBead(int bead) {
        return immutableDiscretePolymerState.getChainOfBead(bead);
    }

    public double getIdealGasPressure() {
        return energeticsConstants.getTemperature() * getNumBeads() / systemGeometry.getVolume();
    }

    //<editor-fold defaultstate="collapsed" desc="getters">
    public Iterator<Integer> getNearbyBeadIterator(int bead) {
        return beadBinner.getNearbyBeadIterator(bead);
    }

    public ImmutableSystemGeometry getSystemGeometry() {
        return systemGeometry;
    }

    public EnergeticsConstants getEnergeticsConstants() {
        return energeticsConstants;
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

    /**
     * Returns the neighbor of the given bead in the given direction. The bead
     * is represented by its index, which is an integer. The direction may be
     * either 0 or 1, where 0 indicates left and 1 indicates right. If the bead
     * does not have a neighbor in the given direction, then -1 is output.
     *
     * @param bead the bead whose neighbor is to be found
     * @param direction the direction specifying which neighbors is to be found
     * @return the index of the neighbor if it exists, or -1 if it does not.
     */
    public int getNeighbor(int bead, int direction) {
        if (direction == 0) {
            return immutableDiscretePolymerState.getNeighborToLeftOfBead(bead);
        } else if (direction == 1) {
            return immutableDiscretePolymerState.getNeighborToRightOfBead(bead);
        }
        throw new IllegalArgumentException("direction must be 0 or 1");
    }

    public List<double[]> getEndToEndDisplacements() {
        return immutablePolymerState.getEndToEndDisplacements();
    }

    public double[] getBeadPosition(int bead) {
        final int numDimensions = beadPositions[0].length;
        double[] position = new double[numDimensions];
        System.arraycopy(beadPositions[bead], 0, position, 0, numDimensions);
        return position;
    }

    public double getBeadPositionComponent(int bead, int component) {
        return beadPositions[bead][component];
    }

    public boolean isTypeA(int bead) {
        return immutableDiscretePolymerState.isTypeA(bead);
    }
    //</editor-fold>
}
