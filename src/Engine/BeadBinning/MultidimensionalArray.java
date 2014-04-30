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
 * A non-resizable array of arbitrary rank. The size of the array along each
 * dimension is specified when the array is constructed. Individual elements of
 * the array can be querried and mutated. Also there are methods to get the
 * total number of elements in the array, the size of each dimension, and the
 * total number of dimensions (i.e., rank) of the array.
 *
 * @param <T> the type of elements in the array
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

    private static final long serialVersionUID = 0L;
    private final List<Integer> dimensionSizes;
    private final List<Integer> strideOfDimension;
    private final List<T> backingArray;

    /**
     * constructs an array whose rank is the length of the given list and where
     * the size of the <tt>i</tt>th dimension is the <tt>i</tt>th element in the
     * list
     *
     * @param dimensionSizes the list specifying the rank of the array and the
     * size of each dimension.
     */
    public MultidimensionalArray(List<Integer> dimensionSizes) {
        this.dimensionSizes = new ArrayList<>(dimensionSizes);
        strideOfDimension = calculateStrideOfDimension();
        backingArray = makeBackingArray();
    }

    /**
     * constructs an array whose rank is the length of the input array and where
     * the size of the <tt>i</tt>th dimension is the <tt>i</tt>th element in the
     * input array
     *
     * @param dimensionSizes the array specifying the rank of the
     * multidimensional array and the size of each dimension.
     */
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

    /**
     * constructs a copy of the given multidimensional array
     *
     * @param multidimensionalArray the array to be copied.
     */
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

    /**
     * return the element at the given position.
     *
     * @param index the index of the element to be returned
     * @return the element at the specified position
     */
    public T get(List<Integer> index) {
        final int backingIndex = getBackingIndex(index);

        return backingArray.get(backingIndex);
    }

    /**
     * Replaces the element at the specified position of the list with the
     * specified element
     *
     * @param index the position of the element to replace
     * @param value the new element to store at the specified position
     */
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

    /**
     * returns an iterator which generates all possible indices of the array in
     * lexicographical order
     *
     * @return the iterator
     */
    public Iterator<List<Integer>> getIndexIterator() {
        return new IndexIterator();
    }

    /**
     * returns the total number of elements in the array
     *
     * @return the total number of elements in the array
     */
    public int getTotalSize() {
        return backingArray.size();
    }

    /**
     * returns the size of the specified dimension. This si one more than the
     * maximum allowed index in that dimension.
     *
     * @param dimension the dimension whose size is to be returned
     * @return the size of the given dimension
     */
    public int getSizeOfDimension(int dimension) {
        return dimensionSizes.get(dimension);
    }

    /**
     * returns the total number of dimensions, or rank, of the array.
     *
     * @return the total number of dimensions of the array.
     */
    public int getNumDimensions() {
        return dimensionSizes.size();
    }

}
