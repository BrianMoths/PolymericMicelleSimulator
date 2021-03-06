/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.Energetics.EnergeticsConstants;
import Engine.Energetics.EnergyEntropyChange;
import Engine.PolymerState.DiscretePolymerState;
import Engine.PolymerState.ImmutablePolymerState;
import Engine.PolymerState.PolymerPosition;
import Engine.PolymerState.PolymerState;
import Engine.PolymerState.SystemGeometry.GeometricalParameters;
import Engine.PolymerState.SystemGeometry.Implementations.PeriodicGeometry.PeriodicGeometryBuilder;
import Engine.PolymerState.SystemGeometry.Interfaces.SystemGeometry;
import Engine.PolymerTopology.PolymerCluster;
import Engine.SimulationStepping.StepGenerators.CompoundStepGenerators.GeneralStepGenerator;
import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SimulationStepping.StepTypes.SimulationStep;
import Engine.SimulationStepping.StepTypes.StepType;
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
        stepLength = Math.sqrt(physicalConstants.getTemperature() / (physicalConstants.getSpringConstant() / 2));
        geometricalParameters = new GeometricalParameters(stepLength, interactionLength);
        return geometricalParameters;
    }
    //</editor-fold>

    private static final long serialVersionUID = 0L;
    private final SystemGeometry geometry;
    private final PolymerPosition polymerPosition;
    private final PolymerState polymerState;
    private final SystemAnalyzer systemAnalyzer;
    private StepGenerator stepGenerator = GeneralStepGenerator.defaultStepGenerator();
    private EnergyEntropyChange energyEntropy;
    private AcceptanceStatistics acceptanceStatistics;

    /**
     * constructs a reasonable polymer simulator object
     */
    public PolymerSimulator() {

        PolymerCluster polymerCluster = PolymerCluster.makeDefaultPolymerCluster();
        EnergeticsConstants energeticsConstants = EnergeticsConstants.defaultEnergeticsConstants();
        geometry = makeDefaultGeometry(polymerCluster, energeticsConstants);

        resetCounters();
        acceptanceStatistics = new AcceptanceStatistics();
        polymerPosition = makePolymerPosition(polymerCluster, geometry);
        DiscretePolymerState discretePolymerState = new DiscretePolymerState(polymerCluster);
        polymerState = new PolymerState(discretePolymerState, polymerPosition, geometry);
        systemAnalyzer = new SystemAnalyzer(polymerState, energeticsConstants);
        energyEntropy = systemAnalyzer.computeEnergyEntropy();
    }

    public PolymerSimulator(SystemGeometry systemGeometry, PolymerCluster polymerCluster, EnergeticsConstants energeticsConstants) {

        this.geometry = systemGeometry;

        polymerPosition = new PolymerPosition(polymerCluster.getNumBeads(), geometry);

        DiscretePolymerState discretePolymerState = new DiscretePolymerState(polymerCluster);
        polymerState = new PolymerState(discretePolymerState, polymerPosition, geometry);

        resetCounters();
        acceptanceStatistics = new AcceptanceStatistics();

        systemAnalyzer = new SystemAnalyzer(polymerState, energeticsConstants);
        energyEntropy = systemAnalyzer.computeEnergyEntropy();
    }

    public PolymerSimulator(PolymerSimulator polymerSimulator) {
        geometry = polymerSimulator.geometry;
        polymerPosition = new PolymerPosition(polymerSimulator.polymerPosition);
        polymerState = new PolymerState(polymerSimulator.polymerState.getDiscretePolymerState(), polymerPosition, geometry);
        energyEntropy = polymerSimulator.energyEntropy;
        acceptanceStatistics = new AcceptanceStatistics(polymerSimulator.acceptanceStatistics);
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
        stringBuilder.append(energyEntropy.toString()).append("\n");
        stringBuilder.append("Total iterations performed: ").append(Integer.toString(getIterationNumber())).append("\n");
        stringBuilder.append("Number iterations accepted: ").append(Integer.toString(getAcceptedIterations())).append("\n");
        return stringBuilder.toString();
    }

    public synchronized void randomizePositions() {
        resetCounters();
        polymerPosition.randomize();
        energyEntropy = systemAnalyzer.computeEnergyEntropy();
    }

    public synchronized void columnRandomizePositions() {
        resetCounters();
        polymerPosition.columnRandomize();
        energyEntropy = systemAnalyzer.computeEnergyEntropy();
    }

    public synchronized void reasonableMiddleRandomize() {
        polymerState.reasonableMiddleRandomize();
    }

    public synchronized void reasonableColumnRandomize() {
        polymerState.reasonableColumnRandomize();
    }

    public synchronized void reasonableRandomize() {
        polymerState.reasonableRandomize();
    }

    public synchronized void linearInitialize() {
        polymerState.linearInitialize();
    }

    public synchronized void anneal() {
        resetCounters();
        polymerPosition.anneal();
        energyEntropy = systemAnalyzer.computeEnergyEntropy();
    }

    public synchronized void recenter() {
        polymerPosition.recenter();
    }

    public synchronized void rescaleBeadPositions(double rescaleFactor) {
        polymerState.rescaleBeadPositions(rescaleFactor);
        energyEntropy = systemAnalyzer.computeEnergyEntropy();
    }

    public void doIterations(int n) {
        for (int i = 0; i < n; i++) {
            synchronized (this) {
                doIteration();
            }
            if (Thread.interrupted()) {
                return;
            }
        }
    }

    public synchronized void doIteration() {
        SimulationStep simulationStep = stepGenerator.generateStep(systemAnalyzer);
        acceptanceStatistics.recordStepAttemptOfType(simulationStep.getMoveType());
        if (simulationStep.doStep(polymerState, systemAnalyzer)) {
            final EnergyEntropyChange energyEntropyChange = simulationStep.getEnergyEntropyChange();
            if (systemAnalyzer.isEnergeticallyAllowed(energyEntropyChange)) {
                energyEntropy = energyEntropy.incrementedBy(energyEntropyChange);
                acceptanceStatistics.recordStepAcceptanceOfType(simulationStep.getMoveType());
            } else {
                simulationStep.undoStep(polymerState);
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="manage counters">
    private void resetCounters() {
        acceptanceStatistics = new AcceptanceStatistics();
    }
    //</editor-fold>

    public synchronized void setBeadPositions(double[][] beadPositions) {
        polymerPosition.setBeadPositions(beadPositions);
        energyEntropy = systemAnalyzer.computeEnergyEntropy();
    }

    public StepGenerator getStepGenerator() {
        return stepGenerator;
    }

    public void setStepGenerator(StepGenerator stepGenerator) {
        this.stepGenerator = stepGenerator;
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
        return energyEntropy.getEnergy();
    }

    public double getEntropy() {
        return energyEntropy.getEntropy();
    }

    public double getFreeEnergy() {
        return energyEntropy.calculateFreeEnergy(systemAnalyzer.getEnergeticsConstants().getTemperature());
    }

    public EnergyEntropyChange getEnergyEntropy() {
        return energyEntropy;
    }

    public int getIterationNumber() {
        return acceptanceStatistics.getTotalAttemptedSteps();
    }

    public GeometricalParameters getGeometricalParameters() {
        return geometry.getGeometricalParameters();
    }

    public int getAcceptedIterations() {
        return acceptanceStatistics.getTotalAcceptedSteps();
    }

    /**
     *
     * @return The SystemAnalyzer object registered to this PolymerSimulator.
     */
    public SystemAnalyzer getSystemAnalyzer() {
        return systemAnalyzer;
    }

    public int getAttemptedStepsOfType(StepType stepType) {
        return acceptanceStatistics.getAttemptedStepsOfType(stepType);
    }

    public int getAcceptedStepsOfType(StepType stepType) {
        return acceptanceStatistics.getAcceptedStepsOfType(stepType);
    }

    public double getAcceptanceRateForStepOfType(StepType stepType) {
        return (double) getAcceptedStepsOfType(stepType) / getAttemptedStepsOfType(stepType);
    }

    public EnergeticsConstants getEnergeticsConstants() {
        return systemAnalyzer.getEnergeticsConstants();
    }

    public ImmutablePolymerState getPolymerState() {
        return polymerState;
    }
    // </editor-fold>

}
