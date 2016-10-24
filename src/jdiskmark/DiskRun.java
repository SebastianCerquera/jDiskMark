
package jdiskmark;

import java.text.DecimalFormat;
import java.util.Date;

/**
 *
 */
public class DiskRun {
    
    static DecimalFormat df = new DecimalFormat("###.###");
    
    public enum Type { READ, WRITE, READ_WRITE; }
    public enum BlockSequence {SEQUENTIAL, RANDOM; }

    // configuration
    Type runType;
    BlockSequence blockOrder;
    int numMarks = 0;
    int numBlocks = 0;
    int blockSize = 0;
    
    // run data
    int id;
    Date startTime = new Date();
    Date endTime = null;
    int totalMarks = 0;
    double cumMin = 0;
    double cumMax = 0;
    double cumAvg = 0;
    
    @Override
    public String toString() {
        return "Run("+runType+","+blockOrder+"): "+totalMarks+" cum avg: "+cumAvg;
    }
    
    DiskRun(Type type, BlockSequence order) {
        runType = type;
        blockOrder = order;
    }
    
    public String getMin() {
        return cumMin == -1 ? "- -" : df.format(cumMin);
    }
    public String getMax() {
        return cumMax == -1 ? "- -" : df.format(cumMax);
    }
    public String getAvg() {
        return cumAvg == -1 ? "- -" : df.format(cumAvg);
    }
    
    public String getDuration() {
        if (endTime == null) {
            return "unknown";
        }
        long duration = endTime.getTime() - startTime.getTime();
        long diffSeconds = duration / 1000 % 60;
        return String.valueOf(diffSeconds) + "s";
    }
}
