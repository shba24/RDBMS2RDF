package global;

public class LID implements ILID {

    private PageId pageNo;
    private int slotNo;
    private LabelType labelType;
    private EID entityID;
    private PID predicateID;

    public LID() {
    }

    public LID(PageId pageNo, int slotNo) {
        this.pageNo = pageNo;
        this.slotNo = slotNo;
    }

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

    @Override
    public PageId getPageNo() {
        return pageNo;
    }

    @Override
    public void setPageNo(PageId pageNo) {
        this.pageNo = pageNo;
    }

    @Override
    public int getSlotNo() {
        return slotNo;
    }

    @Override
    public void setSlotNo(int slotNo) {
        this.slotNo = slotNo;
    }

    @Override
    public LabelType getLabelType() {
        return labelType;
    }

    @Override
    public void setLabelType(LabelType labelType) {
        this.labelType = labelType;
    }

    @Override
    public EID getEntityID() {
        return entityID;
    }

    @Override
    public void setEntityID(EID entityID) {
        this.entityID = entityID;
    }

    @Override
    public PID getPredicateID() {
        return predicateID;
    }

    @Override
    public void setPredicateID(PID predicateID) {
        this.predicateID = predicateID;
    }

    @Override
    public void copyLid(LID lid) {
        this.pageNo = lid.pageNo;
        this.slotNo = lid.slotNo;
    }

    @Override
    public boolean equals(LID lid) {
        return (this.pageNo.pid == lid.pageNo.pid) && (this.slotNo == lid.slotNo);
    }

    @Override
    public EID returnEID() {
        return this.entityID;
    }

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