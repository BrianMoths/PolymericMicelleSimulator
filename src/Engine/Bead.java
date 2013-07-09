/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author brian
 */
class Bead {
    
    private final int dimension;
    private double[] position;
    private List<Bead> neighbors;
    
    public Bead(int inDimension){
        dimension = inDimension;
        position = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            position[i] = 0;
        }
        neighbors = new ArrayList<>();
    }
    
    public void setPosition(double[] position){
        this.position =  position;
    }
    
    public double[] getPosition(){
        return position;
    }
    
    public void move(double[] shift){
        for (int i = 0; i < dimension; i++) {
            position[i]+=shift[i];
        }
    }
    
    public void addNeighbor(Bead bead){
        neighbors.add(bead);
        bead.neighbors.add(this);
    }
   
    public double getSquareNeighborDistances(){
        double energy = 0;
        for (Bead bead : neighbors) {
            for (int i = 0; i < dimension; i++) {
                energy += (this.position[i]-bead.position[i])*(this.position[i]-bead.position[i]);
            }
        }
        return energy;
    }
}
