/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gui;

import Engine.PolymerSimulator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/**
 *
 * @author bmoths
 */
public class PolymerSimulatorReader {

//    private static class ViewerRunnable implements Runnable{
//
//        private final SystemViewer systemViewer;
//        @Override
//        public void run() {
//            while(true){
//                systemViewer.setPolymerSimulator(readPolymerSimulator());
//            }
//        }
//        
//    }
    public static void main(String[] args) {
        File file = getPolymerSimulatorFile();
        try {
            PolymerSimulatorReader polymerSimulatorReader = new PolymerSimulatorReader(file);
            polymerSimulatorReader.run();
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.toString());
        } catch (IOException ex) {
            Logger.getLogger(PolymerSimulatorReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PolymerSimulatorReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static File getPolymerSimulatorFile() throws IllegalArgumentException {
        String path = System.getProperty("user.dir");
        JFileChooser fileChooser = new JFileChooser(path, null);
        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        } else {
            throw new IllegalArgumentException("User chose to cancel or error happened while choosing file");
        }
    }

    private final File file;
    private final SystemViewer systemViewer;
//    private final Thread viewThread;

    private PolymerSimulatorReader(File file) throws IOException, ClassNotFoundException {
        this.file = file;
        systemViewer = new SystemViewer(readPolymerSimulator());
//        viewThread = makeViewThread();
    }

//    private Thread makeViewThread() {
//        
//    }
    private void run() {
//        viewThread.start();
        while (true) {
            try {
                systemViewer.setPolymerSimulator(readPolymerSimulator());
            } catch (IOException | ClassNotFoundException ex) {
                break;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(PolymerSimulatorReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private PolymerSimulator readPolymerSimulator() throws IOException, ClassNotFoundException {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(
                new FileInputStream(file))) {
            return (PolymerSimulator) objectInputStream.readObject();
        }
    }

}
