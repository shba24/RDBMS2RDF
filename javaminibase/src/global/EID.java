/**
 * Implementation of the class for Predicate ID.
 */
package global;

import static global.GlobalConst.INVALID_PAGE;

import java.io.*;

public class EID implements IEID {

  private int slotNo;
  private PageId pageNo = new PageId();
  private ILID lid;


  /**
   * Default constructor.
   */
  public EID() {
    this.slotNo = -1;
    this.pageNo = new PageId(INVALID_PAGE);
    this.lid = new LID();
  }

  /**
   * constructor to create a Entity ID object from another label ID object.
   *
   * @param lid
   */
  public EID(LID lid) {
    this.slotNo = lid.getSlotNo();
    this.pageNo = lid.getPageNo();
    this.lid = lid;
  }

  /**
   * Constructor for initializing the Entity ID from the page ID and the slot number.
   *
   * @param pageNo
   * @param slotNo
   */
  public EID(PageId pageNo, int slotNo) {
    this.slotNo = slotNo;
    this.pageNo = pageNo;
  }

  /**
   * Copy constructor to create a Entity ID object from another Entity ID object.
   *
   * @param entityID
   */
  public EID(EID entityID) {
    this.slotNo = entityID.getSlotNo();
    this.pageNo = entityID.getPageNo();
    this.lid = entityID.lid;
  }


  /**
   * Returns the page number associated with the Entity ID.
   *
   * @return the PageID object
   */
  @Override
  public PageId getPageNo() {
    return this.pageNo;
  }

  /**
   * Set the page number associated with the Entity ID.
   *
   * @param pageId object
   */
  @Override
  public void setPageNo(PageId pageId) {
    this.pageNo = pageId;
  }

  /**
   * Returns the slot number associated with the Entity ID.
   *
   * @return the integer value of the slot number
   */
  @Override
  public int getSlotNo() {
    return this.slotNo;
  }

  /**
   * Set the slot number associated with the Entity ID.
   *
   * @param slotNo
   */
  @Override
  public void setSlotNo(int slotNo) {
    this.slotNo = slotNo;
  }

  /**
   * Copy the state of the specified Entity ID. The implementation of this function should be the
   * same as the copy constructor.
   *
   * @param eid
   */
  @Override
  public void copyEid(EID eid) {
    this.pageNo = new PageId(eid.getPageNo().pid);
    this.slotNo = eid.getSlotNo();
    this.lid = eid.lid;
  }

  /**
   * Check if the specified Entity ID and this Entity ID are equal.
   *
   * @param eid
   * @return boolean value indicating if they are equal
   */
  @Override
  public boolean equals(EID eid) {
    if ((this.pageNo.pid == eid.pageNo.pid)
        && (this.slotNo == eid.slotNo) && (this.lid == eid.lid)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Returns the label ID that this Entity ID is associated with.
   *
   * @return the label ID (LID) object
   */
  @Override
  public ILID returnLID() {
    return this.lid;
  }

  /**
   * Write the Entity ID into a byte array at the specified offset. The first 4 bytes after the
   * offset will store the slot number and the next 4 bytes will store the page ID.
   *
   * @param array  the specified byte array
   * @param offset the offset of byte array to write
   * @throws java.io.IOException I/O errors
   */
  @Override
  public void writeToByteArray(byte[] array, int offset) throws IOException {
    Convert.setIntValue(slotNo, offset, array);
    Convert.setIntValue(pageNo.pid, offset + 4, array);
    Convert.setStrValue(this.lid.toString(), offset + 8, array);
  }

  @Override
  public String toString() {
    return "EID{" +
        "pageNo=" + this.pageNo +
        ", slotNo=" + this.slotNo +
        ", LID=" + this.lid +
        '}';
  }
}
