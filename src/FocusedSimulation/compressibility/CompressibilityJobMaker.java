/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.compressibility;

import Engine.Energetics.EnergeticsConstants.EnergeticsConstantsBuilder;
import Engine.Energetics.ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder;
import Engine.PolymerTopology.PolymerChain;
import Engine.PolymerTopology.PolymerCluster;
import Engine.SimulatorParameters.SystemParametersBuilder;
import FocusedSimulation.JobParameters.JobParametersBuilder;
import SGEManagement.Input;
import SGEManagement.Input.InputBuilder;

/**
 *
 * @author bmoths
 */
public class CompressibilityJobMaker {

//    public static void main(String[] args) {
//        final List<Input> inputs = makeInputs();
//        JobSubmitter.submitJobs(inputs);
//    }
//    static private List<Input> makeInputs() {
//        return testAspectRatioEffectOnDensity();
//    }
    //<editor-fold defaultstate="collapsed" desc="default input">
    static private InputBuilder getDefaultInputSurfaceTensionBuilder() {
        SystemParametersBuilder systemParametersBuilder = getDefaultSystemParametersBuilder();
        JobParametersBuilder jobParametersBuilder = JobParametersBuilder.getDefaultJobParametersBuilder();
        Input.InputBuilder inputBuilder = new SGEManagement.Input.InputBuilder();
        inputBuilder.setSystemParametersBuilder(systemParametersBuilder);
        inputBuilder.setJobParametersBuilder(jobParametersBuilder);
        return inputBuilder;
    }

    static private final double defaultAspectRatio = .0286;
    static private final double defaultOverlapCoefficient = -.06;
    static private final double defaultInteractionLength = 4.;
    static private final double defaultXPosition = 50;
    static private final double defaultSpringConstant = 10;
    static private final int defaultNumBeadsPerChain = 15;
    static private final int defaultNumChains = 75;
    static private final double defaultDensity = .0175;

    private static SystemParametersBuilder getDefaultSystemParametersBuilder() {
        SystemParametersBuilder systemParametersBuilder = new SystemParametersBuilder();
        systemParametersBuilder.setAspectRatio(defaultAspectRatio);
        EnergeticsConstantsBuilder energeticsConstantsBuilder = EnergeticsConstantsBuilder.zeroEnergeticsConstantsBuilder();
        energeticsConstantsBuilder.setBBOverlapCoefficient(defaultOverlapCoefficient);
        ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder();
        externalEnergyCalculatorBuilder.setXPositionAndSpringConstant(defaultXPosition, defaultSpringConstant);
        energeticsConstantsBuilder.setExternalEnergyCalculator(externalEnergyCalculatorBuilder.build());
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
        jobParametersBuilder.setNumSurfaceTensionTrials(defaultNumSurfaceTensionTrials);
        jobParametersBuilder.setJobNumber(defaultJobNumber);
        return jobParametersBuilder;
    }
    //</editor-fold>

}
