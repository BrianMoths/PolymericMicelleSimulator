/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.SystemGeometry.AreaOverlap;
import Engine.SystemGeometry.SystemGeometry;
import SystemAnalysis.BeadRectangle;
import SystemAnalysis.GeometryAnalyzer;
import SystemAnalysis.GeometryAnalyzer.AreaPerimeter;
import SystemAnalysis.RectanglesAndPerimeter;
import SystemAnalysis.SimulationHistory;
import SystemAnalysis.SimulationHistory.TrackedVariable;
import SystemAnalysis.SurfaceTensionFinder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class SystemAnalyzer implements Serializable {

    //create a multivariate function whose arguments are the fit parameters and whose outputs are the function values
    //use apache leastSquaresConverter and this vector valued function to a scalar function giving residuals
    //minimize this function using one of the optimization routines.
    static private final int statisticsWindow = 1000;
    private final int[][] neighbors;
    private final SystemGeometry systemGeometry;
    private final PhysicalConstants physicalConstants;
    private final int numBeads, numABeads;
    private BeadBinner beadBinner;
    private double[][] beadPositions;
    private SimulationHistory simulationHistory;
    private SurfaceTensionFinder surfaceTensionFinder;

    public SystemAnalyzer(SystemGeometry systemGeometry,
            PolymerCluster polymerCluster,
            PhysicalConstants physicalConstants) {
        this.systemGeometry = systemGeometry;
        this.physicalConstants = physicalConstants;
        neighbors = polymerCluster.makeNeighbors();
        numBeads = polymerCluster.getNumBeads();
        numABeads = polymerCluster.getNumABeads();
        beadPositions = new double[numBeads][systemGeometry.getDimension()];
        beadBinner = new BeadBinner(beadPositions, systemGeometry);
        simulationHistory = new SimulationHistory(statisticsWindow);
        surfaceTensionFinder = new SurfaceTensionFinder(this);
    }

    public SystemAnalyzer(SystemAnalyzer systemAnalyzer) {
        systemGeometry = systemAnalyzer.systemGeometry;
        neighbors = systemAnalyzer.neighbors;
        physicalConstants = systemAnalyzer.physicalConstants;
        numBeads = systemAnalyzer.numBeads;
        numABeads = systemAnalyzer.numABeads;
        beadPositions = new double[systemAnalyzer.numBeads][systemGeometry.getDimension()];
        systemGeometry.checkedCopyPositions(systemAnalyzer.beadPositions, beadPositions);
        beadBinner = new BeadBinner(systemAnalyzer.beadBinner); //check this
        surfaceTensionFinder = new SurfaceTensionFinder(systemAnalyzer.surfaceTensionFinder);
    }

    private void rebinBeads() {
        beadBinner = new BeadBinner(beadPositions, systemGeometry);
    }

    public double findArea() {
        List<BeadRectangle> beadRectangles = makeBeadRectangles();
        return GeometryAnalyzer.findArea(beadRectangles);
    }

    public void recordSurface() {
        surfaceTensionFinder.recordSurfaceTension();
    }

    public double estimateSurfaceTension() {
        return surfaceTensionFinder.findSurfaceTension();
    }

    public double findRightEdge(double yValue) {
        final double halfWidth = systemGeometry.getParameters().getInteractionLength() / 2;
        final double lowerCutoff = yValue - halfWidth;
        final double upperCutoff = yValue + halfWidth;
        double rightEdge = 0;
        for (int bead = 0; bead < numBeads; bead++) {
            final double beadY = beadPositions[bead][1];
            if (beadY > lowerCutoff && beadY < upperCutoff) {
                final double beadX = beadPositions[bead][0];
                if (beadX > rightEdge) {
                    rightEdge = beadX;
                }
            }
        }
        return rightEdge;
    }

    public AreaPerimeter findAreaAndPerimeter() {
        RectanglesAndPerimeter rectanglesAndPerimeter;
        rectanglesAndPerimeter = systemGeometry.getRectanglesAndPerimeterFromPositions(beadPositions);
        AreaPerimeter areaPerimeter;
        areaPerimeter = GeometryAnalyzer.findAreaAndPerimeter(rectanglesAndPerimeter.beadRectangles);
        areaPerimeter.perimeter -= rectanglesAndPerimeter.gluedPerimeter * 2;
        return areaPerimeter;
    }

    private List<BeadRectangle> makeBeadRectangles() {
        List<BeadRectangle> beadRectangles = new ArrayList<>(numBeads);
        for (int bead = 0; bead < numBeads; bead++) {
            final double x = beadPositions[bead][0];
            final double y = beadPositions[bead][0];
            final double halfwidth = systemGeometry.getParameters().getInteractionLength() / 2;
            beadRectangles.add(new BeadRectangle(x - halfwidth, x + halfwidth, y + halfwidth, y - halfwidth));
        }
        return beadRectangles;
    }

    public void addPerimeterAreaEnergySnapshot(double perimeter, double area, double energy) {
        simulationHistory.addValue(SimulationHistory.TrackedVariable.PERIMETER, perimeter);
        simulationHistory.addValue(SimulationHistory.TrackedVariable.AREA, area);
        simulationHistory.addValue(SimulationHistory.TrackedVariable.ENERGY, energy);
    }

    public double getAverage(TrackedVariable trackedVariable) {
        return simulationHistory.getAverage(trackedVariable);
    }

    public double totalSpringStretching() {
        double sqLength = 0;

        for (int bead = 0; bead < numBeads; bead++) {
            sqLength += beadStretching(bead);
        }

        return sqLength / 2; //divide by two since double counting
    }

    public double beadStretching(int bead) {
        double sqLength = 0;
        for (int direction = 0; direction < 2; direction++) {
            int neighborIndex = neighbors[bead][direction];
            if (neighborIndex >= 0) {
                sqLength += systemGeometry.sqDist(beadPositions[bead], beadPositions[neighborIndex]);
            }
        }
        return sqLength;
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
        Iterator<Integer> nearbyBeadIterator = beadBinner.getNearbyBeadIterator(beadPosition);
        while (nearbyBeadIterator.hasNext()) {
            final int currentBead = nearbyBeadIterator.next();
            if (currentBead < numABeads) {
                AOverlap.increment(systemGeometry.twoBeadOverlap(beadPosition, beadPositions[currentBead]));
            } else {
                BOverlap.increment(systemGeometry.twoBeadOverlap(beadPosition, beadPositions[currentBead]));
            }
        }

        return AreaOverlap.overlapOfBead(isTypeA(bead), AOverlap, BOverlap);
    }

    public double energy() {
        return springEnergy() + densityEnergy();
    }

    public double springEnergy() {
        double sqLength = totalSpringStretching();

        return physicalConstants.springEnergy(sqLength);
    }

    public double densityEnergy() {
        AreaOverlap overlap = totalOverlap();

        return physicalConstants.densityEnergyWithCore(overlap);
    }

    public double beadEnergy(int bead) {
        return beadSpringEnergy(bead) + beadDensityEnergy(bead);
    }

    public double beadSpringEnergy(int bead) {
        final double beadStretching = beadStretching(bead);

        return physicalConstants.springEnergy(beadStretching);
    }

    public double beadDensityEnergy(int bead) {
        final AreaOverlap overlap = beadOverlap(bead);

        return physicalConstants.densityEnergyWithCore(overlap);
    }

    public boolean isTypeA(int bead) {
        return bead < numABeads;
    }

    public SimulationHistory getSimulationHistory() {
        return simulationHistory;
    }

    public void setBeadPositions(double[][] beadPositions) {
        this.beadPositions = beadPositions;
        rebinBeads();
    }

    void updateBinWithMove(int stepBead) {
        beadBinner.updateBeadPosition(stepBead, beadPositions[stepBead]);
    }

    public List<Integer> getChainOfBead(int bead) {
        List<Integer> chain = new ArrayList<>();
        chain.add(bead);
        addBeadsLeftToChain(bead, chain);
        addBeadsRightToChain(bead, chain);

        return chain;
    }

    private void addBeadsLeftToChain(int bead, List<Integer> chain) {
        int nextBead = getBeadToLeft(bead);
        while (nextBead != -1) {
            chain.add(nextBead);
            nextBead = getBeadToLeft(nextBead);
        }
    }

    private int getBeadToLeft(int bead) {
        return neighbors[bead][0];
    }

    private void addBeadsRightToChain(int bead, List<Integer> chain) {
        int nextBead = getBeadToRight(bead);
        while (nextBead != -1) {
            chain.add(nextBead);
            nextBead = getBeadToRight(nextBead);
        }
    }

    private int getBeadToRight(int bead) {
        return neighbors[bead][1];
    }

    public SystemGeometry getSystemGeometry() {
        return systemGeometry;
    }

    public PhysicalConstants getPhysicalConstants() {
        return physicalConstants;
    }

    public int getNumBeads() {
        return numBeads;
    }

    public int getNumABeads() {
        return numABeads;
    }

    public int getNeighbor(int bead, int direction) {
        return neighbors[bead][direction];
    }

    public double getBeadPositionComponent(int bead, int component) {
        return beadPositions[bead][component];
    }

    public SurfaceTensionFinder getSurfaceTensionFinder() {
        return surfaceTensionFinder;
    }
}
