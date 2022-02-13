package global;

public interface IEID {
  void copyPid(IEID pid);
  boolean equals(IEID pid);
  IEID returnEID();
  void writeToByteArray(byte[] array, int offset);
  PageId getPageNo();
  // Additional public attribute manipulation methods
  void setPageNo(PageId pageId);
  int getSlotNo();
  void setSlotNo(int slotNo);
}
