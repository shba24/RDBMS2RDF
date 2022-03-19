package index;

import btree.quadbtree.BTFileScan;
import btree.quadbtree.KeyDataEntry;
import diskmgr.rdf.SelectFilter;
import global.QID;
import heap.quadrupleheap.QuadrupleHeapFile;
import heap.Quadruple;
import iterator.*;

import java.io.IOException;

public class QuadIndexScan extends Iterator {
  private BTFileScan btFileScan;
  private SelectFilter selectFilter;
  private QuadrupleHeapFile quadrupleHeapFile;
  /**
   * class constructor. set up the index scan.
   */
  public QuadIndexScan(SelectFilter _filter, BTFileScan scan, QuadrupleHeapFile _quadrupleHeapFile) {
    super();
    btFileScan = scan;
    selectFilter = _filter;
    quadrupleHeapFile = _quadrupleHeapFile;
  }

  /**
   * Returns the next Quadruple in the QuadIndexScan
   *
   * @return
   * @throws Exception
   */
  public Quadruple get_next()
      throws Exception {
    KeyDataEntry entry = btFileScan.get_next();
    while (entry != null) {
      QID qid = ((btree.quadbtree.LeafData) entry.data).getData();
      Quadruple quadruple = quadrupleHeapFile.getQuadruple(qid);
      quadruple.setDefaultHeader();

      if (QuadPredEval.Eval(selectFilter, quadruple) == true) {
        return quadruple;
      }
      entry = btFileScan.get_next();
    }
    return null;
  }

  /**
   * Cleaning up the index scan, does not remove either the original
   * relation or the index from the database.
   *
   * @throws IndexException error from the lower layer
   * @throws IOException    from the lower layer
   */
  public void close() throws IOException, IndexException {
    if (!closeFlag) {
      try {
        btFileScan.DestroyBTreeFileScan();
      } catch (Exception e) {
        throw new IndexException(e, "BTree error in destroying index scan.");
      }
      closeFlag = true;
    }
  }
}