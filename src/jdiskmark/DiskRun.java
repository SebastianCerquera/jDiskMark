
package jdiskmark;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 */
@Entity
@Table(name="DiskRun")
@NamedQueries({
@NamedQuery(name="DiskRun.findAll",
    query="SELECT d FROM DiskRun d")
})
public class DiskRun implements Serializable {
    
    static DecimalFormat df = new DecimalFormat("###.###");
    
    public enum Type { READ, WRITE, READ_WRITE; }
    public enum BlockSequence {SEQUENTIAL, RANDOM; }

    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    
    // configuration
    @Column
    Type runType;
    @Column
    BlockSequence blockOrder;
    @Column
    int numMarks = 0;
    @Column
    int numBlocks = 0;
    @Column
    int blockSize = 0;
    @Column
    long txSize = 0;
    @Temporal(TemporalType.TIMESTAMP)
    Date startTime;
    @Temporal(TemporalType.TIMESTAMP)
    Date endTime = null;
    @Column
    int totalMarks = 0;
    @Column
    double cumMin = 0;
    @Column
    double cumMax = 0;
    @Column
    double cumAvg = 0;
    
    @Override
    public String toString() {
        return "Run("+runType+","+blockOrder+"): "+totalMarks+" cum avg: "+cumAvg;
    }
    
    public DiskRun() {
        this.startTime = new Date();
    }
    
    DiskRun(Type type, BlockSequence order) {
        this.startTime = new Date();
        runType = type;
        blockOrder = order;
    }
    
    public String getMin() {
        return cumMin == -1 ? "- -" : df.format(cumMin);
    }
    
    public void setMin(double min) {
        cumMin = min;
    }
    
    public String getMax() {
        return cumMax == -1 ? "- -" : df.format(cumMax);
    }
    
    public void setMax(double max) {
        cumMax = max;
    }
    
    public String getAvg() {
        return cumAvg == -1 ? "- -" : df.format(cumAvg);
    }
    
    public void SetAvg(double avg) {
        cumAvg = avg;
    }
    
    public String getDuration() {
        if (endTime == null) {
            return "unknown";
        }
        long duration = endTime.getTime() - startTime.getTime();
        long diffSeconds = duration / 1000 % 60;
        return String.valueOf(diffSeconds) + "s";
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    static List<DiskRun> findAll() {
        EntityManager em = EM.getEntityManager();
        return em.createNamedQuery("DiskRun.findAll", DiskRun.class).getResultList();
    }
    
    static int deleteAll() {
        EntityManager em = EM.getEntityManager();
        em.getTransaction().begin();
        int deletedCount = em.createQuery("DELETE FROM DiskRun").executeUpdate();
        em.getTransaction().commit();
        return deletedCount;
    }
}
