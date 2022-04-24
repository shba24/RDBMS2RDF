package diskmgr.rdf;

import btree.StringKey;
import db.JoinQuery;
import diskmgr.IndexingSchemes.IndexScheme;
import heap.BasicPatternClass;
import heap.Quadruple;
import heap.quadrupleheap.QuadrupleHeapFile;
import iterator.BPIndexScan;
import iterator.BPLeftIndexRightIndexHashJoin;
import iterator.BPSort;
import iterator.Iterator;

public class BPHashJoinLeftIndexRightIndexStream implements IJStream {
  private Iterator iter;

  public BPHashJoinLeftIndexRightIndexStream(
      JoinQuery joinQuery,
      IndexScheme indexScheme,
      SelectFilter leftSelectFilter,
      SelectFilter rightSelectFilter1,
      SelectFilter rightSelectFilter2,
      QuadrupleHeapFile quadrupleHeapFile
  ) throws Exception {
    Quadruple quad = new Quadruple();
    quad.setSubjectID(leftSelectFilter.subjectID)
        .setPredicateID(leftSelectFilter.predicateID)
        .setObjectID(leftSelectFilter.objectID)
        .setConfidence(leftSelectFilter.confidenceFilter);
    StringKey lo_key = indexScheme.getKey(quad);
    StringKey hi_key = new StringKey(lo_key.getKey());
    BPIndexScan am1 = new BPIndexScan(
        indexScheme.getBtreeFile().new_scan(lo_key, hi_key),
        quadrupleHeapFile,
        leftSelectFilter
    );
    BPLeftIndexRightIndexHashJoin bpLeftScanRightScanJoin = new BPLeftIndexRightIndexHashJoin(
        joinQuery.getNumBuf(),
        3,
        am1,
        joinQuery.getJnp1(),
        joinQuery.getJoso1(),
        rightSelectFilter1,
        indexScheme,
        quadrupleHeapFile,
        joinQuery.getLonp1(),
        joinQuery.getOrs1(),
        joinQuery.getOro1()
    );
    BPLeftIndexRightIndexHashJoin bpLeftScanRightScanJoin1 = new BPLeftIndexRightIndexHashJoin(
        joinQuery.getNumBuf(),
        joinQuery.getLonp1().length + joinQuery.getOrs1() + joinQuery.getOro1() + 1,
        bpLeftScanRightScanJoin,
        joinQuery.getJnp2(),
        joinQuery.getJoso2(),
        rightSelectFilter2,
        indexScheme,
        quadrupleHeapFile,
        joinQuery.getLonp2(),
        joinQuery.getOrs2(),
        joinQuery.getOro2()
    );
    iter = new BPSort(
        bpLeftScanRightScanJoin1,
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

