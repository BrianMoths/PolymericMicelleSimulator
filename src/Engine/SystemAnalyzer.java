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

    public void draw(final Graphics graphics) {
        if (graphics == null) {
            return;
        }

        if (systemGeometry.getDimension() != 2) {
            return;
        }

        final int displaySize = 600;

        final double scaleFactor = displaySize / systemGeometry.getRMax()[0];

        final int diameter = (int) Math.round(systemGeometry.getParameters().getInteractionLength() * scaleFactor); //make diameter smaller
        final int radius = diameter / 2;

        graphics.clearRect(0, 0, displaySize, displaySize);//fix this later

        class Drawer {

            private void draw() {
                drawBeads();
                drawBonds();
            }

            private void drawBeads() {
                drawABeads();
                drawBBeads();
            }

            private void drawABeads() {
                graphics.setColor(Color.RED);
                for (int i = 0; i < numABeads; i++) {
                    drawBead(i);
                }
            }

            private void drawBBeads() {
                graphics.setColor(Color.BLUE);
                for (int i = numABeads; i < numBeads; i++) {
                    drawBead(i);
                }
            }

            private void drawBead(int i) {
                Point point = beadCenterPixel(i);
                graphics.fillRect(point.x - radius, point.y - radius, diameter, diameter);
            }

            private void drawBonds() {
                graphics.setColor(Color.BLACK);
                for (int bead1 = 0; bead1 < numBeads; bead1++) {
                    for (int bondDirection = 0; bondDirection < 2; bondDirection++) {
                        int bead2 = neighbors[bead1][bondDirection];
                        if (bead2 > bead1) { //tests for bead2!=-1 (bead1 has no neighbor in that direction) *and* makes sure to draw each bond only once
                            drawBond(bead1, bead2);
                        }
                    }
                }
            }

            private void drawBond(int bead1, int bead2) {
                Point point1 = beadCenterPixel(bead1);
                Point point2 = beadCenterPixel(bead2);

                if (isCloseEnough(point1, point2)) {
                    graphics.drawLine(point1.x, point1.y, point2.x, point2.y);
                }
            }

            private boolean isCloseEnough(Point point1, Point point2) {
                return Math.abs(point1.x - point2.x) < displaySize / 2 && Math.abs(point1.y - point2.y) < displaySize / 2;
            }

            private Point beadCenterPixel(int i) {
                return new Point((int) Math.round(beadPositions[i][0] * scaleFactor),
                        (int) Math.round(beadPositions[i][1] * scaleFactor));
            }
        }

        Drawer drawer = new Drawer();
        drawer.draw();
        if (surfaceTensionFinder != null) {
            surfaceTensionFinder.drawDots(graphics);
        }
    }

    void updateBinWithMove(int stepBead) {
        beadBinner.updateBeadPosition(stepBead, beadPositions[stepBead]);
    }

    public SystemGeometry getSystemGeometry() {
        return systemGeometry;
    }

    public PhysicalConstants getPhysicalConstants() {
        return physicalConstants;
    }
}
