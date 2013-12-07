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

        private double xSpringConstant, xEquilibriumPosition, ySpringConstant, yEquilibriumPosition, pressure;

        public ExternalEnergyCalculatorBuilder() {
            xSpringConstant = 0;
            xEquilibriumPosition = 0;
            ySpringConstant = 0;
            yEquilibriumPosition = 0;
            pressure = 0;
        }

        public ExternalEnergyCalculator build() {
            return new ExternalEnergyCalculator(this);
        }

        public double getxSpringConstant() {
            return xSpringConstant;
        }

        public void setxSpringConstant(double xSpringConstant) {
            this.xSpringConstant = xSpringConstant;
        }

        public double getxEquilibriumPosition() {
            return xEquilibriumPosition;
        }

        public void setxEquilibriumPosition(double xEquilibriumPosition) {
            this.xEquilibriumPosition = xEquilibriumPosition;
        }

        public double getySpringConstant() {
            return ySpringConstant;
        }

        public void setySpringConstant(double ySpringConstant) {
            this.ySpringConstant = ySpringConstant;
        }

        public double getyEquilibriumPosition() {
            return yEquilibriumPosition;
        }

        public void setyEquilibriumPosition(double yEquilibriumPosition) {
            this.yEquilibriumPosition = yEquilibriumPosition;
        }

        public double getPressure() {
            return pressure;
        }

        public void setPressure(double pressure) {
            this.pressure = pressure;
        }

    }
//</editor-fold>

    private final double xSpringConstant, xEquilibriumPosition, ySpringConstant, yEquilibriumPosition, pressure;

    public ExternalEnergyCalculator() {
        xSpringConstant = 0;
        xEquilibriumPosition = 0;
        ySpringConstant = 0;
        yEquilibriumPosition = 0;
        pressure = 0;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.xSpringConstant) ^ (Double.doubleToLongBits(this.xSpringConstant) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.xEquilibriumPosition) ^ (Double.doubleToLongBits(this.xEquilibriumPosition) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.ySpringConstant) ^ (Double.doubleToLongBits(this.ySpringConstant) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.yEquilibriumPosition) ^ (Double.doubleToLongBits(this.yEquilibriumPosition) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.pressure) ^ (Double.doubleToLongBits(this.pressure) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final ExternalEnergyCalculator other = (ExternalEnergyCalculator) object;
        return xSpringConstant == other.xSpringConstant
                && xEquilibriumPosition == other.xEquilibriumPosition
                && ySpringConstant == other.ySpringConstant
                && yEquilibriumPosition == other.yEquilibriumPosition
                && pressure == other.pressure;
    }

    private ExternalEnergyCalculator(ExternalEnergyCalculatorBuilder builder) {
        xSpringConstant = builder.xSpringConstant;
        xEquilibriumPosition = builder.xEquilibriumPosition;
        ySpringConstant = builder.ySpringConstant;
        yEquilibriumPosition = builder.yEquilibriumPosition;
        pressure = builder.pressure;
    }

    public double calculateExternalEnergy(double[] rMax) {
        final double x = rMax[0];
        final double y = rMax[1];

        final double xDisplacement = x - xEquilibriumPosition;
        final double yDisplacement = y - yEquilibriumPosition;


        return .5 * (xSpringConstant * xDisplacement * xDisplacement + ySpringConstant * yDisplacement * yDisplacement)
                + pressure * x * y;
    }

    //<editor-fold defaultstate="collapsed" desc="getters">
    public double getxSpringConstant() {
        return xSpringConstant;
    }

    public double getxEquilibriumPosition() {
        return xEquilibriumPosition;
    }

    public double getySpringConstant() {
        return ySpringConstant;
    }

    public double getyEquilibriumPosition() {
        return yEquilibriumPosition;
    }

    public double getPressure() {
        return pressure;
    }
    //</editor-fold>

}
