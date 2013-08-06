/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import java.util.Arrays;

/**
 *
 * @author bmoths
 */
public class SimulationStep {

    private int stepBead;
    private double[] initialPosition;
    private double[] finalPosition;

    public SimulationStep() {
        stepBead = 0;
    }

    public SimulationStep(SimulationStep simulationStep) {
        if (simulationStep != null) {
            stepBead = simulationStep.stepBead;

            if (simulationStep.initialPosition != null) {
                initialPosition = new double[simulationStep.initialPosition.length];
                System.arraycopy(simulationStep.initialPosition, 0, initialPosition, 0, initialPosition.length);
            }
            if (simulationStep.finalPosition != null) {
                finalPosition = new double[simulationStep.finalPosition.length];
                System.arraycopy(simulationStep.finalPosition, 0, finalPosition, 0, finalPosition.length);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Bead to be stepped: ").append(Integer.valueOf(stepBead)).append("\n");
        stringBuilder.append("Initial Position of step bead ").append(Arrays.toString(initialPosition)).append("\n");
        stringBuilder.append("Final Position of step bead: ").append(Arrays.toString(finalPosition)).append("\n");
        return stringBuilder.toString();
    }

    public int getStepBead() {
        return stepBead;
    }

    public void setStepBead(int stepBead) {
        this.stepBead = stepBead;
    }

    public double[] getInitialPosition() {
        return initialPosition;
    }

    public void setInitialPosition(double[] initialPosition) {
        this.initialPosition = Arrays.copyOf(initialPosition, initialPosition.length);
    }

    public double[] getFinalPosition() {
        return finalPosition;
    }

    public void setFinalPosition(double[] finalPosition) {
        this.finalPosition = Arrays.copyOf(finalPosition, finalPosition.length);
    }
}
