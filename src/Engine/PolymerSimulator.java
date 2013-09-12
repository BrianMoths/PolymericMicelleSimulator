/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.SimulationStepping.MoveType;
import Engine.SimulationStepping.SimulationStep;
import Engine.SimulationStepping.ChainMoveStepGenerator;
import Engine.SimulationStepping.StepGenerator;
import Engine.SystemGeometry.HardWallGeometry.HardWallGeometryBuilder;
import Engine.SystemGeometry.PeriodicGeometry.PeriodicGeometryBuilder;
import Engine.SystemGeometry.SystemGeometry;
import java.io.Serializable;

/**
 *
 * @author brian
 */
public class PolymerSimulator implements Serializable {

    public static SimulationParameters makeDefaultParameters(PolymerCluster polymerCluster, double boxLength, int dimension, PhysicalConstants physicalConstants) {
        return makeDefaultParametersPrivate(polymerCluster, boxLength, dimension, physicalConstants);
    }

    static private SimulationParameters makeDefaultParametersPrivate(PolymerCluster polymerCluster, double boxLength, int dimension, PhysicalConstants physicalConstants) {
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

    static private SystemGeometry makeGeometry(PolymerCluster polymerCluster, PhysicalConstants physicalConstants) {
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

    private final SystemGeometry geometry;
    private final PhysicalConstants physicalConstants;
    private final PolymerPosition polymerPosition;
    private final SystemAnalyzer systemAnalyzer;
    private final StepGenerator stepGenerator = new ChainMoveStepGenerator(0.);
    private double energy;
    private int iterationNumber;
    private int acceptedIterations;
    private int numChainMoves;
    private int acceptedChainMoves;

    public PolymerSimulator() {

        PolymerCluster polymerCluster = PolymerCluster.makeDefaultPolymerCluster();
        physicalConstants = PhysicalConstants.defaultPhysicalConstants();
        geometry = makeGeometry(polymerCluster, physicalConstants);

        resetCounters();
        polymerPosition = makePolymerPosition(polymerCluster, geometry);
        systemAnalyzer = new SystemAnalyzer(geometry, polymerCluster, physicalConstants);
        polymerPosition.registerAnalyzer(systemAnalyzer);
        energy = systemAnalyzer.computeEnergy();
    }

    public PolymerSimulator(SystemGeometry systemGeometry, PolymerCluster polymerCluster, PhysicalConstants physicalConstants) {

        this.geometry = systemGeometry;

        this.physicalConstants = physicalConstants;

        polymerPosition = new PolymerPosition(polymerCluster, systemGeometry);

        resetCounters();

        systemAnalyzer = new SystemAnalyzer(geometry, polymerCluster, physicalConstants);
        polymerPosition.registerAnalyzer(systemAnalyzer);
        energy = systemAnalyzer.computeEnergy();
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

    private PolymerPosition makePolymerPosition(PolymerCluster polymerCluster, SystemGeometry geometry) {
        PolymerPosition defaultPolymerPosition = new PolymerPosition(polymerCluster, geometry);
        defaultPolymerPosition.randomize();
        return defaultPolymerPosition;
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
        resetCounters();
        polymerPosition.randomize();
        energy = systemAnalyzer.computeEnergy();
    }

    public synchronized void columnRandomizePositions() {
        resetCounters();
        polymerPosition.columnRandomize();
        energy = systemAnalyzer.computeEnergy();
    }

    private void resetCounters() {
        iterationNumber = 0;
        acceptedIterations = 0;
        acceptedChainMoves = 0;
        numChainMoves = 0;
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
        SimulationStep simulationStep = stepGenerator.generateStep(systemAnalyzer);
        if (simulationStep.getMoveType() == MoveType.SINGLE_CHAIN) {
            numChainMoves++;
        }
        if (simulationStep.doStep(polymerPosition, systemAnalyzer)) {
            final double energyChange = simulationStep.getEnergyChange();
            if (physicalConstants.isEnergeticallyAllowed(energyChange)) {
                energy += energyChange;
                acceptedIterations++;
                if (simulationStep.getMoveType() == MoveType.SINGLE_CHAIN) {
                    acceptedChainMoves++;
                }
            } else {
                simulationStep.undoStep(polymerPosition);
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

    public int getNumChainMoves() {
        return numChainMoves;
    }

    public int getAcceptedChainMoves() {
        return acceptedChainMoves;
    }
    // </editor-fold>

}
