/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.PhysicalConstants.PhysicalConstantsBuilder;
import Engine.SystemGeometry.AreaOverlap;
import Engine.SystemGeometry.HardWallGeometry.HardWallGeometryBuilder;
import Engine.SystemGeometry.PeriodicGeometry.PeriodicGeometryBuilder;
import Engine.SystemGeometry.SystemGeometry;
import java.awt.Graphics;

/**
 *
 * @author brian
 */
public class PolymerSimulator {

    private final SystemGeometry geometry;
    private final PhysicalConstants physicalConstants;
    private final PolymerPosition polymerPosition;
    private double energy;
    private int iterationNumber;
    private int acceptedIterations;

    public PolymerSimulator() {
        final int ABeadsPerChain = 6, BBeadsPerChain = 6, numChains = 100;

        final int numBeads = (ABeadsPerChain + BBeadsPerChain) * numChains;
        iterationNumber = 0;
        acceptedIterations = 0;

        physicalConstants = makeDefaultPhysicalConstants();
        geometry = makeGeometryBuilder(numBeads);
        polymerPosition = makePolymerPosition();

        energy = energy();
    }

    private SystemGeometry makeGeometryBuilder(int numBeads) {
        final int dimension = 2;
        final double boxLength = 20;
//        HardWallGeometryBuilder geometryBuilder = new HardWallGeometryBuilder();
        PeriodicGeometryBuilder geometryBuilder = new PeriodicGeometryBuilder();
        geometryBuilder.setDimension(dimension);
        for (int i = 0; i < dimension; i++) {
            geometryBuilder.setDimensionSize(i, boxLength);
        }

        geometryBuilder.setParameters(makeDefaultParameters(numBeads, boxLength, dimension));

        return geometryBuilder.buildGeometry();
    }

    private SimulationParameters makeDefaultParameters(int numBeads, double boxLength, int dimension) {
        SimulationParameters simulationParameters;
        double interactionLength;
        interactionLength = Math.pow(14 * Math.pow(boxLength, dimension) / numBeads, 1.0 / dimension);
        double stepLength;
        stepLength = Math.sqrt(physicalConstants.getTemperature() / physicalConstants.getSpringCoefficient());
        simulationParameters = new SimulationParameters(stepLength, interactionLength);
        return simulationParameters;
    }

//    private SimulationParameters makeDefaultParameters() {
//        double interactionLength = Math.pow(14 * 400 / 100, .5);
//        double stepLength = interactionLength / 10;
//        SimulationParameters defaultParameters;
//        defaultParameters = new SimulationParameters(stepLength, interactionLength);
//
//        return defaultParameters;
//    }
    private PhysicalConstants makeDefaultPhysicalConstants() {
        double temperature, similarOverlapCoefficient, differentOverlapCoefficient, springCoefficient;
        PhysicalConstantsBuilder defaultPhysicalConstantsBuilder = new PhysicalConstantsBuilder();
        temperature = 120;
        similarOverlapCoefficient = 5;
        differentOverlapCoefficient = 15;
        springCoefficient = 40;

        defaultPhysicalConstantsBuilder
                .setTemperature(temperature)
                .setABOverlapCoefficient(differentOverlapCoefficient)
                .setAAOverlapCoefficient(similarOverlapCoefficient)
                .setBBOverlapCoefficient(similarOverlapCoefficient)
                .setSpringCoefficient(springCoefficient);

        return defaultPhysicalConstantsBuilder.buildPhysicalConstants();
    }

    private PolymerPosition makePolymerPosition() {
        PolymerChain polymerChain = PolymerChain.makeChainStartingWithA(6, 6);
        PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, 100);
        PolymerPosition defaultPolymerPosition = new PolymerPosition(polymerCluster, geometry);
        defaultPolymerPosition.randomize();
        return defaultPolymerPosition;
    }

    public PolymerSimulator(SystemGeometry systemGeometry,
            PolymerCluster polymerCluster,
            PhysicalConstants physicalConstants) {

        geometry = systemGeometry;

        this.physicalConstants = physicalConstants;

        polymerPosition = new PolymerPosition(polymerCluster, systemGeometry);

        iterationNumber = 0;
        acceptedIterations = 0;


        energy = energy();
    }

    public void randomizePositions() {
        iterationNumber = 0;
        acceptedIterations = 0;
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
            final double energyChange = beadEnergyChange();
            if (physicalConstants.isEnergeticallyAllowed(energyChange)) {
                energy += energyChange;
                polymerPosition.doStep();
                acceptedIterations++;
            }
        }

    }

    private double beadEnergyChange() {
        return beadSpringEnergyChange() + beadDensityEnergyChange();
    }

    private double beadSpringEnergyChange() {
        double sqLengthChange = polymerPosition.sqLengthChange();

        return physicalConstants.springEnergy(sqLengthChange);
    }

    private double beadDensityEnergyChange() {
        AreaOverlap overlapChange = polymerPosition.overlapChange();

        return physicalConstants.densityEnergy(overlapChange);
    }

    private double beadEnergy() {
        return beadSpringEnergy() + beadDensityEnergy();
    }

    private double beadSpringEnergy() {
        double sqLength = polymerPosition.stepBeadSpringStretching();

        return physicalConstants.springEnergy(sqLength);
    }

    private double beadDensityEnergy() {
        AreaOverlap areaOverlap = polymerPosition.stepBeadOverlap();

        return physicalConstants.densityEnergy(areaOverlap);
    }

    private double energy() {
        return springEnergy() + densityEnergy();
    }

    public double springEnergy() {
        double sqLength = polymerPosition.totalSpringStretching();

        return physicalConstants.springEnergy(sqLength);
    }

    public double densityEnergy() {
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

    public int getAcceptedIterations() {
        return acceptedIterations;
    }
    // </editor-fold>

    public void setGraphics(Graphics graphics) {
        polymerPosition.setGraphics(graphics);
    }

    public void draw() {
        polymerPosition.draw();
    }
}
