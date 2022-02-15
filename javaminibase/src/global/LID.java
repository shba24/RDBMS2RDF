package global;

public class LID implements ILID {

    /**
     * page id
     */
    private PageId pageNo;

    /**
     * slot number
     */
    private int slotNo;

    /**
     * label type
     */
    private LabelType labelType;

    /**
     * entity id
     */
    private EID entityID;

    /**
     * predicate id
     */
    private PID predicateID;

    /**
     * constructor to lid
     */
    public LID() {
    }

    /**
     * constructor with two fields
     * @param pageNo - page number
     * @param slotNo - slot number
     */
    public LID(PageId pageNo, int slotNo) {
        this.pageNo = pageNo;
        this.slotNo = slotNo;
    }

    /**
     * create lid object
     * @param lid
     */
    public LID(LID lid) {
        this.pageNo = lid.pageNo;
        this.slotNo = lid.slotNo;
        this.labelType = lid.labelType;

        if(this.labelType == LabelType.SUBJECT || this.labelType == LabelType.OBJECT) {
            this.entityID = lid.entityID;
        } else if(this.labelType == LabelType.PREDICATE) {
            this.predicateID = lid.predicateID;
        }

    }

    /**
     * gets page number
     * @return
     */
    @Override
    public PageId getPageNo() {
        return pageNo;
    }

    /**
     * sets the page number
     * @param pageNo
     */
    @Override
    public void setPageNo(PageId pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * gets the slot number
     * @return
     */
    @Override
    public int getSlotNo() {
        return slotNo;
    }

    /**
     * sets the slot number
     * @param slotNo
     */
    @Override
    public void setSlotNo(int slotNo) {
        this.slotNo = slotNo;
    }

    /**
     * gets the label type
     * @return
     */
    @Override
    public LabelType getLabelType() {
        return labelType;
    }

    /**
     * sets label type
     * @param labelType
     */
    @Override
    public void setLabelType(LabelType labelType) {
        this.labelType = labelType;
    }

    /**
     * gets entity id
     * @return
     */
    @Override
    public EID getEntityID() {
        return entityID;
    }

    /**
     * sets entity id
     * @param entityID
     */
    @Override
    public void setEntityID(EID entityID) {
        this.entityID = entityID;
    }

    /**
     * gets predicate id
     * @return
     */
    @Override
    public PID getPredicateID() {
        return predicateID;
    }

    /**
     * sets predicate id
     * @param predicateID
     */
    @Override
    public void setPredicateID(PID predicateID) {
        this.predicateID = predicateID;
    }

    /**
     * Copy of lid
     * @param lid
     */
    @Override
    public void copyLid(LID lid) {
        this.pageNo = lid.pageNo;
        this.slotNo = lid.slotNo;
    }

    /**
     * compares lid
     * @param lid
     * @return
     */
    @Override
    public boolean equals(LID lid) {
        return (this.pageNo.pid == lid.pageNo.pid) && (this.slotNo == lid.slotNo);
    }

    /**
     * returns entity id
     * @return
     */
    @Override
    public EID returnEID() {
        return this.entityID;
    }

    /**
     * return predicate id
     * @return
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
        Convert.setIntValue(pageNo.pid, offset + 4, array);
    }

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