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
        double tempCoreLength;

        interactionLength = 5;
        tempCoreLength = setCoreLength(interactionLength);
        coreLength = tempCoreLength;
        stepLength = interactionLength / 2;
    }

    public SimulationParameters(double stepLength, double interactionLength) {
        double tempCoreLength;

        this.interactionLength = interactionLength;
        this.stepLength = stepLength;

        tempCoreLength = setCoreLength(interactionLength);
        coreLength = tempCoreLength;
    }

    public SimulationParameters(SimulationParameters simulationParameters) {
        double tempCoreLength;

        this.stepLength = simulationParameters.stepLength;
        this.interactionLength = simulationParameters.interactionLength;

        tempCoreLength = setCoreLength(interactionLength);
        coreLength = tempCoreLength;
    }

    private double setCoreLength(double interactionLength) {
        return interactionLength;
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
