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
