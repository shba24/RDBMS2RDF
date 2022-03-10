package heap;

import global.AttrType;
import global.Convert;

import java.io.IOException;

/**
 * Label
 * Tuple is modified into label which can be used by subject, predicate or object
 */
public class Label {

    private Tuple tuple;

    /**
     * label name
     */
    private String labelName;

    /**
     * Get the length of the quadruple
     *
     * @return size of the current tuple
     */
    public short size() {
        return this.tuple.size();
    }

    /**
     * Label constructor
     *
     * @param data   byte array
     * @param offset offset in the data array
     * @param len    length of data
     * @throws IOException
     */
    public Label(byte[] data, int offset, int len) throws IOException {
        labelName = Convert.getStrValue(offset, data, len);
    }

    /**
     * Constructor for the label class with the size of 1
     *
     * @throws InvalidTupleSizeException
     * @throws IOException
     * @throws InvalidTypeException
     */
    public Label(String label) throws InvalidTupleSizeException, IOException, InvalidTypeException, FieldNumberOutOfBoundException {
        AttrType[] attrType = new AttrType[1]; //ToDo: I am assuming label requires just one size tuple to store a string
        attrType[0] = new AttrType(AttrType.attrString);

        short[] attrSize = new short[1];
        attrSize[0] = (short) label.length();

        tuple = new Tuple();
        try {
            tuple.setHdr((short) 1, attrType, attrSize);
            tuple.setStrFld(0, label);
        } catch (Exception e) {
            System.err.println("[Quadruple] Error in creating Quadruple object.");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Gets a label
     *
     * @return
     */
    public String getLabel() {
        return labelName;
    }

    /**
     * sets the label
     *
     * @param name given label
     * @return
     */
    public Label setLabel(String name) {
        labelName = name;
        return this;
    }

    /**
     * print the label
     */
    public void print() throws FieldNumberOutOfBoundException, IOException {
        System.out.println("Label name: " + tuple.getStrFld(1));
    }

    /**
     * returns the length
     *
     * @return
     */
    public int getLength() {
        return labelName.length();
    }

    /**
     * return the tuple in byte array
     *
     * @return
     */
    public byte[] returnTupleByteArray() {
        return labelName.getBytes();
    }

    /**
     * returns offset
     *
     * @return
     */
    public int getOffset() {
        return 0;
    }

    public void labelCopy(Label newLabel) {
        labelName = newLabel.getLabel();
    }

    /**
     * Convert this field into integer
     *
     * @param fldNo the field number
     * @return the converted integer if success
     * @throws IOException                    I/O errors
     * @throws FieldNumberOutOfBoundException Tuple field number out of bound
     */
    public int getIntFld(int fldNo)
            throws IOException, FieldNumberOutOfBoundException {
        return tuple.getIntFld(fldNo);
    }

    /**
     * Convert this field in to float
     *
     * @param fldNo the field number
     * @return the converted float number  if success
     * @throws IOException                    I/O errors
     * @throws FieldNumberOutOfBoundException Tuple field number out of bound
     */

    public float getFloFld(int fldNo)
            throws IOException, FieldNumberOutOfBoundException {
        return tuple.getFloFld(fldNo);
    }

    /**
     * Convert this field into String
     *
     * @param fldNo the field number
     * @return the converted string if success
     * @throws IOException                    I/O errors
     * @throws FieldNumberOutOfBoundException Tuple field number out of bound
     */

    public String getStrFld(int fldNo)
            throws IOException, FieldNumberOutOfBoundException {
        return tuple.getStrFld(fldNo);
    }

    /**
     * Convert this field into a character
     *
     * @param fldNo the field number
     * @return the character if success
     * @throws IOException                    I/O errors
     * @throws FieldNumberOutOfBoundException Tuple field number out of bound
     */

    public char getCharFld(int fldNo)
            throws IOException, FieldNumberOutOfBoundException {
        return tuple.getCharFld(fldNo);
    }

    /**
     * Set this field to integer value
     *
     * @param fldNo the field number
     * @param val   the integer value
     * @throws IOException                    I/O errors
     * @throws FieldNumberOutOfBoundException Tuple field number out of bound
     */

    public void setIntFld(int fldNo, int val)
            throws IOException, FieldNumberOutOfBoundException {
        tuple.setIntFld(fldNo, val);
    }

    /**
     * Set this field to float value
     *
     * @param fldNo the field number
     * @param val   the float value
     * @throws IOException                    I/O errors
     * @throws FieldNumberOutOfBoundException Tuple field number out of bound
     */

    public void setFloFld(int fldNo, float val)
            throws IOException, FieldNumberOutOfBoundException {
        tuple.setFloFld(fldNo, val);
    }

    /**
     * Set this field to String value
     *
     * @param fldNo the field number
     * @param val   the string value
     * @throws IOException                    I/O errors
     * @throws FieldNumberOutOfBoundException Tuple field number out of bound
     */

    public void setStrFld(int fldNo, String val)
            throws IOException, FieldNumberOutOfBoundException {
        tuple.setStrFld(fldNo, val);
    }

    /**
     * setHdr will set the header of this tuple.
     *
     * @param numFlds  number of fields
     * @param types    contains the types that will be in this tuple
     * @param strSizes contains the sizes of the string
     * @throws IOException               I/O errors
     * @throws InvalidTypeException      Invalid tupe type
     * @throws InvalidTupleSizeException Tuple size too big
     */

    public void setHdr(short numFlds, AttrType types[], short strSizes[])
            throws IOException, InvalidTypeException, InvalidTupleSizeException {
        tuple.setHdr(numFlds, types, strSizes);
    }
}
