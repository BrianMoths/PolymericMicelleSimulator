/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.Energetics;

import Engine.Energetics.ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder;
import Engine.PolymerState.SystemGeometry.AreaOverlap;
import Engine.PolymerState.SystemGeometry.GeometricalParameters;
import Engine.PolymerState.SystemGeometry.Interfaces.ImmutableSystemGeometry;
import java.io.Serializable;
import java.util.Random;

/**
 *
 * @author bmoths
 */
public final class EnergeticsConstants implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="builder class">
    static public class EnergeticsConstantsBuilder {

        static public EnergeticsConstantsBuilder zeroEnergeticsConstantsBuilder() {
            EnergeticsConstantsBuilder energeticsConstantsBuilder;
            energeticsConstantsBuilder = new EnergeticsConstantsBuilder();
            energeticsConstantsBuilder.AAOverlapCoefficient = 0;
            energeticsConstantsBuilder.ABOverlapCoefficient = 0;
            energeticsConstantsBuilder.BBOverlapCoefficient = 0;
            energeticsConstantsBuilder.temperature = 1;
            energeticsConstantsBuilder.springConstant = 2; //want springConstant = 2 so that 2/2 T = 1/2 springConstant L^2 when L=1 and T =1
            energeticsConstantsBuilder.hardOverlapCoefficient = 0;
            return energeticsConstantsBuilder;
        }

        static public EnergeticsConstantsBuilder defaultEnergeticsConstantsBuilder() {
            EnergeticsConstantsBuilder energeticsConstantsBuilder;
            energeticsConstantsBuilder = new EnergeticsConstantsBuilder();
            energeticsConstantsBuilder.AAOverlapCoefficient = 5. / 120;
            energeticsConstantsBuilder.ABOverlapCoefficient = .1;
            energeticsConstantsBuilder.BBOverlapCoefficient = 5. / 120.;
            energeticsConstantsBuilder.temperature = 1;
            energeticsConstantsBuilder.springConstant = 2. / 3.;
            energeticsConstantsBuilder.hardOverlapCoefficient = 0;
            return energeticsConstantsBuilder;
        }

        private double temperature,
                AAOverlapCoefficient,
                BBOverlapCoefficient,
                ABOverlapCoefficient,
                //                springCoefficient,
                springConstant,
                hardOverlapCoefficient;
        private ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder();

        private EnergeticsConstantsBuilder() {
        }

        public EnergeticsConstantsBuilder(EnergeticsConstants energeticsConstants) {
            temperature = energeticsConstants.temperature;
            AAOverlapCoefficient = energeticsConstants.AAOverlapCoefficient;
            BBOverlapCoefficient = energeticsConstants.BBOverlapCoefficient;
            ABOverlapCoefficient = energeticsConstants.ABOverlapCoefficient;
            springConstant = energeticsConstants.springConstant;
            hardOverlapCoefficient = energeticsConstants.hardOverlapCoefficient;
            externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder(energeticsConstants.getExternalEnergyCalculator());
        }

        public EnergeticsConstants buildEnergeticsConstants() {
            return new EnergeticsConstants(this);
        }

        public void setHardOverlapCoefficientFromParameters(GeometricalParameters geometricalParameters) {
            hardOverlapCoefficient = hardOverlapCoefficientFromParameters(geometricalParameters);
        }

        public double hardOverlapCoefficientFromParameters(GeometricalParameters geometricalParameters) {
            if (geometricalParameters.getCoreLength() > 1e-10) {
                final double bondingEnergyInT = .5;
                final double coreRepulsionInT = 5; //5
                double minCoefficientForBonding = -bondingEnergyInT * temperature / (geometricalParameters.getInteractionLength() * geometricalParameters.getInteractionLength());
                double minAttraction = Math.min(Math.min(BBOverlapCoefficient, AAOverlapCoefficient), minCoefficientForBonding);
                return (coreRepulsionInT * temperature - geometricalParameters.getInteractionLength() * geometricalParameters.getInteractionLength() * minAttraction) / (geometricalParameters.getCoreLength() * geometricalParameters.getCoreLength());
            } else {
                return 0;
            }
        }

        public double idealStepLength() {
            return Math.sqrt(getTemperature() / (getSpringConstant() / 2));
        }

        //<editor-fold defaultstate="collapsed" desc="setters">
        public EnergeticsConstantsBuilder setTemperature(double temperature) {
            if (temperature > 0) {
                this.temperature = temperature;
            }
            return this;
        }

        public EnergeticsConstantsBuilder setAAOverlapCoefficient(double AAOverlapCoefficient) {
            this.AAOverlapCoefficient = AAOverlapCoefficient;
            return this;
        }

        public EnergeticsConstantsBuilder setBBOverlapCoefficient(double BBOverlapCoefficient) {
            this.BBOverlapCoefficient = BBOverlapCoefficient;
            return this;
        }

        public EnergeticsConstantsBuilder setABOverlapCoefficient(double ABOverlapCoefficient) {
            this.ABOverlapCoefficient = ABOverlapCoefficient;
            return this;
        }

        public EnergeticsConstantsBuilder setSpringConstant(double springConstant) {
            this.springConstant = springConstant;
            return this;
        }

        public EnergeticsConstantsBuilder setHardOverlapCoefficient(double hardOverlapCoefficient) {
            this.hardOverlapCoefficient = hardOverlapCoefficient;
            return this;
        }

        public EnergeticsConstantsBuilder setExternalEnergyCalculatorBuilder(ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder) {
            this.externalEnergyCalculatorBuilder = externalEnergyCalculatorBuilder;
            return this;
        }

        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="getters">
        public double getTemperature() {
            return temperature;
        }

        public double getAAOverlapCoefficient() {
            return AAOverlapCoefficient;
        }

        public double getBBOverlapCoefficient() {
            return BBOverlapCoefficient;
        }

        public double getABOverlapCoefficient() {
            return ABOverlapCoefficient;
        }

        public double getSpringConstant() {
            return springConstant;
        }

        public double getHardOverlapCoefficient() {
            return hardOverlapCoefficient;
        }

        public ExternalEnergyCalculatorBuilder getExternalEnergyCalculatorBuilder() {
            return externalEnergyCalculatorBuilder;
        }
        //</editor-fold>

    }
    //</editor-fold>

    private static final long serialVersionUID = 0L;
    static private Random randomNumberGenerator = new Random();

    static public EnergeticsConstants defaultEnergeticsConstants() {
        return new EnergeticsConstantsBuilder().buildEnergeticsConstants();
    }

    static public EnergeticsConstants zeroEnergeticsConstants() {
        EnergeticsConstantsBuilder energeticsConstantsBuilder = EnergeticsConstantsBuilder.zeroEnergeticsConstantsBuilder();
        return energeticsConstantsBuilder.buildEnergeticsConstants();
    }

    private final double temperature,
            AAOverlapCoefficient,
            BBOverlapCoefficient,
            ABOverlapCoefficient,
            hardOverlapCoefficient,
            springConstant;
    private final ExternalEnergyCalculator externalEnergyCalculator;

    private EnergeticsConstants(EnergeticsConstantsBuilder energeticsConstantsBuilder) {
        temperature = energeticsConstantsBuilder.temperature;
        AAOverlapCoefficient = energeticsConstantsBuilder.AAOverlapCoefficient;
        BBOverlapCoefficient = energeticsConstantsBuilder.BBOverlapCoefficient;
        ABOverlapCoefficient = energeticsConstantsBuilder.ABOverlapCoefficient;
        hardOverlapCoefficient = energeticsConstantsBuilder.hardOverlapCoefficient;
        springConstant = energeticsConstantsBuilder.springConstant;
        externalEnergyCalculator = energeticsConstantsBuilder.externalEnergyCalculatorBuilder.build();

    }

    //<editor-fold defaultstate="collapsed" desc="calculate energies">
    public double springEnergy(double squareLength) {
        return springConstant / 2 * squareLength;
    }

    public double calculateSpringForce(double displacement) {
        return -springConstant * displacement;
    }

    public double densityEnergy(AreaOverlap areaOverlap) {
        return 2 * (AAOverlapCoefficient * areaOverlap.AAOverlap
                + BBOverlapCoefficient * areaOverlap.BBOverlap
                + ABOverlapCoefficient * areaOverlap.ABOverlap
                + hardOverlapCoefficient * areaOverlap.hardOverlap); //why a factor of two?
    }

    public double externalEnergy(ImmutableSystemGeometry systemGeometry) {
        return externalEnergyCalculator.calculateExternalEnergy(systemGeometry.getRMax());
    }
    //</editor-fold>

    public boolean isEnergeticallyAllowed(EnergyEntropyChange energyEntropyChange) {
        final double freeEnergyChange = energyEntropyChange.calculateFreeEnergyChange(temperature);
        return freeEnergyChange < 0
                || randomNumberGenerator.nextDouble() < Math.exp(-freeEnergyChange / temperature);
    }

    public double idealStepLength() {
        return Math.sqrt(getTemperature() / (getSpringConstant() / 2));
    }

    //<editor-fold defaultstate="collapsed" desc="to string">
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Temperature: ").append(Double.toString(temperature)).append("\n");
        stringBuilder.append("AA Overlap Coefficient: ").append(Double.toString(AAOverlapCoefficient)).append("\n");
        stringBuilder.append("BB Overlap Coefficient: ").append(Double.toString(BBOverlapCoefficient)).append("\n");
        stringBuilder.append("AB Overlap Coefficient: ").append(Double.toString(ABOverlapCoefficient)).append("\n");
        stringBuilder.append("Hard Overlap Coefficient: ").append(Double.toString(hardOverlapCoefficient)).append("\n");
        stringBuilder.append("Spring Constant: ").append(Double.toString(springConstant)).append("\n");
        return stringBuilder.toString();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="getters">
    public double getTemperature() {
        return temperature;
    }

    public double getAAOverlapCoefficient() {
        return AAOverlapCoefficient;
    }

    public double getBBOverlapCoefficient() {
        return BBOverlapCoefficient;
    }

    public double getABOverlapCoefficient() {
        return ABOverlapCoefficient;
    }

    public double getHardOverlapCoefficient() {
        return hardOverlapCoefficient;
    }

    public double getSpringConstant() {
        return springConstant;
    }

    public ExternalEnergyCalculator getExternalEnergyCalculator() {
        return externalEnergyCalculator;
    }

    //</editor-fold>
}
