/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.homopolymer.blob;

import Engine.PolymerTopology.PolymerChain;
import Engine.PolymerTopology.PolymerCluster;
import Engine.SimulationStepping.StepGenerators.CompoundStepGenerators.GeneralStepGenerator;
import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SimulationStepping.StepTypes.StepType;
import FocusedSimulation.AbstractFocusedSimulation;
import FocusedSimulation.DoubleWithUncertainty;
import FocusedSimulation.simulationrunner.StatisticsTracker.TrackableVariable;
import FocusedSimulation.homopolymer.compressibility.CompressibilityFinder;
import FocusedSimulation.homopolymer.compressibility.CompressibilityJobMaker;
import FocusedSimulation.homopolymer.surfacetension.SurfaceTensionResultsWriter;
import SGEManagement.Input;
import SGEManagement.Input.InputBuilder;
import SystemAnalysis.CenterDensityTrackable;
import SystemAnalysis.FractionalVolumeStressTrackable;
import SystemAnalysis.NumCenterBeadsTrackable;
import java.io.FileNotFoundException;
import java.util.EnumMap;

/**
 *
 * @author bmoths
 */
public class BlobFinder extends AbstractFocusedSimulation<BlobResultsWriter> {

    static private final DoubleWithUncertainty SURFACE_TENSION = new DoubleWithUncertainty(.8, .05);
    static public final DoubleWithUncertainty COMPRESSIBILITY = new DoubleWithUncertainty(.223, .003);
    public static final DoubleWithUncertainty NATURAL_DENSITY = new DoubleWithUncertainty(.2970, .0003);
    static private final double stressTrackableSizeFraction = .1;

    public static void main(String[] args) {
        final Input input = readInput(args);
        try {
            final BlobFinder blobFinder;
            blobFinder = new BlobFinder(input);
            blobFinder.doSimulation();
            blobFinder.closeOutputWriter();
        } catch (FileNotFoundException ex) {
            System.out.println("File not able to be opened");
        }
    }

    private static Input readInput(String[] args) {
        if (args.length == 0) {
            final double verticalScaleFactor = .1;
            final double horizontalScaleFactor = 4;

            InputBuilder inputBuilder = BlobJobMaker.makeRescaleInputBuilderWithHorizontalRescaling(verticalScaleFactor, horizontalScaleFactor, 0);
            inputBuilder.getJobParametersBuilder().setNumAnneals(5);
            inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumSamples(200); //1000
            inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(true);
            inputBuilder.getJobParametersBuilder().setConvergencePrecision(.1);
            final double concentrationInWater = inputBuilder.getSystemParametersBuilder().getPolymerCluster().getConcentrationInWater();
//            final PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(PolymerChain.makeSingletChainOfType(false), 450);
            final PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(PolymerChain.makeChainOfType(false, 15), 30);
            polymerCluster.setConcentrationInWater(concentrationInWater);
//            polymerCluster.setConcentrationInWater(.3);
            inputBuilder.getSystemParametersBuilder().setPolymerCluster(polymerCluster);
            return inputBuilder.buildInput();
        } else if (args.length == 1) {
            final String fileName = args[0];
            return Input.readInputFromFile(fileName);
        } else {
            throw new IllegalArgumentException("At most one input allowed");
        }
    }

    private final FractionalVolumeStressTrackable middleStressTrackable;
    private final CenterDensityTrackable centerDensityTrackable;

    public BlobFinder(Input input) throws FileNotFoundException {
        super(input, new BlobResultsWriter(input));
        middleStressTrackable = new FractionalVolumeStressTrackable(stressTrackableSizeFraction);
        centerDensityTrackable = new CenterDensityTrackable(stressTrackableSizeFraction);
    }

    //<editor-fold defaultstate="collapsed" desc="initialize">
    @Override
    protected void initializePositions() {
        polymerSimulator.reasonableMiddleRandomize();
    }

//    @Override
//    protected void doMeasurementTrial() {
//        polymerSimulator.rescaleBeadPositions(.7);
//        polymerSimulator.recenter();
//        super.doMeasurementTrial(); //To change body of generated methods, choose Tools | Templates.
//    }
    @Override
    protected StepGenerator makeMainStepGenerator() {
        EnumMap<StepType, Double> stepweights = new EnumMap<>(StepType.class);
        stepweights.put(StepType.SINGLE_BEAD, 1.);
        stepweights.put(StepType.REPTATION, .1);
        stepweights.put(StepType.SINGLE_CHAIN, .01);
        return new GeneralStepGenerator(stepweights);
    }

    @Override
    protected StepGenerator makeInitialStepGenerator() {
        EnumMap<StepType, Double> stepweights = new EnumMap<>(StepType.class);
        stepweights.put(StepType.SINGLE_BEAD, 1.);
        stepweights.put(StepType.REPTATION, .01);
        stepweights.put(StepType.SINGLE_CHAIN, .01);
        return new GeneralStepGenerator(stepweights);
    }

    @Override
    protected void registerTrackablesToSimulationRunner() {
        simulationRunner.trackVariable(middleStressTrackable.getStress11Trackable());
        simulationRunner.trackVariable(middleStressTrackable.getStress12Trackable());
        simulationRunner.trackVariable(middleStressTrackable.getStress22Trackable());
        simulationRunner.trackVariable(centerDensityTrackable);
        simulationRunner.trackVariable(TrackableVariable.OCCUPIED_VOLUME);
        setVariablesTestedForConvergence(TrackableVariable.OCCUPIED_VOLUME);
    }

    @Override
    protected void printInitialOutput() {
        outputWriter.printEquilibriumDensity();
        outputWriter.printNaturalCompressibility();
    }
    //</editor-fold>

    @Override
    protected void analyzeAndPrintResults() {
        simulationRunner.getPolymerSimulator().recenter();
        analyzeAndPrintDensity();
        outputWriter.printStress(simulationRunner, middleStressTrackable);
    }

    private void analyzeAndPrintDensity() {
        final DoubleWithUncertainty occupiedVolume = simulationRunner.getRecentMeasurementForTrackedVariable(TrackableVariable.OCCUPIED_VOLUME);
        final DoubleWithUncertainty radius = occupiedVolume.dividedBy(Math.PI).sqrt();
        outputWriter.printEstimatedRadius(radius);
        final DoubleWithUncertainty measuredDensity = occupiedVolume.reciprocalTimes(simulationRunner.getPolymerSimulator().getNumBeads());
        outputWriter.printMeasuredDensity(measuredDensity);
        final DoubleWithUncertainty measuredMiddleDensity = simulationRunner.getRecentMeasurementForTrackedVariable(centerDensityTrackable);
        outputWriter.printMiddleDensity(measuredMiddleDensity);
        final DoubleWithUncertainty overpressure = SURFACE_TENSION.dividedBy(radius);
        outputWriter.printOverpressure(overpressure);
        final DoubleWithUncertainty expectedMiddleDensity = NATURAL_DENSITY.dividedBy(DoubleWithUncertainty.ONE.minus(COMPRESSIBILITY.times(overpressure)));
        outputWriter.printExpectedMiddleDensity(expectedMiddleDensity);
        final DoubleWithUncertainty expectedDensityIncrease = NATURAL_DENSITY.dividedBy(COMPRESSIBILITY.times(overpressure).reciprocal().minus(1));
        outputWriter.printExpectedDensityIncrease(expectedDensityIncrease);
        final DoubleWithUncertainty actualDensityIncrease = measuredMiddleDensity.minus(NATURAL_DENSITY);
        outputWriter.printActualDensityIncrease(actualDensityIncrease);
        outputWriter.printExpectedMechanicalPressure(overpressure.minus(measuredMiddleDensity));
    }

    @Override
    protected boolean isConverged() {
        return simulationRunner.isConverged(TrackableVariable.SYSTEM_VOLUME);
    }

    @Override
    protected void printFinalOutput() {
    }

}
