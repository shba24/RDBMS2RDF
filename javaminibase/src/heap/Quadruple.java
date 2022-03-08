package heap;

import global.AttrType;
import global.Convert;
import global.EID;
import global.GlobalConst;
import global.IEID;
import global.IPID;
import global.PID;
import global.PageId;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Quadruple
 * <p>
 * Similar to Tuple, but with fixed structure.
 * Quadruple can't have arbitary number of fields
 * or arbitary field types.
 */
public class Quadruple {
  private Tuple tuple;

  /**
   * Constructor for the quadruple class with the size of Tuple.max_size
   *
   * @throws InvalidTupleSizeException
   * @throws IOException
   * @throws InvalidTypeException
   */
  public Quadruple() throws InvalidTupleSizeException, IOException, InvalidTypeException {
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
      System.err.println("[Quadruple] Error in creating Quadruple object.");
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * Constructor for the quadruple class from the byte array
   *
   * @param aquadruple a byte array of quadruples
   * @param offset     offset to add quadruples in the byte array
   * @param length     length of the byte array of the quadruple
   */
  public Quadruple(byte[] aquadruple, int offset, int length) throws Exception {
    if (aquadruple.length > Tuple.max_size) {
      throw new Exception("[Quadruple] Error, aquadruple byte " +
          "array length exceeds max allowed size");
    }
    tuple = new Tuple(aquadruple, offset, length);
  }

  /**
   * Constructor for the quadruple class from another
   * quadruple class through copy
   *
   * @param fromQuadruple a byte array which contains the quadruple
   */
  public Quadruple(Quadruple fromQuadruple) {
    tuple = new Tuple(fromQuadruple.tuple);
  }

  /**
   * Class constructor
   * Creates a new quadruple with length = size,tuple offset = 0.
   *
   * @param size
   */
  public Quadruple(int size) {
    tuple = new Tuple(size);
  }

  /**
   * Gets the length of the byte array of the quadruple
   *
   * @return get the length of a tuple
   */
  public int getLength() {
    return tuple.getLength();
  }

  /**
   * get the offset of the quadruple
   *
   * @return offset of the quadruple in byte array
   */
  public int getOffset() { return tuple.getOffset(); }

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
   * Returns the subject ID
   *
   * @return Subject object
   * @throws FieldNumberOutOfBoundException
   * @throws IOException
   */
  public IEID getSubjectID() throws FieldNumberOutOfBoundException, IOException {
    IEID subject = new EID();
    String data;
    try {
      data = tuple.getStrFld(1);
    } catch (Exception e) {
      System.err.println("[Quadruple] Error in getting subject id of the quadruple.");
      e.printStackTrace();
      throw e;
    }
    subject.setPageNo(new PageId(Convert.getIntValue(0, data.getBytes())));
    subject.setSlotNo(Convert.getIntValue(4, data.getBytes()));

    return subject;
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
    byte[] data = new byte[GlobalConst.MAX_EID_OBJ_SIZE];
    try {
      Convert.setIntValue(subjectId.getPageNo().pid, 0, data);
      Convert.setIntValue(subjectId.getSlotNo(), 4, data);
      tuple.setStrFld(1, new String(data, StandardCharsets.UTF_8));
    } catch (Exception e) {
      System.err.println("[Quadruple] Error in setting subject id of the quadruple.");
      e.printStackTrace();
      throw e;
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
    String data;
    try {
      data = tuple.getStrFld(2);
    } catch (Exception e) {
      System.err.println("[Quadruple] Error in getting predicate id of the quadruple.");
      e.printStackTrace();
      throw e;
    }
    predicate.setPageNo(new PageId(Convert.getIntValue(0, data.getBytes())));
    predicate.setSlotNo(Convert.getIntValue(4, data.getBytes()));

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
    byte[] data = new byte[GlobalConst.MAX_PID_OBJ_SIZE];
    try {
      Convert.setIntValue(predicateId.getPageNo().pid, 0, data);
      Convert.setIntValue(predicateId.getSlotNo(), 4, data);
      tuple.setStrFld(2, new String(data, StandardCharsets.UTF_8));
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
    String data;
    try {
      data = tuple.getStrFld(3);
    } catch (Exception e) {
      System.err.println("[Quadruple] Error in getting object id of the quadruple.");
      e.printStackTrace();
      throw e;
    }
    object.setPageNo(new PageId(Convert.getIntValue(0, data.getBytes())));
    object.setSlotNo(Convert.getIntValue(4, data.getBytes()));

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
    byte[] data = new byte[GlobalConst.MAX_EID_OBJ_SIZE];
    try {
      Convert.setIntValue(objectId.getPageNo().pid, 0, data);
      Convert.setIntValue(objectId.getSlotNo(), 4, data);
      tuple.setStrFld(3, new String(data, StandardCharsets.UTF_8));
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
  public double getConfidence() throws FieldNumberOutOfBoundException, IOException {
    double confidence;
    try {
      confidence = tuple.getFloFld(4);
    } catch (Exception e) {
      System.err.println("[Quadruple] Error in getting confidence of the quadruple.");
      e.printStackTrace();
      throw e;
    }

    return confidence;
  }

  /**
   * Set the confidence
   *
   * @param confidence
   * @return Current Quadruple Object
   * @throws FieldNumberOutOfBoundException
   * @throws IOException
   */
  public Quadruple setConfidence(double confidence) throws FieldNumberOutOfBoundException, IOException {
    byte[] data = new byte[GlobalConst.MAX_FLOAT_SIZE];
    try {
      Convert.setFloValue((float) confidence, 0, data);
      tuple.setStrFld(4, new String(data, StandardCharsets.UTF_8));
    } catch (Exception e) {
      System.err.println("[Quadruple] Error in setting confidence of the quadruple.");
      e.printStackTrace();
      throw e;
    }
    return this;
  }

  /**
   * Copy the quadruple to byte array out
   *
   * @return byte array of this quadruple object
   */
  public byte[] getQuadrupleByteArray() {
    return tuple.getTupleByteArray();
  }

  /**
   * return the data byte array
   *
   * @return data byte array
   */
  public byte[] returnQuadrupleByteArray() { return tuple.returnTupleByteArray(); }

  /**
   * Print out the quadruple
   *
   * @throws FieldNumberOutOfBoundException
   * @throws IOException
   */
  public void print() throws FieldNumberOutOfBoundException, IOException {
    IEID subject = getSubjectID();
    IPID predicate = getPredicateID();
    IEID object = getObjectID();
    double confidence = getConfidence();

    System.out.println("[");
    System.out.println("subject.pageNo.pid = " + subject.getPageNo().pid);
    System.out.println("subject.slotNo = " + subject.getSlotNo());
    System.out.println("predicate.pageNo.pid = " + predicate.getPageNo().pid);
    System.out.println("predicate.slotNo = " + predicate.getSlotNo());
    System.out.println("object.pageNo.pid = " + object.getPageNo().pid);
    System.out.println("object.slotNo = " + object.getSlotNo());
    System.out.println("confidence = " + confidence);
    System.out.println("]");
  }

  /**
   * Returns number of fields in this quadruple
   *
   * @return the number of fields in this quadruple
   */
  public short noOfFlds() { return tuple.noOfFlds(); }

  /**
   * Makes a copy of the fldOffset array
   *
   * @return a copuy of the fldOffset array
   */
  public short[] copyFldOffset() { return tuple.copyFldOffset(); }

  /**
   * Get the length of the quadruple
   *
   * @return size of the current tuple
   */
  public short size() {
    return this.tuple.size();
  }

  /**
   * Copy the given quadruple
   *
   * @param fromQuadruple
   */
  public void quadrupleCopy(Quadruple fromQuadruple) {
    tuple.tupleCopy(fromQuadruple.tuple);
  }

  /**
   * This is used when you do not want
   * to use the constructor
   *
   * @param aquadruple
   * @param offset
   */
  public void quadrupleInit(byte[] aquadruple, int offset) {
    tuple.tupleInit(aquadruple, offset, 4);
  }

  /**
   * Set a quadruple with the given
   * byte array and offset
   *
   * @param fromquadruple
   * @param offset
   */
  public void quadrupleSet(byte[] fromquadruple, int offset) {
    tuple.tupleSet(fromquadruple, offset, 4);
  }

  public int getIntFld(int q1_fld_no) {
    return 0;
  }


  public float getFloFld(int q1_fld_no) {
    return 0;
  }

  public String getStrFld(int q1_fld_no) {
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
