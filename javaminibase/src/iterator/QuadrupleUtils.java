package iterator;

import global.AttrType;
import heap.FieldNumberOutOfBoundException;
import heap.InvalidTupleSizeException;
import heap.InvalidTypeException;
import heap.Quadruple;

import heap.labelheap.LabelHeapFile;
import java.io.IOException;
import java.util.Arrays;

/**
 * some useful method when processing Quadruple
 */
public class QuadrupleUtils {
  public static LabelHeapFile entityHeapFile;
  public static LabelHeapFile predicateHeapFile;
  public static byte[] maxi = new byte[8];
  public static byte[] mini = new byte[8];

  public static void init(LabelHeapFile _entityHeapFile, LabelHeapFile _predicateHeapFile) {
    entityHeapFile = _entityHeapFile;
    predicateHeapFile = _predicateHeapFile;
    Arrays.fill(maxi, Byte.MAX_VALUE);
    Arrays.fill(mini, Byte.MIN_VALUE);
  }

  private static String getValid(Quadruple q, int fld_no) throws Exception {
    byte[] value = q.getBytesFld(fld_no);
    if (Arrays.equals(maxi, value)) {
      return new String(String.valueOf(Character.MAX_VALUE));
    } else if (Arrays.equals(mini, value)) {
      return new String(String.valueOf(Character.MIN_VALUE));
    }
    if (fld_no==1) return q.getSubjectLabel();
    else if (fld_no==2) return q.getPredicateLabel();
    return q.getObjectLabel();
  }

  private static int CompareQuadrupleWithQuadruple(
      Quadruple q1,
      Quadruple q2,
      int fld_no) throws TupleUtilsException {
    if (fld_no==1) {
      // Subject
      try {
        String subject1 = q1.getSubjectLabel();
        String subject2 = getValid(q2, fld_no);
        return subject1.compareTo(subject2);
      } catch (Exception e) {
        System.err.println("Error comparing subjects.");
        e.printStackTrace();
      }
    } else if (fld_no==2) {
      // Predicate
      try {
        String predicate1 = q1.getPredicateLabel();
        String predicate2 = getValid(q2, fld_no);
        return predicate1.compareTo(predicate2);
      } catch (Exception e) {
        System.err.println("Error comparing predicate.");
        e.printStackTrace();
      }
    } else if (fld_no==3) {
      // Object
      try {
        String object1 = q1.getObjectLabel();
        String object2 = getValid(q2, fld_no);
        return object1.compareTo(object2);
      } catch (Exception e) {
        System.err.println("Error comparing object.");
        e.printStackTrace();
      }
    } else if (fld_no==4){
      // Confidence
      try {
        Float confidence1 = q1.getConfidence();
        Float confidence2 = q2.getConfidence();
        int cmp = confidence1.compareTo(confidence2);
        if (cmp<0) return -1;
        else if (cmp==0) return 0;
        else return 1;
      } catch (Exception e) {
        System.err.println("Error comparing confidence.");
        e.printStackTrace();
      }
    }
    throw new TupleUtilsException("Wrong field number fld_no="+fld_no);
  }

  /**
   * This function compares a quadruple with another quadruple
   * in respective field by getting the string from label heap files.
   *
   * @param fldType   the type of the field being compared.
   * @param q1        one quadruple.
   * @param q2        another quadruple.
   * @param q1_fld_no the field numbers in the quadruples to be compared.
   * @param q2_fld_no the field numbers in the quadruples to be compared.
   * @return 0        if the two are equal,
   * 1        if the quadruple is greater,
   * -1        if the quadruple is smaller,
   * @throws UnknowAttrType      don't know the attribute type
   * @throws IOException         some I/O fault
   * @throws QuadrupleUtilsException exception from this class
   */
  public static int CompareQuadrupleWithQuadruple(
      AttrType fldType,
      Quadruple q1, int q1_fld_no,
      Quadruple q2, int q2_fld_no) throws TupleUtilsException {
    if (q1_fld_no!=q2_fld_no) {
      throw new TupleUtilsException(
          "Wrong field number q1_fld_no="+q1_fld_no+" q2_fld_no="+q2_fld_no);
    }
    return CompareQuadrupleWithQuadruple(q1, q2, q1_fld_no);
  }

  /**
   * This function  compares  quadruple1 with another quadruple2 whose
   * field number is same as the quadruple1
   *
   * @param fldType   the type of the field being compared.
   * @param q1        one quadruple
   * @param value     another quadruple.
   * @param q1_fld_no the field numbers in the quadruples to be compared.
   * @return 0        if the two are equal,
   * 1        if the quadruple is greater,
   * -1        if the quadruple is smaller,
   * @throws UnknowAttrType      don't know the attribute type
   * @throws IOException         some I/O fault
   * @throws QuadrupleUtilsException exception from this class
   */
  public static int CompareQuadrupleWithValue(
      AttrType fldType,
      Quadruple q1, int q1_fld_no,
      Quadruple value)
          throws IOException,
          UnknowAttrType, TupleUtilsException {
    return CompareQuadrupleWithQuadruple(fldType, q1, q1_fld_no, value, q1_fld_no);
  }

  /**
   * This function Compares two Quadruple inn all fields
   *
   * @param q1     the first quadruple
   * @param q2     the secocnd quadruple
   * @param types the field types
   * @param len    the field numbers
   * @return 0        if the two are not equal,
   * 1        if the two are equal,
   * @throws UnknowAttrType      don't know the attribute type
   * @throws IOException         some I/O fault
   * @throws QuadrupleUtilsException exception from this class
   */

  public static boolean Equal(Quadruple q1, Quadruple q2, AttrType types[], int len)
          throws IOException, UnknowAttrType, QuadrupleUtilsException, TupleUtilsException {
    int i;

    for (i = 1; i <= len; i++) {
      if (CompareQuadrupleWithQuadruple(types[i - 1], q1, i, q2, i) != 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * set up a quadruple in specified field from a quadruple
   *
   * @param value   the quadruple to be set
   * @param quadruple   the given quadruple
   * @param fld_no  the field number
   * @param fldType the quadruple attr type
   * @throws UnknowAttrType      don't know the attribute type
   * @throws IOException         some I/O fault
   * @throws QuadrupleUtilsException exception from this class
   */
  public static void SetValue(Quadruple value, Quadruple quadruple, int fld_no, AttrType fldType)
          throws IOException,
          UnknowAttrType,
          FieldNumberOutOfBoundException {

    switch (fldType.attrType) {
      case AttrType.attrInteger:
        value.setIntFld(fld_no, quadruple.getIntFld(fld_no));
        break;
      case AttrType.attrReal:
        value.setFloFld(fld_no, quadruple.getFloFld(fld_no));
        break;
      case AttrType.attrString:
        value.setStrFld(fld_no, quadruple.getStrFld(fld_no));
        break;
      case AttrType.attrBytes:
        value.setBytesFld(fld_no, quadruple.getBytesFld(fld_no));
        break;
      default:
        throw new UnknowAttrType(null, "Don't know how to handle attrSymbol, attrNull");
    }

    return;
  }

  /**
   * set up the Jquadruple's attrtype, string size,field number for using join
   *
   * @param Jquadruple       reference to an actual quadruple  - no memory has been malloced
   * @param res_attrs    attributes type of result quadruple
   * @param in1          array of the attributes of the quadruple (ok)
   * @param len_in1      num of attributes of in1
   * @param in2          array of the attributes of the quadruple (ok)
   * @param len_in2      num of attributes of in2
   * @param q1_str_sizes shows the length of the string fields in S
   * @param q2_str_sizes shows the length of the string fields in R
   * @param proj_list    shows what input fields go where in the output quadruple
   * @param nOutFlds     number of outer relation fileds
   * @throws IOException         some I/O fault
   * @throws QuadrupleUtilsException exception from this class
   */
  public static short[] setup_op_quadruple(
      Quadruple Jquadruple, AttrType[] res_attrs,
      AttrType in1[], int len_in1, AttrType in2[],
      int len_in2, short q1_str_sizes[],
      short q2_str_sizes[],
      FldSpec proj_list[], int nOutFlds)
      throws QuadrupleUtilsException {
    try {
      Jquadruple.setDefaultHeader();
    } catch (Exception e) {
      System.err.println("Error in setup_op_quadruple.");
      e.printStackTrace();
      throw new QuadrupleUtilsException("");
    }
    return Quadruple.getDefaultAttrSize();
  }

  /**
   * set up the Jquadruple's attrtype, string size,field number for using project
   *
   * @param Jquadruple       reference to an actual quadruple  - no memory has been malloced
   * @param res_attrs    attributes type of result quadruple
   * @param in1          array of the attributes of the quadruple (ok)
   * @param len_in1      num of attributes of in1
   * @param q1_str_sizes shows the length of the string fields in S
   * @param proj_list    shows what input fields go where in the output quadruple
   * @param nOutFlds     number of outer relation fileds
   * @throws IOException         some I/O fault
   * @throws QuadrupleUtilsException exception from this class
   * @throws InvalidRelation     invalid relation
   */

  public static short[] setup_op_quadruple(
      Quadruple Jquadruple, AttrType res_attrs[],
      AttrType in1[], int len_in1,
      short q1_str_sizes[],
      FldSpec proj_list[], int nOutFlds)
      throws QuadrupleUtilsException {
    try {
      Jquadruple.setDefaultHeader();
    } catch (Exception e) {
      System.err.println("Error in setup_op_quadruple.");
      e.printStackTrace();
      throw new QuadrupleUtilsException("");
    }
    return Quadruple.getDefaultAttrSize();
  }
}




