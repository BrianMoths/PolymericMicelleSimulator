/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping.StepGenerators.CompoundStepGenerators;

import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SimulationStepping.StepTypes.StepType;
import Engine.SimulationStepping.StepTypes.SimulationStep;
import Engine.SystemAnalyzer;
import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.Random;

/**
 *
 * @author bmoths
 */
public class GeneralStepGenerator implements StepGenerator {

    static private Random random;

    static {
        random = new Random();
    }

    static public GeneralStepGenerator defaultStepGenerator() {
        EnumMap<StepType, Double> weights = makeDefaultWeights();
        return new GeneralStepGenerator(weights);
    }

    static private EnumMap<StepType, Double> makeDefaultWeights() {
        EnumMap<StepType, Double> weights = new EnumMap<>(StepType.class);
        weights.put(StepType.SINGLE_BEAD, 1.);//1
        weights.put(StepType.SINGLE_CHAIN, .00);//.01
        weights.put(StepType.SINGLE_WALL_RESIZE, .0001);//.0001
        weights.put(StepType.REPTATION, .00);//.01
        weights.put(StepType.NO_STRETCH_WALL, .0000);//.0001
        return weights;
    }

    private final EnumMap<StepType, Double> weights;
    private final double weightSum;

    public GeneralStepGenerator(EnumMap<StepType, Double> weights) {
        this.weights = new EnumMap<>(weights);
        weightSum = calculateWeightSum();
    }

    @Override
    public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
        StepType moveType = getRandomMoveType();
        return moveType.getSimulationStep(systemAnalyzer);
    }

    private StepType getRandomMoveType() {
        double randomDouble = random.nextDouble() * weightSum;

        for (Entry<StepType, Double> entry : weights.entrySet()) {
            double weight = entry.getValue();
            if (randomDouble <= weight) {
                return entry.getKey();
            }
            randomDouble -= weight;
        }
        throw new AssertionError("I did not choose a step. This is a bug", null);
    }

    private double calculateWeightSum() {
        double partialWeightSum = 0;
        for (Double weight : weights.values()) {
            partialWeightSum += weight;
        }
        return partialWeightSum;
    }

}
