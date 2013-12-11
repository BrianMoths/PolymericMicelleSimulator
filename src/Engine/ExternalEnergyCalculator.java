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

//    //<editor-fold defaultstate="collapsed" desc="builder class">
//    static public class ExternalEnergyCalculatorBuilder {
//
//        private double xSpringConstant, xEquilibriumPosition, ySpringConstant, yEquilibriumPosition, pressure;
//
//        public ExternalEnergyCalculatorBuilder() {
//            xSpringConstant = 0;
//            xEquilibriumPosition = 0;
//            ySpringConstant = 0;
//            yEquilibriumPosition = 0;
//            pressure = 0;
//        }
//
//        public ExternalEnergyCalculator build() {
//            return new ExternalEnergyCalculator(this);
//        }
//
//        public double getxSpringConstant() {
//            return xSpringConstant;
//        }
//
//        public void setxSpringConstant(double xSpringConstant) {
//            this.xSpringConstant = xSpringConstant;
//        }
//
//        public double getxEquilibriumPosition() {
//            return xEquilibriumPosition;
//        }
//
//        public void setxEquilibriumPosition(double xEquilibriumPosition) {
//            this.xEquilibriumPosition = xEquilibriumPosition;
//        }
//
//        public double getySpringConstant() {
//            return ySpringConstant;
//        }
//
//        public void setySpringConstant(double ySpringConstant) {
//            this.ySpringConstant = ySpringConstant;
//        }
//
//        public double getyEquilibriumPosition() {
//            return yEquilibriumPosition;
//        }
//
//        public void setyEquilibriumPosition(double yEquilibriumPosition) {
//            this.yEquilibriumPosition = yEquilibriumPosition;
//        }
//
//        public double getPressure() {
//            return pressure;
//        }
//
//        public void setPressure(double pressure) {
//            this.pressure = pressure;
//        }
//
//    }
////</editor-fold>
//
//    private final double xSpringConstant, xEquilibriumPosition, ySpringConstant, yEquilibriumPosition, pressure;
//
//    public ExternalEnergyCalculator() {
//        xSpringConstant = 0;
//        xEquilibriumPosition = 0;
//        ySpringConstant = 0;
//        yEquilibriumPosition = 0;
//        pressure = 0;
//    }
//
//    @Override
//    public int hashCode() {
//        int hash = 5;
//        hash = 59 * hash + (int) (Double.doubleToLongBits(this.xSpringConstant) ^ (Double.doubleToLongBits(this.xSpringConstant) >>> 32));
//        hash = 59 * hash + (int) (Double.doubleToLongBits(this.xEquilibriumPosition) ^ (Double.doubleToLongBits(this.xEquilibriumPosition) >>> 32));
//        hash = 59 * hash + (int) (Double.doubleToLongBits(this.ySpringConstant) ^ (Double.doubleToLongBits(this.ySpringConstant) >>> 32));
//        hash = 59 * hash + (int) (Double.doubleToLongBits(this.yEquilibriumPosition) ^ (Double.doubleToLongBits(this.yEquilibriumPosition) >>> 32));
//        hash = 59 * hash + (int) (Double.doubleToLongBits(this.pressure) ^ (Double.doubleToLongBits(this.pressure) >>> 32));
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object object) {
//        if (object == null) {
//            return false;
//        }
//        if (getClass() != object.getClass()) {
//            return false;
//        }
//        final ExternalEnergyCalculator other = (ExternalEnergyCalculator) object;
//        return xSpringConstant == other.xSpringConstant
//                && xEquilibriumPosition == other.xEquilibriumPosition
//                && ySpringConstant == other.ySpringConstant
//                && yEquilibriumPosition == other.yEquilibriumPosition
//                && pressure == other.pressure;
//    }
//
//    private ExternalEnergyCalculator(ExternalEnergyCalculatorBuilder builder) {
//        xSpringConstant = builder.xSpringConstant;
//        xEquilibriumPosition = builder.xEquilibriumPosition;
//        ySpringConstant = builder.ySpringConstant;
//        yEquilibriumPosition = builder.yEquilibriumPosition;
//        pressure = builder.pressure;
//    }
//
//    public double calculateExternalEnergy(double[] rMax) {
//        final double x = rMax[0];
//        final double y = rMax[1];
//
//        final double xDisplacement = x - xEquilibriumPosition;
//        final double yDisplacement = y - yEquilibriumPosition;
//
//
//        return .5 * (xSpringConstant * xDisplacement * xDisplacement + ySpringConstant * yDisplacement * yDisplacement)
//                + pressure * x * y;
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="getters">
//    public double getxSpringConstant() {
//        return xSpringConstant;
//    }
//
//    public double getxEquilibriumPosition() {
//        return xEquilibriumPosition;
//    }
//
//    public double getySpringConstant() {
//        return ySpringConstant;
//    }
//
//    public double getyEquilibriumPosition() {
//        return yEquilibriumPosition;
//    }
//
//    public double getPressure() {
//        return pressure;
//    }
//    //</editor-fold>
//
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

        public ExternalEnergyCalculator build() {
            return new ExternalEnergyCalculator(this);
        }

        public void setxTensionAndQuadratic(double xTension, double xQuadratic) {
            this.xTension = xTension;
            this.xQuadratic = xQuadratic;
        }

        public void setyTensionAndQuadratic(double yTension, double yQuadratic) {
            this.yTension = yTension;
            this.yQuadratic = yQuadratic;
        }

        public void setXPositionAndSpringConstant(double xPosition, double xSpringConstant) {
            this.xTension = tensionFromPositionAndSpringConstant(xPosition, xSpringConstant);
            this.xQuadratic = quadraticFromPositionAndSpringConstant(xPosition, xSpringConstant);
        }

        public void setYPositionAndSpringConstant(double yPosition, double ySpringConstant) {
            this.yTension = tensionFromPositionAndSpringConstant(yPosition, ySpringConstant);
            this.yQuadratic = quadraticFromPositionAndSpringConstant(yPosition, ySpringConstant);
        }

        public void setPressure(double pressure) {
            this.pressure = pressure;
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

    static private double tensionFromPositionAndSpringConstant(double position, double springConstant) {
        return -springConstant * position;
    }

    static private double quadraticFromPositionAndSpringConstant(double position, double springConstant) {
        return 0.5 * springConstant;
    }

    static private double equilbriumPositionFromTensionAndQuadratic(double tension, double quadratic) {
        return tension / (2 * quadratic);
    }

    static private double springConstantFromTensionAndQuadratic(double tension, double quadratic) {
        return (2 * quadratic);
    }

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
