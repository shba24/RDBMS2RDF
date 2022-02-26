package heap;

import diskmgr.Page;
import global.*;

import java.io.IOException;

/**
 * Class label heap file page. The design assumes that records are kept compacted when deletions are
 * performed.
 */

public class LHFPage {

  private HFPage hfpage;

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
    this.hfpage = new HFPage(page);
  }

  /**
   * Checks if the object is valid
   *
   * @return boolean representing the
   * validity of object
   */
  private boolean IsValid() {
    return hfpage != null;
  }

  /**
   * Checks if the object is valid and if invalid
   * throws exception, other does nothing
   *
   * @throws NullPointerException
   */
  private void checkNullObjectAndThrowException() throws NullPointerException {
    if (!IsValid()) {
      throw new NullPointerException("Label heap file page Error, Not initialized");
    }
  }

  /**
   * Constructor of class LHFPage initialize a new page
   *
   * @param pageNo the page number of a new page to be initialized
   * @param apage  the Page to be initialized
   * @throws IOException I/O errors
   * @see Page
   */
  public void init(PageId pageNo, Page apage)
      throws IOException {
    checkNullObjectAndThrowException();
    try {
      this.hfpage.init(pageNo, apage);
    }catch (Exception e) {
      System.err.println("Error in initializing Label");
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * Constructor of class LHFPage open a existed LHFPage
   *
   * @param apage a page in buffer pool
   */
  public void openLHFPage(Page apage) {
    checkNullObjectAndThrowException();
    this.hfpage.openHFpage(apage);
  }


  /**
   * @return byte array
   */
  public byte[] getLHFPageArray() {
    checkNullObjectAndThrowException();
    return this.hfpage.getHFpageArray();
  }

  /**
   * Dump contents of a page
   *
   * @throws IOException I/O errors
   */
  public void dumpPage()
      throws IOException {
    checkNullObjectAndThrowException();
    hfpage.dumpPage();
  }

  /**
   * @throws IOException I/O errors
   * @return PageId of previous page
   */
  public PageId getPrevPage()
      throws IOException {
    checkNullObjectAndThrowException();
    return hfpage.getPrevPage();
  }

  /**
   * sets value of prevPage to pageNo
   *
   * @param pageNo page number for previous page
   * @throws IOException I/O errors
   */
  public void setPrevPage(PageId pageNo)
      throws IOException {
    checkNullObjectAndThrowException();
    hfpage.setPrevPage(pageNo);
  }

  /**
   * @return page number of next page
   * @throws IOException I/O errors
   */
  public PageId getNextPage()
      throws IOException {
    checkNullObjectAndThrowException();
    return hfpage.getNextPage();
  }

  /**
   * sets value of nextPage to pageNo
   *
   * @throws IOException I/O errors
   * @param  pageNo  page number for next page
   */
  public void setNextPage(PageId pageNo)
      throws IOException {
    checkNullObjectAndThrowException();
    hfpage.setNextPage(pageNo);
  }

  /**
   * @return page number of current page
   * @throws IOException I/O errors
   */
  public PageId getCurPage()
      throws IOException {
    checkNullObjectAndThrowException();
    return hfpage.getCurPage();
  }

  /**
   * sets value of curPage to pageNo
   *
   * @throws IOException I/O errors
   * @param  pageNo  page number for current page
   */
  public void setCurPage(PageId pageNo)
      throws IOException {
    checkNullObjectAndThrowException();
    hfpage.setCurPage(pageNo);
  }

  /**
   * @return the ype
   * @throws IOException I/O errors
   */
  public short getType()
      throws IOException {
    checkNullObjectAndThrowException();
    return hfpage.getType();
  }

  /**
   * sets value of type
   *
   * @throws IOException I/O errors
   * @param  valtype an arbitrary value
   */
  public void setType(short valtype)
      throws IOException {
    checkNullObjectAndThrowException();
    hfpage.setType(valtype);
  }

  /**
   * @return slotCnt used in this page
   * @throws IOException I/O errors
   */
  public short getSlotCnt()
      throws IOException {
    checkNullObjectAndThrowException();
    return hfpage.getSlotCnt();
  }

  /**
   * sets slot contents
   *
   * @param slotno the slot number
   * @param length length of quadruple the slot contains
   * @throws IOException I/O errors
   * @param  offset offset of quadruple
   */
  public void setSlot(int slotno, int length, int offset)
      throws IOException {
    checkNullObjectAndThrowException();
    hfpage.setSlot(slotno, length, offset);
  }

  /**
   * @throws IOException I/O errors
   * @param  slotno  slot number
   * @return the length of quadruple the given slot contains
   */
  public short getSlotLength(int slotno)
      throws IOException {
    checkNullObjectAndThrowException();
    return hfpage.getSlotLength(slotno);
  }

  /**
   * @param slotno slot number
   * @return the offset of quadruple the given slot contains
   * @throws IOException I/O errors
   */
  public short getSlotOffset(int slotno)
      throws IOException {
    checkNullObjectAndThrowException();
    return hfpage.getSlotOffset(slotno);
  }

  /**
   * returns the amount of available space on the page.
   *
   * @return the amount of available space on the page
   * @throws IOException I/O errors
   */
  public int available_space()
      throws IOException {
    checkNullObjectAndThrowException();
    return hfpage.available_space();
  }

  /**
   * Determining if the page is empty
   *
   * @return true if the LHFPage is has no records in it, false otherwise
   * @throws IOException I/O errors
   */
  public boolean empty()
      throws IOException {
    checkNullObjectAndThrowException();
    return hfpage.empty();
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
    checkNullObjectAndThrowException();

    RID rid;
    try {
      rid = hfpage.insertRecord(label);
    } catch (Exception e) {
      System.err.println("Error in inserting Label");
      e.printStackTrace();
      throw e;
    }

    ILID lid = new LID(rid.pageNo, rid.slotNo);
    return lid;
  }

  /**
   * delete the record with the specified lid
   *
   * @param lid the Label ID in C++ Status deleteRecord(const LID& lid)
   * @throws InvalidSlotNumberException Invalid slot number
   */
  public void deleteLabel(ILID lid)
      throws InvalidSlotNumberException, IOException {
    checkNullObjectAndThrowException();
    RID rid = new RID(lid.getPageNo(), lid.getSlotNo());
    try {
      hfpage.deleteRecord(rid);
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
    checkNullObjectAndThrowException();
    RID rid;
    try {
      rid = hfpage.firstRecord();
    } catch (Exception e) {
      System.err.println("Error in fetching first Label");
      e.printStackTrace();
      throw e;
    }

    ILID lid = new LID(rid.pageNo, rid.slotNo);
    return lid;
  }

  /**
   * @param curLid current label ID
   * @return LID of next label on the page, null if no more labels exist on the page
   * @throws IOException I/O errors in C++ Status nextRecord (LID curLid, LID& nextLid)
   */
  public ILID nextLID(ILID curLid)
      throws IOException {
    checkNullObjectAndThrowException();
    RID curRid = new RID(curLid.getPageNo(), curLid.getSlotNo());

    try {
      curRid = hfpage.nextRecord(curRid);
    } catch (Exception e) {
      System.err.println("Error in fetching next Label");
      e.printStackTrace();
      throw e;
    }

    ILID lid = new LID(curRid.pageNo, curRid.slotNo);

    return lid;
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
    checkNullObjectAndThrowException();
    RID rid = new RID(lid.getPageNo(), lid.getSlotNo());

    Tuple tuple = null;
    try {
      tuple = hfpage.getRecord(rid);
    } catch (Exception e) {
      System.err.println("Error in fetching Label");
      e.printStackTrace();
      throw e;
    }
    Label label = new Label();
    label.setTuple(tuple);
    label.setLid(lid);
    return label;
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
    checkNullObjectAndThrowException();

    RID rid = new RID(lid.getPageNo(), lid.getSlotNo());

    Tuple tuple = null;
    try {
      tuple = hfpage.returnRecord(rid);
    } catch (Exception e) {
      System.err.println("Error in fetching Label in byte array");
      e.printStackTrace();
      throw e;
    }
    Label label = new Label();
    label.setTuple(tuple);
    label.setLid(lid);
    return label;
  }

  /**
   * Compacts the slot directory on an LHFPage. WARNING -- this will probably lead to a change in
   * the LIDs of records on the page.  You CAN'T DO THIS on most kinds of pages.
   *
   * @throws IOException I/O errors
   */
  protected void compact_slot_dir()
      throws IOException {
    checkNullObjectAndThrowException();
    this.hfpage.compact_slot_dir();
  }

}

