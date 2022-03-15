package heap.labelheap;

/**
 * File DataPageInfo.java
 */

import global.Convert;
import global.GlobalConst;
import global.PageId;
import heap.InvalidTupleSizeException;
import heap.Label;

import java.io.IOException;

/**
 * DataPageInfo class : the type of records stored on a directory page.
 * <p>
 * April 9, 1998
 */

class DataPageInfo implements GlobalConst {

    /**
     * auxiliary fields of DataPageInfo
     */

    public static final int size = 12;// size of DataPageInfo object in bytes
    /**
     * HFPage returns int for avail space, so we use int here
     */
    int availspace;
    /**
     * for efficient implementation of getRecCnt()
     */
    int recct;
    /**
     * obvious: id of this particular data page (a HFPage)
     */
    PageId pageId = new PageId();
    private byte[] data;  // a data buffer

    private int offset;

    /**
     *  We can store roughly pagesize/sizeof(DataPageInfo) records per
     *  directory page; for any given HeapFile insertion, it is likely
     *  that at least one of those referenced data pages will have
     *  enough free space to satisfy the request.
     */

    /**
     * Default constructor
     */
    public DataPageInfo() {
        data = new byte[12]; // size of datapageinfo
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
     * constructor: translate a tuple to a DataPageInfo object
     * it will make a copy of the data in the tuple
     */
    public DataPageInfo(Label label)
            throws InvalidTupleSizeException, IOException, InvalidTupleSizeException {
        // need check _atuple size == this.size ?otherwise, throw new exception
        if (label.getLength() != 12) {
            throw new InvalidTupleSizeException(null, "HEAPFILE: TUPLE SIZE ERROR");
        } else {
            data = label.returnTupleByteArray();
            offset = label.getOffset();

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
     * convert this class objcet to a tuple(like cast a DataPageInfo to Tuple)
     */
    public Label convertToLabel()
            throws IOException {

        // 1) write availspace, recct, pageId into data []
        Convert.setIntValue(availspace, offset, data);
        Convert.setIntValue(recct, offset + 4, data);
        Convert.setIntValue(pageId.pid, offset + 8, data);

        // 2) creat a Tuple object using this array
        Label label = new Label(data, offset, size);

        // 3) return tuple object
        return label;
    }

    /**
     * write this object's useful fields(availspace, recct, pageId)
     * to the data[](may be in buffer pool)
     */
    public void flushToLabel() throws IOException {
        // write availspace, recct, pageId into "data[]"
        Convert.setIntValue(availspace, offset, data);
        Convert.setIntValue(recct, offset + 4, data);
        Convert.setIntValue(pageId.pid, offset + 8, data);

        // here we assume data[] already points to buffer pool

    }
}






