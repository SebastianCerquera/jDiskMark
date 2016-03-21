
package jdiskmark;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker.StateValue;
import static javax.swing.SwingWorker.StateValue.STARTED;
import javax.swing.UIManager;

/**
 * Primary class for global variables.
 */
public class App {
    
    public static final String VERSION = "v0.1";
    public static final String PROPERTIESFILE = "jdm.properties";
    public static final String DATADIRNAME = "jDiskMarkData";
    public static final int MEGABYTE = 1024 * 1024;
    public static final int KILOBYTE = 1024;
    public static final int IDLE_STATE = 0;
    public static final int DISK_TEST_STATE = 1;
    public static enum State {IDLE_STATE, DISK_TEST_STATE};
    
    public static State state = State.IDLE_STATE;
    public static Properties p;
    public static File locationDir = null;
    public static File dataDir = null;
    public static File testFile = null;
    public static boolean multiFile = true;
    public static boolean autoRemoveData = false;
    public static boolean autoReset = true;
    public static boolean showMaxMin = true;
    public static boolean readTest = false;
    public static boolean writeTest = false;
    public static boolean randomEnable = false;
    public static boolean writeSyncEnable = true;
    public static int nextMarkNumber = 1;   // number of the next mark
    public static int numOfFiles = 25;      // desired number of marks
    public static int numOfBlocks = 32;     // desired number of blocks
    public static int blockSizeKb = 512;    // size of a block in KBs
    public static DiskWorker worker = null;
    public static double wMax = -1, wMin = -1, wAvg = -1;
    public static double rMax = -1, rMin = -1, rAvg = -1;
    
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
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(App::init);
    }
    
    public static void init() {
        Gui.mainFrame = new MainFrame();
        Gui.selFrame = new SelectFrame();
        p = new Properties();
        loadConfig();
        System.out.println(App.getConfigString());
        Gui.mainFrame.refreshConfig();
        Gui.mainFrame.setLocationRelativeTo(null);
        Gui.mainFrame.setVisible(true);
        Gui.progressBar = Gui.mainFrame.getProgressBar();
        
        // save configuration on exit...
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() { App.saveConfig(); }
        });
    }
    
    public static void loadConfig() {
        File pFile = new File(PROPERTIESFILE);
        if (!pFile.exists()) { return; }
        try {
            InputStream is = new FileInputStream(pFile);
            p.load(is);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        String value;
        value = p.getProperty("locationDir", System.getProperty("user.home"));
        locationDir = new File(value);        
        value = p.getProperty("multiFile", String.valueOf(multiFile));
        multiFile = Boolean.valueOf(value);
        value = p.getProperty("autoRemoveData", String.valueOf(autoRemoveData));
        autoRemoveData = Boolean.valueOf(value);
        value = p.getProperty("autoReset", String.valueOf(autoReset));
        autoReset = Boolean.valueOf(value);
        value = p.getProperty("randomEnable", String.valueOf(randomEnable));
        randomEnable = Boolean.valueOf(value);
        value = p.getProperty("showMaxMin", String.valueOf(showMaxMin));
        showMaxMin = Boolean.valueOf(value);
        value = p.getProperty("numOfFiles", String.valueOf(numOfFiles));
        numOfFiles = Integer.valueOf(value);
        value = p.getProperty("numOfBlocks", String.valueOf(numOfBlocks));
        numOfBlocks = Integer.valueOf(value);
        value = p.getProperty("blockSizeKb", String.valueOf(blockSizeKb));
        blockSizeKb = Integer.valueOf(value);
        value = p.getProperty("writeTest", String.valueOf(writeTest));
        writeTest = Boolean.valueOf(value);
        value = p.getProperty("readTest", String.valueOf(readTest));
        readTest = Boolean.valueOf(value);
        value = p.getProperty("writeSyncEnable", String.valueOf(writeSyncEnable));
        writeSyncEnable = Boolean.valueOf(value);
    }
    
    public static void saveConfig() {
        p.setProperty("locationDir", App.locationDir.getAbsolutePath());
        p.setProperty("multiFile", String.valueOf(multiFile));
        p.setProperty("autoRemoveData", String.valueOf(autoRemoveData));
        p.setProperty("autoReset", String.valueOf(autoReset));
        p.setProperty("randomEnable", String.valueOf(randomEnable));
        p.setProperty("showMaxMin", String.valueOf(showMaxMin));
        p.setProperty("numOfFiles", String.valueOf(numOfFiles));
        p.setProperty("numOfBlocks", String.valueOf(numOfBlocks));
        p.setProperty("blockSizeKb", String.valueOf(blockSizeKb));
        p.setProperty("writeTest", String.valueOf(writeTest));
        p.setProperty("readTest", String.valueOf(readTest));
        p.setProperty("writeSyncEnable", String.valueOf(writeSyncEnable));
        
        try {
            OutputStream out = new FileOutputStream(new File(PROPERTIESFILE));
            p.store(out, "jDiskMark Properties File");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SelectFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SelectFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static String getConfigString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Config for Java Disk Mark ").append(VERSION).append('\n');
        sb.append("readTest: ").append(readTest).append('\n');
        sb.append("writeTest: ").append(writeTest).append('\n');
        sb.append("locationDir: ").append(locationDir).append('\n');
        sb.append("multiFile: ").append(multiFile).append('\n');
        sb.append("autoRemoveData: ").append(autoRemoveData).append('\n');
        sb.append("autoReset: ").append(autoReset).append('\n');
        sb.append("randomEnable: ").append(randomEnable).append('\n');
        sb.append("showMaxMin: ").append(showMaxMin).append('\n');
        sb.append("numOfFiles: ").append(numOfFiles).append('\n');
        sb.append("numOfBlocks: ").append(numOfBlocks).append('\n');
        sb.append("blockSizeKb: ").append(blockSizeKb).append('\n');
        return sb.toString();
    }
    
    public static void msg(String message) {
        Gui.mainFrame.msg(message);
    }
    
    public static void cancelBenchmark() {
        if (worker == null) { 
            msg("worker is null abort..."); 
            return;
        }
        worker.cancel(true);
    }
    
    public static void startBenchmark() {
        
        //1. check that there isn't already a worker in progress
        if (state == State.DISK_TEST_STATE) {
            //if (!worker.isCancelled() && !worker.isDone()) {
                msg("Test in progress, aborting...");
                return;
            //}
        }
        
        //2. check can write to location
        if (locationDir.canWrite() == false) {
            msg("Selected directory can not be written to... aborting");
            return;
        }
        
        //3. update state
        state = State.DISK_TEST_STATE;
        Gui.mainFrame.adjustSensitivity();
        
        //4. create data dir reference
        dataDir = new File (locationDir.getAbsolutePath()+File.separator+DATADIRNAME);
        
        //5. remove existing test data if exist
        if (App.autoRemoveData && dataDir.exists()) {
            if (dataDir.delete()) {
                msg("removed existing data dir");
            } else {
                msg("unable to remove existing data dir");
            }
        }
        
        //6. create data dir if not already present
        if (dataDir.exists() == false) { dataDir.mkdirs(); }
        
        //7. start disk worker thread
        worker = new DiskWorker();
        worker.addPropertyChangeListener((final PropertyChangeEvent event) -> {
            switch (event.getPropertyName()) {
                case "progress":
                    int value = (Integer)event.getNewValue();
                    Gui.progressBar.setValue(value);
                    break;
                case "state":
                    switch ((StateValue) event.getNewValue()) {
                        case STARTED:
                            Gui.progressBar.setString(String.valueOf(App.getTotalTxSizeKb()));
                            break;
                        case DONE:
                            break;
                    } // end inner switch
                    break;
            }
        });
        worker.execute();
    }
    
    public static long getTotalFileSizeKb() {
        return blockSizeKb * numOfBlocks;
    }
    
    public static long getTotalTxSizeKb() {
        return blockSizeKb * numOfBlocks * numOfFiles;
    }
    
    public static void updateMetrics(DiskMark mark) {
        if (mark.type==DiskMark.MarkType.WRITE) {
            if (wMax==-1 || wMax < mark.bwMbSec) {
                wMax = mark.bwMbSec;
            }
            if (wMin==-1 || wMin > mark.bwMbSec) {
                wMin = mark.bwMbSec;
            }
            if (wAvg==-1) {
                wAvg = mark.bwMbSec;
            } else {
                int n = mark.markNum;
                wAvg = (((double)(n-1)*wAvg)+mark.bwMbSec)/(double)n;
            }
            mark.cumAvg = wAvg;
            mark.cumMax = wMax;
            mark.cumMin = wMin;
        } else {
            if (rMax==-1 || rMax < mark.bwMbSec) {
                rMax = mark.bwMbSec;
            }
            if (rMin==-1 || rMin > mark.bwMbSec) {
                rMin = mark.bwMbSec;
            }
            if (rAvg==-1) {
                rAvg = mark.bwMbSec;
            } else {
                int n = mark.markNum;
                rAvg = (((double)(n-1)*rAvg)+mark.bwMbSec)/(double)n;
            }
            mark.cumAvg = rAvg;
            mark.cumMax = rMax;
            mark.cumMin = rMin;
        }
    }
    
    static public void resetSequence() {
        nextMarkNumber = 1;
    }
    
    static public void resetTestData() {
        nextMarkNumber = 1;
        wAvg = -1;
        wMax = -1;
        wMin = -1;
        rAvg = -1;
        rMax = -1;
        rMin = -1;
    }
}
