/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SGEManagement;

import Engine.Energetics.ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder;
import Engine.PolymerTopology.PolymerChain;
import Engine.PolymerTopology.PolymerCluster;
import SGEManagement.Input.InputBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class SGEManager {

    public static void main(String[] args) {
        final List<Input> inputs = makeInputs();
        JobSubmitter.submitJobs(inputs);
    }

    static private List<Input> makeInputs() {
        List<Input> inputs = new ArrayList<>();

        int jobNumber = 1;
//        int numChains = 100 / 3;
        double a = 10;
        double b = 50 / 3;
        double density = .05;
        Input input;
        double verticalRescaleFactor;
        final double horizontalRescaleFactor = 3;

///////////Different vertical sizes of the bridge.
//        scaleFactor = 1. / 10.;
//        input = makeRescaleInput(scaleFactor, jobNumber);
//        inputs.add(input);
//        jobNumber++;
//
//        scaleFactor = 1. / 5.;
//        input = makeRescaleInput(scaleFactor, jobNumber);
//        inputs.add(input);
//        jobNumber++;
//
//        scaleFactor = 1. / 3.;
//        input = makeRescaleInput(scaleFactor, jobNumber);
//        inputs.add(input);
//        jobNumber++;
//
//        scaleFactor = 1. / 1.5;
//        input = makeRescaleInput(scaleFactor, jobNumber);
//        inputs.add(input);
//        jobNumber++;
//
//        scaleFactor = 1.;
//        input = makeRescaleInput(scaleFactor, jobNumber);
//        inputs.add(input);
//        jobNumber++;
//
//        scaleFactor = 1.5;
//        input = makeRescaleInput(scaleFactor, jobNumber);
//        inputs.add(input);
//        jobNumber++;

        verticalRescaleFactor = .07;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;

        verticalRescaleFactor = .18;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;

        verticalRescaleFactor = .3;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;

        verticalRescaleFactor = 1;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;

        verticalRescaleFactor = 3;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;

        verticalRescaleFactor = 10;
        input = makeRescaleInput(verticalRescaleFactor, horizontalRescaleFactor, jobNumber);
        inputs.add(input);
        jobNumber++;
        return inputs;


///////////////////Repeatability
//        InputBuilder inputBuilder;
//
//        inputBuilder = SGEManager.makeRescaleInputBuilder(.5, jobNumber);
//        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
//        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
//        inputBuilder.getSystemParametersBuilder().setAspectRatio(1);
//        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
//        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
//        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//        inputs.add(inputBuilder.buildInput());
//        jobNumber++;
//
//        inputBuilder = SGEManager.makeRescaleInputBuilder(.5, jobNumber);
//        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
//        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
//        inputBuilder.getSystemParametersBuilder().setAspectRatio(1);
//        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
//        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
//        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//        inputs.add(inputBuilder.buildInput());
//        jobNumber++;
//
//        inputBuilder = SGEManager.makeRescaleInputBuilder(.5, jobNumber);
//        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
//        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
//        inputBuilder.getSystemParametersBuilder().setAspectRatio(1);
//        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
//        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
//        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//        inputs.add(inputBuilder.buildInput());
//        jobNumber++;
//
//        inputBuilder = SGEManager.makeRescaleInputBuilder(.5, jobNumber);
//        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
//        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
//        inputBuilder.getSystemParametersBuilder().setAspectRatio(1);
//        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
//        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
//        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//        inputs.add(inputBuilder.buildInput());
//        jobNumber++;
//
//        inputBuilder = SGEManager.makeRescaleInputBuilder(.5, jobNumber);
//        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
//        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
//        inputBuilder.getSystemParametersBuilder().setAspectRatio(1);
//        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
//        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
//        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//        inputs.add(inputBuilder.buildInput());
//        jobNumber++;
//
//        inputBuilder = SGEManager.makeRescaleInputBuilder(.5, jobNumber);
//        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
//        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
//        inputBuilder.getSystemParametersBuilder().setAspectRatio(1);
//        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
//        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
//        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//        inputs.add(inputBuilder.buildInput());
//        jobNumber++;




/////////////Effect of aspect ratio on natural density.
//        inputBuilder = SGEManager.makeRescaleInputBuilder(.5, jobNumber);
//        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
//        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
//        inputBuilder.getSystemParametersBuilder().setAspectRatio(.1);
//        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
//        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
//        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//        inputs.add(inputBuilder.buildInput());
//        jobNumber++;
//
//        inputBuilder = SGEManager.makeRescaleInputBuilder(.5, jobNumber);
//        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
//        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
//        inputBuilder.getSystemParametersBuilder().setAspectRatio(.1);
//        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
//        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
//        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//        inputs.add(inputBuilder.buildInput());
//        jobNumber++;
//
//        inputBuilder = SGEManager.makeRescaleInputBuilder(.5, jobNumber);
//        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
//        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
//        inputBuilder.getSystemParametersBuilder().setAspectRatio(.1);
//        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
//        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
//        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//        inputs.add(inputBuilder.buildInput());
//        jobNumber++;
//
//        inputBuilder = SGEManager.makeRescaleInputBuilder(.5, jobNumber);
//        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
//        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
//        inputBuilder.getSystemParametersBuilder().setAspectRatio(.1);
//        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
//        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
//        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//        inputs.add(inputBuilder.buildInput());
//        jobNumber++;
//
//        inputBuilder = SGEManager.makeRescaleInputBuilder(.5, jobNumber);
//        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
//        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
//        inputBuilder.getSystemParametersBuilder().setAspectRatio(.3);
//        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
//        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
//        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//        inputs.add(inputBuilder.buildInput());
//        jobNumber++;
//
//        inputBuilder = SGEManager.makeRescaleInputBuilder(.5, jobNumber);
//        inputBuilder.getSystemParametersBuilder().getPolymerCluster().setConcentrationInWater(1);
//        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(new ExternalEnergyCalculator());
//        inputBuilder.getSystemParametersBuilder().setAspectRatio(.3);
//        inputBuilder.getJobParametersBuilder().setNumAnneals(10);
//        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(10);
//        inputBuilder.getJobParametersBuilder().setShouldIterateUntilConvergence(false);
//        inputs.add(inputBuilder.buildInput());
//        jobNumber++;
//
//        return inputs;
    }

    public static Input makeRescaleInput(final double scaleFactor, int jobNumber) {
        InputBuilder inputBuilder = makeRescaleInputBuilder(scaleFactor, jobNumber);
        final Input input = inputBuilder.buildInput();
        return input;
    }

    public static InputBuilder makeRescaleInputBuilder(final double scaleFactor, int jobNumber) {
        InputBuilder inputBuilder;
        inputBuilder = InputBuilder.getDefaultInputBuilder();
        final double aspectRatio = inputBuilder.getSystemParametersBuilder().getAspectRatio() / 3.5;
        inputBuilder.getSystemParametersBuilder().setAspectRatio(aspectRatio / scaleFactor);
        final int numChains = inputBuilder.getSystemParametersBuilder().getPolymerCluster().getNumChains();
        final int numBeadsPerChain = (int) Math.round(inputBuilder.getSystemParametersBuilder().getPolymerCluster().getNumBeadsPerChain());
        final PolymerChain polymerChain = PolymerChain.makeChainOfType(false, numBeadsPerChain);
        final PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, (int) (numChains * scaleFactor));
        polymerCluster.setConcentrationInWater(.05 * 3.5);
        inputBuilder.getSystemParametersBuilder().setPolymerCluster(polymerCluster);
        final ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder();
        externalEnergyCalculatorBuilder.setXPositionAndSpringConstant(16, 50);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(externalEnergyCalculatorBuilder.build());
        inputBuilder.getJobParametersBuilder().setJobNumber(jobNumber);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(70);
        return inputBuilder;
    }

    public static Input makeRescaleInput(final double verticalScale, final double horizontalScale, final int jobNumber) {
        final InputBuilder inputBuilder = makeRescaleInputBuilderWithHorizontalRescaling(verticalScale, horizontalScale, jobNumber);
        return inputBuilder.buildInput();
    }

    public static InputBuilder makeRescaleInputBuilderWithHorizontalRescaling(final double verticalScale, final double horizontalScale, int jobNumber) {
        InputBuilder inputBuilder;
        inputBuilder = InputBuilder.getDefaultInputBuilder();
        final double aspectRatio = inputBuilder.getSystemParametersBuilder().getAspectRatio() / 3.5;
        inputBuilder.getSystemParametersBuilder().setAspectRatio(aspectRatio * horizontalScale / verticalScale);
        final int numChains = inputBuilder.getSystemParametersBuilder().getPolymerCluster().getNumChains();
        final int numBeadsPerChain = (int) Math.round(inputBuilder.getSystemParametersBuilder().getPolymerCluster().getNumBeadsPerChain());
        final PolymerChain polymerChain = PolymerChain.makeChainOfType(false, numBeadsPerChain);
        final PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, (int) (numChains * verticalScale * horizontalScale));
        polymerCluster.setConcentrationInWater(.05 * 3.5);
        inputBuilder.getSystemParametersBuilder().setPolymerCluster(polymerCluster);
        final ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder();
        externalEnergyCalculatorBuilder.setXPositionAndSpringConstant(16 * horizontalScale, 50);
        inputBuilder.getSystemParametersBuilder().getEnergeticsConstantsBuilder().setExternalEnergyCalculator(externalEnergyCalculatorBuilder.build());
        inputBuilder.getJobParametersBuilder().setJobNumber(jobNumber);
        inputBuilder.getJobParametersBuilder().setNumSurfaceTensionTrials(70);
        return inputBuilder;
    }

}
