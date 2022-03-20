package heap.labelheap;

import diskmgr.Page;
import global.*;
import heap.FieldNumberOutOfBoundException;
import heap.HFPage;
import heap.InvalidSlotNumberException;
import heap.InvalidTupleSizeException;
import heap.InvalidTypeException;
import heap.Label;
import heap.Tuple;

import java.io.IOException;

/**
 * Class label heap file page. The design assumes that records are kept compacted when deletions are
 * performed.
 */

public class LHFPage extends HFPage {

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
  public LID insertLabel(byte[] label)
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

        LID lid = new LID();
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
  public void deleteLabel(LID lid)
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

        // move used Ptr forward
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
  public LID firstLabel()
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

      LID lid = new LID();
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
  public LID nextLabel(LID curLid)
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
      LID lid = new LID();
      lid.setSlotNo(i);
      curPage.setPid(Convert.getIntValue(CUR_PAGE, data));
      lid.getPageNo().pid = curPage.pid;

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
  public Label getLabel(LID lid)
      throws InvalidSlotNumberException, IOException, FieldNumberOutOfBoundException, InvalidTupleSizeException,
      InvalidTypeException {
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
        Label label = new Label();
        label.tupleSet(data, offset, recLen);
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
  public Label returnLabel(LID lid)
      throws InvalidSlotNumberException, IOException {

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
        return new Label(data, offset, recLen);
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
