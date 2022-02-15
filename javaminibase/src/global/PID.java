package global;

import java.io.IOException;

public class PID implements IPID {

  private int slotNo;

  private PageId pageNo = new PageId();

  private LID lid;

  private LabelType labelType;

  /*default constructor of class
   */
  public PID(){}

  public PID(PageId pageNo, int slotNo)
  {
    this.slotNo=slotNo;
    this.pageNo=pageNo;
  }

  public PID(LID lid)
  {
    this.slotNo=lid.getSlotNo();
    this.pageNo=lid.getPageNo();
    this.lid=lid;
  }


  @Override
  public void copyPid(PID pid) {
    pageNo=pid.getPageNo();
    slotNo=pid.getSlotNo();

  }

  @Override
  public boolean equals(PID pid) {

    if ((this.pageNo.pid==pid.pageNo.pid)
            &&(this.slotNo==pid.slotNo))
      return true;
    else
      return false;
  }

  @Override
  public LID returnLID() {
    return null;
  }

  @Override
  public void writeToByteArray(byte[] ary, int offset) throws java.io.IOException {
    Convert.setIntValue ( slotNo, offset, ary);
    Convert.setIntValue ( pageNo.pid, offset+4, ary);

  }

  @Override
  public PageId getPageNo() {

    return this.pageNo;
  }

  @Override
  public void setPageNo(PageId pageId) {
    this.pageNo=pageId;

  }

  @Override
  public int getSlotNo() {

    return slotNo;
  }

  @Override
  public void setSlotNo(int slotNo) {
    this.slotNo=slotNo;

  }

  @Override
  public String toString() {
    return "PID{" +
            "pageNo=" + pageNo +
            ", slotNo=" + slotNo +
            ", LID=" + lid +
            '}';
  }

}
