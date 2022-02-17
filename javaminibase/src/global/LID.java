package global;

/**
 * Implementation of the class for label ID.
 */
public class LID implements ILID {

    private PageId pageNo;
    private int slotNo;
    private LabelType labelType;
    private EID entityID;
    private PID predicateID;

    /**
     * Default constructor. Nothing is being done in this constructor.
     */
    public LID() {
    }

    /**
     * Constructor for initializing the label ID from the page ID and the slot number.
     *
     * @param pageNo
     * @param slotNo
     */
    public LID(PageId pageNo, int slotNo) {
        this.pageNo = pageNo;
        this.slotNo = slotNo;
    }

    /**
     * Copy constructor to create a label ID object from another label ID object.
     *
     * @param lid
     */
    public LID(LID lid) {
        this.pageNo = new PageId(lid.getPageNo().pid);
        this.slotNo = lid.getSlotNo();

        // will get a copy of the reference pointing to the singleton
        this.labelType = lid.getLabelType();

        if(this.labelType == LabelType.SUBJECT || this.labelType == LabelType.OBJECT) {
            this.entityID = new EID(lid.getEntityID());
        } else if(this.labelType == LabelType.PREDICATE) {
            this.predicateID = new PID(lid.getPredicateID());
        }
    }

    /**
     * Returns the page number associated with the label ID.
     *
     * @return the PageID object
     */
    @Override
    public PageId getPageNo() {
        return pageNo;
    }

    /**
     * Set the page number associated with the label ID.
     *
     * @param pageNo
     */
    @Override
    public void setPageNo(PageId pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * Returns the slot number associated with the label ID.
     *
     * @return the integer value of the slot number
     */
    @Override
    public int getSlotNo() {
        return slotNo;
    }

    /**
     * Set the slot number associated with the label ID.
     *
     * @param slotNo
     */
    @Override
    public void setSlotNo(int slotNo) {
        this.slotNo = slotNo;
    }

    /**
     * Return the LabelType enum associated with the label ID.
     *
     * @return the LabelType enum
     */
    @Override
    public LabelType getLabelType() {
        return labelType;
    }

    /**
     * Set the LabelType enum for the label ID.
     *
     * @param labelType
     */
    @Override
    public void setLabelType(LabelType labelType) {
        this.labelType = labelType;
    }

    /**
     * Returns the entity ID that this label ID is associated with.
     *
     * @return the entity ID (EID) object
     */
    @Override
    public EID getEntityID() {
        return entityID;
    }

    /**
     * Set the entity ID based on the entity that this label ID represents.
     *
     * @param entityID
     */
    @Override
    public void setEntityID(EID entityID) {
        this.entityID = entityID;
    }

    /**
     * Returns the predicate ID that this label ID is associated with.
     *
     * @return the predicate ID (PID) object
     */
    @Override
    public PID getPredicateID() {
        return predicateID;
    }

    /**
     * Set the predicate ID based on the predicate that this label ID represents.
     *
     * @param predicateID
     */
    @Override
    public void setPredicateID(PID predicateID) {
        this.predicateID = predicateID;
    }

    /**
     * Copy the state of the specified label ID. The implementation
     * of this function should be the same as the copy constructor.
     *
     * @param lid
     */
    @Override
    public void copyLid(LID lid) {
        this.pageNo = new PageId(lid.getPageNo().pid);
        this.slotNo = lid.getSlotNo();

        // will get a copy of the reference pointing to the singleton
        this.labelType = lid.getLabelType();

        if(this.labelType == LabelType.SUBJECT || this.labelType == LabelType.OBJECT) {
            this.entityID = new EID(lid.getEntityID());
        } else if(this.labelType == LabelType.PREDICATE) {
            this.predicateID = new PID(lid.getPredicateID());
        }
    }

    /**
     * Check if the specified label ID and this label ID are equal.
     *
     * @param lid
     * @return boolean value indicating if they are equal
     */
    @Override
    public boolean equals(LID lid) {
        return (this.pageNo.pid == lid.getPageNo().pid) && (this.slotNo == lid.slotNo);
    }

    /**
     * Returns the entity ID that this label ID is associated with.
     *
     * @return the entity ID (EID) object
     */
    @Override
    public EID returnEID() {
        return this.entityID;
    }

    /**
     * Returns the predicate ID that this label ID is associated with.
     *
     * @return the predicate ID (PID) object
     */
    @Override
    public PID returnPID() {
        return this.predicateID;
    }

    /**
     * Write the label ID into a byte array at the specified offset.
     * The first 4 bytes after the offset will store the slot number and
     * the next 4 bytes will store the page ID.
     *
     * @param array  the specified byte array
     * @param offset the offset of byte array to write
     * @throws java.io.IOException I/O errors
     */
    @Override
    public void writeToByteArray(byte[] array, int offset) throws java.io.IOException {
        Convert.setIntValue(slotNo, offset, array);
        // 4 bytes ahead of slot no
        Convert.setIntValue(pageNo.pid, offset + 4, array);
        // 8 bytes ahead of slot number and page ID
        Convert.setCharValue(labelType.getAsChar(), offset + 4 + 4, array);

        /*
         TODO: Need to write the entity ID or the predicate ID to the byte array
         once the member is available.
         */

        if(labelType == LabelType.SUBJECT || labelType == LabelType.OBJECT) {
//            Convert.setIntValue(entityID.eid, offset + 4 + 4 + 1, array);
        } else if(labelType == LabelType.PREDICATE) {
//            Convert.setIntValue(predicateID.pid, offset + 4 + 4 + 1, array);
        }
    }

    /**
     * Returns a string representation of the label ID.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        return "LID{" +
                "pageNo=" + pageNo +
                ", slotNo=" + slotNo +
                ", labelType=" + labelType +
                ", entityID=" + entityID +
                ", predicateID=" + predicateID +
                '}';
    }
}