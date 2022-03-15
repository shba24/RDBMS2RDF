package heap.quadrupleheap;

/**
 * File QuadDataPageInfo.java
 */

import global.Convert;
import global.GlobalConst;
import global.PageId;

import heap.InvalidTupleSizeException;
import heap.Quadruple;
import java.io.IOException;

/**
 * QuadDataPageInfo class : the type of records stored on a directory page.
 * <p>
 * April 9, 1998
 */

public class DataPageInfo implements GlobalConst {

  /**
   * auxiliary fields of QuadDataPageInfo
   */

  public static final int size = 12;// size of QuadDataPageInfo object in bytes
  /**
   * THFPage returns int for avail space, so we use int here
   */
  int availspace;
  /**
   * for efficient implementation of getRecCnt()
   */
  int recct;
  /**
   * obvious: id of this particular data page (a THFPage)
   */
  PageId pageId = new PageId();
  private byte[] data;  // a data buffer

  private int offset;

  /**
   *  We can store roughly pagesize/sizeof(QuadDataPageInfo) Quadruples per
   *  directory page; for any given HeapFile insertion, it is likely
   *  that at least one of those referenced data pages will have
   *  enough free space to satisfy the request.
   */

  /**
   * Default constructor
   */
  public DataPageInfo() {
    data = new byte[12]; // size of QuadDataPageInfo
    int availspace = 0;
    recct = 0;
    pageId.pid = INVALID_PAGE;
    offset = 0;
  }

  /**
   * Constructor
   *
   * @param array a byte array
   */
  public DataPageInfo(byte[] array) {
    data = array;
    offset = 0;
  }

  /**
   * constructor: translate a QUadruple to a QuadDataPageInfo object it will make a copy of the data in
   * the tuple
   */
  public DataPageInfo(Quadruple quadruple)
      throws InvalidTupleSizeException, IOException, InvalidTupleSizeException {
    // need check _atuple size == this.size ?otherwise, throw new exception
    if (quadruple.getLength() != 12) {
      throw new InvalidTupleSizeException(null, "HEAPFILE: TUPLE SIZE ERROR");
    } else {
      data = quadruple.returnQuadrupleByteArray();
      offset = quadruple.getOffset();

      availspace = Convert.getIntValue(offset, data);
      recct = Convert.getIntValue(offset + 4, data);
      pageId = new PageId();
      pageId.pid = Convert.getIntValue(offset + 8, data);
    }
  }


  public byte[] returnByteArray() {
    return data;
  }

  /**
   * convert this class objcet to a QuadDataPageInfo(like cast a QuadDataPageInfo to Quadruple)
   */
  public Quadruple convertToQuadruple()
      throws Exception {

    // 1) write availspace, recct, pageId into data []
    Convert.setIntValue(availspace, offset, data);
    Convert.setIntValue(recct, offset + 4, data);
    Convert.setIntValue(pageId.pid, offset + 8, data);

    // 2) creat a Quadruple object using this array
    Quadruple quadruple = new Quadruple(data, offset, size);

    // 3) return Quadruple object
    return quadruple;
  }

  /**
   * write this object's useful fields(availspace, recct, pageId) to the data[](may be in buffer
   * pool)
   */
  public void flushToQuadruple() throws IOException {
    // write availspace, recct, pageId into "data[]"
    Convert.setIntValue(availspace, offset, data);
    Convert.setIntValue(recct, offset + 4, data);
    Convert.setIntValue(pageId.pid, offset + 8, data);

    // here we assume data[] already points to buffer pool

  }
}

