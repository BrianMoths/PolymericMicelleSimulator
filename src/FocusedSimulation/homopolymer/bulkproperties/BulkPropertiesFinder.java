/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.homopolymer.bulkproperties;

import FocusedSimulation.AbstractFocusedSimulation;
import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.simulationrunner.ConvergenceMonitor;
import FocusedSimulation.simulationrunner.StatisticsTracker.TrackableVariable;
import SGEManagement.Input;
import SGEManagement.Input.InputBuilder;
import SystemAnalysis.FullStressTrackable;
import java.io.FileNotFoundException;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author bmoths
 */
public class BulkPropertiesFinder<U extends BulkPropertiesResultsWriter> extends AbstractFocusedSimulation<U> {

    public static void main(String[] args) {
        final Input input = readInput(args);
        try {
            final BulkPropertiesFinder densityFinder;
            densityFinder = makeBulkPropertiesFinderWithDefaultWriter(input);
            densityFinder.doSimulation();
            densityFinder.closeOutputWriter();
            System.exit(0);//47 seconds
        } catch (FileNotFoundException ex) {
            System.out.println("File not able to be opened");
        }
    }

    private static Input readInput(String[] args) {
        if (args.length == 0) {
            final double verticalScaleFactor = .1;
            final double horizontalScaleFactor = 4;

            InputBuilder inputBuilder = BulkPropertiesJobMaker.makeRescaleInputBuilderWithHorizontalRescaling(verticalScaleFactor, horizontalScaleFactor, 0);
            inputBuilder.getJobParametersBuilder().setNumAnneals(1);
            inputBuilder.getJobParametersBuilder().setNumSimulationTrials(1);
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumIterationsPerAnneal(100);
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumIterationsPerSample(1000);
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumSamples(2000);
            inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//            final EnergeticsConstantsBuilder energeticsConstantsBuilder = inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder();
//            energeticsConstantsBuilder.setHardOverlapCoefficient(0);
//            energeticsConstantsBuilder.setAAOverlapCoefficient(energeticsConstantsBuilder.getBBOverlapCoefficient());
//            energeticsConstantsBuilder.setABOverlapCoefficient(-energeticsConstantsBuilder.getAAOverlapCoefficient());
//            final PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(PolymerChain.makeSingletChainOfType(false), 500);
//            polymerCluster.addChainMultipleTimes(PolymerChain.makeSingletChainOfType(true), 500);
//            polymerCluster.setConcentrationInWater(.3);
//            inputBuilder.getSystemParametersBuilder().setPolymerCluster(polymerCluster);
            return inputBuilder.buildInput();
        } else if (args.length == 1) {
            final String fileName = args[0];
            return Input.readInputFromFile(fileName);
        } else {
            throw new IllegalArgumentException("At most one input allowed");
        }
    }

    private static BulkPropertiesFinder<BulkPropertiesResultsWriter> makeBulkPropertiesFinderWithDefaultWriter(Input input) throws FileNotFoundException {
        return new BulkPropertiesFinder<>(input, new BulkPropertiesResultsWriter(input));
    }

    private final LeftBeadsTrackable numLeftBeadsTrackable;

    protected BulkPropertiesFinder(Input input, U bulkPropertiesResultsWriter) throws FileNotFoundException {
        super(input, bulkPropertiesResultsWriter);
        final double systemFraction = .2;//.02
        numLeftBeadsTrackable = new LeftBeadsTrackable(systemFraction, simulationRunner);
    }

    //<editor-fold defaultstate="collapsed" desc="initialize">
    @Override
    protected void initializePositions() {
        polymerSimulator.reasonableRandomize();
    }

    @Override
    protected void registerTrackablesToSimulationRunner() {
        simulationRunner.trackVariable(numLeftBeadsTrackable);
        simulationRunner.trackVariable(TrackableVariable.SYSTEM_VOLUME);
        simulationRunner.trackVariable(TrackableVariable.AVERAGE_NON_NEIGHBOR_ENERGY);
        simulationRunner.trackVariable(TrackableVariable.NUMBER_DENSITY);
        simulationRunner.trackVariable(TrackableVariable.SYSTEM_ENERGY);
        simulationRunner.trackVariable(TrackableVariable.SYSTEM_SPRING_ENERGY);
        simulationRunner.trackVariable(TrackableVariable.SYSTEM_OVERLAP_ENERGY);
        simulationRunner.trackVariable(TrackableVariable.SYSTEM_ENTROPY);
        simulationRunner.trackVariable(TrackableVariable.IDEAL_GAS_PRESSURE);
        simulationRunner.trackVariable((FullStressTrackable.FULL_REGION_STRESS_TRACKABLE).getStress11Trackable());
        simulationRunner.trackVariable((FullStressTrackable.FULL_REGION_STRESS_TRACKABLE).getStress12Trackable());
        simulationRunner.trackVariable((FullStressTrackable.FULL_REGION_STRESS_TRACKABLE).getStress22Trackable());
        setVariablesTestedForConvergence(TrackableVariable.SYSTEM_VOLUME);
    }

    @Override
    protected void printInitialOutput() {
        outputWriter.printPressure();
    }
    //</editor-fold>

    @Override
    protected void analyzeAndPrintResults() {
        analyzeAndPrintDensity();
        outputWriter.printIdealGasPressure(simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.IDEAL_GAS_PRESSURE));
        analyzeAndPrintEnergyPerBead();
        analyzeAndPrintSpringEnergyPerBead();
        analyzeAndPrintOverlapEnergyPerBead();
        analyzeAndPrintEntropyPerBead();
        analyzeAndPrintNonNeighborEnergy();
        analyzeAndPrintCompressibilityFromNumberFluctuations();
        analyzeAndPrintCompressibilityFromVolumeFluctuations();

        outputWriter.printFullStress(simulationRunner);
    }

    @Override
    protected boolean isConverged() {
        return ConvergenceMonitor.isConvergedWithPrecision(simulationRunner.getStatisticsFor(TrackableVariable.SYSTEM_VOLUME), .1);
    }

    @Override
    protected void printFinalOutput() {
    }

    private void analyzeAndPrintDensity() throws IllegalArgumentException {
        DoubleWithUncertainty measuredDensity = simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.NUMBER_DENSITY);
        outputWriter.printMeasuredDensity(measuredDensity);
    }

    private void analyzeAndPrintEnergyPerBead() throws IllegalArgumentException {
        DoubleWithUncertainty measuredEnergy = simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.SYSTEM_ENERGY);
        DoubleWithUncertainty measuredEnergyPerBead = measuredEnergy.dividedBy(polymerSimulator.getNumBeads());
        outputWriter.printMeasuredEnergyPerBead(measuredEnergyPerBead);
    }

    private void analyzeAndPrintEntropyPerBead() throws IllegalArgumentException {
        DoubleWithUncertainty measuredEntropy = simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.SYSTEM_ENTROPY);
        DoubleWithUncertainty measuredEntropyPerBead = measuredEntropy.dividedBy(polymerSimulator.getNumBeads());
        outputWriter.printMeasuredEntropyPerBead(measuredEntropyPerBead);
    }

    private void analyzeAndPrintOverlapEnergyPerBead() throws IllegalArgumentException {
        DoubleWithUncertainty measuredOverlapEnergy = simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.SYSTEM_OVERLAP_ENERGY);
        DoubleWithUncertainty measuredOverlapEnergyPerBead = measuredOverlapEnergy.dividedBy(polymerSimulator.getNumBeads());
        outputWriter.printMeasuredOverlapEnergyPerBead(measuredOverlapEnergyPerBead);
    }

    private void analyzeAndPrintSpringEnergyPerBead() throws IllegalArgumentException {
        DoubleWithUncertainty measuredSpringEnergy = simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.SYSTEM_SPRING_ENERGY);
        DoubleWithUncertainty measuredSpringEnergyPerBead = measuredSpringEnergy.dividedBy(polymerSimulator.getNumBeads());
        outputWriter.printMeasuredSpringEnergyPerBead(measuredSpringEnergyPerBead);
    }

    private void analyzeAndPrintNonNeighborEnergy() {
        DoubleWithUncertainty nonNeighborEnergy = simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.AVERAGE_NON_NEIGHBOR_ENERGY);
        outputWriter.printNonNeighborEnergy(nonNeighborEnergy);
    }

    private void analyzeAndPrintCompressibilityFromNumberFluctuations() {
        final DescriptiveStatistics numLeftBeadsStatistics = simulationRunner.getStatisticsFor(numLeftBeadsTrackable);
        final double mean = numLeftBeadsStatistics.getMean();
        final double standardDeviation = numLeftBeadsStatistics.getStandardDeviation();
        final double variance = standardDeviation * standardDeviation;
        final double leftRegionVolume = numLeftBeadsTrackable.getLeftRegionVolume();
        final double temperature = simulationRunner.getPolymerSimulator().getEnergeticsConstants().getTemperature();

//        final double compressibilityValue = standardDeviation * standardDeviation / (mean * mean / (numLeftBeadsTrackable.getLeftRegionVolume() * numLeftBeadsTrackable.getLeftRegionVolume()) * simulationRunner.getPolymerSimulator().getEnergeticsConstants().getTemperature());
        final double compressibilityValue = leftRegionVolume * variance / (mean * mean * temperature);

        final double numSamples = numLeftBeadsStatistics.getN();
        final double standardError = standardDeviation / Math.sqrt(numSamples);
        final double standardDeviationUncertainty = standardError / Math.sqrt(2);

        final double meanRelativeUncertainty = standardError / mean;
        final double standardDeviationRelativeUncertainty = standardDeviationUncertainty / standardDeviation;
        final double compressibiltyRelativeUncertainty = 2 * Math.sqrt((standardDeviationRelativeUncertainty * standardDeviationRelativeUncertainty) + (meanRelativeUncertainty * meanRelativeUncertainty));
        final double compressibiltyUncertainty = compressibilityValue * compressibiltyRelativeUncertainty;

        final DoubleWithUncertainty compressibility = new DoubleWithUncertainty(compressibilityValue, compressibiltyUncertainty);
        outputWriter.printCompressibilityFromNumber(compressibility);
    }

    private void analyzeAndPrintCompressibilityFromVolumeFluctuations() {
        final double temperature = simulationRunner.getPolymerSimulator().getEnergeticsConstants().getTemperature();
        final DescriptiveStatistics volumeStatistics = simulationRunner.getStatisticsFor(TrackableVariable.SYSTEM_VOLUME);
        final double meanVolume = volumeStatistics.getMean();
        final double volumeStandardDeviation = volumeStatistics.getStandardDeviation();
        final long numSamples = volumeStatistics.getN();
        final double compressibilityValue = volumeStandardDeviation * volumeStandardDeviation / (meanVolume * temperature);

        final double compressibilityUncertainty = compressibilityValue * Math.sqrt((2 + volumeStandardDeviation / meanVolume) / numSamples);

        final DoubleWithUncertainty compressibility = new DoubleWithUncertainty(compressibilityValue, compressibilityUncertainty);
        outputWriter.printCompressibilityFromVolume(compressibility);

    }

}
