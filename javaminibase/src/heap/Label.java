package heap;

import java.io.IOException;

/**
 * Label
 * Currently its just using the data array of the
 * Tuple as there are cases in DataPageInfo which
 * tries to parse it as Label even though those
 * are just DataPageInfo.
 * Tuple is modified into label which can be used
 * by subject, predicate or object
 */
public class Label extends Tuple {

    public Label() {
        super();
    }

    /**
     * Label constructor
     *
     * @param data   byte array
     * @param offset offset in the data array
     * @param len    length of data
     * @throws IOException
     */
    public Label(byte[] data, int offset, int len) {
        super(data, offset, len);
    }

    /**
     * Constructor for the label class with the size of 1
     *
     * @throws InvalidTupleSizeException
     * @throws IOException
     * @throws InvalidTypeException
     */
    public Label(String label) {
        super(label.getBytes(), 0, label.getBytes().length);
    }

    /**
     * Gets a label
     *
     * @return
     */
    public String getLabel() throws FieldNumberOutOfBoundException, IOException {
        return new String(getTupleByteArray());
    }

    /**
     * sets the label
     *
     * @param name given label
     * @return
     */
    public void setLabel(String name) throws FieldNumberOutOfBoundException, IOException {
        this.tupleSet(name.getBytes(), 0, name.getBytes().length);
    }

    /**
     * print the label
     */
    public void print() throws FieldNumberOutOfBoundException, IOException {
        System.out.println("Label name: " + this.getLabel());
    }

    /**
     * Copies the label to this class.
     *
     * @param newLabel
     * @throws FieldNumberOutOfBoundException
     * @throws IOException
     */
    public void labelCopy(Label newLabel) throws FieldNumberOutOfBoundException, IOException {
        this.setLabel(newLabel.getLabel());
    }
}
