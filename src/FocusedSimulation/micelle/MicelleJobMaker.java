/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.micelle;

import Engine.Energetics.EnergeticsConstants.EnergeticsConstantsBuilder;
import Engine.PolymerTopology.PolymerCluster;
import Engine.SimulatorParameters.SystemParametersBuilder;
import FocusedSimulation.AbstractFocusedSimulation;
import FocusedSimulation.JobParameters.JobParametersBuilder;
import SGEManagement.Input;
import SGEManagement.Input.InputBuilder;
import SGEManagement.JobSubmitter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class MicelleJobMaker {

    public static final String pathToFocusedSimulationClass = AbstractFocusedSimulation.pathToFocusedSimulation + "micelle/MicelleFinder";

    public static void main(String[] args) {
        final List<Input> inputs = makeInputs();
        JobSubmitter.submitJobs(pathToFocusedSimulationClass, inputs);
    }

    static private List<Input> makeInputs() {
        //return makeMicelleInputs(1);
        return makeAsymmetryInputs(1);
    }

    private static List<Input> makeAsymmetryInputs(int jobNumber) {
        final double[] hydrophobicFractions = {.6, .65, .7, .75, .8};
        final double verticalScaleFactor = .2;//.3
        final double horizontalScaleFactor = 6;//6
        jobNumber = 1;
        List<Input> inputs = new ArrayList<>();
        for (double hydrophobicFraction : hydrophobicFractions) {
            InputBuilder inputBuilder = getDefaultInputDensityBuilder(hydrophobicFraction);
            inputBuilder.rescale(verticalScaleFactor, horizontalScaleFactor);
            inputBuilder.getJobParametersBuilder().setJobNumber(jobNumber);
            inputs.add(inputBuilder.buildInput());
            jobNumber++;
        }
        return inputs;
    }

    public static InputBuilder makeRescaleInputBuilderWithHorizontalRescaling(final double verticalScale, final double horizontalScale, int jobNumber) {
        InputBuilder inputBuilder = getDefaultInputDensityBuilder();
        inputBuilder.rescale(verticalScale, horizontalScale);
        inputBuilder.getJobParametersBuilder().setJobNumber(jobNumber);

        return inputBuilder;
    }

    //<editor-fold defaultstate="collapsed" desc="default input">
    static public InputBuilder getDefaultInputDensityBuilder() {
        return getDefaultInputDensityBuilder(defaultHydrophobicFraction);
    }

    static public InputBuilder getDefaultInputDensityBuilder(double hydrophobicFraction) {
        SystemParametersBuilder systemParametersBuilder = getDefaultSystemParametersBuilder(hydrophobicFraction);
        JobParametersBuilder jobParametersBuilder = getDefaultJobParametersBuilder();
        Input.InputBuilder inputBuilder = new SGEManagement.Input.InputBuilder();
        inputBuilder.setSystemParametersBuilder(systemParametersBuilder);
        inputBuilder.setJobParametersBuilder(jobParametersBuilder);
        return inputBuilder;
    }

    static private final double defaultAspectRatio = 1;
    static private final double defaultOverlapCoefficient = -.378;
    static private final double defaultInteractionLength = 4.;
    static private final int defaultNumBeadsPerChain = 200;
    static private final int defaultNumChains = 1;
    static public final double defaultDensity = .02;
    private static final double defaultHydrophobicFraction = .7;
    static private final int numSubblocks = 10;

    private static SystemParametersBuilder getDefaultSystemParametersBuilder() {
        return getDefaultSystemParametersBuilder(defaultHydrophobicFraction);
    }

    private static SystemParametersBuilder getDefaultSystemParametersBuilder(double hydrophobicFraction) {
        SystemParametersBuilder systemParametersBuilder = new SystemParametersBuilder();
        systemParametersBuilder.setAspectRatio(defaultAspectRatio);
        EnergeticsConstantsBuilder energeticsConstantsBuilder = EnergeticsConstantsBuilder.zeroEnergeticsConstantsBuilder();
        energeticsConstantsBuilder.setBBOverlapCoefficient(defaultOverlapCoefficient);
        energeticsConstantsBuilder.setAAOverlapCoefficient(0);
        energeticsConstantsBuilder.setABOverlapCoefficient(defaultOverlapCoefficient / 4);//defaultOverlapCoefficient / 2
        systemParametersBuilder.setEnergeticsConstantsBuilder(energeticsConstantsBuilder);
        systemParametersBuilder.setInteractionLength(defaultInteractionLength);
        systemParametersBuilder.autosetCoreParameters();
        systemParametersBuilder.setPolymerCluster(getDefaultPolymerCluster(hydrophobicFraction));
        return systemParametersBuilder;
    }

    private static PolymerCluster getDefaultPolymerCluster(double hydrophobicFraction) {
        return PolymerCluster.makePolymerCluster(hydrophobicFraction, defaultNumChains, defaultNumBeadsPerChain, numSubblocks, defaultDensity);
    }

    static private final int defaultNumAnneals = 5;//50
    static private final int defaultNumSurfaceTensionTrials = 20;
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
