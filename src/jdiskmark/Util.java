
package jdiskmark;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;
import javax.swing.filechooser.FileSystemView;

/**
 * Utility methods for jDiskMark
 */
public class Util {
    
    static final DecimalFormat DF = new DecimalFormat("###.##");
    
    /**
     * Deletes the Directory and all files within
     * @param path
     * @return 
     */
    static public boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        return (path.delete());
    }
    
    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
    
    /*
     * Not used kept here for reference.
     */
    static public void readPhysicalDrive() throws FileNotFoundException, IOException {
        File diskRoot = new File ("\\\\.\\PhysicalDrive0");
        RandomAccessFile diskAccess = new RandomAccessFile (diskRoot, "r");
        byte[] content = new byte[1024];
        diskAccess.readFully (content);
        System.out.println("done reading fully");
        System.out.println("content "+Arrays.toString(content));
    }
    
    /*
     * Not used kept here for reference.
     */
    public static void sysStats() {
        /* Total number of processors or cores available to the JVM */
        System.out.println("Available processors (cores): " + 
            Runtime.getRuntime().availableProcessors());

        /* Total amount of free memory available to the JVM */
        System.out.println("Free memory (bytes): " + 
            Runtime.getRuntime().freeMemory());

        /* This will return Long.MAX_VALUE if there is no preset limit */
        long maxMemory = Runtime.getRuntime().maxMemory();
        /* Maximum amount of memory the JVM will attempt to use */
        System.out.println("Maximum memory (bytes): " + 
            (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));

        /* Total memory currently available to the JVM */
        System.out.println("Total memory available to JVM (bytes): " + 
            Runtime.getRuntime().totalMemory());

        /* Get a list of all filesystem roots on this system */
        File[] roots = File.listRoots();

        /* For each filesystem root, print some info */
        for (File root : roots) {
          System.out.println("File system root: " + root.getAbsolutePath());
          System.out.println("Total space (bytes): " + root.getTotalSpace());
          System.out.println("Free space (bytes): " + root.getFreeSpace());
          System.out.println("Usable space (bytes): " + root.getUsableSpace());
          System.out.println("Drive Type: "+getDriveType(root));
        }
    }
    
    public static String displayString(double num) {
        return DF.format(num);
    }
    
    /**
     * Gets the drive type string for a root file such as C:\
     * 
     * @param file
     * @return 
     */
    public static String getDriveType(File file) {
        FileSystemView fsv = FileSystemView.getFileSystemView();
        return fsv.getSystemTypeDescription(file);
    }
    
    /**
     * Get OS specific disk info based on the drive the path is mapped to.
     * @param dataDir the data directory being used in the run.
     * @return Disk info if available.
     */
    public static String getDiskInfo(File dataDir) {
        Path dataDirPath = Paths.get(dataDir.getAbsolutePath());
        
        if (System.getProperty("os.name").contains("Linux")) {
            // get disk info for linux
            return "implementation pending";
        } else if (System.getProperty("os.name").contains("Windows")) {
            // get disk info for windows
            String driveLetter = dataDirPath.getRoot().toFile().toString().split(":")[0];
            return Util.getModelFromLetter(driveLetter);
        }
        return "unknown";
    }
    
    /**
     * Get the drive model description based on the windows drive letter. 
     * Uses the powershell script disk-model.ps1
     * 
     * @param driveLetter The single character drive letter.
     * @return Disk Drive Model description or empty string if not found.
     */
    public static String getModelFromLetter(String driveLetter) {
            try {
                Process p = Runtime.getRuntime().exec("powershell -ExecutionPolicy ByPass -File disk-model.ps1");
                p.waitFor();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line=reader.readLine();

                String curDriveLetter = null;
                String curDiskModel = null;
                while (line != null) {
                    System.out.println(line);
                    if (line.trim().isEmpty()) {
                        if (curDriveLetter != null && curDiskModel != null &&
                                curDriveLetter.equalsIgnoreCase(driveLetter)) {
                            return curDiskModel;
                        }
                    }
                    if (line.contains("DriveLetter : ")) {
                        curDriveLetter = line.split(" : ")[1].substring(0, 1);
                        System.out.println("current letter=" + curDriveLetter);
                    }
                    if (line.contains("DiskModel   : ")) {
                        curDiskModel = line.split(" : ")[1];
                        System.out.println("current model="+curDiskModel);
                    }
                    line = reader.readLine();
                }
            }
            catch(IOException | InterruptedException e) {}
            return "";
    }
}
