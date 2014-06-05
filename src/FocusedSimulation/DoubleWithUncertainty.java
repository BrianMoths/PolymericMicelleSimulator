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

    public DoubleWithUncertainty plus(DoubleWithUncertainty addend) {
        return new DoubleWithUncertainty(value + addend.value, Math.sqrt(getVariance() + addend.getVariance()));
    }

    public DoubleWithUncertainty times(double factor) {
        return new DoubleWithUncertainty(value * factor, uncertainty * factor);
    }

    public DoubleWithUncertainty negation() {
        return new DoubleWithUncertainty(-value, uncertainty);
    }

    public DoubleWithUncertainty reciprocal() {
        final double newValue = 1 / value;
        return new DoubleWithUncertainty(newValue, newValue * getRelativeError());
    }

    public DoubleWithUncertainty reciprocalTimes(double factor) {
        return reciprocal().times(factor);
    }

    public DoubleWithUncertainty dividedBy(double divisor) {
        return times(1 / divisor);
    }

    public DoubleWithUncertainty sqrt() {
        final double sqrtValue = Math.sqrt(value);
        final double sqrtUncertainty = getRelativeError() * sqrtValue / 2;
        return new DoubleWithUncertainty(sqrtValue, sqrtUncertainty);
    }

    public double getValue() {
        return value;
    }

    public double getUncertainty() {
        return uncertainty;
    }

    public double getVariance() {
        return getUncertainty() * getUncertainty();
    }

    public double getRelativeError() {
        return getUncertainty() / getValue();
    }

    @Override
    public String toString() {
        return value + " +/- " + uncertainty;
    }

}
