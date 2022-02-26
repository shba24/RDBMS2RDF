package global;

/**
 * Interface for specifying the behavior of the quadruple ID.
 */
public interface IQID {

  PageId getPageNo();

  void setPageNo(PageId pageNo);

  int getSlotNo();

  void setSlotNo(int slotNo);

  void copyQid(QID qid);

  void writeToByteArray(byte[] array, int offset) throws java.io.IOException;

  boolean equals(QID qid);
}
