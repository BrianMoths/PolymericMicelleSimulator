/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping.StepGenerators.ElementaryStepGenerators;

import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SimulationStepping.StepTypes.SimulationStep;
import Engine.SimulationStepping.StepTypes.SingleWallResizeStep;
import Engine.SystemAnalyzer;
import java.util.Random;

/**
 *
 * @author bmoths
 */
public class RelativeResizeStepGenerator implements StepGenerator {

    static private final int resizeDimension = 0;
    static private final Random random;

    static {
        random = new Random();
    }

    static public SimulationStep getRelativeResizeStep(SystemAnalyzer systemAnalyzer) {
        RelativeResizeStepGenerator relativeResizeStepGenerator = new RelativeResizeStepGenerator(systemAnalyzer.getNumBeads());
        return relativeResizeStepGenerator.generateStep(systemAnalyzer);
    }

    static public SimulationStep getRelativeResizeStep(SystemAnalyzer systemAnalyzer, double maxScalingFactor) {
        RelativeResizeStepGenerator relativeResizeStepGenerator = new RelativeResizeStepGenerator(maxScalingFactor, systemAnalyzer.getNumBeads());
        return relativeResizeStepGenerator.generateStep(systemAnalyzer);
    }

    private final double lowerRandom, randomRange, power;

    public RelativeResizeStepGenerator(int numBeads) {
        this(.01, numBeads);//.0001
    }

    public RelativeResizeStepGenerator(double maxScalingFactor, int numBeads) {
        power = calculatePowerForNumBeads(numBeads);
//        power = 1. / 2;
//        if (numBeads == 1) {
//            double upperRandom = Math.log(1 + maxScalingFactor);
////            double upperRandom = Math.log1p(maxScalingFactor);
//            lowerRandom = -upperRandom;
//            randomRange = upperRandom - lowerRandom;
//        } else {
//            double upperRandom = Math.pow(1 + maxScalingFactor, (1.0 - numBeads) / 2);
//            lowerRandom = 1 / upperRandom;
//            randomRange = upperRandom - lowerRandom;
//        }
        double upperRandom = Math.pow(1 + maxScalingFactor, power);
        lowerRandom = 1 / upperRandom;
        randomRange = upperRandom - lowerRandom;
    }

    private double calculatePowerForNumBeads(int numBeads) {
        //get rid of 1 if we don't want to include the wall as a dof.
        return (1. + numBeads) / 2.;
    }

    @Override
    public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
        final double rescaleFactor = generateRescaleFactor();
        final double sizeChange = getSizeChange(rescaleFactor, systemAnalyzer);
        return new SingleWallResizeStep(resizeDimension, sizeChange);
    }

    private double generateRescaleFactor() {
        final double transformedScaleFactor = lowerRandom + random.nextDouble() * randomRange;
//        return transformedScaleFactor * transformedScaleFactor;
//        if (numBeads == 1) {
//            return Math.exp(transformedScaleFactor);
//        } else {
//            return Math.pow(transformedScaleFactor, 2. / (1 - numBeads));
//        }
        final double rescaleFactor = Math.pow(transformedScaleFactor, 1. / power);
        return rescaleFactor;
    }

    private double getSizeChange(double rescaleFactor, SystemAnalyzer systemAnalyzer) {
        final double changeFactor = rescaleFactor - 1;
        final double originalSize = systemAnalyzer.getSystemGeometry().getSizeOfDimension(resizeDimension);
        return changeFactor * originalSize;
    }

}
