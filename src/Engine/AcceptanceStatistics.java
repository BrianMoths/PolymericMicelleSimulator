/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import Engine.SimulationStepping.StepTypes.StepType;
import java.util.EnumMap;

/**
 *
 * @author bmoths
 */
public class AcceptanceStatistics {

    private final EnumMap<StepType, Integer> attemptedIterations, acceptedIterations;

    public AcceptanceStatistics() {
        attemptedIterations = new EnumMap<>(StepType.class);
        acceptedIterations = new EnumMap<>(StepType.class);
        initiatlizeEnumMaps();
    }

    private void initiatlizeEnumMaps() {
        for (StepType stepType : StepType.values()) {
            attemptedIterations.put(stepType, 0);
            acceptedIterations.put(stepType, 0);
        }
    }

    public AcceptanceStatistics(AcceptanceStatistics acceptanceStatistics) {
        attemptedIterations = new EnumMap<>(acceptanceStatistics.attemptedIterations);
        acceptedIterations = new EnumMap<>(acceptanceStatistics.acceptedIterations);
    }

    public void attemptStepOfType(StepType stepType) {
        attemptedIterations.put(stepType, attemptedIterations.get(stepType) + 1);
    }

    public void acceptStepOfType(StepType stepType) {
        acceptedIterations.put(stepType, acceptedIterations.get(stepType) + 1);
    }

    public double getAcceptanceRateOfType(StepType stepType) {
        return (double) getAcceptedStepsOfType(stepType) / getAttemptedStepsOfType(stepType);
    }

    public double getAggregateAcceptanceRate() {
        return (double) getTotalAcceptedSteps() / getTotalAttemptedSteps();
    }

    public int getTotalAcceptedSteps() {
        int numAcceptMoves = 0;
        for (StepType stepType : StepType.values()) {
            numAcceptMoves += getAcceptedStepsOfType(stepType);
        }
        return numAcceptMoves;
    }

    public int getTotalAttemptedSteps() {
        int numAttemptedMoves = 0;
        for (StepType stepType : StepType.values()) {
            numAttemptedMoves += getAttemptedStepsOfType(stepType);
        }
        return numAttemptedMoves;
    }

    public int getAttemptedStepsOfType(StepType stepType) {
        return attemptedIterations.get(stepType);
    }

    public int getAcceptedStepsOfType(StepType stepType) {
        return acceptedIterations.get(stepType);
    }

}
