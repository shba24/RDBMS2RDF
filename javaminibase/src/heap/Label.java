package heap;

import global.Convert;
import java.io.IOException;

/**
 * Label
 * Tuple is modified into label which can be used by subject, predicate or object
 */
public class Label {

  /**
   * label name
   */
  private String labelName;

  /**
   * Label constructor
   *
   * @param name
   */
  public Label(String name) {
    labelName = name;
  }

  /**
   * Label constructor
   *
   * @param data      byte array
   * @param offset    offset in the data array
   * @param len       length of data
   * @throws IOException
   */
  public Label(byte[] data, int offset, int len) throws IOException {
    labelName = Convert.getStrValue(offset, data, len);
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
  public void print() {
    System.out.println(this.getLabel());
  }

  public int getLength() {
    return labelName.length();
  }

  public byte[] returnTupleByteArray() {
    return labelName.getBytes();
  }

  public int getOffset() {
    return 0;
  }

  public void labelCopy(Label newLabel) {
     labelName = newLabel.getLabel();
  }
}
