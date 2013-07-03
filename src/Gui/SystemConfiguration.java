/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gui;

import Engine.PhysicalConstants;
import Engine.PolymerChain;
import Engine.PolymerCluster;
import Engine.PolymerSimulator;
import Engine.SimulationParameters;
import Engine.SystemGeometry.HardWallSystemGeometry;
import Engine.SystemGeometry.PeriodicSystemGeometry;
import Engine.SystemGeometry.SystemGeometry;

/**
 *
 * @author bmoths
 */
public class SystemConfiguration extends javax.swing.JFrame {

    private final MicelleGui gui;

    /**
     * Creates new form SystemConfiguration
     */
    public SystemConfiguration(MicelleGui gui) {
        initComponents();
        this.gui = gui;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        boundaryConditionBgp = new javax.swing.ButtonGroup();
        geometryPanel = new javax.swing.JPanel();
        dimensionCaptionLbl = new javax.swing.JLabel();
        xMaxCaptionLbl = new javax.swing.JLabel();
        yMaxCaptionLbl = new javax.swing.JLabel();
        dimensionFld = new javax.swing.JTextField();
        xMaxFld = new javax.swing.JTextField();
        yMaxFld = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        hardWallRdo = new javax.swing.JRadioButton();
        periodicRdo = new javax.swing.JRadioButton();
        physicalConstantsPanel = new javax.swing.JPanel();
        temperatureCaptionLbl = new javax.swing.JLabel();
        temperatureFld = new javax.swing.JTextField();
        similarCoefficientCaptionLbl = new javax.swing.JLabel();
        similarOverlapCoefficientFld = new javax.swing.JTextField();
        differentCoefficientCaptionLbl = new javax.swing.JLabel();
        differentOverlapCoefficientFld = new javax.swing.JTextField();
        springConstantCaptionLbl = new javax.swing.JLabel();
        springConstantFld = new javax.swing.JTextField();
        buildSystembtn = new javax.swing.JButton();
        PolymerClusterPanel = new javax.swing.JPanel();
        numberOfChainsCaptionLbl = new javax.swing.JLabel();
        numberOfChainsFld = new javax.swing.JTextField();
        diblockRdo = new javax.swing.JRadioButton();
        numABeadsCaptionLbl = new javax.swing.JLabel();
        numABeadsFld = new javax.swing.JTextField();
        numBBeadsCaptionLbl = new javax.swing.JLabel();
        numBBeadsFld = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Simulator Configuration");

        geometryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("System Geometry"));

        dimensionCaptionLbl.setText("Dimension:");

        xMaxCaptionLbl.setText("Width in X:");

        yMaxCaptionLbl.setText("Width in Y:");

        dimensionFld.setEditable(false);
        dimensionFld.setText("2");

        xMaxFld.setText("20");
        xMaxFld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xMaxFldActionPerformed(evt);
            }
        });

        yMaxFld.setText("20");

        jLabel1.setText("Boundary Conditions:");

        boundaryConditionBgp.add(hardWallRdo);
        hardWallRdo.setText("Hard Wall");

        boundaryConditionBgp.add(periodicRdo);
        periodicRdo.setSelected(true);
        periodicRdo.setText("Periodic");

        javax.swing.GroupLayout geometryPanelLayout = new javax.swing.GroupLayout(geometryPanel);
        geometryPanel.setLayout(geometryPanelLayout);
        geometryPanelLayout.setHorizontalGroup(
            geometryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, geometryPanelLayout.createSequentialGroup()
                .addGroup(geometryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(yMaxCaptionLbl)
                    .addComponent(xMaxCaptionLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(geometryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(xMaxFld)
                    .addComponent(yMaxFld)))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, geometryPanelLayout.createSequentialGroup()
                .addComponent(dimensionCaptionLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dimensionFld))
            .addGroup(geometryPanelLayout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(geometryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(geometryPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(geometryPanelLayout.createSequentialGroup()
                        .addComponent(periodicRdo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(hardWallRdo)
                        .addGap(22, 22, 22))))
        );
        geometryPanelLayout.setVerticalGroup(
            geometryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(geometryPanelLayout.createSequentialGroup()
                .addGroup(geometryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dimensionCaptionLbl)
                    .addComponent(dimensionFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(geometryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(xMaxCaptionLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(xMaxFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(geometryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(yMaxCaptionLbl)
                    .addComponent(yMaxFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(geometryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hardWallRdo)
                    .addComponent(periodicRdo))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        physicalConstantsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Physical Constants"));

        temperatureCaptionLbl.setText("Temperature:");

        temperatureFld.setText("400");
        temperatureFld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                temperatureFldActionPerformed(evt);
            }
        });

        similarCoefficientCaptionLbl.setText("SimilarCoeff.:");

        similarOverlapCoefficientFld.setText("1");

        differentCoefficientCaptionLbl.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        differentCoefficientCaptionLbl.setText("Diff. Coeff.:");

        differentOverlapCoefficientFld.setText("4");

        springConstantCaptionLbl.setText("Spring Const:");

        springConstantFld.setText("40");
        springConstantFld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                springConstantFldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout physicalConstantsPanelLayout = new javax.swing.GroupLayout(physicalConstantsPanel);
        physicalConstantsPanel.setLayout(physicalConstantsPanelLayout);
        physicalConstantsPanelLayout.setHorizontalGroup(
            physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(physicalConstantsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(springConstantCaptionLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(differentCoefficientCaptionLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(similarCoefficientCaptionLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(temperatureCaptionLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(temperatureFld, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .addComponent(similarOverlapCoefficientFld)
                    .addComponent(differentOverlapCoefficientFld)
                    .addComponent(springConstantFld))
                .addContainerGap())
        );
        physicalConstantsPanelLayout.setVerticalGroup(
            physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(physicalConstantsPanelLayout.createSequentialGroup()
                .addGroup(physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(temperatureCaptionLbl)
                    .addComponent(temperatureFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(similarCoefficientCaptionLbl)
                    .addComponent(similarOverlapCoefficientFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(differentCoefficientCaptionLbl)
                    .addComponent(differentOverlapCoefficientFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(physicalConstantsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(springConstantCaptionLbl)
                    .addComponent(springConstantFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        buildSystembtn.setText("Build System");
        buildSystembtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildSystembtnActionPerformed(evt);
            }
        });

        PolymerClusterPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Polymer Description"));

        numberOfChainsCaptionLbl.setText("Number of Chains:");

        numberOfChainsFld.setText("10");

        diblockRdo.setSelected(true);
        diblockRdo.setText("Diblock Copolymer");

        numABeadsCaptionLbl.setLabelFor(numABeadsFld);
        numABeadsCaptionLbl.setText("Number of A Beads:");

        numABeadsFld.setText("10");

        numBBeadsCaptionLbl.setText("Number of B Beads:");

        numBBeadsFld.setText("10");
        numBBeadsFld.setToolTipText("");

        javax.swing.GroupLayout PolymerClusterPanelLayout = new javax.swing.GroupLayout(PolymerClusterPanel);
        PolymerClusterPanel.setLayout(PolymerClusterPanelLayout);
        PolymerClusterPanelLayout.setHorizontalGroup(
            PolymerClusterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PolymerClusterPanelLayout.createSequentialGroup()
                .addGroup(PolymerClusterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PolymerClusterPanelLayout.createSequentialGroup()
                        .addComponent(numberOfChainsCaptionLbl)
                        .addGap(2, 2, 2)
                        .addComponent(numberOfChainsFld))
                    .addGroup(PolymerClusterPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(PolymerClusterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PolymerClusterPanelLayout.createSequentialGroup()
                                .addComponent(diblockRdo)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(PolymerClusterPanelLayout.createSequentialGroup()
                                .addGroup(PolymerClusterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(numABeadsCaptionLbl)
                                    .addComponent(numBBeadsCaptionLbl))
                                .addGap(2, 2, 2)
                                .addGroup(PolymerClusterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(numABeadsFld)
                                    .addComponent(numBBeadsFld))))))
                .addContainerGap())
        );
        PolymerClusterPanelLayout.setVerticalGroup(
            PolymerClusterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PolymerClusterPanelLayout.createSequentialGroup()
                .addGroup(PolymerClusterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numberOfChainsCaptionLbl)
                    .addComponent(numberOfChainsFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(diblockRdo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PolymerClusterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numABeadsCaptionLbl)
                    .addComponent(numABeadsFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PolymerClusterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numBBeadsCaptionLbl)
                    .addComponent(numBBeadsFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(physicalConstantsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(geometryPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PolymerClusterPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buildSystembtn, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 8, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(geometryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(physicalConstantsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PolymerClusterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buildSystembtn, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void xMaxFldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xMaxFldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_xMaxFldActionPerformed

    private void temperatureFldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_temperatureFldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_temperatureFldActionPerformed

    private void springConstantFldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_springConstantFldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_springConstantFldActionPerformed

    private void buildSystembtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buildSystembtnActionPerformed
        SystemGeometry systemGeometry = new PeriodicSystemGeometry();
        PhysicalConstants physicalConstants = new PhysicalConstants();
        SimulationParameters simulationParameters = new SimulationParameters();

        int dimension, numChains, numABeads, numBBeads;
        double xMax = 0, yMax = 0, zMax = 0;
        double temperature, similarOverlapCoefficient, differentOverlapCoefficient, springConstant;

        try {
            final String dimensionString = dimensionFld.getText();
            dimension = Integer.parseInt(dimensionString);
        } catch (NumberFormatException e) {
            System.err.println("couldn't parse dimension");
            return;
        }

        try {
            final String xMaxString = xMaxFld.getText();
            xMax = Double.parseDouble(xMaxString);
        } catch (NumberFormatException e) {
            System.err.println("couldn't parse xMax");
            return;
        }

        try {
            final String yMaxString = yMaxFld.getText();
            yMax = Double.parseDouble(yMaxString);
        } catch (NumberFormatException e) {
            System.err.println("couldn't parse yMax");
            return;
        }

        try {
            final String temperatureString = temperatureFld.getText();
            temperature = Double.parseDouble(temperatureString);
        } catch (NumberFormatException e) {
            System.err.println("couldn't parse temperature");
            return;
        }

        try {
            final String similarOverlapCoefficientString = similarOverlapCoefficientFld.getText();
            similarOverlapCoefficient = Double.parseDouble(similarOverlapCoefficientString);
        } catch (NumberFormatException e) {
            System.err.println("couldn't parse similarOverlapCoefficient");
            return;
        }

        try {
            final String differentOverlapCoefficientString = differentOverlapCoefficientFld.getText();
            differentOverlapCoefficient = Double.parseDouble(differentOverlapCoefficientString);
        } catch (NumberFormatException e) {
            System.err.println("couldn't parse differentOverlapCoefficient");
            return;
        }

        try {
            final String springConstantString = springConstantFld.getText();
            springConstant = Double.parseDouble(springConstantString);
        } catch (NumberFormatException e) {
            System.err.println("couldn't parse spring constant");
            return;
        }

        try {
            final String numChainsString = numberOfChainsFld.getText();
            numChains = Integer.parseInt(numChainsString);
        } catch (NumberFormatException e) {
            System.err.println("couldn't parse number of Chains");
            return;
        }

        try {
            final String numABeadsString = numABeadsFld.getText();
            numABeads = Integer.parseInt(numABeadsString);
        } catch (NumberFormatException e) {
            System.err.println("couldn't parse number of A Beads");
            return;
        }

        try {
            final String numBBeadsString = numBBeadsFld.getText();
            numBBeads = Integer.parseInt(numBBeadsString);
        } catch (NumberFormatException e) {
            System.err.println("couldn't parse number of B Beads.");
            return;
        }

        if (dimension < 1) {
            System.err.println("dimension must be greater than 0.");
            return;
        }

        if (numChains < 1) {
            System.err.println("must have at least one chain.");
            return;
        }

        if (numABeads < 0) {
            System.err.println("number of A Beads must be positive.");
            return;
        }

        if (numBBeads < 0) {
            System.err.println("number of B Beads must be positive.");
            return;
        }

        if (numABeads + numBBeads < 1) {
            System.err.println("There must be at least one bead per chain.");
        }

        if (periodicRdo.isSelected()) {
            systemGeometry = new PeriodicSystemGeometry();
        } else if (hardWallRdo.isSelected()) {
            systemGeometry = new HardWallSystemGeometry();
        }



        systemGeometry.setDimension(dimension);
        systemGeometry.setDimensionSize(0, xMax);
        systemGeometry.setDimensionSize(1, yMax);
        systemGeometry.setDimensionSize(2, zMax);

        PolymerChain polymerChain = PolymerChain.makeChainStartingWithA(numABeads, numBBeads);
        PolymerCluster polymerCluster = PolymerCluster.makeRepeatedChainCluster(polymerChain, numChains);

        physicalConstants.setTemperature(temperature);
        physicalConstants.setSimilarOverlapCoefficient(similarOverlapCoefficient);
        physicalConstants.setDifferentOverlapCoefficient(differentOverlapCoefficient);
        physicalConstants.setSpringCoefficient(springConstant);

        PolymerSimulator polymerSystem;
        polymerSystem = new PolymerSimulator(
                systemGeometry,
                polymerCluster,
                physicalConstants,
                simulationParameters);

        gui.setSystem(polymerSystem);
    }//GEN-LAST:event_buildSystembtnActionPerformed
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(SystemConfiguration.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(SystemConfiguration.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(SystemConfiguration.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(SystemConfiguration.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new SystemConfiguration().setVisible(true);
//            }
//        });
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PolymerClusterPanel;
    private javax.swing.ButtonGroup boundaryConditionBgp;
    private javax.swing.JButton buildSystembtn;
    private javax.swing.JRadioButton diblockRdo;
    private javax.swing.JLabel differentCoefficientCaptionLbl;
    private javax.swing.JTextField differentOverlapCoefficientFld;
    private javax.swing.JLabel dimensionCaptionLbl;
    private javax.swing.JTextField dimensionFld;
    private javax.swing.JPanel geometryPanel;
    private javax.swing.JRadioButton hardWallRdo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel numABeadsCaptionLbl;
    private javax.swing.JTextField numABeadsFld;
    private javax.swing.JLabel numBBeadsCaptionLbl;
    private javax.swing.JTextField numBBeadsFld;
    private javax.swing.JLabel numberOfChainsCaptionLbl;
    private javax.swing.JTextField numberOfChainsFld;
    private javax.swing.JRadioButton periodicRdo;
    private javax.swing.JPanel physicalConstantsPanel;
    private javax.swing.JLabel similarCoefficientCaptionLbl;
    private javax.swing.JTextField similarOverlapCoefficientFld;
    private javax.swing.JLabel springConstantCaptionLbl;
    private javax.swing.JTextField springConstantFld;
    private javax.swing.JLabel temperatureCaptionLbl;
    private javax.swing.JTextField temperatureFld;
    private javax.swing.JLabel xMaxCaptionLbl;
    private javax.swing.JTextField xMaxFld;
    private javax.swing.JLabel yMaxCaptionLbl;
    private javax.swing.JTextField yMaxFld;
    // End of variables declaration//GEN-END:variables
}
