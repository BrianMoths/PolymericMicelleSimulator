/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SystemAnalysis;

import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.optim.nonlinear.vector.ModelFunction;
import org.apache.commons.math3.optim.nonlinear.vector.ModelFunctionJacobian;
import org.apache.commons.math3.optim.nonlinear.vector.Target;
import org.apache.commons.math3.optim.nonlinear.vector.Weight;
import org.apache.commons.math3.optim.nonlinear.vector.jacobian.LevenbergMarquardtOptimizer;

/**
 *
 * @author bmoths
 */
public class MechanicalPropertiesFinder {

    private static class MechanicalParameterOptimizer extends LevenbergMarquardtOptimizer {

        private static int numParameters = 3; //redo this using set options
        private int numObservations;
        private double averageEnergy, averageArea, averagePerimeter;
        private double[] observedPerimeters, observedAreas, observedEnergies;

        public MechanicalProperties getOptimalMechanicalProperties() {
            PointVectorValuePair parametersAndResidual = doOptimize();
            double[] mechanicalPropertiesArray = parametersAndResidual.getPoint();

            MechanicalProperties mechanicalProperties;
            mechanicalProperties = new MechanicalProperties();
            mechanicalProperties.surfaceTension = mechanicalPropertiesArray[0];
            mechanicalProperties.pressure = mechanicalPropertiesArray[1];
            mechanicalProperties.compressibility = mechanicalPropertiesArray[2];

            return mechanicalProperties;
        }

        @Override
        protected double[] computeObjectiveValue(double[] parameters) {
            double[] predictedEnergies = new double[numObservations];
            final double surfaceTension = parameters[0];
            final double pressure = parameters[1];
            final double halfCompressibiltyVolumeInverse = parameters[2];

            for (int i = 0; i < numObservations; i++) {
                final double perimeterFluctuation, areaFluctuation;
                perimeterFluctuation = observedPerimeters[i] - averagePerimeter;
                areaFluctuation = observedAreas[i] - averageArea;
                predictedEnergies[i] = averageEnergy
                        + surfaceTension * perimeterFluctuation
                        + areaFluctuation * (-pressure + areaFluctuation * halfCompressibiltyVolumeInverse);
            }

            return predictedEnergies;
        }

        @Override
        protected double[][] computeJacobian(double[] parameters) {
            double[][] jacobian = new double[numObservations][numParameters];

            for (int i = 0; i < numObservations; i++) {
                double[] jacobianrow = jacobian[i];
                final double perimeterFluctuation, areaFluctuation;
                perimeterFluctuation = observedPerimeters[i] - averagePerimeter;
                areaFluctuation = observedAreas[i] - averageArea;
                jacobianrow[0] = perimeterFluctuation;
                jacobianrow[1] = -areaFluctuation;
                jacobianrow[2] = areaFluctuation * areaFluctuation;
            }

            return jacobian;
        }

        @Override
        public double[] getStartPoint() {
            return new double[]{1.0, 1., 1.};
        }

        @Override
        public double[] getTarget() {
            return observedEnergies;
        }

        public void setAverageEnergy(double averageEnergy) {
            this.averageEnergy = averageEnergy;
        }

        public void setAverageArea(double averageArea) {
            this.averageArea = averageArea;
        }

        public void setAveragePerimeter(double averagePerimeter) {
            this.averagePerimeter = averagePerimeter;
        }

        public void setObservedPerimeters(double[] observedPerimeters) {
            this.observedPerimeters = observedPerimeters;
        }

        public void setObservedAreas(double[] observedAreas) {
            this.observedAreas = observedAreas;
        }

        public void setObservedEnergies(double[] observedEnergies) {
            this.observedEnergies = observedEnergies;
        }
    }
    static private int numParameters = 3;
    double averagePerimeter, averageEnergy, averageArea;
    double[] observedPerimeters, observedEnergies, observedAreas;
    int numObservations;

    public MechanicalProperties findMechanicalProperties(SimulationHistory simulationHistory) {
        averagePerimeter = simulationHistory.getAverage(SimulationHistory.TrackedVariable.PERIMETER);
        averageEnergy = simulationHistory.getAverage(SimulationHistory.TrackedVariable.ENERGY);
        averageArea = simulationHistory.getAverage(SimulationHistory.TrackedVariable.AREA);

        observedPerimeters = simulationHistory.getStoredValues(SimulationHistory.TrackedVariable.PERIMETER);
        observedEnergies = simulationHistory.getStoredValues(SimulationHistory.TrackedVariable.ENERGY);
        observedAreas = simulationHistory.getStoredValues(SimulationHistory.TrackedVariable.AREA);

        numObservations = observedAreas.length;

        OptimizationData initialGuess = makeInitialGuess();
        OptimizationData modelFunction = makeModelFunction();
        OptimizationData modelFunctionJacobian = makeModelFunctionJacobian();
        OptimizationData target = makeTarget();
        OptimizationData weight = makeWeights();
        OptimizationData maxEval = MaxEval.unlimited();
        OptimizationData maxIter = MaxIter.unlimited();


        OptimizationData[] optimizationData = new OptimizationData[]{
            initialGuess,
            modelFunction,
            modelFunctionJacobian,
            target,
            weight,
            maxEval,
            maxIter
        };

        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        PointVectorValuePair pointValuePair = optimizer.optimize(optimizationData);
        double[] optimalParameters = pointValuePair.getPoint();
        MechanicalProperties foundMechanicalProperties;
        foundMechanicalProperties = new MechanicalProperties();
        foundMechanicalProperties.surfaceTension = optimalParameters[0];
        foundMechanicalProperties.pressure = optimalParameters[1];
        foundMechanicalProperties.compressibility = 1 / (2 * averageArea * optimalParameters[2]);

        return foundMechanicalProperties;


//        mechanicalParameterOptimizer.setAverageEnergy(averageEnergy);
//        mechanicalParameterOptimizer.setAveragePerimeter(averagePerimeter);
//        mechanicalParameterOptimizer.setAverageArea(averageArea);
//        mechanicalParameterOptimizer.setObservedAreas(observedAreas);
//        mechanicalParameterOptimizer.setObservedPerimeters(observedPerimeters);
//        mechanicalParameterOptimizer.setObservedEnergies(observedEnergies);

//        return mechanicalParameterOptimizer.getOptimalMechanicalProperties();
    }

    static private InitialGuess makeInitialGuess() {
        return new InitialGuess(new double[]{1., 1., 1.});
    }

    private ModelFunction makeModelFunction() {
        MultivariateVectorFunction myFunction;
        myFunction = new MultivariateVectorFunction() {
            @Override
            public double[] value(double[] parameters) throws IllegalArgumentException {
                return computeEnergy(parameters);
            }
        };
        return new ModelFunction(myFunction);
    }

    private double[] computeEnergy(double[] parameters) {
        double[] predictedEnergies = new double[numObservations];
        final double surfaceTension = parameters[0];
        final double pressure = parameters[1];
        final double halfCompressibiltyVolumeInverse = parameters[2];

        for (int i = 0; i < numObservations; i++) {
            final double perimeterFluctuation, areaFluctuation;
            perimeterFluctuation = observedPerimeters[i] - averagePerimeter;
            areaFluctuation = observedAreas[i] - averageArea;
            predictedEnergies[i] = averageEnergy
                    + surfaceTension * perimeterFluctuation
                    + areaFluctuation * (-pressure + areaFluctuation * halfCompressibiltyVolumeInverse);
        }

        return predictedEnergies;
    }

    private ModelFunctionJacobian makeModelFunctionJacobian() {
        MultivariateMatrixFunction myJacobian;
        myJacobian = new MultivariateMatrixFunction() {
            @Override
            public double[][] value(double[] parameters) throws IllegalArgumentException {
                return computeJacobian(parameters);
            }
        };
        return new ModelFunctionJacobian(myJacobian);
    }

    private double[][] computeJacobian(double[] parameters) {
        double[][] jacobian = new double[numObservations][numParameters];

        for (int i = 0; i < numObservations; i++) {
            double[] jacobianrow = jacobian[i];
            final double perimeterFluctuation, areaFluctuation;
            perimeterFluctuation = observedPerimeters[i] - averagePerimeter;
            areaFluctuation = observedAreas[i] - averageArea;
            jacobianrow[0] = perimeterFluctuation;
            jacobianrow[1] = -areaFluctuation;
            jacobianrow[2] = areaFluctuation * areaFluctuation;
        }

        return jacobian;
    }

    private Target makeTarget() {
        return new Target(observedEnergies);
    }

    private Weight makeWeights() {
        double[] weightArray = new double[numObservations];
        for (int i = 0; i < weightArray.length; i++) {
            weightArray[i] = 1;
        }
        return new Weight(weightArray);
    }
}
