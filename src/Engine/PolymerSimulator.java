/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.PolymerState.DiscretePolymerState;
import Engine.PolymerState.PolymerPosition;
import Engine.PolymerState.PolymerState;
import Engine.PolymerState.SystemGeometry.GeometricalParameters;
import Engine.SimulationStepping.StepTypes.StepType;
import Engine.SimulationStepping.StepTypes.SimulationStep;
import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.PolymerState.SystemGeometry.Implementations.PeriodicGeometry.PeriodicGeometryBuilder;
import Engine.PolymerState.SystemGeometry.Interfaces.SystemGeometry;
import Engine.SimulationStepping.StepGenerators.CompoundStepGenerators.GeneralStepGenerator;
import SystemAnalysis.GeometryAnalyzer;
import java.io.Serializable;

/**
 *
 * @author brian
 */
public class PolymerSimulator implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="default constructor helpers">
    static private SystemGeometry makeDefaultGeometry(PolymerCluster polymerCluster, EnergeticsConstants physicalConstants) {
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

    static private GeometricalParameters makeDefaultParametersPrivate(PolymerCluster polymerCluster, double boxLength, int dimension, EnergeticsConstants physicalConstants) {
        GeometricalParameters geometricalParameters;
        int averageNumberOfNeighbors = 14; //14
        double interactionLength;
        interactionLength = Math.pow(averageNumberOfNeighbors * Math.pow(boxLength, dimension) / polymerCluster.getNumBeadsIncludingWater(), 1.0 / dimension);
        double stepLength;
        stepLength = Math.sqrt(physicalConstants.getTemperature() / physicalConstants.getSpringCoefficient());
        geometricalParameters = new GeometricalParameters(stepLength, interactionLength);
        return geometricalParameters;
    }
    //</editor-fold>

    private final SystemGeometry geometry;
    private final PolymerPosition polymerPosition;
    private final PolymerState polymerState;
    private final SystemAnalyzer systemAnalyzer;
    private final StepGenerator stepGenerator = GeneralStepGenerator.defaultStepGenerator();
    private double energy;
    private int iterationNumber;
    private int acceptedIterations;
    private int numChainMoves;
    private int acceptedChainMoves;

    public PolymerSimulator() {

        PolymerCluster polymerCluster = PolymerCluster.makeDefaultPolymerCluster();
        EnergeticsConstants energeticsConstants = EnergeticsConstants.defaultEnergeticsConstants();
        geometry = makeDefaultGeometry(polymerCluster, energeticsConstants);

        resetCounters();
        polymerPosition = makePolymerPosition(polymerCluster, geometry);
        DiscretePolymerState discretePolymerState = new DiscretePolymerState(polymerCluster);
        polymerState = new PolymerState(discretePolymerState, polymerPosition, geometry);
        systemAnalyzer = new SystemAnalyzer(polymerState, energeticsConstants);
        energy = systemAnalyzer.computeEnergy();
    }

    public PolymerSimulator(SystemGeometry systemGeometry, PolymerCluster polymerCluster, EnergeticsConstants energeticsConstants) {

        this.geometry = systemGeometry;


        polymerPosition = new PolymerPosition(polymerCluster.getNumBeads(), geometry);

        DiscretePolymerState discretePolymerState = new DiscretePolymerState(polymerCluster);
        polymerState = new PolymerState(discretePolymerState, polymerPosition, geometry);

        resetCounters();

        systemAnalyzer = new SystemAnalyzer(polymerState, energeticsConstants);
        energy = systemAnalyzer.computeEnergy();
    }

    public PolymerSimulator(PolymerSimulator polymerSimulator) {
        geometry = polymerSimulator.geometry;
        polymerPosition = new PolymerPosition(polymerSimulator.polymerPosition);
        polymerState = new PolymerState(polymerSimulator.polymerState.getDiscretePolymerState(), polymerPosition, geometry);
        energy = polymerSimulator.energy;
        iterationNumber = polymerSimulator.iterationNumber;
        acceptedIterations = polymerSimulator.acceptedIterations;
        systemAnalyzer = new SystemAnalyzer(polymerSimulator.systemAnalyzer);
    }

    private PolymerPosition makePolymerPosition(PolymerCluster polymerCluster, SystemGeometry geometry) {
        PolymerPosition defaultPolymerPosition = new PolymerPosition(polymerCluster.getNumBeads(), geometry);
        defaultPolymerPosition.randomize();
        return defaultPolymerPosition;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        //add systemAnalyzer toString
//        stringBuilder.append("Physical Constants: ").append(energeticsConstants.toString()).append("\n");
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

    public synchronized void anneal() {
        resetCounters();
        polymerPosition.anneal();
        energy = systemAnalyzer.computeEnergy();
    }

    public void doIterations(int n) { //possibly optomize by unrolling loop and tracking pairwise interactions
        for (int i = 0; i < n; i++) {
            synchronized (this) {
                doIteration();
            }
            if (Thread.interrupted()) {
                return;
            }
        }
    }

    public synchronized void doIteration() { //todo: cache bead energies
        SimulationStep simulationStep = stepGenerator.generateStep(systemAnalyzer);
        iterateAttemptCounters(simulationStep.getMoveType());
        if (simulationStep.doStep(polymerState, systemAnalyzer)) {
            final double energyChange = simulationStep.getEnergyChange();
            if (systemAnalyzer.isEnergeticallyAllowed(energyChange)) {
                energy += energyChange;
                iterateAcceptedCounters(simulationStep.getMoveType());
            } else {
                simulationStep.undoStep(polymerState);
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="manage counters">
    private void resetCounters() {
        iterationNumber = 0;
        acceptedIterations = 0;
        acceptedChainMoves = 0;
        numChainMoves = 0;
    }

    private void iterateAttemptCounters(StepType moveType) {
        iterationNumber++;
        if (moveType == StepType.SINGLE_CHAIN) {
            numChainMoves++;
        }
    }

    private void iterateAcceptedCounters(StepType moveType) {
        acceptedIterations++;
        if (moveType == StepType.SINGLE_CHAIN) {
            acceptedChainMoves++;
        }
    }
    //</editor-fold>

    public void equilibrate() {
        while (!systemAnalyzer.isEquilibrated()) {
            doIterations(10000);
            final GeometryAnalyzer.AreaPerimeter areaPerimeter = systemAnalyzer.findAreaAndPerimeter();
            systemAnalyzer.addPerimeterAreaEnergySnapshot(areaPerimeter.perimeter, areaPerimeter.area, energy);
        }
    }

    public synchronized void setBeadPositions(double[][] beadPositions) {
        polymerPosition.setBeadPositions(beadPositions);
    }

    // <editor-fold defaultstate="collapsed" desc="getters">
    public synchronized double[][] getBeadPositions() {
        return polymerPosition.getBeadPositions();
    }

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

    public GeometricalParameters getGeometricalParameters() {
        return geometry.getParameters();
    }

    public int getAcceptedIterations() {
        return acceptedIterations;
    }

    /**
     *
     * @return The SystemAnalyzer object registered to this PolymerSimulator.
     */
    public SystemAnalyzer getSystemAnalyzer() {
        return systemAnalyzer;
    }

    public int getNumChainMoves() {
        return numChainMoves;
    }

    public int getAcceptedChainMoves() {
        return acceptedChainMoves;
    }

    public EnergeticsConstants getEnergeticsConstants() {
        return systemAnalyzer.getEnergeticsConstants();
    }
    // </editor-fold>

}
