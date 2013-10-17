/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

/**
 *
 * @author bmoths
 */
public class ExternalEnergyCalculator {

    //<editor-fold defaultstate="collapsed" desc="builder class">
    static public class ExternalEnergyCalculatorBuilder {

        double xTension, xQuadratic, yTension, yQuadratic, pressure;

        public ExternalEnergyCalculatorBuilder() {
            xTension = 0;
            xQuadratic = 0;
            yTension = 0;
            yQuadratic = 0;
            pressure = 0;
        }

        public ExternalEnergyCalculator build() {
            return new ExternalEnergyCalculator(this);
        }

        public double getxTension() {
            return xTension;
        }

        public void setxTension(double xTension) {
            this.xTension = xTension;
        }

        public double getxQuadratic() {
            return xQuadratic;
        }

        public void setxQuadratic(double xQuadratic) {
            this.xQuadratic = xQuadratic;
        }

        public double getyTension() {
            return yTension;
        }

        public void setyTension(double yTension) {
            this.yTension = yTension;
        }

        public double getyQuadratic() {
            return yQuadratic;
        }

        public void setyQuadratic(double yQuadratic) {
            this.yQuadratic = yQuadratic;
        }

        public double getPressure() {
            return pressure;
        }

        public void setPressure(double pressure) {
            this.pressure = pressure;
        }

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

    public double calculateExternalEnergy(double[] rMax) {
        final double x = rMax[0];
        final double y = rMax[1];

        return xTension * x + xQuadratic * x * x + yTension * y + yQuadratic * y * y - pressure * x * y;
    }

    //<editor-fold defaultstate="collapsed" desc="getters">
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
