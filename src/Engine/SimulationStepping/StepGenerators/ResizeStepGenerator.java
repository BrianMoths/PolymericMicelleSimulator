/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping.StepGenerators;

import Engine.SimulationStepping.StepTypes.SimulationStep;
import Engine.SimulationStepping.StepTypes.SingleWallResizeStep;
import Engine.SystemAnalyzer;
import java.util.Random;

/**
 *
 * @author bmoths
 */
public class ResizeStepGenerator implements StepGenerator {

    static private Random random;
    static private final int resizeDimension = 0;

    static {
        random = new Random();
    }

    private final double resizeStepChance;
    private final double lowerRandom, randomRange;

    public ResizeStepGenerator() {
        this(.01, .0001);
    }

    public ResizeStepGenerator(double maxScalingFactor, double resizeStepChance) {
        this.resizeStepChance = resizeStepChance;

        lowerRandom = Math.sqrt(1 / (1 + maxScalingFactor));
        double upperRandom = Math.sqrt(1 + maxScalingFactor);
        randomRange = upperRandom - lowerRandom;
    }

    @Override
    public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
        if (random.nextDouble() < resizeStepChance) {
            return generateResizeMove(systemAnalyzer);
        } else {
            return BeadMoveGenerator.getBeadMove(systemAnalyzer);
        }
    }

    public SimulationStep generateResizeMove(SystemAnalyzer systemAnalyzer) {
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
        final double originalSize = systemAnalyzer.getSystemGeometry().getRMax()[resizeDimension];
        return changeFactor * originalSize;
    }

}
