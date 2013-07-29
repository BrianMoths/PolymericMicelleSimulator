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

    //public static SystemGeometry makeDefaultSystemGeometry(SystemGeometry systemGeometry, PolymerCluster polymerCluster)
    public static SimulationParameters makeDefaultParameters(PolymerCluster polymerCluster, double boxLength, int dimension, PhysicalConstants physicalConstants) {
        return makeDefaultParametersPrivate(polymerCluster, boxLength, dimension, physicalConstants);
    }

    private static SimulationParameters makeDefaultParametersPrivate(PolymerCluster polymerCluster, double boxLength, int dimension, PhysicalConstants physicalConstants) {
        SimulationParameters simulationParameters;
//        int averageNumberOfNeighbors = 14
        int averageNumberOfNeighbors = 14;
        double interactionLength;
        interactionLength = Math.pow(averageNumberOfNeighbors * Math.pow(boxLength, dimension) / polymerCluster.getNumBeadsIncludingWater(), 1.0 / dimension);
        double stepLength;
        stepLength = Math.sqrt(physicalConstants.getTemperature() / physicalConstants.getSpringCoefficient());
        simulationParameters = new SimulationParameters(stepLength, interactionLength);
        return simulationParameters;
    }

    static public PolymerCluster makeDefaultPolymerCluster() {
        PolymerChain polymerChain = PolymerChain.makeChainStartingWithA(6, 6);
        PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, 100);
        return polymerCluster;
    }

    public PolymerSimulator() {
        iterationNumber = 0;
        acceptedIterations = 0;

        PolymerCluster polymerCluster = makeDefaultPolymerCluster();
        physicalConstants = makeDefaultPhysicalConstants();

        geometry = makeGeometry(polymerCluster);
        polymerPosition = makePolymerPosition(polymerCluster, geometry);
        energy = energy();
    }

    // <editor-fold defaultstate="collapsed" desc="default constructor helpers">
    private SystemGeometry makeGeometry(PolymerCluster polymerCluster) {
        final int dimension = 2;
        final double boxLength = 20;
//        HardWallGeometryBuilder geometryBuilder = new HardWallGeometryBuilder();
        PeriodicGeometryBuilder geometryBuilder = new PeriodicGeometryBuilder();
        geometryBuilder.setDimension(dimension);
        for (int i = 0; i < dimension; i++) {
            geometryBuilder.setDimensionSize(i, boxLength);
        }

        geometryBuilder.setParameters(makeDefaultParametersPrivate(polymerCluster, boxLength, dimension, physicalConstants));

        return geometryBuilder.buildGeometry();
    }

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

    private PolymerPosition makePolymerPosition(PolymerCluster polymerCluster, SystemGeometry geometry) {
        PolymerPosition defaultPolymerPosition = new PolymerPosition(polymerCluster, geometry);
        defaultPolymerPosition.randomize();
        return defaultPolymerPosition;
    }
// </editor-fold>

    public PolymerSimulator(SystemGeometry systemGeometry,
            PolymerCluster polymerCluster,
            PhysicalConstants physicalConstants) {

        this.geometry = systemGeometry;

        this.physicalConstants = physicalConstants;

        polymerPosition = new PolymerPosition(polymerCluster, systemGeometry);

        iterationNumber = 0;
        acceptedIterations = 0;
        energy = energy();
    }

    public synchronized void randomizePositions() {
        iterationNumber = 0;
        acceptedIterations = 0;
        polymerPosition.randomize();
        energy = energy();
    }

    public synchronized void doIterations(int n) { //possibly optomize by unrolling loop and tracking pairwise interactions
        for (int i = 0; i < n; i++) {
            doIteration();
            if (Thread.interrupted()) {
                return;
            }
        }
    }

    public synchronized void doIteration() {
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

        return physicalConstants.densityEnergyWithCore(overlapChange);
    }

    private double energy() {
        return springEnergy() + densityEnergy();
    }

    public double springEnergy() {
        double sqLength = polymerPosition.totalSpringStretching();

        return physicalConstants.springEnergy(sqLength);
    }

    public double densityEnergy() {

        AreaOverlap overlap = polymerPosition.totalOverlap();

        return physicalConstants.densityEnergyWithCore(overlap);
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

    public PhysicalConstants getPhysicalConstants() {
        return physicalConstants;
    }

    public SimulationParameters getSimulationParameters() {
        return geometry.getParameters();
    }

    public int getAcceptedIterations() {
        return acceptedIterations;
    }
    // </editor-fold>

//    public void setGraphics(Graphics graphics) {
//        polymerPosition.setGraphics(graphics);
//    }
    public void draw(Graphics graphics) {
        polymerPosition.draw(graphics);
    }
}
