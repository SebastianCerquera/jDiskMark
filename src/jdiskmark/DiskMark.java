
package jdiskmark;

import java.text.DecimalFormat;

/**
 *
 */
public class DiskMark {
    
    static DecimalFormat df = new DecimalFormat("###.###");
    
    public enum MarkType { READ,WRITE; }
    
    DiskMark(MarkType type) {
        this.type=type;
    }
    
    MarkType type;
    int markNum = 0;       // x-axis
    double bwMbSec = 0;    // y-axis
    double cumMin = 0;
    double cumMax = 0;
    double cumAvg = 0;
    
    @Override
    public String toString() {
        return "Mark("+type+"): "+markNum+" bwMbSec: "+getBwMbSec()+" avg: "+getAvg();
    }
    
    String getBwMbSec() {
        return df.format(bwMbSec);
    }
    
    String getMin() {
        return df.format(cumMin);
    }
    
    String getMax() {
        return df.format(cumMax);
    }
    
    String getAvg() {
        return df.format(cumAvg);
    }
}
