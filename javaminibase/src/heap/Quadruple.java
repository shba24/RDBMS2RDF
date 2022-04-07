package heap;

import global.AttrType;
import global.Convert;
import global.EID;
import global.GlobalConst;
import global.IEID;
import global.IPID;
import global.PID;
import global.PageId;
import iterator.QuadrupleUtils;
import java.io.IOException;

/**
 * Quadruple
 * <p>
 * Similar to Tuple, but with fixed structure.
 * Quadruple can't have arbitary number of fields
 * or arbitary field types.
 */
public class Quadruple extends Tuple {
  /**
   * Constructor for the quadruple class with the size of Tuple.max_size
   *
   * @throws InvalidTupleSizeException
   * @throws IOException
   * @throws InvalidTypeException
   */
  public Quadruple() throws InvalidTupleSizeException, IOException, InvalidTypeException {
    setDefaultHeader();
  }

  /**
   * Public constructor
   * @param quad_size
   */
  public Quadruple(int quad_size) {
    super(quad_size);
  }

  /**
   * Public constructor
   * @param fromQuadruple
   */
  public Quadruple(Quadruple fromQuadruple) {
    super(fromQuadruple);
  }

  public Quadruple(Tuple fromTuple) {
    super(fromTuple);
  }

  /**
   * Constructor for the quadruple class from the byte array
   *
   * @param aquadruple a byte array of quadruples
   * @param offset     offset to add quadruples in the byte array
   * @param length     length of the byte array of the quadruple
   */
  public Quadruple(byte[] aquadruple, int offset, int length) throws Exception {
    super(aquadruple, offset, length);
  }

  /**
   * Returns the default attribute types in an array
   * @return
   */
  public static AttrType[] getDefaultAttrType() {
    AttrType[] attrType = new AttrType[4];
    attrType[0] = new AttrType(AttrType.attrBytes);
    attrType[1] = new AttrType(AttrType.attrBytes);
    attrType[2] = new AttrType(AttrType.attrBytes);
    attrType[3] = new AttrType(AttrType.attrReal);
    return attrType;
  }

  /**
   * Returns the default string and bytes attribute
   * size in an array.
   * @return
   */
  public static short[] getDefaultAttrSize() {
    short[] attrSize = new short[3];
    attrSize[0] = GlobalConst.MAX_EID_OBJ_SIZE;
    attrSize[1] = GlobalConst.MAX_PID_OBJ_SIZE;
    attrSize[2] = GlobalConst.MAX_EID_OBJ_SIZE;
    return attrSize;
  }

  /**
   * Sets the default header for the quadruple
   *
   * @throws InvalidTupleSizeException
   * @throws IOException
   * @throws InvalidTypeException
   */
  public void setDefaultHeader() throws InvalidTupleSizeException, IOException, InvalidTypeException {
    AttrType[] attrType = getDefaultAttrType();
    short[] attrSize = getDefaultAttrSize();

    try {
      this.setHdr((short) 4, attrType, attrSize);
    } catch (Exception e) {
      System.err.println("[Quadruple] Error in creating Quadruple object.");
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * Returns the subject ID
   *
   * @return Subject object
   * @throws FieldNumberOutOfBoundException
   * @throws IOException
   */
  public IEID getSubjectID() throws FieldNumberOutOfBoundException, IOException {
    IEID subject = new EID();
    byte[] buffer;
    try {
      buffer = this.getBytesFld(1);
      subject.setPageNo(new PageId(Convert.getIntValue(0, buffer)));
      subject.setSlotNo(Convert.getIntValue(4, buffer));
    } catch (Exception e) {
      System.err.println("[Quadruple] Error in getting subject id of the quadruple.");
      e.printStackTrace();
      throw e;
    }

    return subject;
  }

  /**
   * Get the subject label
   *
   * @return
   * @throws Exception
   */
  public String getSubjectLabel() throws Exception {
    return QuadrupleUtils.entityHeapFile.getLabel(getSubjectID().returnLID()).getLabel();
  }

  /**
   * Get Predicate label
   *
   * @return
   * @throws Exception
   */
  public String getPredicateLabel() throws Exception {
    return QuadrupleUtils.predicateHeapFile.getLabel(getPredicateID().returnLID()).getLabel();
  }

  /**
   * Get Object Label
   *
   * @return
   * @throws Exception
   */
  public String getObjectLabel() throws Exception {
    return QuadrupleUtils.entityHeapFile.getLabel(getObjectID().returnLID()).getLabel();
  }

  /**
   * Set the subject ID
   *
   * @param subjectId
   * @return Current Quadruple object
   * @throws IOException
   * @throws FieldNumberOutOfBoundException
   */
  public Quadruple setSubjectID(IEID subjectId) throws IOException, FieldNumberOutOfBoundException {
    byte[] buffer = new byte[GlobalConst.MAX_EID_OBJ_SIZE];
    try {
      Convert.setIntValue(subjectId.getPageNo().pid, 0, buffer);
      Convert.setIntValue(subjectId.getSlotNo(), 4, buffer);
      this.setBytesFld(1, buffer);
    } catch (Exception e) {
      System.err.println("[Quadruple] Error in setting subject id of the quadruple.");
      e.printStackTrace();
    }
    return this;
  }

  /**
   * Returns the predicate ID
   *
   * @return Predicate object
   * @throws FieldNumberOutOfBoundException
   * @throws IOException
   */
  public IPID getPredicateID() throws FieldNumberOutOfBoundException, IOException {
    IPID predicate = new PID();
    byte[] buffer;
    try {
      buffer = this.getBytesFld(2);
      predicate.setPageNo(new PageId(Convert.getIntValue(0, buffer)));
      predicate.setSlotNo(Convert.getIntValue(4, buffer));
    } catch (Exception e) {
      System.err.println("[Quadruple] Error in getting predicate id of the quadruple.");
      e.printStackTrace();
      throw e;
    }

    return predicate;
  }

  /**
   * Set the predicate ID
   *
   * @param predicateId
   * @return Current Quadruple Object
   * @throws FieldNumberOutOfBoundException
   * @throws IOException
   */
  public Quadruple setPredicateID(IPID predicateId) throws FieldNumberOutOfBoundException, IOException {
    byte[] buffer = new byte[GlobalConst.MAX_EID_OBJ_SIZE];
    try {
      Convert.setIntValue(predicateId.getPageNo().pid, 0, buffer);
      Convert.setIntValue(predicateId.getSlotNo(), 4, buffer);
      this.setBytesFld(2, buffer);
    } catch (Exception e) {
      System.err.println("[Quadruple] Error in setting predicate id of the quadruple.");
      e.printStackTrace();
      throw e;
    }
    return this;
  }

  /**
   * Returns the object ID
   *
   * @return Object
   * @throws FieldNumberOutOfBoundException
   * @throws IOException
   */
  public IEID getObjectID() throws FieldNumberOutOfBoundException, IOException {
    IEID object = new EID();
    byte[] buffer;
    try {
      buffer = this.getBytesFld(3);
      object.setPageNo(new PageId(Convert.getIntValue(0, buffer)));
      object.setSlotNo(Convert.getIntValue(4, buffer));
    } catch (Exception e) {
      System.err.println("[Quadruple] Error in getting object id of the quadruple.");
      e.printStackTrace();
      throw e;
    }

    return object;
  }

  /**
   * Set the object ID
   *
   * @param objectId
   * @return Current Quadruple Object
   * @throws FieldNumberOutOfBoundException
   * @throws IOException
   */
  public Quadruple setObjectID(IEID objectId) throws FieldNumberOutOfBoundException, IOException {
    byte[] buffer = new byte[GlobalConst.MAX_EID_OBJ_SIZE];
    try {
      Convert.setIntValue(objectId.getPageNo().pid, 0, buffer);
      Convert.setIntValue(objectId.getSlotNo(), 4, buffer);
      this.setBytesFld(3, buffer);
    } catch (Exception e) {
      System.err.println("[Quadruple] Error in setting object id of the quadruple.");
      e.printStackTrace();
      throw e;
    }
    return this;
  }

  /**
   * Returns the confidence
   *
   * @return Returns the confidence
   * @throws FieldNumberOutOfBoundException
   * @throws IOException
   */
  public Float getConfidence() throws FieldNumberOutOfBoundException, IOException {
    Float confidence;
    try {
      confidence = this.getFloFld(4);
    } catch (Exception e) {
      System.err.println("[Quadruple] Error in getting confidence of the quadruple.");
      e.printStackTrace();
      throw e;
    }

    return confidence;
  }

  /**
   * Gets the byte array of the quadruple without confidence
   *
   * @return
   * @throws FieldNumberOutOfBoundException
   */
  public byte[] getQuadWithoutConfidence() throws FieldNumberOutOfBoundException {
    byte[] buffer = new byte[3 * GlobalConst.MAX_EID_OBJ_SIZE];
    try {
      Convert.setBytesValue(this.getBytesFld(1), 0, buffer);
      Convert.setBytesValue(this.getBytesFld(2), GlobalConst.MAX_EID_OBJ_SIZE, buffer);
      Convert.setBytesValue(this.getBytesFld(3), 2 * GlobalConst.MAX_EID_OBJ_SIZE, buffer);
    } catch (FieldNumberOutOfBoundException e) {
      System.err.println("[Quadruple] Error in getting Quad byte array without confidence.");
      e.printStackTrace();
      throw e;
    }

    return buffer;
  }

  /**
   * Set the confidence
   *
   * @param confidence
   * @return Current Quadruple Object
   * @throws FieldNumberOutOfBoundException
   * @throws IOException
   */
  public Quadruple setConfidence(float confidence) throws FieldNumberOutOfBoundException, IOException {
    try {
      this.setFloFld(4, confidence);
    } catch (Exception e) {
      System.err.println("[Quadruple] Error in setting confidence of the quadruple.");
      e.printStackTrace();
      throw e;
    }
    return this;
  }

  /**
   * Print out the quadruple
   *
   * @throws FieldNumberOutOfBoundException
   * @throws IOException
   */
  public void print() throws Exception {
    String subject = getSubjectLabel();
    String predicate = getPredicateLabel();
    String object = getObjectLabel();
    float confidence = getConfidence();

    System.out.println("[");
    System.out.println("subject = " + subject);
    System.out.println("predicate = " + predicate);
    System.out.println("object = " + object);
    System.out.println("confidence = " + confidence);
    System.out.println("]");
  }

  /**
   * Copy the given quadruple
   *
   * @param fromQuadruple
   */
  public void quadrupleCopy(Quadruple fromQuadruple) {
    this.tupleCopy(fromQuadruple);
  }

}
