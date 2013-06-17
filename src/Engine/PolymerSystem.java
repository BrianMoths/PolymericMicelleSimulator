/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import java.util.Random;

/**
 *
 * @author brian
 */
public class PolymerSystem {

    private static final Random randomNumberGenerator;
    private final int dimension, numBeads, numABeads;//maybe package private

    private final int[] rMin, rMax;
    private final double temperature, similarOverlapCoefficient,
            differentOverlapCoefficient, interactionLength;
    private double energy, stepLength;
    private double[][] beadPositions;
    private int iterationNumber;

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

        temperature = 1;
        similarOverlapCoefficient = 1;
        differentOverlapCoefficient = 4;
        interactionLength = 5;

        stepLength = interactionLength;

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
                beadPositions[i][j] = randomNumberGenerator.nextDouble() * (rMax[j]-rMin[j]) + rMin[j];
            }
        }
        energy = densityEnergy();
    }

    private void doIterations(int n) {
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
        }

        final double newEnergy = densityEnergy();

        if ((newEnergy > energy
                && randomNumberGenerator.nextDouble() > Math.exp((energy - newEnergy) / temperature))
                || beadPositions[stepBead][0] < rMin[0]
                || beadPositions[stepBead][0] > rMax[0]
                || beadPositions[stepBead][1] < rMin[1]
                || beadPositions[stepBead][1] > rMax[1]) {
            for (int i = 0; i < dimension; i++) {
                beadPositions[stepBead][i] -= stepVector[i];
            }
        } else {
            energy = newEnergy;
        }
    }

    private double densityEnergy() {
        final double[][] areaOverlapMatrix;
        areaOverlapMatrix = areaOverlapMatrix(beadPositions);

        double similarOverlap = 0, differentOverlap = 0;

        for (int i = 0; i < numABeads; i++) {
            for (int j = 0; j < numABeads; j++) {
                similarOverlap += areaOverlapMatrix[i][j];
            }
        }

        for (int i = numABeads; i < numBeads; i++) {
            for (int j = numABeads; j < numBeads; j++) {
                similarOverlap += areaOverlapMatrix[i][j];
            }
        }

        for (int i = 0; i < numABeads; i++) {
            for (int j = numABeads; j < numBeads; j++) {
                differentOverlap += areaOverlapMatrix[i][j];
            }
        }

        return similarOverlapCoefficient * similarOverlap + 2 * differentOverlapCoefficient * differentOverlap;
    }

    private double[][] areaOverlapMatrix(double[][] beadPositions) {
        final double[][] areaOverlapMatrix = new double[numBeads][numBeads];
        for (int i = 0; i < numBeads; i++) {
            for (int j = 0; j < numBeads; j++) {
                areaOverlapMatrix[i][j] = areaOverlap(beadPositions[i], beadPositions[j]);
            }
        }
        return areaOverlapMatrix;
    }

    private double areaOverlap(double[] position1, double[] position2) {
        double overlap = 1;

        for (int i = 0; i < dimension; i++) {
            overlap *= Math.max(interactionLength - Math.abs(position1[i] - position2[i]), 0.0);
        }

        return overlap;
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
    // </editor-fold>


}