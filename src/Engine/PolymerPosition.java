/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.SystemGeometry.AreaOverlap;
import Engine.SystemGeometry.SystemGeometry;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

/**
 *
 * @author bmoths
 */
public class PolymerPosition {

    private static final Random randomNumberGenerator = new Random();
    private final int numBeads, numABeads;
    private final int[][] neighbors;
    private final SystemGeometry systemGeometry;
    private BeadBinner beadBinner;
    private double[][] beadPositions;
//    private Graphics graphics;
    private SimulationStep simulationStep;

    public PolymerPosition(PolymerCluster polymerCluster, SystemGeometry systemGeometry) {
        numBeads = polymerCluster.getNumBeads();
        numABeads = polymerCluster.getNumABeads();
        neighbors = polymerCluster.makeNeighbors();

        this.systemGeometry = systemGeometry;

        simulationStep = new SimulationStep();

        randomizePrivate();
    }

    public PolymerPosition(PolymerPosition polymerPosition) {
        numBeads = polymerPosition.numBeads;
        numABeads = polymerPosition.numABeads;
        neighbors = polymerPosition.neighbors;
        systemGeometry = polymerPosition.systemGeometry;
        beadBinner = new BeadBinner(polymerPosition.beadBinner);
        beadPositions = polymerPosition.getBeadPositions();
        simulationStep = new SimulationStep(polymerPosition.simulationStep);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Number of Beads: ").append(Integer.toString(numBeads)).append("\n");
        stringBuilder.append("Number of A Beads: ").append(Integer.toString(numABeads)).append("\n");
        stringBuilder.append("Number of B Beads: ").append(Integer.toString(numBeads - numABeads)).append("\n");
        stringBuilder.append("List of bead positions: \n ").append(beadPositionString(beadPositions)).append("\n");
        stringBuilder.append("Array specifying each beads backward and forward neighbor on the chain: \n ").append(neighborString(neighbors)).append("\n");
        stringBuilder.append("Last step considered: \n").append(simulationStep.toString()).append("\n");
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

    public void randomize() {
        randomizePrivate();
    }

    private void randomizePrivate() {
        beadPositions = systemGeometry.randomPositions(numBeads);
        beadBinner = new BeadBinner(beadPositions, systemGeometry);
    }

    public int randomBeadIndex() {
        return randomNumberGenerator.nextInt(numBeads);
    }

    public void setStep(int stepBead, double[] stepVector) {
        simulationStep.setStepBead(stepBead);
        simulationStep.setInitialPosition(beadPositions[stepBead]);
        systemGeometry.incrementFirstVector(stepVector, beadPositions[stepBead]); //this is a really bad idea
        simulationStep.setFinalPosition(stepVector);
        beadBinner.setStep(simulationStep);
    }

    public boolean isStepInBounds() {
        return systemGeometry.isPositionValid(simulationStep.getFinalPosition());
    }

    public boolean isPositionValid(int stepBead) {
        return systemGeometry.isPositionValid(beadPositions[stepBead]);
    }

    public void doStep() {
        beadPositions[simulationStep.getStepBead()] = simulationStep.getFinalPosition();
        beadBinner.doStep();
    }

    public void doStep(int stepBead, double[] stepVector) {
        systemGeometry.incrementFirstVector(beadPositions[stepBead], stepVector);
    }

    public void undoStep() {
        beadPositions[simulationStep.getStepBead()] = simulationStep.getInitialPosition();
        beadBinner.undoStep();
    }

    public void undoStep(int stepBead, double[] stepVector) {
        systemGeometry.decrementFirstVector(beadPositions[stepBead], stepVector);
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
            overlap.incrementBy(beadOverlapWithHardCore(i));
        }

        overlap.halve(); //divide by two since double counting

        return overlap;
    }

    public AreaOverlap beadOverlap(int bead) {
        double AOverlap = 0, BOverlap = 0;
        final double[] beadPosition = beadPositions[bead];
        Iterator<Integer> nearbyBeadIterator = beadBinner.getNearbyBeadIterator(beadPosition);
        while (nearbyBeadIterator.hasNext()) {
            final int currentBead = nearbyBeadIterator.next();
            //System.out.println(String.valueOf(currentBead));
            if (currentBead < numABeads) {
                AOverlap += systemGeometry.areaOverlap(beadPosition, beadPositions[currentBead]);
            } else {
                BOverlap += systemGeometry.areaOverlap(beadPosition, beadPositions[currentBead]);
            }
        }

        return AreaOverlap.overlapOfBead(isTypeA(bead), AOverlap, BOverlap);
    }

    public AreaOverlap beadOverlapWithHardCore(int bead) {
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

        return AreaOverlap.overlapOfBeadWithCore(isTypeA(bead), AOverlap, BOverlap);
    }

    public double sqLengthChange() {
        double initialSqLength = stepBeadSpringStretching();
        doStep();
        double finalSqLength = stepBeadSpringStretching();
        undoStep();
        return finalSqLength - initialSqLength;
    }

    public double stepBeadSpringStretching() {
        return beadStretching(simulationStep.getStepBead());
    }

    public AreaOverlap overlapChange() {
        AreaOverlap initialAreaOverlap = stepBeadOverlap();

        doStep();
        AreaOverlap finalAreaOverlap = stepBeadOverlap();

        undoStep();

        return AreaOverlap.subtract(finalAreaOverlap, initialAreaOverlap);
    }

    public AreaOverlap stepBeadOverlap() {
        return beadOverlapWithHardCore(simulationStep.getStepBead());
    }

//    public void setGraphics(Graphics inGraphics) {
//        graphics = inGraphics;
//    }
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
    }

    public int getNumBeads() {
        return numBeads;
    }

    public void setBeadPositions(double[][] beadPositions) {
//        if (beadPositions.length != numBeads) {
//            throw new IllegalArgumentException("Unable to set bead position. Wrong number of beads!");
//        }
//        for (int bead = 0; bead < numBeads; bead++) {
//            if (beadPositions[bead].length != systemGeometry.getDimension()) {
//                throw new IllegalArgumentException("Unable to set bead position. Each bead postion must have length equal to number of dimensions: " + systemGeometry.getDimension() + "!");
//            }
//            for (int j = 0; j < 10; j++) {
//                if (!systemGeometry.isPositionValid(beadPositions[bead])) {
//                    throw new IllegalArgumentException("One of the bead positions is not valid");
//                }
//            }
//        }
//
//        for (int bead = 0; bead < numBeads; bead++) {
//            System.arraycopy(beadPositions[bead], 0, this.beadPositions[bead], 0, systemGeometry.getDimension());
//        }

        systemGeometry.checkedCopyPositions(beadPositions, this.beadPositions);
    }

    public double[][] getBeadPositions() {
        double[][] beadPositionsCopy = new double[numBeads][systemGeometry.getDimension()];
        for (int bead = 0; bead < numBeads; bead++) {
            System.arraycopy(beadPositions[bead], 0, beadPositionsCopy[bead], 0, systemGeometry.getDimension());
        }
        return beadPositionsCopy;
    }

    public boolean isTypeA(int bead) {
        return bead < numABeads;
    }
}
