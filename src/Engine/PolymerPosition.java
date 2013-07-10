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
    private Graphics graphics;
    private SimulationStep simulationStep;

    public PolymerPosition(PolymerCluster polymerCluster, SystemGeometry systemGeometry) {
        numBeads = polymerCluster.getNumBeads();
        numABeads = polymerCluster.getNumABeads();
        neighbors = polymerCluster.makeNeighbors();

        this.systemGeometry = systemGeometry;

        simulationStep = new SimulationStep();

        randomizePrivate();
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

        for (int i = 0; i < numBeads; i++) {
            for (int j = 0; j < 2; j++) {
                int neighborIndex = neighbors[i][j];
                if (neighborIndex >= 0) {
                    sqLength += systemGeometry.sqDist(beadPositions[i], beadPositions[neighborIndex]);
                }
            }
        }

        return sqLength / 2; //divide by two since double counting
    }

    public double totalSimilarOverlap() {
        double similarOverlap = 0;

        for (int i = 0; i < numABeads; i++) {
            for (int j = 0; j < numABeads; j++) {
                similarOverlap += systemGeometry.areaOverlap(beadPositions[i], beadPositions[j]);
            }
        }

        for (int i = numABeads; i < numBeads; i++) {
            for (int j = numABeads; j < numBeads; j++) {
                similarOverlap += systemGeometry.areaOverlap(beadPositions[i], beadPositions[j]);
            }
        }

        return similarOverlap / 2;
    }

    public double totalDifferentOverlap() {
        double differentOverlap = 0;

        for (int i = 0; i < numABeads; i++) {
            for (int j = numABeads; j < numBeads; j++) {
                differentOverlap += systemGeometry.areaOverlap(beadPositions[i], beadPositions[j]);
            }
        }

        return differentOverlap;
    }

    public double sqLengthChange() {
        double sqLengthChange = stepBeadSpringStretching();
        doStep();
        sqLengthChange = stepBeadSpringStretching() - sqLengthChange;
        undoStep();
        return sqLengthChange;
    }

    public double stepBeadSpringStretching() {
        double sqLength = 0;

        for (int direction = 0; direction < 2; direction++) {
            int neighborIndex = neighbors[simulationStep.getStepBead()][direction];
            if (neighborIndex >= 0) {
                sqLength += systemGeometry.sqDist(beadPositions[simulationStep.getStepBead()], beadPositions[neighborIndex]);
            }
        }

        return sqLength;
    }

    public AreaOverlap overlapChange() {
        AreaOverlap initialAreaOverlap = stepBeadOverlap();

        doStep();
        AreaOverlap finalAreaOverlap = stepBeadOverlap();

        undoStep();

        return AreaOverlap.subtract(finalAreaOverlap, initialAreaOverlap);
    }

    public AreaOverlap stepBeadOverlap() {
        double AOverlap = 0, BOverlap = 0;
        final double[] stepBeadPosition = beadPositions[simulationStep.getStepBead()];
        Iterator<Integer> nearbyBeadIterator = beadBinner.getNearbyBeadIterator();
        while (nearbyBeadIterator.hasNext()) {
            final int currentBead = nearbyBeadIterator.next();
            //System.out.println(String.valueOf(currentBead));
            if (currentBead < numABeads) {
                AOverlap += systemGeometry.areaOverlap(stepBeadPosition, beadPositions[currentBead]);
            } else {
                BOverlap += systemGeometry.areaOverlap(stepBeadPosition, beadPositions[currentBead]);
            }
        }

        AreaOverlap areaOverlap = new AreaOverlap();
        if (isTypeA(simulationStep.getStepBead())) {
            areaOverlap.AAOverlap = AOverlap;
            areaOverlap.ABOverlap = BOverlap;
        } else {
            areaOverlap.ABOverlap = AOverlap;
            areaOverlap.BBOverlap = BOverlap;
        }

        return areaOverlap;
    }

    public void setGraphics(Graphics inGraphics) {
        graphics = inGraphics;
    }

    public void draw() {
        if (graphics == null) {
            return;
        }

        if (systemGeometry.getDimension() != 2) {
            return;
        }

        final int displaySize = 600;

        final double scaleFactor = displaySize / systemGeometry.getRMax()[0];

        final int diameter = (int) Math.round(systemGeometry.getParameters().getInteractionLength() * scaleFactor) / 5;
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

    public boolean isTypeA(int bead) {
        return bead < numABeads;
    }
}
