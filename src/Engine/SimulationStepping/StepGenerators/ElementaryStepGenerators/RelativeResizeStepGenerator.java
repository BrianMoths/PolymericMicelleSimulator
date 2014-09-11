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

    static private final double defaultMaxScalingFactor = .01;
    private final int resizeDimension;
    static private final Random random;

    static {
        random = new Random();
    }

    static public SimulationStep getHorizontalRelativeResizeStep(SystemAnalyzer systemAnalyzer) {
        RelativeResizeStepGenerator relativeResizeStepGenerator = new RelativeResizeStepGenerator();
        return relativeResizeStepGenerator.generateStep(systemAnalyzer);
    }

    static public SimulationStep getHorizontalRelativeResizeStep(SystemAnalyzer systemAnalyzer, double maxScalingFactor) {
        RelativeResizeStepGenerator relativeResizeStepGenerator = new RelativeResizeStepGenerator(maxScalingFactor);
        return relativeResizeStepGenerator.generateStep(systemAnalyzer);
    }

    static public SimulationStep getRelativeResizeStep(SystemAnalyzer systemAnalyzer, int resizeDimension) {
        RelativeResizeStepGenerator relativeResizeStepGenerator = new RelativeResizeStepGenerator(defaultMaxScalingFactor, resizeDimension);
        return relativeResizeStepGenerator.generateStep(systemAnalyzer);
    }

    static public SimulationStep getRelativeResizeStep(SystemAnalyzer systemAnalyzer, double maxScalingFactor, int resizeDimension) {
        RelativeResizeStepGenerator relativeResizeStepGenerator = new RelativeResizeStepGenerator(maxScalingFactor, resizeDimension);
        return relativeResizeStepGenerator.generateStep(systemAnalyzer);
    }

    private final double lowerRandom, randomRange;

    public RelativeResizeStepGenerator() {
        this(defaultMaxScalingFactor);
    }

    public RelativeResizeStepGenerator(double maxScalingFactor) {
        resizeDimension = 0;
        final double upperRandom = Math.log(1 + maxScalingFactor);
        lowerRandom = -upperRandom;
        randomRange = upperRandom - lowerRandom;
    }

    public RelativeResizeStepGenerator(double maxScalingFactor, int resizeDimension) {
        this.resizeDimension = resizeDimension;
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
        final double rescaleFactor = Math.exp(transformedScaleFactor);
        return rescaleFactor;
    }

    private double getSizeChange(double rescaleFactor, SystemAnalyzer systemAnalyzer) {
        final double changeFactor = rescaleFactor - 1;
        final double originalSize = systemAnalyzer.getSystemGeometry().getSizeOfDimension(resizeDimension);
        return changeFactor * originalSize;
    }

}
