/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.PolymerState.SystemGeometry.Interfaces;

/**
 *
 * @author bmoths
 */
public interface SystemGeometry extends ImmutableSystemGeometry {

    static final long serialVersionUID = 0L;

    public void setRMax(int index, double rMax);

}
