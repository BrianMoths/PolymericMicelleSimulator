/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FocusedSimulation;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * This class determines whether a given variable generated from a markov
 * process has equilibrated yet.
 *
 * @author bmoths
 */
public class ConvergenceMonitor {

    /**
     * This class gives details about the convergence properties of samples of a
     * random variable
     */
    static public class ConvergenceResults {

        private final int numSamplesPerMean;
        private final double standardDeviation;
        private final double meansStandardDeviation;
        private final double precision;
        private final double mean;

        private ConvergenceResults(int numSamplesPerMean, double standardDeviation, double meansStandardDeviation, double mean, double precision) {
            this.numSamplesPerMean = numSamplesPerMean;
            this.standardDeviation = standardDeviation;
            this.meansStandardDeviation = meansStandardDeviation;
            this.precision = precision;
            this.mean = mean;
        }

        private ConvergenceResults(int numSamplesPerMean, double standardDeviation, double meansStandardDeviation, double mean) {
            this(numSamplesPerMean, standardDeviation, meansStandardDeviation, mean, 0);
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("standard deviation: ").append(standardDeviation).append("\n")
                    .append("expected means standard deviation: ").append(standardDeviation / Math.sqrt(numSamplesPerMean)).append("\n")
                    .append("actual means standard deviation: ").append(meansStandardDeviation).append("\n");

            return stringBuilder.toString();
        }

        /**
         * tests whether the samples of the random variable had converged.
         *
         * @return whether the samples of the random variable had converged
         */
        public boolean isConverged() {
            return ConvergenceMonitor.isConverged(meansStandardDeviation, standardDeviation, numSamplesPerMean);
        }

        public boolean isConvergedWithPrecision() {
            return ConvergenceMonitor.isPreciseEnough(standardDeviation, getNumSamples(), mean, precision) && isConverged();
        }

        /**
         * returns the total number of samples
         *
         * @return the total number of samples
         */
        public int getNumSamples() {
            return numSamplesPerMean * NUM_MEANS;
        }

        /**
         * returns the number of samples in each that the samples were
         * partitioned into.
         *
         * @return The number of samples in each subset used to calculate means.
         */
        public int getNumSamplesPerMean() {
            return numSamplesPerMean;
        }

        /**
         * gives the standard deviation of the set of all samples under
         * consideration
         *
         * @return the standard deviation of all the samples.
         */
        public double getStandardDeviation() {
            return standardDeviation;
        }

        /**
         * returns the standard deviation of the set of means of the subsets.
         *
         * @returnthe standard deviation of the set of means of the subsets.
         */
        public double getMeansStandardDeviation() {
            return meansStandardDeviation;
        }

        /**
         * returns the relative precision to which convergence was tested
         *
         * @return the relative precision used as the criterion for convergence
         */
        public double getPrecision() {
            return precision;
        }

        /**
         * returns the mean of all the samples
         *
         * @return the mean of all the samples
         */
        public double getMean() {
            return mean;
        }

    }

    static public final int NUM_MEANS = 20;
    static private final int minSamplesPerMean = 0;

    /**
     * tests to see if the samples in the given descriptive statistics object
     * have converged to the specified precision. The number of samples in the
     * descriptive statistics must be less than or equal to Integer.MAXINT
     *
     * @param descriptiveStatistics an object containing the samples whose
     * convergence is to be tested
     * @param precision the required relative precision to be achieved if the
     * test is to be passed
     * @return an object containing the results of the test
     * @throws IndexOutOfBoundsException if the number of samples in
     * descriptiveStatistics is greater than Integer.MAXINT
     */
    public static ConvergenceResults getConvergenceResultsForPrecision(final DescriptiveStatistics descriptiveStatistics, double precision) throws IndexOutOfBoundsException {
        int numSamples = getNumSamples(descriptiveStatistics);
        final double mean = descriptiveStatistics.getMean();
        final int numSamplesPerMean = numSamples / NUM_MEANS;
        double meansStandardDeviation = getMeansStandardDeviation(descriptiveStatistics, numSamplesPerMean);
        final double standardDeviation = descriptiveStatistics.getStandardDeviation();
        ConvergenceResults convergenceResults = new ConvergenceResults(numSamplesPerMean, standardDeviation, meansStandardDeviation, mean, precision);
        System.out.println(convergenceResults.toString());
        return convergenceResults;
    }

    public static ConvergenceResults getConvergenceResults(final DescriptiveStatistics descriptiveStatistics) throws IndexOutOfBoundsException {
        return getConvergenceResultsForPrecision(descriptiveStatistics, 0);
    }

    static public boolean isConverged(final DescriptiveStatistics descriptiveStatistics) {
        return isConvergedWithPrecision(descriptiveStatistics, 0);
    }

    static public boolean isConvergedWithPrecision(final DescriptiveStatistics descriptiveStatistics, double relativePrecision) {
        ConvergenceResults convergenceResults = getConvergenceResultsForPrecision(descriptiveStatistics, relativePrecision);
        return convergenceResults.isConverged();
    }

    static private int beginningIndexForSubset(int numSamplesPerMean, int i) {
        return numSamplesPerMean * i;
    }

    private static int getNumSamples(final DescriptiveStatistics descriptiveStatistics) throws IndexOutOfBoundsException {
        final long longNumSamples = descriptiveStatistics.getN();
        if (longNumSamples > Integer.MAX_VALUE || longNumSamples < Integer.MIN_VALUE) {
            throw new IndexOutOfBoundsException();
        }
        return ((int) (longNumSamples));
    }

    private static double getMeansStandardDeviation(final DescriptiveStatistics descriptiveStatistics, final int numSamplesPerMean) {
        final double[] samples = descriptiveStatistics.getValues();
        double[] means = getMeans(numSamplesPerMean, samples);
        final DescriptiveStatistics meanDescriptiveStatistics = new DescriptiveStatistics(means);
        double meansStandardDeviation = meanDescriptiveStatistics.getStandardDeviation();
        return meansStandardDeviation;
    }

    private static double[] getMeans(final int numSamplesPerMean, final double[] samples) {
        final double[] means = new double[NUM_MEANS];
        for (int i = 0; i < NUM_MEANS; i++) {
            final double[] subsetValues = new double[numSamplesPerMean];
            System.arraycopy(samples, beginningIndexForSubset(numSamplesPerMean, i), subsetValues, 0, numSamplesPerMean);
            DescriptiveStatistics subsetStatistics = new DescriptiveStatistics(subsetValues);
            means[i] = subsetStatistics.getMean();
        }
        return means;
    }

    private static boolean isConverged(double meansStandardDeviation, final double standardDeviation, final int numSamplesPerMean) {
        final double targetStandardDeviation = standardDeviation / Math.sqrt(numSamplesPerMean);

        System.out.println("standard deviation over square root n: " + targetStandardDeviation);
        System.out.println("actual means standard deviation: " + meansStandardDeviation);
        System.out.println("Num samples per mean: " + numSamplesPerMean);
        final boolean sufficientSamplesPerMean = numSamplesPerMean >= minSamplesPerMean;
        System.out.println("SufficientSamplesPerMean: " + sufficientSamplesPerMean);
        final boolean isTargetStandardDeviationMet = meansStandardDeviation < targetStandardDeviation;
        System.out.println("is target standard deviation met: " + isTargetStandardDeviationMet);
        final boolean isConverged = sufficientSamplesPerMean && isTargetStandardDeviationMet;
        System.out.println("Is sample converged: " + isConverged);
        return isConverged;
    }

    private static boolean isPreciseEnough(final double standardDeviation, final double numSamples, final double mean, double relativePrecision) {
        final double standardError = standardDeviation / Math.sqrt(numSamples);
        final double relativeStandardError = standardError / mean;
        return relativeStandardError > relativePrecision;
    }

}
