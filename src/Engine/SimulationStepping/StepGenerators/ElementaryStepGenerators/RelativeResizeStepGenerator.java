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

    private final double lowerRandom, randomRange;

    public RelativeResizeStepGenerator() {
//        this(maxScalingFactor, resizeStepChance);
        this(.01);//.0001
    }

    public RelativeResizeStepGenerator(double maxScalingFactor) {
        lowerRandom = Math.sqrt(1 / (1 + maxScalingFactor));
        double upperRandom = Math.sqrt(1 + maxScalingFactor);
        randomRange = upperRandom - lowerRandom;
    }

    @Override
    public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
        final double rescaleFactor = generateRescaleFactor();
        final double sizeChange = getSizeChange(rescaleFactor, systemAnalyzer);
        return new SingleWallResizeStep(resizeDimension, sizeChange);
    }

    private double generateRescaleFactor() {
        final double rootScaleFactor = lowerRandom + random.nextDouble() * randomRange;
        return rootScaleFactor * rootScaleFactor;
    }

    private double getSizeChange(double rescaleFactor, SystemAnalyzer systemAnalyzer) {
        final double changeFactor = rescaleFactor - 1;
        final double originalSize = systemAnalyzer.getSystemGeometry().getSizeOfDimension(resizeDimension);
        return changeFactor * originalSize;
    }

}
