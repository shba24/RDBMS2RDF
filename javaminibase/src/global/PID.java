/**
 * Implementation of the class for Predicate ID.
 */

package global;

import static global.GlobalConst.INVALID_PAGE;

import java.io.*;

public class PID implements IPID {

  private int slotNo;
  private PageId pageNo = new PageId();
  private ILID lid;


  /**
   * Default constructor. Nothing is being done in this constructor.
   */
  public PID() {
    this.slotNo = -1;
    this.pageNo = new PageId(INVALID_PAGE);
    this.lid = new LID();
  }

  /**
   * Constructor for initializing the Predicate ID from the page ID and the slot number.
   *
   * @param pageNo
   * @param slotNo
   */
  public PID(PageId pageNo, int slotNo) {
    this.slotNo = slotNo;
    this.pageNo = pageNo;
  }


  /**
   * constructor to create a Predicate ID object from another label ID object.
   *
   * @param lid
   */
  public PID(LID lid) {
    this.slotNo = lid.getSlotNo();
    this.pageNo = lid.getPageNo();
    this.lid = lid;
  }


  /**
   * Copy constructor to create a Predicate ID object from another Predicate ID object.
   *
   * @param predicateID
   */
  public PID(PID predicateID) {
    this.slotNo = predicateID.getSlotNo();
    this.pageNo = predicateID.getPageNo();
    this.lid = predicateID.lid;
  }

  /**
   * Returns the page number associated with the Predicate ID.
   *
   * @return the PageID object
   */
  @Override
  public PageId getPageNo() {
    return this.pageNo;
  }

  /**
   * Set the page number associated with the Predicate ID.
   *
   * @param pageId object
   */
  @Override
  public void setPageNo(PageId pageId) {
    this.pageNo = pageId;
  }

  /**
   * Returns the slot number associated with the Predicate ID.
   *
   * @return the integer value of the slot number
   */
  @Override
  public int getSlotNo() {
    return this.slotNo;
  }

  /**
   * Set the slot number associated with the Predicate ID.
   *
   * @param slotNo
   */
  @Override
  public void setSlotNo(int slotNo) {
    this.slotNo = slotNo;
  }

  /**
   * Copy the state of the specified Predicate ID. The implementation of this function should be the
   * same as the copy constructor.
   *
   * @param pid
   */
  @Override
  public void copyPid(PID pid) {
    this.pageNo = new PageId(pid.getPageNo().pid);
    this.slotNo = pid.getSlotNo();
    this.lid = pid.lid;
  }

  /**
   * Check if the specified Predicate ID and this Predicate ID are equal.
   *
   * @param pid
   * @return boolean value indicating if they are equal
   */
  @Override
  public boolean equals(PID pid) {

    if ((this.pageNo.pid == pid.pageNo.pid)
        && (this.slotNo == pid.slotNo) && (this.lid == pid.lid)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Returns the label ID that this Predicate ID is associated with.
   *
   * @return the label ID (LID) object
   */
  @Override
  public ILID returnLID() {
    return this.lid;
  }

  /**
   * Write the Predicate ID into a byte array at the specified offset. The first 4 bytes after the
   * offset will store the slot number and the next 4 bytes will store the page ID.
   *
   * @param ary    the specified byte array
   * @param offset the offset of byte array to write
   * @throws java.io.IOException I/O errors
   */
  @Override
  public void writeToByteArray(byte[] ary, int offset) throws java.io.IOException {
    Convert.setIntValue(this.slotNo, offset, ary);
    Convert.setIntValue(this.pageNo.pid, offset + 4, ary);
    Convert.setStrValue(this.lid.toString(), offset + 8, ary);
  }

  @Override
  public String toString() {
    return "PID{" +
        "pageNo=" + this.pageNo +
        ", slotNo=" + this.slotNo +
        ", LID=" + this.lid +
        '}';
  }
}
