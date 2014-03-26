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

    public int getNeighborToLeftOfBead(int bead);

    public int getNeighborToRightOfBead(int bead);

    public int getNumBeads();

    public int getNumABeads();

    public int getNumBBeads();

    public boolean isTypeA(int bead);

}
