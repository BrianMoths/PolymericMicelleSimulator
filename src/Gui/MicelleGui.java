/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gui;

import Engine.PolymerSystem;

/**
 *
 * @author brian
 */
public class MicelleGui extends javax.swing.JFrame {

    private PolymerSystem system;

    /**
     * Creates new form MicelleGui
     */
    public MicelleGui() {
        initComponents();
        system = new PolymerSystem();
    }

    public void initialize() {
        registerGuiWithSystem();
        system.randomizePositions();
        update();
    }

    private void registerGuiWithSystem() {
        system.setGraphics(displayPanel.getGraphics());
    }

    private void update() {
        energyLbl.setText(String.valueOf(system.getEnergy()));
        numIterationsLbl.setText(String.valueOf(system.getIterationNumber()));
        system.draw();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        displayPanel = new javax.swing.JPanel();
        iterateBtn = new javax.swing.JButton();
        doIterationsBtn = new javax.swing.JButton();
        numIterationsFld = new javax.swing.JTextField();
        randomizeBtn = new javax.swing.JButton();
        energyCaptionLbl = new javax.swing.JLabel();
        energyLbl = new javax.swing.JLabel();
        numIterationsCaptionLbl = new javax.swing.JLabel();
        numIterationsLbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        displayPanel.setPreferredSize(new java.awt.Dimension(600, 600));

        javax.swing.GroupLayout displayPanelLayout = new javax.swing.GroupLayout(displayPanel);
        displayPanel.setLayout(displayPanelLayout);
        displayPanelLayout.setHorizontalGroup(
            displayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );
        displayPanelLayout.setVerticalGroup(
            displayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(displayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(doIterationsBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(iterateBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(numIterationsFld)
                            .addComponent(randomizeBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(numIterationsCaptionLbl)
                            .addComponent(energyCaptionLbl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(energyLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                            .addComponent(numIterationsLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(displayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(iterateBtn)
                        .addGap(43, 43, 43)
                        .addComponent(numIterationsFld, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(doIterationsBtn)
                        .addGap(18, 18, 18)
                        .addComponent(randomizeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(energyCaptionLbl)
                    .addComponent(energyLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(numIterationsCaptionLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(numIterationsLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(36, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void iterateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iterateBtnActionPerformed
        system.doIteration();
        update();
    }//GEN-LAST:event_iterateBtnActionPerformed

    private void doIterationsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doIterationsBtnActionPerformed
        int numIterations;
        try {
            final String iterationString = numIterationsFld.getText();
            numIterations = Integer.parseInt(iterationString);
        } catch (NumberFormatException e) {
            return;
        }
        if (numIterations > 0) {
            system.doIterations(numIterations);
            update();
        }
    }//GEN-LAST:event_doIterationsBtnActionPerformed

    private void randomizeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_randomizeBtnActionPerformed
        system.randomizePositions();
        update();
    }//GEN-LAST:event_randomizeBtnActionPerformed

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
            java.util.logging.Logger.getLogger(MicelleGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MicelleGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MicelleGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MicelleGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MicelleGui gui = new MicelleGui();
                gui.setVisible(true);
                gui.initialize();
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel displayPanel;
    private javax.swing.JButton doIterationsBtn;
    private javax.swing.JLabel energyCaptionLbl;
    private javax.swing.JLabel energyLbl;
    private javax.swing.JButton iterateBtn;
    private javax.swing.JLabel numIterationsCaptionLbl;
    private javax.swing.JTextField numIterationsFld;
    private javax.swing.JLabel numIterationsLbl;
    private javax.swing.JButton randomizeBtn;
    // End of variables declaration//GEN-END:variables
}
