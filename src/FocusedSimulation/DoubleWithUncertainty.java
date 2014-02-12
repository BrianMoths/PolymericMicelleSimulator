/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation;

/**
 *
 * @author bmoths
 */
public class DoubleWithUncertainty {

    private final double value;
    private final double uncertainty;

    public DoubleWithUncertainty(double value, double uncertainty) {
        this.value = value;
        this.uncertainty = uncertainty;
    }

    public double getValue() {
        return value;
    }

    public double getUncertainty() {
        return uncertainty;
    }

}
