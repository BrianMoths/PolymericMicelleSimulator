/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.SystemGeometry.HardWallSystemGeometry;
import Engine.SystemGeometry.PeriodicSystemGeometry;
import Engine.SystemGeometry.SystemGeometry;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

/**
 *
 * @author bmoths
 */
public class PolymerPosition {

    private static final Random randomNumberGenerator = new Random();
    private final int numBeads, numABeads;
    private final int[][] neighbors;
    private double[][] beadPositions;
    private SystemGeometry systemGeometry;
    private Graphics graphics;
    private int stepBead;
    private double[] stepVector;

//    public PolymerPosition() {
//        PolymerCluster polymerCluster = new PolymerCluster();
//
//        numBeads = polymerCluster.getNumBeads();
//        numABeads = polymerCluster.getNumABeads();
//        neighbors = polymerCluster.makeNeighbors();
//
////        systemGeometry = new HardWallSystemGeometry();
//        systemGeometry = new PeriodicSystemGeometry();
//
//        randomizePrivate();
//    }
    public PolymerPosition(PolymerCluster polymerCluster, SystemGeometry systemGeometry) {
        numBeads = polymerCluster.getNumBeads();
        numABeads = polymerCluster.getNumABeads();
        neighbors = polymerCluster.makeNeighbors();

        this.systemGeometry = systemGeometry;

        randomizePrivate();
    }

    private void randomizePrivate() {
        beadPositions = systemGeometry.randomPositions(numBeads);
    }

    public void randomize() {
        randomizePrivate();
    }

    public int randomBeadIndex() {
        return randomNumberGenerator.nextInt(numBeads);
    }

    public void setStep(int stepBead, double[] stepVector) {
        this.stepBead = stepBead;
        this.stepVector = stepVector;
    }

    public boolean isStepInBounds() {
        return systemGeometry.isSumInBounds(beadPositions[stepBead], stepVector);
    }

    public boolean isPositionValid(int stepBead) {
        return systemGeometry.isPositionValid(beadPositions[stepBead]);
    }

    public void doStep() {
        systemGeometry.doStep(beadPositions[stepBead], stepVector);
    }

    public void doStep(int stepBead, double[] stepVector) {
        systemGeometry.doStep(beadPositions[stepBead], stepVector);
    }

    public void undoStep() {
        systemGeometry.undoStep(beadPositions[stepBead], stepVector);
    }

    public void undoStep(int stepBead, double[] stepVector) {
        systemGeometry.undoStep(beadPositions[stepBead], stepVector);
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

        return sqLength;
    }

    public double stepBeadSpringStretching() {
        double sqLength = 0;

        for (int direction = 0; direction < 2; direction++) {
            int neighborIndex = neighbors[stepBead][direction];
            if (neighborIndex >= 0) {
                sqLength += systemGeometry.sqDist(beadPositions[stepBead], beadPositions[neighborIndex]);
            }
        }

        return sqLength;
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

        return similarOverlap;
    }

    public double totalDifferentOverlap() {
        double differentOverlap = 0;

        for (int i = 0; i < numABeads; i++) {
            for (int j = numABeads; j < numBeads; j++) {
                differentOverlap += systemGeometry.areaOverlap(beadPositions[i], beadPositions[j]);
            }
        }

        return 2 * differentOverlap;
    }

    public double stepBeadSimilarOverlap() {
        double similarOverlap = 0;
        int begin, end;

        if (isTypeA(stepBead)) {
            begin = 0;
            end = numABeads;
        } else {
            begin = numABeads;
            end = numBeads;
        }

        for (int bead = begin; bead < end; bead++) {
            similarOverlap += systemGeometry.areaOverlap(beadPositions[stepBead], beadPositions[bead]);
        }

        return similarOverlap;
    }

    public double stepBeadDifferentOverlap() {
        double differentOverlap = 0;
        int begin, end;

        if (isTypeA(stepBead)) {
            begin = numABeads;
            end = numBeads;
        } else {
            begin = 0;
            end = numABeads;
        }

        for (int bead = begin; bead < end; bead++) {
            differentOverlap += systemGeometry.areaOverlap(beadPositions[stepBead], beadPositions[bead]);
        }

        return differentOverlap;
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

        double scaleFactor = 600 / systemGeometry.getRMax()[0];

        int diameter = (int) Math.round(systemGeometry.getParameters().getInteractionLength() * scaleFactor) / 2;
        int radius = diameter / 2;

        graphics.clearRect(0, 0, 600, 600);//fix this later

        graphics.setColor(Color.RED);
        for (int i = 0; i < numABeads; i++) {
            graphics.fillRect((int) Math.round(beadPositions[i][0] * scaleFactor) - radius,
                    (int) Math.round(beadPositions[i][1] * scaleFactor) - radius,
                    diameter,
                    diameter);
        }

        graphics.setColor(Color.BLUE);
        for (int i = numABeads; i < numBeads; i++) {
            graphics.fillRect((int) Math.round(beadPositions[i][0] * scaleFactor) - radius,
                    (int) Math.round(beadPositions[i][1] * scaleFactor) - radius,
                    diameter,
                    diameter);
        }

        graphics.setColor(Color.BLACK);
        for (int i = 0; i < numBeads; i++) {
            for (int j = 0; j < 2; j++) {
                int neighborIndex = neighbors[i][j];
                if (neighborIndex >= i) {
                    graphics.drawLine((int) Math.round(beadPositions[i][0] * scaleFactor),
                            (int) Math.round(beadPositions[i][1] * scaleFactor),
                            (int) Math.round(beadPositions[neighborIndex][0] * scaleFactor),
                            (int) Math.round(beadPositions[neighborIndex][1] * scaleFactor));
                }
            }
        }
    }

    public int getNumBeads() {
        return numBeads;
    }

    public boolean isTypeA(int bead) {
        return bead < numABeads;
    }
}
