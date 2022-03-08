package heap;

import global.AttrType;
import global.GlobalConst;
import global.ILID;
import global.PageId;

import java.io.IOException;

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
     * Constructor for the Label class with the size of Tuple.max_size
     *
     * @throws InvalidTupleSizeException
     * @throws IOException
     * @throws InvalidTypeException
     */
    public Label() throws InvalidTupleSizeException, IOException, InvalidTypeException {
        AttrType[] attrType = new AttrType[4];
        attrType[0] = new AttrType(AttrType.attrString);
        attrType[1] = new AttrType(AttrType.attrString);
        attrType[2] = new AttrType(AttrType.attrString);
        attrType[3] = new AttrType(AttrType.attrReal);

        short[] attrSize = new short[3];
        attrSize[0] = GlobalConst.MAX_EID_OBJ_SIZE;
        attrSize[1] = GlobalConst.MAX_PID_OBJ_SIZE;
        attrSize[2] = GlobalConst.MAX_EID_OBJ_SIZE;

        tuple = new Tuple();
        try {
            tuple.setHdr((short) 4, attrType, attrSize);
        } catch (Exception e) {
            System.err.println("[Label] Error in creating label object.");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Constructor for the label  class from the byte array
     *
     * @param alabel  a byte array of label s
     * @param offset     offset to add label s in the byte array
     * @param length     length of the byte array of the label 
     */
    public Label(byte[] alabel , int offset, int length) throws Exception {
        if (alabel .length > Tuple.max_size) {
            throw new Exception("[Label] Error, alabel  byte " +
                    "array length exceeds max allowed size");
        }
        tuple = new Tuple(alabel , offset, length);
    }

    /**
     * Constructor for the label  class from another
     * label  class through copy
     *
     * @param fromLabel  a byte array which contains the label 
     */
    public Label(Label fromLabel ) {
        tuple = new Tuple(fromLabel .tuple);
    }

    /**
     * Class constructor
     * Creates a new label  with length = size,tuple offset = 0.
     *
     * @param size
     */
    public Label(int size) {
        tuple = new Tuple(size);
    }

    /**
     * Gets the length of the byte array of the label 
     *
     * @return get the length of a tuple
     */
    public int getLength() {
        return tuple.getLength();
    }

    /**
     * get the offset of the label 
     *
     * @return offset of the label  in byte array
     */
    public int getOffset() {
        return tuple.getOffset();
    }

    /**
     * Checks if the object is valid
     *
     * @return boolean representing the
     * validity of object
     */
    private boolean IsValid() {
        return tuple != null;
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
     * Parametric constructor for intoalizing Pageno and slot no
     *
     * @param pageNo pageno
     * @param slotNo slot no
     */
    public Label(PageId pageNo, int slotNo) {
        this.lid.setPageNo(pageNo);
        this.lid.setSlotNo(slotNo);
    }

    public int getIntFld(int l1_fld_no) throws FieldNumberOutOfBoundException, IOException {
        return tuple.getIntFld(l1_fld_no);
    }


    public float getFloFld(int l1_fld_no) throws FieldNumberOutOfBoundException, IOException {
        return tuple.getFloFld(l1_fld_no);
    }

    public String getStrFld(int l1_fld_no) throws FieldNumberOutOfBoundException, IOException {
        return tuple.getStrFld(l1_fld_no);
    }

    public void setIntFld(int fld_no, int intFld) throws FieldNumberOutOfBoundException, IOException {
        tuple.setIntFld(fld_no, intFld);
    }

    public void setFloFld(int fld_no, float floFld) throws FieldNumberOutOfBoundException, IOException {
        tuple.setFloFld(fld_no, floFld);
    }

    public void setStrFld(int fld_no, String strFld) throws FieldNumberOutOfBoundException, IOException {
        tuple.setStrFld(fld_no, strFld);
    }

    public void setHdr(short nOutFlds, AttrType[] res_attrs, short[] attrSize) throws InvalidTupleSizeException, IOException, InvalidTypeException {
        tuple.setHdr(nOutFlds, res_attrs, attrSize);
    }
}
