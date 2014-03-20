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
        RelativeResizeStepGenerator relativeResizeStepGenerator = new RelativeResizeStepGenerator();
        return relativeResizeStepGenerator.generateStep(systemAnalyzer);
    }

    static public SimulationStep getRelativeResizeStep(SystemAnalyzer systemAnalyzer, double maxScalingFactor) {
        RelativeResizeStepGenerator relativeResizeStepGenerator = new RelativeResizeStepGenerator(maxScalingFactor);
        return relativeResizeStepGenerator.generateStep(systemAnalyzer);
    }

    private final double lowerRandom, randomRange, power;

    public RelativeResizeStepGenerator() {
        this(.01);//.01
    }

    public RelativeResizeStepGenerator(double maxScalingFactor) {
        power = .5;
//        final double upperRandom = Math.pow(1 + maxScalingFactor, power);
//        lowerRandom = 1 / upperRandom;
        final double upperRandom = Math.log(1 + maxScalingFactor);
        lowerRandom = -upperRandom;
        randomRange = upperRandom - lowerRandom;

    }

    @Override
    public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
        final double rescaleFactor = generateRescaleFactor();
        final double sizeChange = getSizeChange(rescaleFactor, systemAnalyzer);
        return new SingleWallResizeStep(resizeDimension, sizeChange);
    }

    private double generateRescaleFactor() {
        final double transformedScaleFactor = lowerRandom + random.nextDouble() * randomRange;
//        final double rescaleFactor = Math.pow(transformedScaleFactor, 1. / power);
        final double rescaleFactor = Math.exp(transformedScaleFactor);
        return rescaleFactor;
    }

    private double getSizeChange(double rescaleFactor, SystemAnalyzer systemAnalyzer) {
        final double changeFactor = rescaleFactor - 1;
        final double originalSize = systemAnalyzer.getSystemGeometry().getSizeOfDimension(resizeDimension);
        return changeFactor * originalSize;
    }

}
