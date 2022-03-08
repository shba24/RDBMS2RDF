package heap;

import global.AttrType;
import global.ILID;
import global.PageId;

public class Label {

    private ILID lid;

    private Tuple tuple;

    /**
     * Parameterized Constructor
     *
     * @param record
     * @param i
     * @param recLen
     */
    public Label(byte[] record, int i, short recLen) {
        //To be implemented
    }

    /**
     * Sets the tuple member
     *
     * @param tuple to be assigned
     */
    public void setTuple(Tuple tuple) {
        this.tuple = tuple;
    }

    /**
     * Sets the lid
     *
     * @param lid to be assigned
     */
    public void setLid(ILID lid) {
        this.lid.setPageNo(lid.getPageNo());
        this.lid.setSlotNo(lid.getSlotNo());
    }

    /**
     * Default constructor of class
     */
    public Label() {
    }

    /**
     * Parametric constructor for intoalizing Pageno and slot no
     *
     * @param pageNo pageno
     * @param slotNo slot no
     */
    public Label(PageId pageNo, int slotNo) {
        this.lid.setPageNo(pageNo);
        this.lid.setSlotNo(slotNo);
    }

    public int getIntFld(int l1_fld_no) {
        return 0;
    }

    public float getFloFld(int l1_fld_no) {
        return 0;
    }

    public String getStrFld(int l1_fld_no) {
        return null;
    }

    public void setIntFld(int fld_no, int intFld) {

    }

    public void setFloFld(int fld_no, float floFld) {

    }

    public void setStrFld(int fld_no, String strFld) {

    }

    public void setHdr(short nOutFlds, AttrType[] res_attrs, short[] res_str_sizes) {

    }
}
