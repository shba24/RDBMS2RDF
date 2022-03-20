package global;

/**
 * Interface for specifying the behaviour of label ID.
 */
public interface ILID {

    /**
     * get page number
     * @return
     */
    PageId getPageNo();

    /**
     * set page number
     * @param pageNo
     */
    void setPageNo(PageId pageNo);

    /**
     * get slot number
     * @return
     */
    int getSlotNo();

    /**
     * set slot number
     * @param slotNo
     */
    void setSlotNo(int slotNo);

    /**
     * get label type
     * @return
     */
    LabelType getLabelType();

    /**
     * gets entity id
     * @return
     */
    EID getEntityID();

    /**
     * gets predicate id
     * @return
     */
    PID getPredicateID();

    /**
     * copy lid object
     * @param lid
     */
    void copyLid(LID lid);

    /**
     * compares lid
     * @param lid
     * @return
     */
    boolean equals(LID lid);

    /**
     * writes data to byte array
     * @param array - data
     * @param offset - offset
     * @throws java.io.IOException
     */
    void writeToByteArray(byte[] array, int offset) throws java.io.IOException;
}