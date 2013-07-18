/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

/**
 *
 * @author bmoths
 */
public class SimulationParameters {

    private final double interactionLength, coreLength, stepLength;

    public SimulationParameters() {
        interactionLength = 5;
        coreLength = interactionLength / 2;
        stepLength = interactionLength / 2;
    }

    public SimulationParameters(double stepLength, double interactionLength) {
        this.interactionLength = interactionLength;
        this.stepLength = stepLength;
        coreLength = interactionLength / 2;
    }

    public SimulationParameters(SimulationParameters simulationParameters) {
        this.stepLength = simulationParameters.stepLength;
        this.interactionLength = simulationParameters.interactionLength;
        coreLength = interactionLength / 2;
    }

    public double getStepLength() {
        return stepLength;
    }

    public double getInteractionLength() {
        return interactionLength;
    }

    public double getCoreLength() {
        return coreLength;
    }
}
