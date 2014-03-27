/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.Energetics;

import java.io.Serializable;

/**
 *
 * @author bmoths
 */
public class ExternalEnergyCalculator implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="builder class">
    static public class ExternalEnergyCalculatorBuilder {

        private double xTension, xQuadratic, yTension, yQuadratic, pressure;

        public ExternalEnergyCalculatorBuilder() {
            xTension = 0;
            xQuadratic = 0;
            yTension = 0;
            yQuadratic = 0;
            pressure = 0;
        }

        public ExternalEnergyCalculatorBuilder(ExternalEnergyCalculator externalEnergyCalculator) {
            xTension = externalEnergyCalculator.getxTension();
            xQuadratic = externalEnergyCalculator.getxQuadratic();
            yTension = externalEnergyCalculator.getyTension();
            yQuadratic = externalEnergyCalculator.getyQuadratic();
            pressure = externalEnergyCalculator.getPressure();
        }

        public ExternalEnergyCalculator build() {
            return new ExternalEnergyCalculator(this);
        }

        public ExternalEnergyCalculatorBuilder setxTensionAndQuadratic(double xTension, double xQuadratic) {
            this.xTension = xTension;
            this.xQuadratic = xQuadratic;
            return this;
        }

        public ExternalEnergyCalculatorBuilder setyTensionAndQuadratic(double yTension, double yQuadratic) {
            this.yTension = yTension;
            this.yQuadratic = yQuadratic;
            return this;
        }

        public ExternalEnergyCalculatorBuilder setXPositionAndSpringConstant(double xPosition, double xSpringConstant) {
            this.xTension = tensionFromPositionAndSpringConstant(xPosition, xSpringConstant);
            this.xQuadratic = quadraticFromPositionAndSpringConstant(xPosition, xSpringConstant);
            return this;
        }

        public ExternalEnergyCalculatorBuilder setYPositionAndSpringConstant(double yPosition, double ySpringConstant) {
            this.yTension = tensionFromPositionAndSpringConstant(yPosition, ySpringConstant);
            this.yQuadratic = quadraticFromPositionAndSpringConstant(yPosition, ySpringConstant);
            return this;
        }

        public ExternalEnergyCalculatorBuilder setPressure(double pressure) {
            this.pressure = pressure;
            return this;
        }

        public double getxEquilibriumPosition() {
            return equilbriumPositionFromTensionAndQuadratic(xTension, xQuadratic);
        }

        public double getxSpringConstant() {
            return springConstantFromTensionAndQuadratic(xTension, xQuadratic);
        }

        public double getyEquilibriumPosition() {
            return equilbriumPositionFromTensionAndQuadratic(yTension, yQuadratic);
        }

        public double getySpringConstant() {
            return springConstantFromTensionAndQuadratic(yTension, yQuadratic);
        }

        public double getxTension() {
            return xTension;
        }

        public double getxQuadratic() {
            return xQuadratic;
        }

        public double getyTension() {
            return yTension;
        }

        public double getyQuadratic() {
            return yQuadratic;
        }

        public double getPressure() {
            return pressure;
        }

    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="utility functions">
    static private double tensionFromPositionAndSpringConstant(double position, double springConstant) {
        return -springConstant * position;
    }

    static private double quadraticFromPositionAndSpringConstant(double position, double springConstant) {
        return 0.5 * springConstant;
    }

    static private double equilbriumPositionFromTensionAndQuadratic(double tension, double quadratic) {
        return -tension / (2 * quadratic);
    }

    static private double springConstantFromTensionAndQuadratic(double tension, double quadratic) {
        return (2 * quadratic);
    }
    //</editor-fold>

    private final double xTension, xQuadratic, yTension, yQuadratic, pressure;

    public ExternalEnergyCalculator() {
        xTension = 0;
        xQuadratic = 0;
        yTension = 0;
        yQuadratic = 0;
        pressure = 0;
    }

    private ExternalEnergyCalculator(ExternalEnergyCalculatorBuilder builder) {
        xTension = builder.xTension;
        xQuadratic = builder.xQuadratic;
        yTension = builder.yTension;
        yQuadratic = builder.yQuadratic;
        pressure = builder.pressure;
    }

    //<editor-fold defaultstate="collapsed" desc="equals and hashcode">
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.xTension) ^ (Double.doubleToLongBits(this.xTension) >>> 32));
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.xQuadratic) ^ (Double.doubleToLongBits(this.xQuadratic) >>> 32));
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.yTension) ^ (Double.doubleToLongBits(this.yTension) >>> 32));
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.yQuadratic) ^ (Double.doubleToLongBits(this.yQuadratic) >>> 32));
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.pressure) ^ (Double.doubleToLongBits(this.pressure) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExternalEnergyCalculator other = (ExternalEnergyCalculator) obj;
        if (Double.doubleToLongBits(this.xTension) != Double.doubleToLongBits(other.xTension)) {
            return false;
        }
        if (Double.doubleToLongBits(this.xQuadratic) != Double.doubleToLongBits(other.xQuadratic)) {
            return false;
        }
        if (Double.doubleToLongBits(this.yTension) != Double.doubleToLongBits(other.yTension)) {
            return false;
        }
        if (Double.doubleToLongBits(this.yQuadratic) != Double.doubleToLongBits(other.yQuadratic)) {
            return false;
        }
        if (Double.doubleToLongBits(this.pressure) != Double.doubleToLongBits(other.pressure)) {
            return false;
        }
        return true;
    }
    //</editor-fold>

    public double calculateExternalEnergy(double[] rMax) {
        final double x = rMax[0];
        final double y = rMax[1];

        return xTension * x + xQuadratic * x * x + yTension * y + yQuadratic * y * y + pressure * x * y;
    }

    //<editor-fold defaultstate="collapsed" desc="getters">
    public double getxEquilibriumPosition() {
        return equilbriumPositionFromTensionAndQuadratic(xTension, xQuadratic);
    }

    public double getxSpringConstant() {
        return springConstantFromTensionAndQuadratic(xTension, xQuadratic);
    }

    public double getyEquilibriumPosition() {
        return equilbriumPositionFromTensionAndQuadratic(yTension, yQuadratic);
    }

    public double getySpringConstant() {
        return springConstantFromTensionAndQuadratic(yTension, yQuadratic);
    }

    public double getxTension() {
        return xTension;
    }

    public double getxQuadratic() {
        return xQuadratic;
    }

    public double getyTension() {
        return yTension;
    }

    public double getyQuadratic() {
        return yQuadratic;
    }

    public double getPressure() {
        return pressure;
    }
//</editor-fold>

}
