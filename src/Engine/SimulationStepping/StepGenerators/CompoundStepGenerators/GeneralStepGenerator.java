/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.SimulationStepping.StepGenerators.CompoundStepGenerators;

import Engine.SimulationStepping.StepGenerators.StepGenerator;
import Engine.SimulationStepping.StepTypes.MoveType;
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
        EnumMap<MoveType, Double> weights = makeDefaultWeights();
        return new GeneralStepGenerator(weights);
    }

    static private EnumMap<MoveType, Double> makeDefaultWeights() {
        EnumMap<MoveType, Double> weights = new EnumMap<>(MoveType.class);
        weights.put(MoveType.SINGLE_BEAD, 1.);
        weights.put(MoveType.SINGLE_CHAIN, .00);//.01
        weights.put(MoveType.SINGLE_WALL_RESIZE, .0001);//.0001
        weights.put(MoveType.REPTATION, .00);//.01
        return weights;
    }

    private final EnumMap<MoveType, Double> weights;
    private final double weightSum;

    public GeneralStepGenerator(EnumMap<MoveType, Double> weights) {
        this.weights = new EnumMap<>(weights);
        weightSum = calculateWeightSum();
    }

    @Override
    public SimulationStep generateStep(SystemAnalyzer systemAnalyzer) {
        MoveType moveType = getRandomMoveType();
        return moveType.getSimulationStep(systemAnalyzer);
    }

    private MoveType getRandomMoveType() {
        double randomDouble = random.nextDouble() * weightSum;

        for (Entry<MoveType, Double> entry : weights.entrySet()) {
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
