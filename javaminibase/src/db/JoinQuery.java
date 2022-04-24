package db;

import diskmgr.rdf.IJStream;
import diskmgr.rdf.RdfDB;
import global.JoinStrategy;
import global.SystemDefs;
import global.TupleOrder;
import heap.BasicPatternClass;
import java.util.Arrays;

public class JoinQuery extends BaseQuery implements IQuery {
  private int numBuf;

  public IndexOption getIndexOption() {
    return indexOption;
  }

  public void setIndexOption(IndexOption indexOption) {
    this.indexOption = indexOption;
  }

  private IndexOption indexOption;
  // Join query parameters
  private String sf1;
  private String pf1;
  private String of1;
  private Float cf1;
  private int jnp1;

  public int getJnp2() {
    return jnp2;
  }

  public void setJnp2(int jnp2) {
    this.jnp2 = jnp2;
  }

  public int getJoso2() {
    return joso2;
  }

  public void setJoso2(int joso2) {
    this.joso2 = joso2;
  }

  public String getRsf2() {
    return rsf2;
  }

  public void setRsf2(String rsf2) {
    this.rsf2 = rsf2;
  }

  public String getRpf2() {
    return rpf2;
  }

  public void setRpf2(String rpf2) {
    this.rpf2 = rpf2;
  }

  public String getRof2() {
    return rof2;
  }

  public void setRof2(String rof2) {
    this.rof2 = rof2;
  }

  public Float getRcf2() {
    return rcf2;
  }

  public void setRcf2(Float rcf2) {
    this.rcf2 = rcf2;
  }

  public int[] getLonp2() {
    return lonp2;
  }

  public void setLonp2(int[] lonp2) {
    this.lonp2 = lonp2;
  }

  public int getOrs2() {
    return ors2;
  }

  public void setOrs2(int ors2) {
    this.ors2 = ors2;
  }

  public int getOro2() {
    return oro2;
  }

  public void setOro2(int oro2) {
    this.oro2 = oro2;
  }

  private int jnp2;
  private int joso1, joso2;
  private String rsf1, rsf2;
  private String rpf1, rpf2;
  private String rof1, rof2;
  private Float rcf1, rcf2;
  private int[] lonp1, lonp2;
  private int ors1, ors2;
  private int oro1, oro2;

  public TupleOrder getSo() {
    return so;
  }

  public void setSo(TupleOrder so) {
    this.so = so;
  }

  public int getSnip() {
    return snip;
  }

  public void setSnip(int snip) {
    this.snip = snip;
  }

  public int getNp() {
    return np;
  }

  public void setNp(int np) {
    this.np = np;
  }

  private TupleOrder so;
  private int snip;
  private int np;

  public String filter(String filter) {
    if (filter.equals("*") ||
        filter.isEmpty()) {
      return null;
    } else {
      return filter;
    }
  }

  public JoinQuery(
      String _dbName, String _indexOption, String _numBuf, String _sf1, String _pf1, String _of1, String _cf1,
      String _jnp1, String _joso1, String _rsf1, String _rpf1, String _rof1, String _rcf1, String[] _lonp1,
      String _ors1, String _oro1, String _jnp2, String _joso2, String _rsf2, String _rpf2, String _rof2, String _rcf2
      , String[] _lonp2, String _ors2, String _oro2, String _so, String _snip, String _np) {
    super(_dbName);
    indexOption = IndexOption.valueOf(_indexOption);
    numBuf = Integer.parseInt(_numBuf);
    sf1 = filter(_sf1);
    pf1 = filter(_pf1);
    of1 = filter(_of1);
    if (_cf1.equals("*") ||
        _cf1.isEmpty()) {
      cf1 = null;
    } else {
      cf1 = Float.parseFloat(_cf1);
    }
    // 2 added as it starts from 0 and confidence at index 1
    jnp1 = Integer.parseInt(_jnp1)+2;
    joso1 = Integer.parseInt(_joso1);
    rsf1 = filter(_rsf1);
    rpf1 = filter(_rpf1);
    rof1 = filter(_rof1);
    if (_rcf1.equals("*") ||
        _rcf1.isEmpty()) {
      rcf1 = null;
    } else {
      rcf1 = Float.parseFloat(_rcf1);
    }
    // 2 added as it starts from 0 and confidence at index 1
    lonp1 = Arrays.stream(_lonp1).mapToInt(Integer::valueOf).map(x -> x+2).toArray();
    ors1 = Integer.parseInt(_ors1);
    oro1 = Integer.parseInt(_oro1);
    // 2 added as it starts from 0 and confidence at index 1
    jnp2 = Integer.parseInt(_jnp2)+2;
    joso2 = Integer.parseInt(_joso2);
    rsf2 = filter(_rsf2);
    rpf2 = filter(_rpf2);
    rof2 = filter(_rof2);
    if (_rcf2.equals("*") ||
        _rcf2.isEmpty()) {
      rcf2 = null;
    } else {
      rcf2 = Float.parseFloat(_rcf2);
    }
    // 2 added as it starts from 0 and confidence at index 1
    lonp2 = Arrays.stream(_lonp2).mapToInt(Integer::valueOf).map(x -> x+2).toArray();
    ors2 = Integer.parseInt(_ors2);
    oro2 = Integer.parseInt(_oro2);
    so = new TupleOrder(Integer.valueOf(_so));
    // 2 added as it starts from 0 and confidence at index 1
    snip = Integer.parseInt(_snip)+2;
    np = Integer.parseInt(_np);
  }

  public void execute(JoinStrategy js) throws Exception {
    IJStream stream = null;
    try {
      stream = ((RdfDB) SystemDefs.JavabaseDB).joinStream(this, js);
      BasicPatternClass bp = stream.getNext();
      while (bp != null) {
        //bp.print();
        bp = stream.getNext();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      if (stream!=null) {
        stream.closeStream();
        stream = null;
      }
    }
  }

  @Override
  public void execute() throws Exception {
    for (int i=0; i<3; i++) {
      JoinStrategy js = new JoinStrategy(i);
      execute(js);
    }
  }

  public int getNumBuf() {
    return numBuf;
  }

  public void setNumBuf(int numBuf) {
    this.numBuf = numBuf;
  }

  public String getSf1() {
    return sf1;
  }

  public void setSf1(String sf1) {
    this.sf1 = sf1;
  }

  public String getPf1() {
    return pf1;
  }

  public void setPf1(String pf1) {
    this.pf1 = pf1;
  }

  public String getOf1() {
    return of1;
  }

  public void setOf1(String of1) {
    this.of1 = of1;
  }

  public Float getCf1() {
    return cf1;
  }

  public void setCf1(Float cf1) {
    this.cf1 = cf1;
  }

  public int getJnp1() {
    return jnp1;
  }

  public void setJnp1(int jnp1) {
    this.jnp1 = jnp1;
  }

  public int getJoso1() {
    return joso1;
  }

  public void setJoso1(int joso1) {
    this.joso1 = joso1;
  }

  public String getRsf1() {
    return rsf1;
  }

  public void setRsf1(String rsf1) {
    this.rsf1 = rsf1;
  }

  public String getRpf1() {
    return rpf1;
  }

  public void setRpf1(String rpf1) {
    this.rpf1 = rpf1;
  }

  public String getRof1() {
    return rof1;
  }

  public void setRof1(String rof1) {
    this.rof1 = rof1;
  }

  public Float getRcf1() {
    return rcf1;
  }

  public void setRcf1(Float rcf1) {
    this.rcf1 = rcf1;
  }

  public int[] getLonp1() {
    return lonp1;
  }

  public void setLonp1(int[] lonp1) {
    this.lonp1 = lonp1;
  }

  public int getOrs1() {
    return ors1;
  }

  public void setOrs1(int ors1) {
    this.ors1 = ors1;
  }

  public int getOro1() {
    return oro1;
  }

  public void setOro1(int oro1) {
    this.oro1 = oro1;
  }
}
