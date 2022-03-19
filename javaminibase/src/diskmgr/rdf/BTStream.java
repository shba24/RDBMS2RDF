package diskmgr.rdf;

import btree.quadbtree.BTFileScan;
import global.QID;
import global.QuadOrder;
import heap.Quadruple;
import heap.quadrupleheap.QuadrupleHeapFile;
import index.QuadIndexScan;
import iterator.Iterator;

public class BTStream extends BaseStream {
  private Iterator iter;

  public BTStream(
      QuadOrder _orderType,
      int _numBuf,
      BTFileScan _scan,
      String _subjectFilter,
      String _predicateFilter,
      String _objectFilter,
      Float _confidenceFilter,
      QuadrupleHeapFile _quadrupleHeapFile)
      throws Exception {
    SelectFilter selectFilter = new SelectFilter(_subjectFilter, _predicateFilter, _objectFilter, _confidenceFilter);
    Iterator am = new QuadIndexScan(selectFilter, _scan, _quadrupleHeapFile);
    iter = init(_orderType, _numBuf, am);
  }

  @Override
  public void closeStream() throws Exception {
    iter.close();
  }

  @Override
  public Quadruple getNext() throws Exception {
    return (Quadruple) iter.get_next();
  }
}
