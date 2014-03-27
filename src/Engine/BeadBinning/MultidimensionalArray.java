/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.BeadBinning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author bmoths
 */
public class MultidimensionalArray<T> implements Serializable {

    private class IndexIterator implements Iterator<List<Integer>> {

        private int nextBackingIndex;

        public IndexIterator() {
            nextBackingIndex = 0;
        }

        @Override
        public boolean hasNext() {
            return nextBackingIndex < getTotalSize();
        }

        @Override
        public List<Integer> next() {
            final List<Integer> nextIndices = getIndicesFromBackingIndex(nextBackingIndex);
            nextBackingIndex++;
            return nextIndices;
        }

        private List<Integer> getIndicesFromBackingIndex(int backingIndex) {
            final int numDimensions = getNumDimensions();
            List<Integer> indices;
            indices = new ArrayList<>(numDimensions);

            for (int dimension = 0; dimension < numDimensions; dimension++) {
                indices.add(backingIndex / strideOfDimension.get(dimension));
                backingIndex %= strideOfDimension.get(dimension);
            }

            return indices;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private final List<Integer> dimensionSizes;
    private final List<Integer> strideOfDimension;
    private final List<T> backingArray;

    public MultidimensionalArray(List<Integer> dimensionSizes) {
        this.dimensionSizes = new ArrayList<>(dimensionSizes);
        strideOfDimension = calculateStrideOfDimension();
        backingArray = makeBackingArray();
    }

    public MultidimensionalArray(int[] dimensionSizes) {
        this.dimensionSizes = makeDimensionSizes(dimensionSizes);
        strideOfDimension = calculateStrideOfDimension();
        backingArray = makeBackingArray();
    }

    private List<Integer> makeDimensionSizes(int[] dimensionSizes) {
        List<Integer> dimensionSizesLocal;
        dimensionSizesLocal = new ArrayList<>(dimensionSizes.length);

        for (int dimension = 0; dimension < dimensionSizes.length; dimension++) {
            final int dimensionSize = dimensionSizes[dimension];
            dimensionSizesLocal.add(dimensionSize);
        }

        return dimensionSizesLocal;
    }

    public MultidimensionalArray(MultidimensionalArray multidimensionalArray) {
        this.dimensionSizes = multidimensionalArray.dimensionSizes;
        this.strideOfDimension = multidimensionalArray.strideOfDimension;
        this.backingArray = new ArrayList<>(multidimensionalArray.backingArray);
    }

    //<editor-fold defaultstate="collapsed" desc="constructor helpers">
    private List<Integer> calculateStrideOfDimension() {
        List<Integer> strideOfDimensionLocal;
        strideOfDimensionLocal = new ArrayList<>(dimensionSizes);

        final int numDimensions = getNumDimensions();

        strideOfDimensionLocal.set(numDimensions - 1, 1);
        for (int dimension = numDimensions - 1; dimension >= 1; dimension--) {
            final int stride = strideOfDimensionLocal.get(dimension) * getSizeOfDimension(dimension);
            strideOfDimensionLocal.set(dimension - 1, stride);
        }


        return strideOfDimensionLocal;
    }

    private List<T> makeBackingArray() {
        final int backingArraySize = calculateBackingArraySize();

        List<T> backingArrayLocal;
        backingArrayLocal = new ArrayList<>(backingArraySize);

        for (int i = 0; i < backingArraySize; i++) {
            backingArrayLocal.add(null);
        }

        return backingArrayLocal;
    }

    private int calculateBackingArraySize() {
        return getSizeOfDimension(0) * strideOfDimension.get(0);
    }
    //</editor-fold>

    public T get(List<Integer> index) {
        final int backingIndex = getBackingIndex(index);

        return backingArray.get(backingIndex);
    }

    public void set(List<Integer> index, T value) {
        final int backingIndex = getBackingIndex(index);

        backingArray.set(backingIndex, value);
    }

    private int getBackingIndex(List<Integer> index) {
        final int numDimensions = getNumDimensions();
        int backingIndex = 0;
        for (int dimension = numDimensions - 1; dimension >= 0; dimension--) {
            backingIndex += index.get(dimension) * strideOfDimension.get(dimension);
        }
        return backingIndex;
    }

    public Iterator<List<Integer>> getIndexIterator() {
        return new IndexIterator();
    }

    public int getTotalSize() {
        return backingArray.size();
    }

    public int getSizeOfDimension(int dimension) {
        return dimensionSizes.get(dimension);
    }

    public int getNumDimensions() {
        return dimensionSizes.size();
    }

}
