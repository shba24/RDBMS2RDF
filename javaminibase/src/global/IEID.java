package global;

import java.io.*;

public interface IEID {

  PageId getPageNo();

  void setPageNo(PageId pageId);

  int getSlotNo();

  void setSlotNo(int slotNo);

  void copyEid(EID eid);

  boolean equals(EID eid);

  ILID returnLID();

  void writeToByteArray(byte[] array, int offset) throws IOException;

}
