package global;

/**
 * Implementation of the class for quadruple ID.
 */
public class QID implements IQID {

    private PageId pageNo;
    private int slotNo;

    /**
     * Default constructor. Nothing is being done in this constructor.
     */
    public QID() {
    }

    /**
     * Constructor for initializing the label ID from the page ID and the slot number.
     *
     * @param pageNo
     * @param slotNo
     */
    public QID(PageId pageNo, int slotNo) {
        this.pageNo = new PageId();
        this.pageNo.copyPageId(pageNo);
        this.slotNo = slotNo;
    }

    /**
     * Copy constructor to create a quadruple ID object from another quadruple ID object.
     *
     * @param qid
     */
    public QID(QID qid) {
        this.pageNo = new PageId();
        this.pageNo.copyPageId(qid.pageNo);
        this.slotNo = qid.getSlotNo();
    }

    /**
     * Returns the page number associated with the quadruple ID.
     *
     * @return the PageId object
     */
    @Override
    public PageId getPageNo() {
        return pageNo;
    }

    /**
     * Set the page number associated with the quadruple ID.
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
     * Copy the state of the specified quadruple ID. The implementation
     * of this function should be the same as the copy constructor.
     *
     * @param qid
     */
    @Override
    public void copyQid(QID qid) {
        this.pageNo = qid.getPageNo();
        this.slotNo = qid.getSlotNo();
    }

    /**
     * Write the quadruple ID into a byte array at the specified offset.
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

    /**
     * Check if the specified quadruple ID and this quadruple ID are equal.
     *
     * @param qid
     * @return a boolean value indicating whether they are equal
     */
    @Override
    public boolean equals(QID qid) {
        return (this.pageNo.pid == qid.pageNo.pid) && (this.slotNo == qid.slotNo);
    }

    /**
     * Returns a string representation of the quadruple ID.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        return "QID{" +
                "pageNo=" + pageNo.pid +
                ", slotNo=" + slotNo +
                '}';
    }
}