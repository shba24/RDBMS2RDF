package global;

/**
 * Interface for specifying the behaviour of label ID.
 */
public interface ILID {
    PageId getPageNo();

    void setPageNo(PageId pageNo);

    int getSlotNo();

    void setSlotNo(int slotNo);

    LabelType getLabelType();

    void setLabelType(LabelType labelType);

    EID getEntityID();

    void setEntityID(EID entityID);

    PID getPredicateID();

    void setPredicateID(PID predicateID);

    void copyLid(LID lid);

    boolean equals(LID lid);

    EID returnEID();

    PID returnPID();

    void writeToByteArray(byte[] array, int offset) throws java.io.IOException;
}