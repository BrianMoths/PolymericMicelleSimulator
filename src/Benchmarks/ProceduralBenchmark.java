package Benchmarks;


import java.util.Random;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author brian
 */
public class ProceduralBenchmark {

    private static final int dimension = 2,
            numBeads = 100,
            similarOverlapCoefficient = 1,
            differentOverlapCoefficient = 4,
            interactionLength = 5,
            temperature = 1;
    private static final int bStart = numBeads / 2,
            stepLength = interactionLength;
    
    private static final int[] rMin,rMax;
    private static double[][] sBeadPositions;
    private static double energy;
    private static final Random random;

    static {
        random = new Random();
        
        rMin = new int[dimension];
        rMin[0]=0;
        rMin[1]=0;
        
        rMax = new int[dimension];
        rMax[0]=20;
        rMax[1]=20;
    }

    
    static private double areaOverlap(double[] position1, double[] position2) {
        double overlap = 1;

        for (int i = 0; i < dimension; i++) {
            overlap *= Math.max(interactionLength - Math.abs(position1[i] - position2[i]), 0.0);
        }

        return overlap;
    }

    static private double[][] areaOverlapMatrix(double[][] beadPositions) {
        double[][] areaOverlapMatrix = new double[numBeads][numBeads];
        for (int i = 0; i < numBeads; i++) {
            for (int j = 0; j < numBeads; j++) {
                areaOverlapMatrix[i][j] = areaOverlap(beadPositions[i], beadPositions[j]);
            }
        }
        return areaOverlapMatrix;
    }

    static private double densityEnergy(double[][] beadPositions) {
        double[][] areaOverlapMatrix;
        areaOverlapMatrix = areaOverlapMatrix(beadPositions);

        double similarOverlap = 0, differentOverlap = 0;

        for (int i = 0; i < bStart; i++) {
            for (int j = 0; j < bStart; j++) {
                similarOverlap += areaOverlapMatrix[i][j];
            }
        }

        for (int i = bStart; i < numBeads; i++) {
            for (int j = bStart; j < numBeads; j++) {
                similarOverlap += areaOverlapMatrix[i][j];
            }
        }

        for (int i = 0; i < bStart; i++) {
            for (int j = bStart; j < numBeads; j++) {
                differentOverlap += areaOverlapMatrix[i][j];
            }
        }

        return similarOverlapCoefficient * similarOverlap + 2 * differentOverlapCoefficient * differentOverlap;
    }

    static private void doIterations() {
        int stepBead = random.nextInt(numBeads);
        double[] stepVector = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            stepVector[i] = random.nextGaussian() * stepLength;
        }

        for (int i = 0; i < dimension; i++) {
            sBeadPositions[stepBead][i] += stepVector[i];
        }

        double newEnergy = densityEnergy(sBeadPositions);

        if ((newEnergy > energy
                && random.nextDouble() > Math.exp((energy - newEnergy) / temperature))
                || sBeadPositions[stepBead][0] < rMin[0]
                || sBeadPositions[stepBead][0] > rMax[0]
                || sBeadPositions[stepBead][1] < rMin[1]
                || sBeadPositions[stepBead][1] > rMax[1]) {
            for (int i = 0; i < dimension; i++) {
                sBeadPositions[stepBead][i] -= stepVector[i];
            } 
        }else{
            energy = newEnergy;
        }
    }
    
    static private void doIterations(int n){
        for (int i = 0; i < n; i++) {
            doIterations();
        }
    }

    public static void main(String[] argv) {
        sBeadPositions = new double[numBeads][dimension];
        for (int i = 0; i < numBeads; i++) {
            for (int j = 0; j < dimension; j++) {
                sBeadPositions[i][j]=random.nextDouble()*rMax[j] + rMin[j];
            }
        }
        
        energy = densityEnergy(sBeadPositions);
        
        System.out.println(energy);
        
        Long initialTime = System.nanoTime();
        
        doIterations(5000);
        
        Long finalTime = System.nanoTime();
        
        System.out.println(energy);
        
        System.out.println((finalTime - initialTime)/1000000);
    }
}
