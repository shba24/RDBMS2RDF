package heap;

import diskmgr.Page;
import global.IQID;
import global.PageId;
import global.QID;
import global.RID;
import java.io.IOException;

/**
 * THFPage is the implementation of the quadruple heap file page. It uses adapter design pattern to
 * deliver its functionality by using HFPage object as a member of this class.
 */
public class THFPage {

  private HFPage hfPage;

  /**
   * Default constructor. No operations take place inside this constructor.
   */
  public THFPage() {

  }

  /**
   * Constructor to initialize a quadruple heap file page from a Page object.
   *
   * @param page page to construct the THFPage from
   */
  public THFPage(Page page) {
    this.hfPage = new HFPage(page);
  }

  /**
   *
   * @return HFpage
   */
  public HFPage getHFPage(){
    return this.hfPage;
  }

  /**
   * Initialize THFPage using a page id and a page. Internally calls init() on the underlying hfPage
   * object.
   *
   * @param pageNo page no to initialize quadruple heap file page with
   * @param page page to initialize quadruple heap file page with
   * @throws IOException          I/O errors originating from Convert usage
   * @throws NullPointerException thrown when the pageNo or page is null
   */
  public void init(PageId pageNo, Page page) throws IOException, NullPointerException {
    if (pageNo == null || page == null) {
      throw new NullPointerException("[THFPage] One or more arguments is null in init()");
    }

    try {
      this.hfPage.init(pageNo, page);
    } catch (Exception e) {
      System.err.println("[THFPage] Error in initializing THFPage object.");
      throw e;
    }
  }

  /**
   * Open an existing quadruple heap file page.
   *
   * @param page page to open the quadruple heap file page from
   */
  public void openTHFPage(Page page) throws NullPointerException {
    if (page == null) {
      throw new NullPointerException("[THFPage] The argument is null in openTHFPage()");
    }

    this.hfPage.openHFpage(page);
  }

  /**
   * Returns the byte array of underlying HFPage object.
   *
   * @return byte array of the underlying HFPage object
   */
  public byte[] getTHFPageArray() {
    return this.hfPage.getHFpageArray();
  }

  /**
   * Dump contents of the quadruple heap file page.
   *
   * @throws IOException I/O errors
   */
  public void dumpPage() throws IOException {
    try {
      this.hfPage.dumpPage();
    } catch (IOException e) {
      System.err.println("[THFPage] I/O error in dumpPage");
      throw e;
    }
  }

  /**
   * Returns the page id of the previous page in the doubly linked list.
   *
   * @return PageId of previous page
   * @throws IOException I/O errors
   */
  public PageId getPrevPage() throws IOException {
    try {
      return this.hfPage.getPrevPage();
    } catch (IOException e) {
      System.err.println("[THFPage] I/O error in getting previous page.");
      throw e;
    }
  }

  /**
   * Sets the previous page id in the doubly linked list.
   *
   * @param pageNo page number for previous page
   * @throws IOException          I/O errors
   * @throws NullPointerException thrown when the pageNo is null
   */
  public void setPrevPage(PageId pageNo) throws IOException, NullPointerException {
    if (pageNo == null) {
      throw new NullPointerException("[THFPage] The argument is null in setPrevPage()");
    }

    try {
      this.hfPage.setPrevPage(pageNo);
    } catch (IOException e) {
      System.err.println("[THFPage] I/O error in setting previous page.");
      throw e;
    }
  }

  /**
   * Returns the page id of the next page.
   *
   * @return page number of next page
   * @throws IOException I/O errors
   */
  public PageId getNextPage() throws IOException {
    try {
      return this.hfPage.getNextPage();
    } catch (IOException e) {
      System.err.println("[THFPage] I/O error in getting next page.");
      throw e;
    }
  }

  /**
   * Sets value of nextPage to pageNo.
   *
   * @param pageNo page number for next page
   * @throws IOException          I/O errors
   * @throws IOException          I/O exception when setting the next page
   * @throws NullPointerException thrown when the pageNo is null
   */
  public void setNextPage(PageId pageNo) throws IOException, NullPointerException {
    if (pageNo == null) {
      throw new NullPointerException("[THFPage] The argument is null in setNextPage()");
    }

    try {
      this.hfPage.setNextPage(pageNo);
    } catch (IOException e) {
      System.err.println("[THFPage] I/O error in setting next page.");
      throw e;
    }
  }

  /**
   * Returns page id of the current page.
   *
   * @return page number of current page
   * @throws IOException I/O errors
   */
  public PageId getCurPage() throws IOException {
    try {
      return this.hfPage.getCurPage();
    } catch (IOException e) {
      System.err.println("[THFPage] I/O error in getting current page.");
      throw e;
    }
  }

  /**
   * Sets the current page to page no.
   *
   * @throws IOException          I/O errors
   * @throws NullPointerException thrown when the pageNo is null
   */
  public void setCurPage(PageId pageNo) throws IOException, NullPointerException {
    if (pageNo == null) {
      throw new NullPointerException("[THFPage] The argument is null in setCurPage()");
    }

    try {
      this.hfPage.setCurPage(pageNo);
    } catch (IOException e) {
      System.err.println("[THFPage] I/O error in setting current page.");
      throw e;
    }
  }

  /**
   * Returns the type of node in the B+ tree.
   *
   * @return short indicating the type
   * @throws IOException I/O exception when reading the type of node
   */
  public short getType() throws IOException {
    try {
      return this.hfPage.getType();
    } catch (IOException e) {
      System.err.println("[THFPage] I/O error in getting type.");
      throw e;
    }
  }

  /**
   * Sets the type to valtype for the node of the B+ tree.
   *
   * @param valtype value of the node type
   * @throws IOException I/O exception when writing the type to the
   * page
   */
  public void setType(short valtype) throws IOException {
    /*
     TODO: Check if valtype is valid based on constants created in
     QuadrtupleBTree or LabelBTree
     */

    try {
      this.hfPage.setType(valtype);
    } catch (IOException e) {
      System.err.println("[THFPage] I/O error in setting type.");
      throw e;
    }
  }

  /**
   * Returns the slot count.
   *
   * @return short value with the slot count
   * @throws IOException I/O exception when reading the slot count
   */
  public short getSlotCnt() throws IOException {
    try {
      return this.hfPage.getSlotCnt();
    } catch (IOException e) {
      System.err.println("[THFPage] I/O error in setting type.");
      throw e;
    }
  }

  /**
   * Sets the details for a slot.
   *
   * @param slotno slot number
   * @param length length of the byte array for the quadruple
   * @param offset offset
   * @throws IOException I/O exception when setting slot details
   * @throws IllegalArgumentException thrown when integer values in the argument are negative
   */
  public void setSlot(int slotno, int length, int offset)
      throws IOException, IllegalArgumentException {
    if (slotno < 0 || length < 0 || offset < 0) {
      throw new IllegalArgumentException(
          "[THFPage] Received unacceptable integer value in one of the arguments of setSlot()");
    }

    try {
      this.hfPage.setSlot(slotno, length, offset);
    } catch (IOException e) {
      System.err.println("[THFPage] I/O error in setting slot.");
      throw e;
    }
  }

  /**
   * Return the quadruple length for the specified slot number.
   *
   * @param slotno slot number for which slot length is requested
   * @return short value indicating the quadruple length
   * @throws IOException              I/O exception when reading slot length
   * @throws IllegalArgumentException thrown when slot no is negative
   */
  public short getSlotLength(int slotno) throws IOException, IllegalArgumentException {
    if (slotno < 0) {
      throw new IllegalArgumentException(
          "[THFPage] Received unacceptable integer value in the argument of getSlotLength()");
    }

    try {
      return this.hfPage.getSlotLength(slotno);
    } catch (IOException e) {
      System.err.println("[THFPage] I/O error in setting slot.");
      throw e;
    }
  }

  /**
   * Returns the offset at which the quadruple is stored.
   *
   * @param slotno slot number
   * @return the offset of quadruple the given slot contains
   * @throws IOException              I/O errors
   * @throws IllegalArgumentException thrown when the slot number is negative
   */
  public short getSlotOffset(int slotno) throws IOException, IllegalArgumentException {
    if (slotno < 0) {
      throw new IllegalArgumentException(
          "[THFPage] Received unacceptable integer value in the argument of getSlotOffset()");
    }

    try {
      return this.hfPage.getSlotOffset(slotno);
    } catch (IOException e) {
      System.err.println("[THFPage] I/O error in getting slot offset.");
      throw e;
    }
  }

  /**
   * Inserts a new quadruple onto the page. Returns the generated QID for the quadruple being
   * stored.
   *
   * @param quadruple a quadruple to be inserted
   * @return QID of quadruple, null if sufficient space does not exist
   * @throws IOException              I/O errors in C++ Status insertRecord(char *recPtr, int
   *                                  recLen, RID& rid)
   * @throws IllegalArgumentException thrown when the byte array is empty
   */
  public IQID insertQuadruple(byte[] quadruple) throws IOException, IllegalArgumentException {
    if (quadruple.length == 0) {
      throw new IllegalArgumentException(
          "[THFPage] Received empty byte array in insertQuadruple()");
    }

    RID rid;

    try {
      rid = this.hfPage.insertRecord(quadruple);
    } catch (IOException e) {
      System.err.println("[THFPage] I/O error in inserting quadruple.");
      throw e;
    }

    /*
      rid is null when insertion of quadruple fails. When QHeapfile calls insertQuadruple,
      it will receive null qid. In that case, QHeapfile should throw new QHeapfileException,
      the way it is done in Heapfile.
     */
    if (rid != null) {
      return new QID(rid.pageNo, rid.slotNo);
    } else {
      return null;
    }
  }

  /**
   * Delete the quadruple with the specified qid.
   *
   * @param qid qid of the quadruple to be deleted
   * @throws IOException                I/O exception when deleting quadruple from the page
   * @throws InvalidSlotNumberException Invalid slot number
   * @throws NullPointerException       NPE when qid is null
   */
  public void deleteQuadruple(IQID qid) throws IOException, InvalidSlotNumberException {
    if (qid == null) {
      throw new NullPointerException("[THFPage] The argument is null in deleteQuadruple()");
    }

    RID rid = new RID();
    rid.pageNo = qid.getPageNo();
    rid.slotNo = qid.getSlotNo();

    try {
      this.hfPage.deleteRecord(rid);
    } catch (IOException e) {
      System.err.println("[THFPage] I/O error in deleting quadruple.");
      throw e;
    } catch (InvalidSlotNumberException e) {
      System.err.println("[THFPage] InvalidSlotNumberException in deleting quadruple.");
      throw e;
    }
  }

  /**
   * Returns the first quadruple stored on the page. In case there are no quadruples, it returns
   * null.
   *
   * @return qid of the first quadruple in the page
   * @throws IOException I/O exception when reading the first quadruple from the page
   */
  public IQID firstQuadruple() throws IOException {
    RID rid;

    try {
      rid = this.hfPage.firstRecord();
    } catch (IOException e) {
      System.err.println("[THFPage] I/O exception in retrieving first quadruple.");
      throw e;
    }

    if (rid == null) {
      return null;
    } else {
      return new QID(rid.pageNo, rid.slotNo);
    }
  }

  /**
   * Returns the next quadruple stored on the page given the QID of the current quadruple.
   *
   * @param curQuad qid of the current quadruple
   * @return qid of the next quadruple
   * @throws IOException          I/O exception when reading the next quadruple
   * @throws NullPointerException NPE when curQuad is null from the page
   */
  public IQID nextQuadruple(IQID curQuad) throws IOException, NullPointerException {
    if (curQuad == null) {
      throw new NullPointerException("[THFPage] The argument is null in nextQuadruple()");
    }

    RID curRid = new RID();
    curRid.pageNo = curQuad.getPageNo();
    curRid.slotNo = curQuad.getSlotNo();

    RID nextRid;

    try {
      nextRid = this.hfPage.nextRecord(curRid);
    } catch (IOException e) {
      System.err.println("[THFPage] I/O exception in retrieving next quadruple.");
      throw e;
    }

    if (nextRid == null) {
      return null;
    } else {
      return new QID(nextRid.pageNo, nextRid.slotNo);
    }
  }

  /**
   * Get the quadruple with the specified QID.
   *
   * @param qid qid of the quadruple to be fetched
   * @return quadruple with the specified QID
   * @throws IOException                I/O exception when reading the quadruple from the page
   * @throws InvalidSlotNumberException Invalid slot number
   * @throws NullPointerException       thrown when qid is null
   */
  public Quadruple getQuadruple(IQID qid)
      throws IOException, InvalidSlotNumberException, NullPointerException {
    if (qid == null) {
      throw new NullPointerException("[THFPage] qid is null in getQuadruple()");
    }

    RID rid = new RID();

    rid.pageNo = qid.getPageNo();
    rid.slotNo = qid.getSlotNo();

    Tuple tuple;

    try {
      tuple = this.hfPage.getRecord(rid);
    } catch (IOException e) {
      System.err.println("[THFPage] I/O exception in getting quadruple.");
      throw e;
    } catch (InvalidSlotNumberException e) {
      System.err.println("[THFPage] Invalid slot number in getting quadruple.");
      throw e;
    }

    // tuple will not be null. When it will be, exception is thrown.
    if (tuple != null) {
      return new Quadruple(tuple.getTupleByteArray(), tuple.getOffset());
    } else {
      return null;
    }
  }

  /**
   * Returns the quadruple for the specified QID. Null is returned if the quadruple is not found.
   *
   * @param qid qid of the quadruple to be fetched
   * @return quadruple to be fetched
   * @throws InvalidSlotNumberException invalid slot number
   * @throws IOException                I/O exception when reading quadruple from the page
   * @throws NullPointerException       NPE when qid is null
   */
  public Quadruple returnQuadruple(IQID qid)
      throws InvalidSlotNumberException, IOException, NullPointerException {
    if (qid == null) {
      throw new NullPointerException("[THFPage] qid is null in returnQuadruple()");
    }

    RID rid = new RID();
    rid.pageNo = qid.getPageNo();
    rid.slotNo = qid.getSlotNo();
    Tuple tuple;

    try {
      tuple = this.hfPage.returnRecord(rid);
    } catch (IOException | InvalidSlotNumberException e) {
      System.err.println("[THFPage] I/O exception in returning quadruple.");
      throw e;
    }

    if (tuple != null) {
      return new Quadruple(tuple.getTupleByteArray(), tuple.getOffset());
    } else {
      return null;
    }
  }

  /**
   * Returns the amount of available space in the page (bytes).
   *
   * @return integer representing the number of bytes
   * @throws IOException I/O exception when reading free space
   */
  public int available_space() throws IOException {
    try {
      return this.hfPage.available_space();
    } catch (IOException e) {
      System.err.println("[THFPage] I/O exception in getting available space.");
      throw e;
    }
  }

  /**
   * Returns a boolean value indicating whether the page is empty.
   *
   * @return boolean indicating whether the page is empty
   * @throws IOException I/O exception when checking if there are any quadruples
   */
  public boolean empty() throws IOException {
    try {
      return this.hfPage.empty();
    } catch (IOException e) {
      System.err.println("[THFPage] I/O exception in empty().");
      throw e;
    }
  }
}
