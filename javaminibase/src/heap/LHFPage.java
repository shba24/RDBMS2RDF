package heap;

import diskmgr.Page;
import global.*;

import java.io.IOException;

/**
 * Class label heap file page. The design assumes that records are kept compacted when deletions are
 * performed.
 */

public class LHFPage extends HFPage{

  public static final int SIZE_OF_SLOT = 4;
  public static final int DPFIXED = 4 * 2 + 3 * 4;

  public static final int SLOT_CNT = 0;
  public static final int USED_PTR = 2;
  public static final int FREE_SPACE = 4;
  public static final int TYPE = 6;
  public static final int CUR_PAGE = 16;

  /* Warning:
     These items must all pack tight, (no padding) for
     the current implementation to work properly.
     Be careful when modifying this class.
  */
  /**
   * page number of this page
   */
  protected PageId curPage = new PageId();
  /**
   * number of slots in use
   */
  private short slotCnt;
  /**
   * offset of first used byte by data records in data[]
   */
  private short usedPtr;
  /**
   * number of bytes free in data[]
   */
  private short freeSpace;
  /**
   * an arbitrary value used by subclasses as needed
   */
  private short type;

  /**
   * Default constructor
   */
  public LHFPage() {
  }

  /**
   * Constructor of class LHFPage open a LHFPage and make this LHFPage point to the given page
   *
   * @param page the given page in Page type
   */
  public LHFPage(Page page) {
    super(page);
  }

  /**
   * inserts a new Label onto the page, returns LID of this Label
   *
   * @throws IOException I/O errors in C++ Status insertLabel(char *recPtr, int recLen, LID& lid)
   * @param  label a label to be inserted
   * @return LID of label, null if sufficient space does not exist
   */
  public ILID insertLabel(byte[] label)
      throws IOException {

    try {

      int recLen = label.length;
      int spaceNeeded = recLen + SIZE_OF_SLOT;

      // Start by checking if sufficient space exists.
      // This is an upper bound check. May not actually need a slot
      // if we can find an empty one.

      freeSpace = Convert.getShortValue(FREE_SPACE, data);
      if (spaceNeeded > freeSpace) {
        return null;
      } else {

        // look for an empty slot
        slotCnt = Convert.getShortValue(SLOT_CNT, data);
        int i;
        short length;
        for (i = 0; i < slotCnt; i++) {
          length = getSlotLength(i);
          if (length == EMPTY_SLOT) {
            break;
          }
        }

        if (i == slotCnt)   //use a new slot
        {
          // adjust free space
          freeSpace -= spaceNeeded;
          Convert.setShortValue(freeSpace, FREE_SPACE, data);

          slotCnt++;
          Convert.setShortValue(slotCnt, SLOT_CNT, data);
        } else {
          // reusing an existing slot
          freeSpace -= recLen;
          Convert.setShortValue(freeSpace, FREE_SPACE, data);
        }

        usedPtr = Convert.getShortValue(USED_PTR, data);
        usedPtr -= recLen;    // adjust usedPtr
        Convert.setShortValue(usedPtr, USED_PTR, data);

        //insert the slot info onto the data page
        setSlot(i, recLen, usedPtr);

        // insert data onto the data page
        System.arraycopy(label, 0, data, usedPtr, recLen);
        curPage.pid = Convert.getIntValue(CUR_PAGE, data);

        ILID lid = new LID();
        lid.getPageNo().pid = curPage.pid;
        lid.setSlotNo(i);
        return lid;
      }
    } catch (Exception e) {
      System.err.println("Error in inserting Label");
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * delete the record with the specified lid
   *
   * @param lid the Label ID in C++ Status deleteRecord(const LID& lid)
   * @throws InvalidSlotNumberException Invalid slot number
   */
  public void deleteLabel(ILID lid)
      throws InvalidSlotNumberException, IOException {
    try {
      int slotNo = lid.getSlotNo();
      short recLen = getSlotLength(slotNo);
      slotCnt = Convert.getShortValue(SLOT_CNT, data);

      // first check if the record being deleted is actually valid
      if ((slotNo >= 0) && (slotNo < slotCnt) && (recLen > 0)) {
        // The records always need to be compacted, as they are
        // not necessarily stored on the page in the order that
        // they are listed in the slot index.

        // offset of record being deleted
        int offset = getSlotOffset(slotNo);
        usedPtr = Convert.getShortValue(USED_PTR, data);
        int newSpot = usedPtr + recLen;
        int size = offset - usedPtr;

        // shift bytes to the right
        System.arraycopy(data, usedPtr, data, newSpot, size);

        // now need to adjust offsets of all valid slots that refer
        // to the left of the record being removed. (by the size of the hole)

        int i, n, chkoffset;
        for (i = 0, n = DPFIXED; i < slotCnt; n += SIZE_OF_SLOT, i++) {
          if ((getSlotLength(i) >= 0)) {
            chkoffset = getSlotOffset(i);
            if (chkoffset < offset) {
              chkoffset += recLen;
              Convert.setShortValue((short) chkoffset, n + 2, data);
            }
          }
        }

        // move used Ptr forwar
        usedPtr += recLen;
        Convert.setShortValue(usedPtr, USED_PTR, data);

        // increase freespace by size of hole
        freeSpace = Convert.getShortValue(FREE_SPACE, data);
        freeSpace += recLen;
        Convert.setShortValue(freeSpace, FREE_SPACE, data);

        setSlot(slotNo, EMPTY_SLOT, 0);  // mark slot free
      } else {
        throw new InvalidSlotNumberException(null, "HEAPFILE: INVALID_SLOTNO");
      }
    } catch (Exception e) {
      System.err.println("Error in deleting Label");
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * @return LID of first label on page, null if page contains no labels.
   * @throws IOException I/O errors in C++ Status firstLabel(LID& firstLid)
   */
  public ILID firstLabel()
      throws IOException {
    try {
      // find the first non-empty slot

      slotCnt = Convert.getShortValue(SLOT_CNT, data);

      int i;
      short length;
      for (i = 0; i < slotCnt; i++) {
        length = getSlotLength(i);
        if (length != EMPTY_SLOT) {
          break;
        }
      }

      if (i == slotCnt) {
        return null;
      }

      // found a non-empty slot

      ILID lid = new LID();
      lid.setSlotNo(i);
      curPage.pid = Convert.getIntValue(CUR_PAGE, data);
      lid.getPageNo().pid = curPage.pid;

      return lid;
    } catch (Exception e) {
      System.err.println("Error in fetching first Label");
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * @param curLid current label ID
   * @return LID of next label on the page, null if no more labels exist on the page
   * @throws IOException I/O errors in C++ Status nextRecord (LID curLid, LID& nextLid)
   */
  public ILID nextLID(ILID curLid)
      throws IOException {
    try {
      slotCnt = Convert.getShortValue(SLOT_CNT, data);

      int i = curLid.getSlotNo();
      short length;

      // find the next non-empty slot
      for (i++; i < slotCnt; i++) {
        length = getSlotLength(i);
        if (length != EMPTY_SLOT) {
          break;
        }
      }

      if (i >= slotCnt) {
        return null;
      }

      // found a non-empty slot
      ILID lid = new LID();
      lid.setSlotNo(i);
      curPage.setPid(Convert.getIntValue(CUR_PAGE, data));
      lid.getPageNo().setPid(curPage.pid);

      return lid;
    } catch (Exception e) {
      System.err.println("Error in fetching next Label");
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * copies out Label with LID lid into label pointer.
   * <br>
   * Status getLabel(LID lid, char *recPtr, int& recLen)
   *
   * @param lid the label ID
   * @return a label contains the record
   * @throws InvalidSlotNumberException Invalid slot number
   * @see Tuple
   */
  public Label getLabel(ILID lid)
      throws InvalidSlotNumberException, IOException {
    Tuple tuple = null;
    try {
      short recLen;
      short offset;
      byte[] record;
      PageId pageNo = new PageId();
      pageNo.pid = lid.getPageNo().pid;
      curPage.pid = Convert.getIntValue(CUR_PAGE, data);
      int slotNo = lid.getSlotNo();

      // length of record being returned
      recLen = getSlotLength(slotNo);
      slotCnt = Convert.getShortValue(SLOT_CNT, data);
      if ((slotNo >= 0) && (slotNo < slotCnt) && (recLen > 0)
          && (pageNo.pid == curPage.pid)) {
        offset = getSlotOffset(slotNo);
        record = new byte[recLen];
        System.arraycopy(data, offset, record, 0, recLen);
        Label label = new Label(record, 0, recLen);
        return label;
      } else {
        throw new InvalidSlotNumberException(null, "HEAPFILE: INVALID_SLOTNO");
      }
    } catch (Exception e) {
      System.err.println("Error in fetching Label");
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * returns a tuple in a byte array[pageSize] with given LID lid.
   * <br>
   * in C++	Status returnLabel(LID lid, char*& recPtr, int& recLen)
   *
   * @param lid the label ID
   * @return a Label  with its length and offset in the byte array
   * @throws InvalidSlotNumberException Invalid slot number
   * @see Tuple
   */
  public Label returnLabel(ILID lid)
      throws InvalidSlotNumberException, IOException {

    Label label = null;
    try {
      short recLen;
      short offset;
      PageId pageNo = new PageId();
      pageNo.pid = lid.getPageNo().pid;

      curPage.pid = Convert.getIntValue(CUR_PAGE, data);
      int slotNo = lid.getSlotNo();

      // length of record being returned
      recLen = getSlotLength(slotNo);
      slotCnt = Convert.getShortValue(SLOT_CNT, data);

      if ((slotNo >= 0) && (slotNo < slotCnt) && (recLen > 0)
          && (pageNo.pid == curPage.pid)) {

        offset = getSlotOffset(slotNo);
        label = new Label(data, offset, recLen);
        return label;
      } else {
        throw new InvalidSlotNumberException(null, "HEAPFILE: INVALID_SLOTNO");
      }
    } catch (Exception e) {
      System.err.println("Error in fetching Label in byte array");
      e.printStackTrace();
      throw e;
    }
  }

}
