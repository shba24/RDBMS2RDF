package global;

import java.io.IOException;

public interface IPID {

  PageId getPageNo();

  void setPageNo(PageId pageId);

  int getSlotNo();

  void setSlotNo(int slotNo);


  void copyPid(PID pid);

  boolean equals(PID pid);

  ILID returnLID();

  void writeToByteArray(byte[] array, int offset) throws IOException;

  // Additional public attribute manipulation methods


}
