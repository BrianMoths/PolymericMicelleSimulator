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

    private final double stepLength, interactionLength;

    public SimulationParameters() {
        interactionLength = 5;
        stepLength = interactionLength / 2;
    }

    public SimulationParameters(double stepLength, double interactionLength) {
        this.interactionLength = interactionLength;
        this.stepLength = stepLength;
    }

    public SimulationParameters(SimulationParameters simulationParameters) {
        this.stepLength = simulationParameters.stepLength;
        this.interactionLength = simulationParameters.interactionLength;
    }

    public double getStepLength() {
        return stepLength;
    }

    public double getInteractionLength() {
        return interactionLength;
    }
}
