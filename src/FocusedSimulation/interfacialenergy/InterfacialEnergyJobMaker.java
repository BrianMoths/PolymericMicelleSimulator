/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation.interfacialenergy;

import Engine.Energetics.EnergeticsConstants.EnergeticsConstantsBuilder;
import Engine.Energetics.ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder;
import Engine.PolymerTopology.PolymerChain;
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
 * @author brian
 */
public class InterfacialEnergyJobMaker {

    public static final String pathToFocusedSimulationClass = AbstractFocusedSimulation.pathToFocusedSimulation + "interfacialenergy/InterfacialEnergyFinder";

    public static void main(String[] args) {
        final List<Input> inputs = makeInputs();
        JobSubmitter.submitJobs(pathToFocusedSimulationClass, inputs);
    }

    static private List<Input> makeInputs() {
        return makePressureInputs(1);
    }

    private static List<Input> makeInterfacialEnergyInputs(int jobNumber) {
        final double[] horizontalRescaleFactors = {1, 1.5, 2};
        final double[] verticalRescaleFactors = {1, 1.5, 2};
        final String jobString = "InterfacialEnergy";

        List<Input> inputs = new ArrayList<>();
        for (double verticalRescaleFactor : verticalRescaleFactors) {
            for (double horizontalRescaleFactor : horizontalRescaleFactors) {
                InputBuilder inputBuilder = makeRescaledInputBuilder(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
                inputBuilder.getJobParametersBuilder().setJobNumber(jobNumber);
                inputBuilder.getJobParametersBuilder().setJobString(jobString);
                inputs.add(inputBuilder.buildInput());
                jobNumber++;
            }
        }
        return inputs;
    }

    private static List<Input> makePressureInputs(int jobNumber) {
        final double horizontalRescaleFactor = 1;
        final double[] verticalRescaleFactors = {1, 2};
        final double[] pressures = {.05, .3, .1};
        final String jobString = "InterfaceFinitePressure";

        List<Input> inputs = new ArrayList<>();
        for (double verticalRescaleFactor : verticalRescaleFactors) {
            for (double pressure : pressures) {
                InputBuilder inputBuilder = makeRescaledInputBuilder(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
                ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder();
                externalEnergyCalculatorBuilder.setPressure(pressure);
                inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculatorBuilder(externalEnergyCalculatorBuilder);
                inputBuilder.getJobParametersBuilder().setJobNumber(jobNumber);
                inputBuilder.getJobParametersBuilder().setJobString(jobString);
                inputs.add(inputBuilder.buildInput());
                jobNumber++;
            }
        }
        return inputs;
    }

    public static InputBuilder makeRescaledInputBuilder(final double verticalScale, final double horizontalScale, int jobNumber) {
        InputBuilder inputBuilder = getDefaultInputBuilder();
        inputBuilder.rescale(verticalScale, horizontalScale);
        inputBuilder.getSystemParametersBuilder().setPolymerCluster(getRescaledDefaultPolymerCluster(horizontalScale, verticalScale));
        inputBuilder.getJobParametersBuilder().setJobNumber(jobNumber);
        return inputBuilder;
    }

    //<editor-fold defaultstate="collapsed" desc="default input">
    static private InputBuilder getDefaultInputBuilder() {
        SystemParametersBuilder systemParametersBuilder = getDefaultSystemParametersBuilder();
        JobParametersBuilder jobParametersBuilder = JobParametersBuilder.getDefaultJobParametersBuilder();
        jobParametersBuilder.getSimulationRunnerParametersBuilder().setNumIterationsPerSample(10000);//20000
        jobParametersBuilder.getSimulationRunnerParametersBuilder().setNumSamples(4000);
        jobParametersBuilder.setNumAnneals(1);
        jobParametersBuilder.setNumSimulationTrials(10);
        Input.InputBuilder inputBuilder = new SGEManagement.Input.InputBuilder();
        inputBuilder.setSystemParametersBuilder(systemParametersBuilder);
        inputBuilder.setJobParametersBuilder(jobParametersBuilder);
        return inputBuilder;
    }

    static private final double defaultAspectRatio = .5;
    static private final double defaultOverlapCoefficient = -.378;
    static private final double defaultInteractionLength = 4.;
    static private final int defaultNumBeadsPerChain = 16;
    static private final int defaultNumChains = 30;
    static private final double defaultDensity = .7;//.7
    static private final double naturalDensity = .297;

    private static SystemParametersBuilder getDefaultSystemParametersBuilder() {
        SystemParametersBuilder systemParametersBuilder = new SystemParametersBuilder();
        systemParametersBuilder.setAspectRatio(defaultAspectRatio);
        EnergeticsConstantsBuilder energeticsConstantsBuilder = EnergeticsConstantsBuilder.zeroEnergeticsConstantsBuilder();
        energeticsConstantsBuilder.setBBOverlapCoefficient(defaultOverlapCoefficient);
        energeticsConstantsBuilder.setAAOverlapCoefficient(defaultOverlapCoefficient);
        energeticsConstantsBuilder.setABOverlapCoefficient(defaultOverlapCoefficient / 2);
        ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder();
        final double equilibriumSpringPosition = Math.sqrt(defaultNumBeadsPerChain * defaultNumChains / naturalDensity * defaultAspectRatio) * 1.1;
        final double springConstant = 10;
        externalEnergyCalculatorBuilder.setXPositionAndSpringConstant(equilibriumSpringPosition, springConstant);
        energeticsConstantsBuilder.setExternalEnergyCalculatorBuilder(externalEnergyCalculatorBuilder);
        systemParametersBuilder.setEnergeticsConstantsBuilder(energeticsConstantsBuilder);
        systemParametersBuilder.setInteractionLength(defaultInteractionLength);
        systemParametersBuilder.autosetCoreParameters();
        systemParametersBuilder.setPolymerCluster(getDefaultPolymerCluster());
        return systemParametersBuilder;
    }

    private static PolymerCluster getRescaledDefaultPolymerCluster(double horizontalScaling, double verticalScaling) {
        PolymerChain typeAChain = PolymerChain.makeChainOfType(true, defaultNumBeadsPerChain);
        PolymerChain typeBChain = PolymerChain.makeChainOfType(false, defaultNumBeadsPerChain);
        final int halfNumChains = (int) (defaultNumChains * horizontalScaling * verticalScaling / 2);
        PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(typeAChain, halfNumChains);
        polymerCluster.addChainMultipleTimes(typeBChain, halfNumChains);
        polymerCluster.setConcentrationInWater(defaultDensity);
        return polymerCluster;
    }

    private static PolymerCluster getDefaultPolymerCluster() {
        return getRescaledDefaultPolymerCluster(1., 1.);
    }

    static private final int defaultNumAnneals = 5;
    static private final int defaultNumSurfaceTensionTrials = 10;
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
