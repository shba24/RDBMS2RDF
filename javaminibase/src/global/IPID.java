package global;

public interface IPID {
  void copyPid(IPID pid);
  boolean equals(IPID pid);
  IPID returnPID();
  void writeToByteArray(byte[] array, int offset);
  PageId getPageNo();
  // Additional public attribute manipulation methods
  void setPageNo(PageId pageId);
  int getSlotNo();
  void setSlotNo(int slotNo);
}
