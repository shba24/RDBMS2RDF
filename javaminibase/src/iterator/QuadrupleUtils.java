package iterator;

import global.AttrType;
import heap.Quadruple;

import java.io.IOException;

/**
 * some useful method when processing Quadruple
 */
public class QuadrupleUtils {

  /**
   * This function compares a quadruple with another quadruple in respective field, and
   * returns:
   * <p>
   * 0        if the two are equal,
   * 1        if the quadruple is greater,
   * -1        if the quadruple is smaller,
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
      Quadruple q2, int q2_fld_no)
      throws IOException,
      UnknowAttrType,
      QuadrupleUtilsException {
    int q1_i, q2_i;
    float q1_r, q2_r;
    String q1_s, q2_s;

    switch (fldType.attrType) {
      case AttrType.attrInteger:                // Compare two integers.
        q1_i = q1.getIntFld(q1_fld_no);
        q2_i = q2.getIntFld(q2_fld_no);
        if (q1_i == q2_i) {
          return 0;
        }
        if (q1_i < q2_i) {
          return -1;
        }
        if (q1_i > q2_i) {
          return 1;
        }

      case AttrType.attrReal:                // Compare two floats
        q1_r = q1.getFloFld(q1_fld_no);
        q2_r = q2.getFloFld(q2_fld_no);
        if (q1_r == q2_r) {
          return 0;
        }
        if (q1_r < q2_r) {
          return -1;
        }
        if (q1_r > q2_r) {
          return 1;
        }

      case AttrType.attrString:                // Compare two strings
        q1_s = q1.getStrFld(q1_fld_no);
        q2_s = q2.getStrFld(q2_fld_no);

        // Now handle the special case that is posed by the max_values for strings...
        if (q1_s.compareTo(q2_s) > 0) {
          return 1;
        }
        if (q1_s.compareTo(q2_s) < 0) {
          return -1;
        }
        return 0;
      default:

        throw new UnknowAttrType(null, "Don't know how to handle attrSymbol, attrNull");
    }
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
      UnknowAttrType,
      QuadrupleUtilsException {
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
      throws IOException, UnknowAttrType, QuadrupleUtilsException {
    int i;

    for (i = 1; i <= len; i++) {
      if (CompareQuadrupleWithQuadruple(types[i - 1], q1, i, q2, i) != 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * get the string specified by the field number
   *
   * @param quadruple the quadruple
   * @param fldno the field number
   * @return the content of the field number
   * @throws QuadrupleUtilsException exception from this class
   */
  public static String Value(Quadruple quadruple, int fldno)
      throws
          QuadrupleUtilsException {
    String temp;
    temp = quadruple.getStrFld(fldno);
    return temp;
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
      QuadrupleUtilsException {

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
      throws IOException,
      QuadrupleUtilsException {
    short[] sizesQ1 = new short[len_in1];
    short[] sizesQ2 = new short[len_in2];
    int i, count = 0;

    for (i = 0; i < len_in1; i++) {
      if (in1[i].attrType == AttrType.attrString) {
        sizesQ1[i] = q1_str_sizes[count++];
      }
    }

    for (count = 0, i = 0; i < len_in2; i++) {
      if (in2[i].attrType == AttrType.attrString) {
        sizesQ2[i] = q2_str_sizes[count++];
      }
    }

    int n_strs = 0;
    for (i = 0; i < nOutFlds; i++) {
      if (proj_list[i].relation.key == RelSpec.outer) {
        res_attrs[i] = new AttrType(in1[proj_list[i].offset - 1].attrType);
      } else if (proj_list[i].relation.key == RelSpec.innerRel) {
        res_attrs[i] = new AttrType(in2[proj_list[i].offset - 1].attrType);
      }
    }

    // Now construct the res_str_sizes array.
    for (i = 0; i < nOutFlds; i++) {
      if (proj_list[i].relation.key == RelSpec.outer && in1[proj_list[i].offset - 1].attrType == AttrType.attrString) {
        n_strs++;
      } else if (proj_list[i].relation.key == RelSpec.innerRel &&
          in2[proj_list[i].offset - 1].attrType == AttrType.attrString) {
        n_strs++;
      }
    }

    short[] res_str_sizes = new short[n_strs];
    count = 0;
    for (i = 0; i < nOutFlds; i++) {
      if (proj_list[i].relation.key == RelSpec.outer && in1[proj_list[i].offset - 1].attrType == AttrType.attrString) {
        res_str_sizes[count++] = sizesQ1[proj_list[i].offset - 1];
      } else if (proj_list[i].relation.key == RelSpec.innerRel &&
          in2[proj_list[i].offset - 1].attrType == AttrType.attrString) {
        res_str_sizes[count++] = sizesQ2[proj_list[i].offset - 1];
      }
    }
    try {
      Jquadruple.setHdr(res_attrs, res_str_sizes);
    } catch (Exception e) {
      throw new QuadrupleUtilsException(e, "setHdr() failed");
    }
    return res_str_sizes;
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
      throws IOException,
      QuadrupleUtilsException,
      InvalidRelation {
    short[] sizesQ1 = new short[len_in1];
    int i, count = 0;

    for (i = 0; i < len_in1; i++) {
      if (in1[i].attrType == AttrType.attrString) {
        sizesQ1[i] = q1_str_sizes[count++];
      }
    }

    int n_strs = 0;
    for (i = 0; i < nOutFlds; i++) {
      if (proj_list[i].relation.key == RelSpec.outer) {
        res_attrs[i] = new AttrType(in1[proj_list[i].offset - 1].attrType);
      } else {
        throw new InvalidRelation("Invalid relation -innerRel");
      }
    }

    // Now construct the res_str_sizes array.
    for (i = 0; i < nOutFlds; i++) {
      if (proj_list[i].relation.key == RelSpec.outer
          && in1[proj_list[i].offset - 1].attrType == AttrType.attrString) {
        n_strs++;
      }
    }

    short[] res_str_sizes = new short[n_strs];
    count = 0;
    for (i = 0; i < nOutFlds; i++) {
      if (proj_list[i].relation.key == RelSpec.outer
          && in1[proj_list[i].offset - 1].attrType == AttrType.attrString) {
        res_str_sizes[count++] = sizesQ1[proj_list[i].offset - 1];
      }
    }

    try {
      Jquadruple.setHdr(res_attrs, res_str_sizes);
    } catch (Exception e) {
      throw new QuadrupleUtilsException(e, "setHdr() failed");
    }
    return res_str_sizes;
  }
}




