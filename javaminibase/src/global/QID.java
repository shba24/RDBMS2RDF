package global;

public class QID implements IQID {

    private PageId pageNo;
    private int slotNo;

    public QID() {
    }

    public QID(PageId pageNo, int slotNo) {
        this.pageNo = pageNo;
        this.slotNo = slotNo;
    }

    public QID(QID qid) {
        this.pageNo = qid.pageNo;
        this.slotNo = qid.slotNo;
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
    public void copyQid(QID qid) {
        this.pageNo = qid.pageNo;
        this.slotNo = qid.slotNo;
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

    @Override
    public boolean equals(QID qid) {
        return (this.pageNo.pid == qid.pageNo.pid) && (this.slotNo == qid.slotNo);
    }

    @Override
    public String toString() {
        return "QID{" +
                "pageNo=" + pageNo.pid +
                ", slotNo=" + slotNo +
                '}';
    }
}