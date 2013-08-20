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
    private final SystemAnalyzer systemAnalyzer;
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
        systemAnalyzer = new SystemAnalyzer(geometry, polymerCluster, physicalConstants);
        polymerPosition.registerAnalyzer(systemAnalyzer);
        energy = systemAnalyzer.energy();
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
        systemAnalyzer = new SystemAnalyzer(geometry, polymerCluster, physicalConstants);
        polymerPosition.registerAnalyzer(systemAnalyzer);
        energy = systemAnalyzer.energy();
    }

    public PolymerSimulator(PolymerSimulator polymerSimulator) {
        geometry = polymerSimulator.geometry;
        physicalConstants = polymerSimulator.physicalConstants;
        polymerPosition = new PolymerPosition(polymerSimulator.polymerPosition);
        energy = polymerSimulator.energy;
        iterationNumber = polymerSimulator.iterationNumber;
        acceptedIterations = polymerSimulator.acceptedIterations;
        systemAnalyzer = new SystemAnalyzer(polymerSimulator.systemAnalyzer);
        polymerPosition.registerAnalyzer(systemAnalyzer);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Physical Constants: ").append(physicalConstants.toString()).append("\n");
        stringBuilder.append("Polymer position: \n").append(polymerPosition.toString()).append("\n");
        stringBuilder.append("Energy: ").append(Double.toString(energy)).append("\n");
        stringBuilder.append("Total iterations performed: ").append(Integer.toString(iterationNumber)).append("\n");
        stringBuilder.append("Number iterations accepted: ").append(Integer.toString(acceptedIterations)).append("\n");
        return stringBuilder.toString();
    }

    public synchronized void randomizePositions() {
        iterationNumber = 0;
        acceptedIterations = 0;
        polymerPosition.randomize();
        energy = systemAnalyzer.energy();
    }

    public synchronized double[][] getBeadPositions() {
        return polymerPosition.getBeadPositions();
    }

    public synchronized void setBeadPositions(double[][] beadPositions) {
        polymerPosition.setBeadPositions(beadPositions);
    }

    public synchronized void doIterations(int n) { //possibly optomize by unrolling loop and tracking pairwise interactions
        for (int i = 0; i < n; i++) {
            doIteration();
            if (Thread.interrupted()) {
                return;
            }
        }
    }

    public synchronized void doIteration() { //todo: cache bead energies
        iterationNumber++;
        final int stepBead = polymerPosition.randomBeadIndex();
        final double[] stepVector = geometry.randomGaussian();
//        polymerPosition.setStep(stepBead, stepVector);

        final double oldBeadEnergy = systemAnalyzer.beadEnergy(stepBead);
//        if (polymerPosition.isStepInBounds()) {
        if (polymerPosition.moveBead(stepBead, stepVector)) {
            final double energyChange = systemAnalyzer.beadEnergy(stepBead) - oldBeadEnergy;
            if (physicalConstants.isEnergeticallyAllowed(energyChange)) {
                energy += energyChange;
                acceptedIterations++;
            } else {
                polymerPosition.undoStep(stepBead, stepVector);
            }
        }

    }

// <editor-fold defaultstate="collapsed" desc="getters">
    public int getNumBeads() {
        return polymerPosition.getNumBeads();
    }

    public SystemGeometry getGeometry() {
        return geometry;
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

    public SystemAnalyzer getSystemAnalyzer() {
        return systemAnalyzer;
    }
    // </editor-fold>

    public void draw(Graphics graphics) {
        systemAnalyzer.draw(graphics);
    }
}
