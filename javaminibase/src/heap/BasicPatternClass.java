package heap;

import global.AttrType;
import global.Convert;
import global.EID;
import global.GlobalConst;
import global.PageId;
import iterator.BPUtils;
import java.io.IOException;
import java.util.Arrays;

/**
 * Basic Pattern Class
 * <p>
 *   Similar to BasicPatternClass, but with fixed structure
 *   BasicPatternClass can have arbitrary number of fields.
 * </p>
 */
public class BasicPatternClass extends Tuple {

  /**
   * Public constructor
   */
  public BasicPatternClass() {
    super();
  }

  /**
   * Public constructor
   *
   * @param tuple_size
   */
  public BasicPatternClass(int tuple_size){
    super(tuple_size);
  }

  /**
   * Public constructor
   *
   * @param fromTuple
   */
  public BasicPatternClass(Tuple fromTuple) {
    super(fromTuple);
  }

  /**
   * Public constructor
   *
   * @param nodeIds
   * @param confidence
   * @throws InvalidTupleSizeException
   * @throws IOException
   * @throws InvalidTypeException
   * @throws FieldNumberOutOfBoundException
   */
  public BasicPatternClass(EID[] nodeIds, Float confidence)
      throws InvalidTupleSizeException, IOException, InvalidTypeException, FieldNumberOutOfBoundException {
    /**
     * Number of tuple members = nodeIds.Length + 1
     * Extra 1 is for confidence which will be stored
     * alongside nodeIds.
     */
    setDefaultHeader(nodeIds.length + 1);
    setConfidence(confidence);
    for (int i=0;i<nodeIds.length;i++) {
      setNodeId(nodeIds[i], i + 2);
    }
  }

  /**
   * Returns the label of the node from the heap file
   *
   * @param idx           index of the node
   * @return
   * @throws Exception
   */
  public String getNodeLabel(int idx) throws Exception {
    try {
      EID eid = getNodeId(idx);
      Label label = BPUtils.entityHeapFile.getLabel(eid.returnLID());
      return label.getLabel();
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * Sets the node id into the tuple
   *
   * @param eid
   * @param idx          Index starting from 2
   * @throws FieldNumberOutOfBoundException
   * @throws IOException
   */
  public void setNodeId(EID eid, int idx) throws FieldNumberOutOfBoundException, IOException {
    byte[] buffer = new byte[GlobalConst.MAX_EID_OBJ_SIZE];
    try {
      Convert.setIntValue(eid.getPageNo().pid, 0, buffer);
      Convert.setIntValue(eid.getSlotNo(), 4, buffer);
      this.setBytesFld(idx, buffer);
    } catch (Exception e) {
      System.err.println("[BasicPatternClass] Error in setting node id of the BasicPatternClass.");
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * Gets the node id using the index provided from the tuple
   *
   * @param idx         Index starting from 2
   * @return
   * @throws FieldNumberOutOfBoundException
   * @throws IOException
   */
  public EID getNodeId(int idx) throws FieldNumberOutOfBoundException, IOException {
    EID eid = new EID();
    byte[] buffer;
    try {
      buffer = this.getBytesFld(idx);
      eid.setPageNo(new PageId(Convert.getIntValue(0, buffer)));
      eid.setSlotNo(Convert.getIntValue(4, buffer));
    } catch (Exception e) {
      System.err.println("[BasicPatternClass] Error in getting node id of the BasicPatternClass.");
      e.printStackTrace();
      throw e;
    }
    return eid;
  }

  /**
   * Sets the confidence in the tuple
   *
   * @param confidence
   * @throws FieldNumberOutOfBoundException
   * @throws IOException
   */
  public void setConfidence(Float confidence) throws FieldNumberOutOfBoundException, IOException {
    try {
      this.setFloFld(1, confidence);
    } catch (Exception e) {
      System.err.println("[BasicPatternClass] Error in setting confidence of the BasicPatternClass.");
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * Gets the confidence which is stored at the index 1 of the tuple
   *
   * @return
   * @throws FieldNumberOutOfBoundException
   * @throws IOException
   */
  public Float getConfidence() throws FieldNumberOutOfBoundException, IOException {
    Float confidence;
    try {
      confidence = this.getFloFld(1);
    } catch (Exception e) {
      System.err.println("[BasicPatternClass] Error in getting confidence of the BasicPatternClass.");
      e.printStackTrace();
      throw e;
    }
    return confidence;
  }

  /**
   * Returns the default attribute types
   *
   * @param n
   * @return
   */
  public static AttrType[] getDefaultAttrTypes(int n) {
    AttrType[] attrTypes = new AttrType[n];
    Arrays.fill(attrTypes, new AttrType(AttrType.attrBytes));
    attrTypes[0] = new AttrType(AttrType.attrReal);
    return attrTypes;
  }

  /**
   * Returns the default attribute sizes
   *
   * @param n
   * @return
   */
  public static short[] getDefaultAttrSizes(int n) {
    short[] attrSizes = new short[n-1];
    Arrays.fill(attrSizes, GlobalConst.MAX_EID_OBJ_SIZE);
    return attrSizes;
  }

  /**
   * Sets the default header for the tuple
   *
   * @param n
   * @throws InvalidTupleSizeException
   * @throws IOException
   * @throws InvalidTypeException
   */
  public void setDefaultHeader(int n) throws InvalidTupleSizeException, IOException, InvalidTypeException {
    AttrType[] attrTypes = getDefaultAttrTypes(n);
    short[] attrSizes = getDefaultAttrSizes(n);

    try {
      this.setHdr((short) n, attrTypes, attrSizes);
    } catch (Exception e) {
      System.err.println("[BasicPatternClass] Error in setting default BasicPatternClass header.");
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * Print out the quadruple
   *
   * @throws FieldNumberOutOfBoundException
   * @throws IOException
   */
  public void print() throws Exception {
    Float confidence = getConfidence();

    System.out.println("[");
    System.out.println("confidence = " + confidence);
    for (int i=1;i<fldCnt;i++) {
      System.out.println("node"+i+" = "+getNodeLabel(i+1));
    }
    System.out.println("]");
  }

}
