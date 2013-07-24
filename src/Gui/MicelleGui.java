/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gui;

import Engine.PolymerSimulator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author brian
 */
public class MicelleGui extends javax.swing.JFrame {

    private class SimulationTask extends FutureTask<Void> {

        public SimulationTask(int numIterations) {
            super(new SimulationRunnable(numIterations), null);
        }
//        @Override
//        protected void done() {
//            simulationTasks.remove(this);
//        }
    }

    private class SimulationRunnable implements Runnable {

        private int numIterations;

        public SimulationRunnable(int numIterations) {
            this.numIterations = numIterations;
        }

        @Override
        public void run() {
            system.doIterations(numIterations);
        }
    }
    private PolymerSimulator system;
    private final Thread updaterThread;
    private final ThreadPoolExecutor simulationExecutor;
    private final Set<SimulationTask> simulationTasks;

    /**
     * Creates new form MicelleGui
     */
    public MicelleGui() {
        initComponents();
        system = new PolymerSimulator();
        updaterThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    updateDisplay();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MicelleGui.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        int numThreadsAlwaysPresent = 1;
        int maxThreads = 1;
        long keepAliveTime = 1;
        simulationExecutor = new ThreadPoolExecutor(numThreadsAlwaysPresent, maxThreads, keepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5));
        simulationTasks = new HashSet<>();
    }

    public void initialize() {
        registerGuiWithSystem();
        system.randomizePositions();
        //updateDisplay();
        updaterThread.start();
    }

    private void registerGuiWithSystem() {
        displayPanel1.setPolymerSimulator(system);
    }

    private void updateDisplay() {
        energyLbl.setText(String.valueOf(system.getEnergy()));
        numIterationsLbl.setText(String.valueOf(system.getIterationNumber()));
        numAcceptedIterationsLbl.setText(String.valueOf(system.getAcceptedIterations()));
        //System.out.println(String.valueOf(system.springEnergy() / system.getNumBeads()));
        repaint();
    }

    public void setSystem(PolymerSimulator system) {
        this.system = system;
        registerGuiWithSystem();
        system.randomizePositions();
        updateDisplay();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        iterateBtn = new javax.swing.JButton();
        doIterationsBtn = new javax.swing.JButton();
        numIterationsFld = new javax.swing.JTextField();
        randomizeBtn = new javax.swing.JButton();
        energyCaptionLbl = new javax.swing.JLabel();
        energyLbl = new javax.swing.JLabel();
        numIterationsCaptionLbl = new javax.swing.JLabel();
        numIterationsLbl = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        numAcceptedIterationsCaptionLbl = new javax.swing.JLabel();
        numAcceptedIterationsLbl = new javax.swing.JLabel();
        displayPanel1 = new Gui.DisplayPanel();
        cancelBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Polymer Simulator");

        iterateBtn.setText("Iterate");
        iterateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iterateBtnActionPerformed(evt);
            }
        });

        doIterationsBtn.setText("Do Iterations");
        doIterationsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doIterationsBtnActionPerformed(evt);
            }
        });

        numIterationsFld.setText("1");

        randomizeBtn.setText("Randomize");
        randomizeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                randomizeBtnActionPerformed(evt);
            }
        });

        energyCaptionLbl.setText("Energy:");

        energyLbl.setText("1");
        energyLbl.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        numIterationsCaptionLbl.setText("Number of Iterations:");

        numIterationsLbl.setText("0");
        numIterationsLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jButton1.setText("Set Parameters");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        numAcceptedIterationsCaptionLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        numAcceptedIterationsCaptionLbl.setText("Accepted Iterations:");

        numAcceptedIterationsLbl.setText("0");
        numAcceptedIterationsLbl.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        displayPanel1.setPreferredSize(new java.awt.Dimension(600, 600));

        javax.swing.GroupLayout displayPanel1Layout = new javax.swing.GroupLayout(displayPanel1);
        displayPanel1.setLayout(displayPanel1Layout);
        displayPanel1Layout.setHorizontalGroup(
            displayPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );
        displayPanel1Layout.setVerticalGroup(
            displayPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );

        cancelBtn.setText("Cancel");
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(numAcceptedIterationsCaptionLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(numIterationsCaptionLbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(energyCaptionLbl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(energyLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                            .addComponent(numIterationsLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(numAcceptedIterationsLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(displayPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(randomizeBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                            .addComponent(doIterationsBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(iterateBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(numIterationsFld, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(cancelBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(iterateBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numIterationsFld, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(doIterationsBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(62, 62, 62)
                        .addComponent(randomizeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(56, 56, 56)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(displayPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(energyLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(energyCaptionLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(numIterationsCaptionLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(numIterationsLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numAcceptedIterationsCaptionLbl)
                    .addComponent(numAcceptedIterationsLbl))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void iterateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iterateBtnActionPerformed
        system.doIteration();
        updateDisplay();
    }//GEN-LAST:event_iterateBtnActionPerformed

    private void doIterationsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doIterationsBtnActionPerformed
        final int numIterations;
        try {
            final String iterationString = numIterationsFld.getText();
            numIterations = Integer.parseInt(iterationString);
        } catch (NumberFormatException e) {
            return;
        }
        SimulationTask simulationTask = new SimulationTask(numIterations);
        simulationTasks.add(simulationTask);
        simulationExecutor.execute(simulationTask);
    }//GEN-LAST:event_doIterationsBtnActionPerformed
    private void randomizeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_randomizeBtnActionPerformed
        system.randomizePositions();
        updateDisplay();
    }//GEN-LAST:event_randomizeBtnActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        SystemConfiguration configurator = new SystemConfiguration(this);
        configurator.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
//        for (SimulationTask simulationTask : simulationTasks) {
//            simulationTask.cancel(false);
//        }
        for (SimulationTask simulationTask : simulationTasks) {
            simulationTask.cancel(true);
        }
        simulationExecutor.purge();
        simulationTasks.removeAll(simulationTasks);
    }//GEN-LAST:event_cancelBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;










                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MicelleGui.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MicelleGui.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MicelleGui.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MicelleGui.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MicelleGui gui = new MicelleGui();
                gui.setVisible(true);
                gui.initialize();
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelBtn;
    private Gui.DisplayPanel displayPanel1;
    private javax.swing.JButton doIterationsBtn;
    private javax.swing.JLabel energyCaptionLbl;
    private javax.swing.JLabel energyLbl;
    private javax.swing.JButton iterateBtn;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel numAcceptedIterationsCaptionLbl;
    private javax.swing.JLabel numAcceptedIterationsLbl;
    private javax.swing.JLabel numIterationsCaptionLbl;
    private javax.swing.JTextField numIterationsFld;
    private javax.swing.JLabel numIterationsLbl;
    private javax.swing.JButton randomizeBtn;
    // End of variables declaration//GEN-END:variables
}
