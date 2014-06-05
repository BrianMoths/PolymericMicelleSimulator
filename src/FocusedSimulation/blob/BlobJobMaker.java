/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.blob;

import Engine.Energetics.EnergeticsConstants.EnergeticsConstantsBuilder;
import Engine.Energetics.ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder;
import Engine.PolymerTopology.PolymerChain;
import Engine.PolymerTopology.PolymerCluster;
import Engine.SimulatorParameters.SystemParametersBuilder;
import FocusedSimulation.AbstractFocusedSimulation;
import FocusedSimulation.JobParameters.JobParametersBuilder;
import static FocusedSimulation.surfacetension.SurfaceTensionJobMaker.makeRescaleInputBuilderWithHorizontalRescaling;
import static FocusedSimulation.surfacetension.SurfaceTensionJobMaker.pathToFocusedSimulationClass;
import SGEManagement.Input;
import SGEManagement.Input.InputBuilder;
import SGEManagement.JobSubmitter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class BlobJobMaker {

    public static final String pathToFocusedSimulationClass = AbstractFocusedSimulation.pathToFocusedSimulation + "blob/BlobFinder";

    public static void main(String[] args) {
        final List<Input> inputs = makeInput(1);
        JobSubmitter.submitJobs(pathToFocusedSimulationClass, inputs);
    }

    private static List<Input> makeInput(int i) {
        final List<Input> inputs = new ArrayList<>();
        InputBuilder inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(.1, 4, 1);
        inputBuilder.getJobParametersBuilder().getSimulationRunnerParametersBuilder().setNumSamples(10_000);
        inputs.add(inputBuilder.buildInput());
        return inputs;
    }

    public static Input makeRescaleInput(final double verticalScale, final double horizontalScale, final int jobNumber) {
        final InputBuilder inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalScale, horizontalScale, jobNumber);
        return inputBuilder.buildInput();
    }

    public static InputBuilder makeRescaleInputBuilderWithHorizontalRescaling(final double verticalScale, final double horizontalScale, int jobNumber) {
        InputBuilder inputBuilder;
        inputBuilder = getDefaultInputBuilder();
        final double aspectRatio = inputBuilder.getSystemParametersBuilder().getAspectRatio();
        inputBuilder.getSystemParametersBuilder().setAspectRatio(aspectRatio * horizontalScale / verticalScale);
        PolymerCluster polymerCluster = getPolymerCluster(verticalScale, horizontalScale);
        inputBuilder.getSystemParametersBuilder().setPolymerCluster(polymerCluster);
        final ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder();
        externalEnergyCalculatorBuilder.setXPositionAndSpringConstant(16 * horizontalScale, 50);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculatorBuilder(externalEnergyCalculatorBuilder);
        inputBuilder.getJobParametersBuilder().setJobNumber(jobNumber);
        inputBuilder.getJobParametersBuilder().setNumAnneals(5);
        inputBuilder.getJobParametersBuilder().setNumSimulationTrials(4);
        inputBuilder.getSystemParametersBuilder().autosetCoreParameters();
        final EnergeticsConstantsBuilder energeticsConstantsBuilder = inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder();
        energeticsConstantsBuilder.setBBOverlapCoefficient(3 * energeticsConstantsBuilder.getBBOverlapCoefficient());
        energeticsConstantsBuilder.setHardOverlapCoefficient(3 * energeticsConstantsBuilder.getHardOverlapCoefficient());

        return inputBuilder;
    }

    private static PolymerCluster getPolymerCluster(final double verticalScale, final double horizontalScale) {
        final PolymerChain polymerChain = PolymerChain.makeChainOfType(false, defaultNumBeadsPerChain);
        final PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, (int) (defaultNumChains * verticalScale * horizontalScale));
        polymerCluster.setConcentrationInWater(defaultDensity);
        return polymerCluster;
    }

    //<editor-fold defaultstate="collapsed" desc="default input">
    static private InputBuilder getDefaultInputBuilder() {
        SystemParametersBuilder systemParametersBuilder = getDefaultSystemParametersBuilder();
        JobParametersBuilder jobParametersBuilder = JobParametersBuilder.getDefaultJobParametersBuilder();
        Input.InputBuilder inputBuilder = new SGEManagement.Input.InputBuilder();
        inputBuilder.setSystemParametersBuilder(systemParametersBuilder);
        inputBuilder.setJobParametersBuilder(jobParametersBuilder);
        return inputBuilder;
    }

    static private final double defaultAspectRatio = .0286;
    static private final double defaultOverlapCoefficient = -.126;//-.126
    static private final double defaultInteractionLength = 4.;
    static private final int defaultNumBeadsPerChain = 15;
    static private final int defaultNumChains = 75;
    static private final double defaultDensity = .06;

    private static SystemParametersBuilder getDefaultSystemParametersBuilder() {
        SystemParametersBuilder systemParametersBuilder = new SystemParametersBuilder();
        systemParametersBuilder.setAspectRatio(defaultAspectRatio);
        EnergeticsConstantsBuilder energeticsConstantsBuilder = EnergeticsConstantsBuilder.zeroEnergeticsConstantsBuilder();
        energeticsConstantsBuilder.setBBOverlapCoefficient(defaultOverlapCoefficient);
        systemParametersBuilder.setEnergeticsConstantsBuilder(energeticsConstantsBuilder);
        systemParametersBuilder.setInteractionLength(defaultInteractionLength);
        systemParametersBuilder.setPolymerCluster(getDefaultPolymerCluster());
        return systemParametersBuilder;
    }

    private static PolymerCluster getDefaultPolymerCluster() {
        PolymerChain polymerChain = PolymerChain.makeChainStartingWithA(0, defaultNumBeadsPerChain);
        PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, defaultNumChains);
        polymerCluster.setConcentrationInWater(defaultDensity);
        return polymerCluster;
    }

    static private final int defaultNumAnneals = 50;//50
    static private final int defaultNumSurfaceTensionTrials = 70;
    static private final int defaultJobNumber = 0;

    static public JobParametersBuilder getDefaultJobParametersBuilder() {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.setNumAnneals(defaultNumAnneals);
        jobParametersBuilder.setNumSimulationTrials(defaultNumSurfaceTensionTrials);
        jobParametersBuilder.setJobNumber(defaultJobNumber);
        return jobParametersBuilder;
    }
    //</editor-fold>

}
