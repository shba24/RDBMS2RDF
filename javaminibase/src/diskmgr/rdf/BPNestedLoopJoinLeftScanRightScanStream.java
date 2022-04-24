package diskmgr.rdf;

import db.JoinQuery;
import heap.BasicPatternClass;
import heap.quadrupleheap.QuadrupleHeapFile;
import iterator.BPFileScan;
import iterator.BPLeftScanRightScanNestedLoopJoin;
import iterator.BPSort;
import iterator.Iterator;

public class BPNestedLoopJoinLeftScanRightScanStream implements IJStream {
  private Iterator iter;

  public BPNestedLoopJoinLeftScanRightScanStream(
      JoinQuery joinQuery,
      SelectFilter leftSelectFilter,
      SelectFilter rightSelectFilter1,
      SelectFilter rightSelectFilter2,
      QuadrupleHeapFile quadrupleHeapFile
  ) throws Exception {
    BPFileScan am1 = new BPFileScan(quadrupleHeapFile, leftSelectFilter);
    BPLeftScanRightScanNestedLoopJoin bpLeftScanRightScanNestedLoopJoin = new BPLeftScanRightScanNestedLoopJoin(
        joinQuery.getNumBuf(),
        3,
        am1,
        joinQuery.getJnp1(),
        joinQuery.getJoso1(),
        rightSelectFilter1,
        quadrupleHeapFile,
        joinQuery.getLonp1(),
        joinQuery.getOrs1(),
        joinQuery.getOro1()
    );
    BPLeftScanRightScanNestedLoopJoin bpLeftScanRightScanNestedLoopJoin1 = new BPLeftScanRightScanNestedLoopJoin(
        joinQuery.getNumBuf(),
        joinQuery.getLonp1().length + joinQuery.getOrs1() + joinQuery.getOro1() + 1,
        bpLeftScanRightScanNestedLoopJoin,
        joinQuery.getJnp2(),
        joinQuery.getJoso2(),
        rightSelectFilter2,
        quadrupleHeapFile,
        joinQuery.getLonp2(),
        joinQuery.getOrs2(),
        joinQuery.getOro2()
    );
    iter = new BPSort(
        bpLeftScanRightScanNestedLoopJoin1,
        joinQuery.getLonp2().length + joinQuery.getOrs2() + joinQuery.getOro2() + 1,
        joinQuery.getSo(),
        joinQuery.getSnip(),
        joinQuery.getNp()
    );
  }

  @Override
  public BasicPatternClass getNext() throws Exception {
    return (BasicPatternClass) iter.get_next();
  }

  @Override
  public void closeStream() throws Exception {
    iter.close();
  }
}
