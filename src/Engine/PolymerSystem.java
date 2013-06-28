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
    private final int dimension, numBeads, numABeads;//maybe package private
    private final double[] rMax;
    private final int[][] neighbors;
    private final double temperature, similarOverlapCoefficient,
            differentOverlapCoefficient, springCoefficient, interactionLength;
    private double energy, stepLength;
    private double[] beadEnergy;
    private double[][] beadPositions;
    private int iterationNumber;
    private Graphics graphics;
    private SystemGeometry geometry;

    static {
        randomNumberGenerator = new Random();
    }

    public PolymerSystem() {
        dimension = 2;
        numBeads = 100;
        numABeads = numBeads / 2;

        rMax = new double[dimension];

        for (int i = 0; i < dimension; i++) {
            rMax[i] = 20;
        }

        neighbors = new int[numBeads][2];

        neighbors[0][0] = -1;
        neighbors[0][1] = 1;
        for (int i = 0, N = numBeads - 1; i < N; i++) {
            neighbors[i][0] = i - 1;
            neighbors[i][1] = i + 1;
        }
        neighbors[numBeads - 1][0] = numBeads - 2;
        neighbors[numBeads - 1][1] = -1;

        temperature = 300;
        similarOverlapCoefficient = 1;
        differentOverlapCoefficient = 4;
        springCoefficient = 40;
        interactionLength = 5;

        stepLength = interactionLength / 2;

        beadPositions = new double[numBeads][dimension];
        iterationNumber = 0;
        beadEnergy = new double[numBeads];

        makeGeometry();
    }

    private void makeGeometry() {
        geometry = new SystemGeometry();
        geometry.setDimension(dimension);
        for (int i = 0; i < dimension; i++) {
            geometry.setDimensionSize(i, rMax[i]);
        }
    }

    public PolymerSystem(SystemGeometry systemGeometry,
            PolymerCluster polymerCluster,
            PhysicalConstants physicalConstants,
            SimulationParameters simulationParameters) {

        dimension = systemGeometry.getDimension();
        rMax = systemGeometry.getRMax();

        numBeads = polymerCluster.getNumBeads();
        numABeads = polymerCluster.getNumABeads();
        neighbors = polymerCluster.makeNeighbors();

        temperature = physicalConstants.getTemperature();
        similarOverlapCoefficient = physicalConstants.getSimilarOverlapCoefficient();
        differentOverlapCoefficient = physicalConstants.getDifferentOverlapCoefficient();
        springCoefficient = physicalConstants.getSpringCoefficient();


        interactionLength = simulationParameters.getInteractionLength();
        stepLength = simulationParameters.getStepLength();

        beadPositions = new double[numBeads][dimension];
        iterationNumber = 0;
        beadEnergy = new double[numBeads];

        geometry = systemGeometry;
    }

    public void randomizePositions() {
        for (int i = 0; i < numBeads; i++) {
            for (int j = 0; j < dimension; j++) {
                beadPositions[i][j] = randomNumberGenerator.nextDouble() * rMax[j];
            }
        }
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
        final double[] stepVector = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            stepVector[i] = randomNumberGenerator.nextGaussian() * stepLength;
        }


        doStep(stepBead, stepVector);

        final double newEnergy = energy();
        final double energyChange = newEnergy - energy;

        boolean isStepInBounds = geometry.isPositionValid(beadPositions[stepBead]);

        if (isStepInBounds && (energyChange < 0 || isStepAllowedAnyway(energyChange))) {
            energy = newEnergy;
        } else {
            for (int i = 0; i < dimension; i++) {
                beadPositions[stepBead][i] -= stepVector[i];
            }
        }
    }

    private void updateBeadEnergy(int beadIndex, double[] stepVector) {
        updateBeadDensityEnergy(beadIndex, stepVector);
        updateBeadSpringEnergy(beadIndex, stepVector);
    }

    private void updateBeadDensityEnergy(int beadIndex, double[] stepVector) {
        double[] initialAreaOverlap = new double[numBeads];
        double[] finalAreaOverlap = new double[numBeads];

        for (int i = 0; i < numBeads; i++) {
            initialAreaOverlap[i] = geometry.areaOverlap(beadPositions[beadIndex], beadPositions[i], interactionLength);
        }
        doStep(beadIndex, stepVector);

        for (int i = 0; i < numBeads; i++) {
            finalAreaOverlap[i] = geometry.areaOverlap(beadPositions[beadIndex], beadPositions[i], interactionLength);
        }

        double energyChange;
        double overlapCoefficientA, overlapCoefficientB;
        if (beadIndex < numABeads) {
            overlapCoefficientA = similarOverlapCoefficient;
            overlapCoefficientB = differentOverlapCoefficient;
        } else {
            overlapCoefficientB = similarOverlapCoefficient;
            overlapCoefficientA = differentOverlapCoefficient;
        }
        for (int i = 0; i < numABeads; i++) {
            energyChange = overlapCoefficientA * (finalAreaOverlap[i] - initialAreaOverlap[i]);
            beadEnergy[i] += energyChange;
            beadEnergy[beadIndex] += energyChange;
        }
        for (int i = numABeads; i < numBeads; i++) {
            energyChange = overlapCoefficientB * (finalAreaOverlap[i] - initialAreaOverlap[i]);
            beadEnergy[i] += energyChange;
            beadEnergy[beadIndex] += energyChange;
        }

        undoStep(beadIndex, stepVector);
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

        return springCoefficient * sqLength;
    }

    private double densityEnergy() {
        double similarOverlap = 0, differentOverlap = 0;

        for (int i = 0; i < numABeads; i++) {
            for (int j = 0; j < numABeads; j++) {
                similarOverlap += geometry.areaOverlap(beadPositions[i], beadPositions[j], interactionLength);
            }
        }

        for (int i = numABeads; i < numBeads; i++) {
            for (int j = numABeads; j < numBeads; j++) {
                similarOverlap += geometry.areaOverlap(beadPositions[i], beadPositions[j], interactionLength);
            }
        }


        for (int i = 0; i < numABeads; i++) {
            for (int j = numABeads; j < numBeads; j++) {
                differentOverlap += geometry.areaOverlap(beadPositions[i], beadPositions[j], interactionLength);
            }
        }

        return similarOverlapCoefficient * similarOverlap + 2 * differentOverlapCoefficient * differentOverlap;
    }

    private boolean isStepAllowedAnyway(double energyChange) {
        return randomNumberGenerator.nextDouble() < Math.exp(-energyChange / temperature);
    }

    private void doStep(int stepBeadIndex, double[] stepVector) {
        for (int i = 0; i < dimension; i++) {
            beadPositions[stepBeadIndex][i] += stepVector[i];
        }
    }

    private void undoStep(int stepBeadIndex, double[] stepVector) {
        for (int i = 0; i < dimension; i++) {
            beadPositions[stepBeadIndex][i] -= stepVector[i];
        }
    }

// <editor-fold defaultstate="collapsed" desc="getters">
    public int getDimension() {
        return dimension;
    }

    public int getNumBeads() {
        return numBeads;
    }

    public int getNumABeads() {
        return numABeads;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getSimilarOverlapCoefficient() {
        return similarOverlapCoefficient;
    }

    public double getDifferentOverlapCoefficient() {
        return differentOverlapCoefficient;
    }

    public double getInteractionLength() {
        return interactionLength;
    }

    public double getEnergy() {
        return energy;
    }

    public double getStepLength() {
        return stepLength;
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

        if (dimension != 2) {
            return;
        }

        double scaleFactor = 600 / (rMax[0]);

        int diameter = (int) Math.round(interactionLength * scaleFactor) / 5;
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
