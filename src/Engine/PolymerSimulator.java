/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.SystemGeometry.HardWallSystemGeometry;
import Engine.SystemGeometry.PeriodicSystemGeometry;
import Engine.SystemGeometry.SystemGeometry;
import java.awt.Graphics;

/**
 *
 * @author brian
 */
public class PolymerSimulator {
    //next implement bead collection

    private double energy;
    private int iterationNumber;
    private SystemGeometry geometry;
    private PhysicalConstants physicalConstants;
    private PolymerPosition polymerPosition;

    public PolymerSimulator() {
        iterationNumber = 0;

        makeGeometry();
        makeParameters();
        makePhysicalConstants();
        makePolymerPosition();

        energy = energy();
    }

    private void makeGeometry() {
        int dimension = 2;
//        geometry = new HardWallSystemGeometry();
        geometry = new PeriodicSystemGeometry();
        geometry.setDimension(dimension);
        for (int i = 0; i < dimension; i++) {
            geometry.setDimensionSize(i, 20);
        }
    }

    private void makeParameters() {
        double interactionLength = Math.pow(14 * 400 / 100, .5);
        double stepLength = interactionLength / 10;
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
        springCoefficient = 400;
        physicalConstants.setTemperature(temperature);
        physicalConstants.setDifferentOverlapCoefficient(differentOverlapCoefficient);
        physicalConstants.setSimilarOverlapCoefficient(similarOverlapCoefficient);
        physicalConstants.setSpringCoefficient(springCoefficient);
    }

    private void makePolymerPosition() {
        PolymerChain polymerChain = PolymerChain.makeChainStartingWithA(30, 30);
        PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, 10);
        polymerPosition = new PolymerPosition(polymerCluster, geometry);
        polymerPosition.randomize();
    }

    public PolymerSimulator(SystemGeometry systemGeometry,
            PolymerCluster polymerCluster,
            PhysicalConstants physicalConstants,
            SimulationParameters simulationParameters) {

        geometry = systemGeometry;
        geometry.setParameters(simulationParameters);

        this.physicalConstants = physicalConstants;

        polymerPosition = new PolymerPosition(polymerCluster, systemGeometry);

        iterationNumber = 0;

        energy = energy();
    }

    public void randomizePositions() {
        polymerPosition.randomize();
        energy = energy();
    }

    public void doIterations(int n) { //possibly optomize by unrolling loop and tracking pairwise interactions
        for (int i = 0; i < n; i++) {
            doIteration();
        }
    }

    public void doIteration() {
        iterationNumber++;
        final int stepBead = polymerPosition.randomBeadIndex();
        final double[] stepVector = geometry.randomGaussian();

        polymerPosition.setStep(stepBead, stepVector);
        if (polymerPosition.isStepInBounds()) {
            double oldBeadEnergry = beadEnergy();
            polymerPosition.doStep();
            double newBeadEnergy = beadEnergy();
            double energyChange = newBeadEnergy - oldBeadEnergry;

            if (physicalConstants.isEnergeticallyAllowed(energyChange)) {
                energy += energyChange;
            } else {
                polymerPosition.undoStep();
            }
        }

    }

    private double beadEnergy() {
        return beadSpringEnergy() + beadDensityEnergy();
    }

    private double beadSpringEnergy() {
        double sqLength = polymerPosition.stepBeadSpringStretching();

        return physicalConstants.springEnergy(sqLength);
    }

    private double beadDensityEnergy() {
        double similarOverlap = polymerPosition.stepBeadSimilarOverlap();
        double differentOverlap = polymerPosition.stepBeadDifferentOverlap();

        return physicalConstants.densityEnergy(similarOverlap, differentOverlap);
    }

    private double energy() {
        return springEnergy() + densityEnergy();
    }

    private double springEnergy() {
        double sqLength = polymerPosition.totalSpringStretching();

        return physicalConstants.springEnergy(sqLength);
    }

    private double densityEnergy() {
        double similarOverlap = polymerPosition.totalSimilarOverlap();
        double differentOverlap = polymerPosition.totalDifferentOverlap();

        return physicalConstants.densityEnergy(similarOverlap, differentOverlap);
    }

// <editor-fold defaultstate="collapsed" desc="getters">
    public int getNumBeads() {
        return polymerPosition.getNumBeads();
    }

    public double getEnergy() {
        return energy;
    }

    public int getIterationNumber() {
        return iterationNumber;
    }
    // </editor-fold>

    public void setGraphics(Graphics inGraphics) {
        polymerPosition.setGraphics(inGraphics);
    }

    public void draw() {
        polymerPosition.draw();
    }
}
