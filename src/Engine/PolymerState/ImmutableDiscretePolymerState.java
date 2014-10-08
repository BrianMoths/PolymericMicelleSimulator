/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.PolymerState;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author bmoths
 */
public interface ImmutableDiscretePolymerState extends Serializable {

    public List<Integer> getChainOfBead(int bead);

    /**
     * Returns the neighbor to the left of the given bead. The bead is
     * represented by its index, which is an integer. If the bead does not have
     * a neighbor to its left, then -1 is output.
     *
     * @param bead the bead whose left neighbor is to be found
     * @return the index of the left neighbor if it exists, or -1 if it does
     * not.
     */
    public int getNeighborToLeftOfBead(int bead);

    /**
     * Returns the neighbor to the right of the given bead. The bead is
     * represented by its index, which is an integer. If the bead does not have
     * a neighbor to its right, then -1 is output.
     *
     * @param bead the bead whose right neighbor is to be found
     * @return the index of the right neighbor if it exists, or -1 if it does
     * not.
     */
    public int getNeighborToRightOfBead(int bead);

    public int getNumBeads();

    public int getNumABeads();

    public int getNumBBeads();

    public boolean isTypeA(int bead);

    List<List<Integer>> getChains();

}
