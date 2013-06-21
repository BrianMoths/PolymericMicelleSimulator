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
    private final int[] rMin, rMax;
    private final int[][] neighbors;
    private final double temperature, similarOverlapCoefficient,
            differentOverlapCoefficient, springCoefficient, interactionLength;
    private double energy, stepLength;
    private double[][] beadPositions;
    private int iterationNumber;
    private Graphics graphics;

    static {
        randomNumberGenerator = new Random();
    }

    public PolymerSystem() {
        dimension = 2;
        numBeads = 100;
        numABeads = numBeads / 2;

        rMin = new int[dimension];
        rMax = new int[dimension];

        for (int i = 0; i < dimension; i++) {
            rMin[i] = 0;
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
    }

    public static void main(String[] argv) {
        PolymerSystem system = new PolymerSystem();
        system.randomizePositions();

        System.out.println(system.getEnergy());

        Long initialTime = System.nanoTime();
        system.doIterations(5000);
        Long finalTime = System.nanoTime();

        System.out.println(system.getEnergy());

        System.out.println((finalTime - initialTime) / 1000000);
    }

    public void randomizePositions() {
        for (int i = 0; i < numBeads; i++) {
            for (int j = 0; j < dimension; j++) {
                beadPositions[i][j] = randomNumberGenerator.nextDouble() * (rMax[j] - rMin[j]) + rMin[j];
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
        final int stepBead = randomNumberGenerator.nextInt(numBeads);
        final double[] stepVector = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            stepVector[i] = randomNumberGenerator.nextGaussian() * stepLength;
        }

        for (int i = 0; i < dimension; i++) {
            beadPositions[stepBead][i] += stepVector[i];

            if (beadPositions[stepBead][i] < rMin[i] || beadPositions[stepBead][i] > rMax[i]) {
                for (int j = i; j >= 0; j--) {
                    beadPositions[stepBead][j] -= stepVector[j];
                }
                return;
            }
        }

        final double newEnergy = energy();
        final double energyChange = newEnergy - energy;

        if (energyChange < 0 || isStepAllowedAnyway(energyChange)) {
            energy = newEnergy;
        } else {
            for (int i = 0; i < dimension; i++) {
                beadPositions[stepBead][i] -= stepVector[i];
            }
        }
        iterationNumber++;
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
                    sqLength += sqDist(beadPositions[i], beadPositions[neighborIndex]);
                }
            }
        }

        return springCoefficient * sqLength;
    }

    private double sqDist(double[] position1, double[] position2) {
        double sqDist = 0;
        for (int i = 0; i < dimension; i++) {
            sqDist += (position1[i] - position2[i]) * (position1[i] - position2[i]);
        }
        return sqDist;
    }

    private double densityEnergy() {
        double similarOverlap = 0, differentOverlap = 0;

        for (int i = 0; i < numABeads; i++) {
            for (int j = 0; j < numABeads; j++) {
                similarOverlap += areaOverlap(beadPositions[i], beadPositions[j]);
            }
        }

        for (int i = numABeads; i < numBeads; i++) {
            for (int j = numABeads; j < numBeads; j++) {
                similarOverlap += areaOverlap(beadPositions[i], beadPositions[j]);
            }
        }


        for (int i = 0; i < numABeads; i++) {
            for (int j = numABeads; j < numBeads; j++) {
                differentOverlap += areaOverlap(beadPositions[i], beadPositions[j]);
            }
        }

        return similarOverlapCoefficient * similarOverlap + 2 * differentOverlapCoefficient * differentOverlap;
    }

    private double areaOverlap(double[] position1, double[] position2) {
        double overlap = 1;

        for (int i = 0; i < dimension; i++) {
            overlap *= Math.max(interactionLength - Math.abs(position1[i] - position2[i]), 0.0);
        }

        return overlap;
    }

    private boolean isStepAllowedAnyway(double energyChange) {
        return randomNumberGenerator.nextDouble() < Math.exp(-energyChange / temperature);
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
    
    public double getIterationNumber(){
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

        double scaleFactor = 600 / (rMax[0] - rMin[0]);

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