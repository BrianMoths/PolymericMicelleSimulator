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

    private double stepLength, interactionLength;

    public SimulationParameters() {
        interactionLength = 5;
        stepLength = interactionLength / 2;
    }

    public double getStepLength() {
        return stepLength;
    }

    public void setStepLength(double stepLength) {
        this.stepLength = stepLength;
    }

    public double getInteractionLength() {
        return interactionLength;
    }

    public void setInteractionLength(double interactionLength) {
        this.interactionLength = interactionLength;
    }
}
