package global;

import java.io.IOException;

public class EID implements IEID {

  private int slotNo;

  private PageId pageNo = new PageId();

  private LID lid;

  private LabelType labelType;

  public EID(){}

  public EID(LID lid)
  {
    this.slotNo=lid.getSlotNo();
    this.pageNo=lid.getPageNo();
    this.lid=lid;
  }

  public EID(PageId pageNo, int slotNo)
  {
    this.slotNo=slotNo;
    this.pageNo=pageNo;
  }

  @Override
  public void copyPid(EID eid) {
    pageNo=eid.getPageNo();
    slotNo=eid.getSlotNo();

  }

  @Override
  public boolean equals(EID eid) {
    if ((this.pageNo.pid==eid.pageNo.pid)
            &&(this.slotNo==eid.slotNo))
      return true;
    else
      return false;
  }

  @Override
  public LID returnLID() {

    return this.lid;
  }

  @Override
  public void writeToByteArray(byte[] array, int offset) throws IOException {

    Convert.setIntValue ( slotNo, offset, array);
    Convert.setIntValue ( pageNo.pid, offset+4, array);

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

    return this.slotNo;
  }

  @Override
  public void setSlotNo(int slotNo) {
    this.slotNo=slotNo;

  }
}
