/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.PolymerState.SystemGeometry.Interfaces;

import java.io.Serializable;

/**
 *
 * @author bmoths
 */
public interface SystemGeometry extends ImmutableSystemGeometry, Serializable {

    public void setRMax(int index, double rMax);

}
