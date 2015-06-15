
package jdiskmark;

/**
 *
 */
public class DiskMark {
    
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
        return "Mark("+type+"): "+markNum+" bwMbSec: "+bwMbSec+" cum avg: "+cumAvg;
    }
}
