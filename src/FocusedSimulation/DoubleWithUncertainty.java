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

    static public DoubleWithUncertainty ZERO = new DoubleWithUncertainty(0, 0);
    static public DoubleWithUncertainty ONE = new DoubleWithUncertainty(1, 0);
    private final double value;
    private final double uncertainty;

    public DoubleWithUncertainty(double value, double uncertainty) {
        this.value = value;
        this.uncertainty = uncertainty;
    }

    public DoubleWithUncertainty plus(double addend) {
        return new DoubleWithUncertainty(value + addend, getUncertainty());
    }

    public DoubleWithUncertainty plus(DoubleWithUncertainty addend) {
        return new DoubleWithUncertainty(value + addend.value, Math.sqrt(getVariance() + addend.getVariance()));
    }

    public DoubleWithUncertainty minus(double addend) {
        return new DoubleWithUncertainty(value - addend, getUncertainty());
    }

    public DoubleWithUncertainty minus(DoubleWithUncertainty addend) {
        return new DoubleWithUncertainty(value - addend.value, Math.sqrt(getVariance() + addend.getVariance()));
    }

    public DoubleWithUncertainty times(double factor) {
        return new DoubleWithUncertainty(value * factor, uncertainty * factor);
    }

    public DoubleWithUncertainty times(DoubleWithUncertainty factor) {
        final double productValue = value * factor.value;
        final double thisRelativeError = getRelativeError();
        final double factorRelativeError = factor.getRelativeError();
        double prodcutFractionalUncertainty = Math.sqrt(thisRelativeError * thisRelativeError + factorRelativeError * factorRelativeError);
        return new DoubleWithUncertainty(productValue, productValue * prodcutFractionalUncertainty);
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

    public DoubleWithUncertainty dividedBy(DoubleWithUncertainty divisor) {
        return times(divisor.reciprocal());
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
