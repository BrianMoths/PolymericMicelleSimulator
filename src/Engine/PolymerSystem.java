/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

/**
 *
 * @author brian
 */
public class PolymerSystem {

    private static final Random randomNumberGenerator;
    private final int numBeads, numABeads;
    private final int[][] neighbors;
    private double energy;
    private double[] beadEnergy;
    private double[][] beadPositions;
    private int iterationNumber;
    private Graphics graphics;
    private SystemGeometry geometry;
    private PhysicalConstants physicalConstants;

    static {
        randomNumberGenerator = new Random();
    }

    public PolymerSystem() {
        numBeads = 100;
        numABeads = numBeads / 2;

        neighbors = new int[numBeads][2];

        neighbors[0][0] = -1;
        neighbors[0][1] = 1;
        for (int i = 0, N = numBeads - 1; i < N; i++) {
            neighbors[i][0] = i - 1;
            neighbors[i][1] = i + 1;
        }
        neighbors[numBeads - 1][0] = numBeads - 2;
        neighbors[numBeads - 1][1] = -1;

        iterationNumber = 0;
        beadEnergy = new double[numBeads];

        makeGeometry();

        makeParameters();

        beadPositions = geometry.randomPositions(numBeads);
        energy = energy();
    }

    private void makeGeometry() {
        int dimension = 2;
        geometry = new SystemGeometry();
        geometry.setDimension(dimension);
        for (int i = 0; i < dimension; i++) {
            geometry.setDimensionSize(i, 20);
        }
    }

    private void makeParameters() {
        double interactionLength = 5;
        double stepLength = interactionLength / 2;
        SimulationParameters parameters;
        parameters = new SimulationParameters();
        parameters.setInteractionLength(interactionLength);
        parameters.setStepLength(stepLength);
        geometry.setParameters(parameters);
    }

    private void makePhysicalConstants() {
        double temperature, similarOverlapCoefficient, differentOverlapCoefficient, springCoefficient;
        physicalConstants = new PhysicalConstants();
        temperature = 300;
        similarOverlapCoefficient = 1;
        differentOverlapCoefficient = 4;
        springCoefficient = 40;
        physicalConstants.setTemperature(temperature);
        physicalConstants.setDifferentOverlapCoefficient(differentOverlapCoefficient);
        physicalConstants.setSimilarOverlapCoefficient(similarOverlapCoefficient);
        physicalConstants.setSpringCoefficient(springCoefficient);
    }

    public PolymerSystem(SystemGeometry systemGeometry,
            PolymerCluster polymerCluster,
            PhysicalConstants physicalConstants,
            SimulationParameters simulationParameters) {

        numBeads = polymerCluster.getNumBeads();
        numABeads = polymerCluster.getNumABeads();
        neighbors = polymerCluster.makeNeighbors();

        geometry = systemGeometry;
        geometry.setParameters(simulationParameters);

        this.physicalConstants = physicalConstants;

        iterationNumber = 0;
        beadEnergy = new double[numBeads];

        beadPositions = geometry.randomPositions(numBeads);
        energy = energy();
    }

    public void randomizePositions() {
        beadPositions = geometry.randomPositions(numBeads);
        energy = energy();
    }

    public void doIterations(int n) {
        for (int i = 0; i < n; i++) {
            doIteration();
        }
    }

    public void doIteration() {
        iterationNumber++;
        final int stepBead = randomNumberGenerator.nextInt(numBeads);
        final double[] stepVector = geometry.randomGaussian();


        geometry.doStep(beadPositions[stepBead], stepVector);
        boolean isStepInBounds = geometry.isPositionValid(beadPositions[stepBead]);

        if (isStepInBounds) {
            final double newEnergy = energy();
            final double energyChange = newEnergy - energy;

            if (physicalConstants.isEnergeticallyAllowed(energyChange)) {
                energy = newEnergy;
            } else {
                geometry.undoStep(beadPositions[stepBead], stepVector);
            }
        }
    }

    private void updateBeadEnergy(int beadIndex, double[] stepVector) {
        updateBeadDensityEnergy(beadIndex, stepVector);
        updateBeadSpringEnergy(beadIndex, stepVector);
    }

    private void updateBeadDensityEnergy(int beadIndex, double[] stepVector) {
//        double[] initialAreaOverlap = new double[numBeads];
//        double[] finalAreaOverlap = new double[numBeads];
//
//        for (int i = 0; i < numBeads; i++) {
//            initialAreaOverlap[i] = geometry.areaOverlap(beadPositions[beadIndex], beadPositions[i]);
//        }
//        geometry.doStep(beadPositions[beadIndex], stepVector);
//
//        for (int i = 0; i < numBeads; i++) {
//            finalAreaOverlap[i] = geometry.areaOverlap(beadPositions[beadIndex], beadPositions[i]);
//        }
//
//        double energyChange;
//        double overlapCoefficientA, overlapCoefficientB;
//        if (beadIndex < numABeads) {
//            overlapCoefficientA = similarOverlapCoefficient;
//            overlapCoefficientB = differentOverlapCoefficient;
//        } else {
//            overlapCoefficientB = similarOverlapCoefficient;
//            overlapCoefficientA = differentOverlapCoefficient;
//        }
//        for (int i = 0; i < numABeads; i++) {
//            energyChange = overlapCoefficientA * (finalAreaOverlap[i] - initialAreaOverlap[i]);
//            beadEnergy[i] += energyChange;
//            beadEnergy[beadIndex] += energyChange;
//        }
//        for (int i = numABeads; i < numBeads; i++) {
//            energyChange = overlapCoefficientB * (finalAreaOverlap[i] - initialAreaOverlap[i]);
//            beadEnergy[i] += energyChange;
//            beadEnergy[beadIndex] += energyChange;
//        }
//
//        geometry.undoStep(beadPositions[beadIndex], stepVector);
    }

    private void updateBeadSpringEnergy(int beadIndex, double[] stepVector) {
    }

    private double energy() {
        return springEnergy() + densityEnergy();
    }

    private double springEnergy() {
        double sqLength = 0;

        for (int i = 0; i < numBeads; i++) {
            for (int j = 0; j < 2; j++) {
                int neighborIndex = neighbors[i][j];
                if (neighborIndex >= 0) {
                    sqLength += geometry.sqDist(beadPositions[i], beadPositions[neighborIndex]);
                }
            }
        }

        return physicalConstants.springEnergy(sqLength);
    }

    private double densityEnergy() {
        double similarOverlap = 0, differentOverlap = 0;

        for (int i = 0; i < numABeads; i++) {
            for (int j = 0; j < numABeads; j++) {
                similarOverlap += geometry.areaOverlap(beadPositions[i], beadPositions[j]);
            }
        }

        for (int i = numABeads; i < numBeads; i++) {
            for (int j = numABeads; j < numBeads; j++) {
                similarOverlap += geometry.areaOverlap(beadPositions[i], beadPositions[j]);
            }
        }


        for (int i = 0; i < numABeads; i++) {
            for (int j = numABeads; j < numBeads; j++) {
                differentOverlap += geometry.areaOverlap(beadPositions[i], beadPositions[j]);
            }
        }

        return physicalConstants.densityEnergy(similarOverlap, differentOverlap);
    }

// <editor-fold defaultstate="collapsed" desc="getters">
    public int getNumBeads() {
        return numBeads;
    }

    public int getNumABeads() {
        return numABeads;
    }

    public double getEnergy() {
        return energy;
    }

    public int getIterationNumber() {
        return iterationNumber;
    }
    // </editor-fold>

    public void setGraphics(Graphics inGraphics) {
        graphics = inGraphics;
    }

    public void draw() {
        if (graphics == null) {
            return;
        }

        if (geometry.getDimension() != 2) {
            return;
        }

        double scaleFactor = 600 / geometry.getRMax()[0];

        int diameter = (int) Math.round(geometry.getParameters().getInteractionLength() * scaleFactor) / 5;
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
}
