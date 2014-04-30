/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.Energetics;

import Engine.Energetics.ExternalEnergyCalculator.ExternalEnergyCalculatorBuilder;
import Engine.PolymerState.SystemGeometry.AreaOverlap;
import Engine.PolymerState.SystemGeometry.GeometricalParameters;
import Engine.PolymerState.SystemGeometry.Interfaces.ImmutableSystemGeometry;
import Engine.PolymerState.SystemGeometry.Interfaces.SystemGeometry;
import java.io.Serializable;
import java.util.Random;

/**
 * A class which keeps all the information about how beads interact. It has
 * methods to get the non-geometrical parameters of the interaction as well as
 * methods to give the interaction energy as a function of separation.
 *
 * @author bmoths
 */
public final class EnergeticsConstants implements Serializable {

    //<editor-fold defaultstate="collapsed" desc="builder class">
    /**
     * A builder class for Energetics constants.
     *
     * @author bmoths
     */
    static public class EnergeticsConstantsBuilder {

        /**
         * constructs a builder for a "null" object of energetics constants. The
         * temperature is set to one and the spring constant of the polymer
         * springs is set to two, but all other parameters are zero.
         *
         * @return a "null" energetics constants builder
         */
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

        /**
         * constructs a builder which has reasonable values.
         *
         * @return a builder with reasonable values.
         */
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

        /**
         * constructs an energetics constants builder corresponding to a given
         * energetics constants. When build is called on the builder, it should
         * an energetics constants which is a copy of the one input to this
         * method
         *
         * @param energeticsConstants the energetics constants whose state
         * should be copied
         */
        public EnergeticsConstantsBuilder(EnergeticsConstants energeticsConstants) {
            temperature = energeticsConstants.temperature;
            AAOverlapCoefficient = energeticsConstants.AAOverlapCoefficient;
            BBOverlapCoefficient = energeticsConstants.BBOverlapCoefficient;
            ABOverlapCoefficient = energeticsConstants.ABOverlapCoefficient;
            springConstant = energeticsConstants.springConstant;
            hardOverlapCoefficient = energeticsConstants.hardOverlapCoefficient;
            externalEnergyCalculatorBuilder = new ExternalEnergyCalculatorBuilder(energeticsConstants.getExternalEnergyCalculator());
        }

        /**
         * builds an energetics constants with the same state as the builder
         *
         * @return an energetics constants with the same state of the builder.
         */
        public EnergeticsConstants buildEnergeticsConstants() {
            return new EnergeticsConstants(this);
        }

        /**
         * sets the hard overlap coefficient to a reasonable value based off of
         * the geometrical parameters of the potential (in particular the
         * interaction length and the core length) and the overlap coefficients.
         *
         * @param geometricalParameters the geometrical parameters describing
         * the interaction
         */
        public void setHardOverlapCoefficientFromParameters(GeometricalParameters geometricalParameters) {
            hardOverlapCoefficient = hardOverlapCoefficientFromParameters(geometricalParameters);
        }

        /**
         * returns a reasonable value of the hard overlap coefficient based off
         * of the geometrical parameters of the potential (in particular the
         * interaction length and the core length) and the overlap coefficients.
         *
         * @param geometricalParameters the geometrical parameters describing
         * @return a reasonable value of the hard overlap coefficient
         */
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

        /**
         * returns the length a spring needs to be stretched for its energy to
         * be the temperature
         *
         * @return the length a spring needs to be stretched for its energy to
         * be the temperature
         */
        public double idealStepLength() {
            return Math.sqrt(getTemperature() / (getSpringConstant() / 2));
        }

        //<editor-fold defaultstate="collapsed" desc="setters">
        /**
         * sets the temperature to the given value, and returns this builder
         * (for chaining). The value of the temperature must be greater than or
         * equal to zero.
         *
         * @param temperature the new value of the temperature
         * @return this object
         */
        public EnergeticsConstantsBuilder setTemperature(double temperature) {
            if (temperature >= 0) {
                this.temperature = temperature;
            }
            return this;
        }

        /**
         * Sets the AA overlap coefficient. This coefficient determines how much
         * interaction energy there is per unit of area between two overlapping
         * beads both of type A. A negative value represents an attractive
         * interaction while a positive value represents a repulsive
         * interaction.
         *
         * @param AAOverlapCoefficient The new value AA overlap coefficient.
         * @return this energetics constants builder
         */
        public EnergeticsConstantsBuilder setAAOverlapCoefficient(double AAOverlapCoefficient) {
            this.AAOverlapCoefficient = AAOverlapCoefficient;
            return this;
        }

        /**
         * Sets the BB overlap coefficient. This coefficient determines how much
         * interaction energy there is per unit of area between two overlapping
         * beads both of type B. A negative value represents an attractive
         * interaction while a positive value represents a repulsive
         * interaction.
         *
         * @param BBOverlapCoefficient The new value BB overlap coefficient.
         * @return this energetics constants builder
         */
        public EnergeticsConstantsBuilder setBBOverlapCoefficient(double BBOverlapCoefficient) {
            this.BBOverlapCoefficient = BBOverlapCoefficient;
            return this;
        }

        /**
         * Sets the AB overlap coefficient. This coefficient determines how much
         * interaction energy there is per unit of area between a bead of type A
         * overlapping with a bead of type B. A negative value represents an
         * attractive interaction while a positive value represents a repulsive
         * interaction.
         *
         * @param BBOverlapCoefficient The new value AB overlap coefficient.
         * @return this energetics constants builder
         */
        public EnergeticsConstantsBuilder setABOverlapCoefficient(double ABOverlapCoefficient) {
            this.ABOverlapCoefficient = ABOverlapCoefficient;
            return this;
        }

        /**
         * sets the spring constant which for the harmonic potential between two
         * adjacent monomers on a polymer chain. The value for this parameter
         * ought to be non-negative.
         *
         * @param springConstant The spring constant for the interaction of
         * adjacent monomers on a polymer.
         * @return this energetics constants builder.
         */
        public EnergeticsConstantsBuilder setSpringConstant(double springConstant) {
            this.springConstant = springConstant;
            return this;
        }

        /**
         * Sets the hard overlap coefficient. This coefficient determines how
         * much interaction energy there is per unit of area of overlap of the
         * hard cores of any two beads. The hard cores are meant to be
         * repulsive, so the value should typically be positive.
         *
         * @param hardOverlapCoefficient the hard overlap coefficient
         * @return this energetics constants builder
         */
        public EnergeticsConstantsBuilder setHardOverlapCoefficient(double hardOverlapCoefficient) {
            this.hardOverlapCoefficient = hardOverlapCoefficient;
            return this;
        }

        /**
         * Sets the external energy calculator builder for this energetics
         * constants builder.
         *
         * @see ExternalEnergyCalculatorBuilder
         * @see ExternalEnergyCalculator
         * @param externalEnergyCalculatorBuilder the new value of the external
         * energy calculator builder
         * @return this energetics constants builder
         */
        public EnergeticsConstantsBuilder setExternalEnergyCalculatorBuilder(ExternalEnergyCalculatorBuilder externalEnergyCalculatorBuilder) {
            this.externalEnergyCalculatorBuilder = externalEnergyCalculatorBuilder;
            return this;
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="getters">
        /**
         * returns the temperature.
         *
         * @return the temperature
         */
        public double getTemperature() {
            return temperature;
        }

        /**
         * Returns the AA overlap coefficient. This coefficient determines how
         * much interaction energy there is per unit of area between two
         * overlapping beads both of type A. A negative value represents an
         * attractive interaction while a positive value represents a repulsive
         * interaction.
         *
         * @return the AA overlap coefficient.
         */
        public double getAAOverlapCoefficient() {
            return AAOverlapCoefficient;
        }

        /**
         * Returns the BB overlap coefficient. This coefficient determines how
         * much interaction energy there is per unit of area between two
         * overlapping beads both of type B. A negative value represents an
         * attractive interaction while a positive value represents a repulsive
         * interaction.
         *
         * @return the BB overlap coefficient.
         */
        public double getBBOverlapCoefficient() {
            return BBOverlapCoefficient;
        }

        /**
         * Returns the AB overlap coefficient. This coefficient determines how
         * much interaction energy there is per unit of area between two
         * overlapping beads of opposite type. A negative value represents an
         * attractive interaction while a positive value represents a repulsive
         * interaction.
         *
         * @return the AB overlap coefficient.
         */
        public double getABOverlapCoefficient() {
            return ABOverlapCoefficient;
        }

        /**
         * Returns the spring constant describing the harmonic potential
         * interaction between monomers which are adjacent on a polymer chain.
         *
         * @return the spring constant
         */
        public double getSpringConstant() {
            return springConstant;
        }

        /**
         * Returns the hard overlap coefficient. This coefficient determines how
         * much interaction energy there is per unit of area of hard core
         * overlap between any two beads.
         *
         * @return the AB overlap coefficient.
         */
        public double getHardOverlapCoefficient() {
            return hardOverlapCoefficient;
        }

        /**
         * Returns the external energy calculator builder.
         *
         * @see ExternalEnergyCalculatorBuilder
         * @see ExternalEnergyCalculator
         * @return the external energy calculator builder.
         */
        public ExternalEnergyCalculatorBuilder getExternalEnergyCalculatorBuilder() {
            return externalEnergyCalculatorBuilder;
        }
        //</editor-fold>

    }
    //</editor-fold>

    private static final long serialVersionUID = 0L;
    static private Random randomNumberGenerator = new Random();

    /**
     * constructs an energetics constants that has reasonable values.
     *
     * @return a reasonable energetics constants
     */
    static public EnergeticsConstants defaultEnergeticsConstants() {
        return new EnergeticsConstantsBuilder().buildEnergeticsConstants();
    }

    /**
     * constructs a "null" object of energetics constants. The temperature is
     * set to one and the spring constant of the polymer springs is set to two,
     * but all other parameters are zero.
     *
     * @return a "null" energetics constants
     */
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
    /**
     * determines the amount of energy needed to create the given separation
     * between two adjacent monomers on a polymer chain. The amount of energy
     * depends on the spring constant.
     *
     * @param squareLength the square of the distance between two adjacent
     * monomers
     * @return the energy resulting from this separation
     */
    public double springEnergy(double squareLength) {
        return springConstant / 2 * squareLength;
    }

    /**
     * Calculates the magnitude of the attractive force resulting from the
     * spring interaction between to monomers adjacent on a polymer chain
     *
     * @param distance the distance between the two monomers
     * @return the magnitude of the attractive force.
     */
    public double calculateSpringForce(double distance) {
        return -springConstant * distance;
    }

    /**
     * calculates the overlap interaction energy of two beads which have the
     * given area overlap. It depends on the AA, AB, or BB overlap coefficients
     * depending on the types of the two beads as well as the hard overlap
     * coefficient.
     *
     * @see AreaOverlap
     * @param areaOverlap the area overlap between the two beads
     * @return the interaction energy resulting from the overlap of the two
     * beads
     */
    public double densityEnergy(AreaOverlap areaOverlap) {
        return 2 * (AAOverlapCoefficient * areaOverlap.AAOverlap
                + BBOverlapCoefficient * areaOverlap.BBOverlap
                + ABOverlapCoefficient * areaOverlap.ABOverlap
                + hardOverlapCoefficient * areaOverlap.hardOverlap); //why a factor of two?
    }

    /**
     * calculates the external energy of based on the size of the system as
     * specified by the system geometry parameter. The result depends on the
     * external energy calculator of this energetics constants.
     *
     * @see ImmutableSystemGeometry
     * @param systemGeometry the geometry of the system whose external energy is
     * to be determined
     * @return the external energy of the given system
     */
    public double externalEnergy(ImmutableSystemGeometry systemGeometry) {
        return externalEnergyCalculator.calculateExternalEnergy(systemGeometry.getRMax());
    }
    //</editor-fold>

    /**
     * outputs a random boolean whose probability of being true is given by
     * boltzmann statistics with the given changes in energy and entropy. The
     * probability of being true will depend on the temperature.
     *
     * @see EnergyEntropyChange
     * @param energyEntropyChange the change in energy and entropy
     * @return whether or not a change with such energy and entropy is allowed
     */
    public boolean isEnergeticallyAllowed(EnergyEntropyChange energyEntropyChange) {
        final double freeEnergyChange = energyEntropyChange.calculateFreeEnergyChange(temperature);
        return freeEnergyChange < 0
                || randomNumberGenerator.nextDouble() < Math.exp(-freeEnergyChange / temperature);
    }

    /**
     * calculates the distance two beads need to be separated in order for their
     * spring energy to equal the temperature.
     *
     * @return the distance two beads need to be separated in order for their
     * spring energy to equal the temperature
     */
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
    /**
     * returns the temperature.
     *
     * @return the temperature
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * Returns the AA overlap coefficient. This coefficient determines how much
     * interaction energy there is per unit of area between two overlapping
     * beads both of type A. A negative value represents an attractive
     * interaction while a positive value represents a repulsive interaction.
     *
     * @return the AA overlap coefficient.
     */
    public double getAAOverlapCoefficient() {
        return AAOverlapCoefficient;
    }

    /**
     * Returns the BB overlap coefficient. This coefficient determines how much
     * interaction energy there is per unit of area between two overlapping
     * beads both of type B. A negative value represents an attractive
     * interaction while a positive value represents a repulsive interaction.
     *
     * @return the BB overlap coefficient.
     */
    public double getBBOverlapCoefficient() {
        return BBOverlapCoefficient;
    }

    /**
     * Returns the AB overlap coefficient. This coefficient determines how much
     * interaction energy there is per unit of area between two overlapping
     * beads of opposite type. A negative value represents an attractive
     * interaction while a positive value represents a repulsive interaction.
     *
     * @return the AB overlap coefficient.
     */
    public double getABOverlapCoefficient() {
        return ABOverlapCoefficient;
    }

    /**
     * Returns the hard overlap coefficient. This coefficient determines how
     * much interaction energy there is per unit of area of hard core overlap
     * between any two beads.
     *
     * @return the AB overlap coefficient.
     */
    public double getHardOverlapCoefficient() {
        return hardOverlapCoefficient;
    }

    /**
     * Returns the spring constant describing the harmonic potential interaction
     * between monomers which are adjacent on a polymer chain.
     *
     * @return the spring constant
     */
    public double getSpringConstant() {
        return springConstant;
    }

    /**
     * Returns the external energy calculator.
     *
     * @see ExternalEnergyCalculator
     * @return the external energy calculator.
     */
    public ExternalEnergyCalculator getExternalEnergyCalculator() {
        return externalEnergyCalculator;
    }

    //</editor-fold>
}
