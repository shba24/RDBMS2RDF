package diskmgr.rdf;

import global.QuadOrder;
import heap.Quadruple;
import heap.quadrupleheap.QuadrupleHeapFile;
import iterator.Iterator;
import iterator.QuadFileScan;

/**
 * Quadruple File Stream
 */
public class TStream extends BaseStream {
  /**
   * Iterator which iterates over the quadruple
   * from the quadruple heap file.
   */
  private Iterator iter;

  /**
   * Public Constructor
   *
   * @param _orderType
   * @param _numBuf
   * @param quadrupleHeapFile
   * @param _subjectFilter
   * @param _predicateFilter
   * @param _objectFilter
   * @param _confidenceFilter
   * @throws Exception
   */
  public TStream(
      QuadOrder _orderType,
      int _numBuf,
      QuadrupleHeapFile quadrupleHeapFile,
      String _subjectFilter,
      String _predicateFilter,
      String _objectFilter,
      Float _confidenceFilter) throws Exception {
    SelectFilter selectFilter = new SelectFilter(_subjectFilter, _predicateFilter, _objectFilter, _confidenceFilter);
    Iterator am = new QuadFileScan(quadrupleHeapFile, selectFilter);
    iter = init(_orderType, _numBuf, am);
  }

  /**
   * Returns the next Quadruple.
   * null if we reached the end of the stream
   *
   * @return
   * @throws Exception
   */
  @Override
  public Quadruple getNext() throws Exception {
    return (Quadruple) iter.get_next();
  }

  /**
   * Closes the stream.
   *
   * @throws Exception
   */
  @Override
  public void closeStream() throws Exception {
    iter.close();
  }
}
