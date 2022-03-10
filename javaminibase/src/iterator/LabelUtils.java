package iterator;

import global.AttrType;
import heap.FieldNumberOutOfBoundException;
import heap.Label;
import heap.Tuple;

import java.io.IOException;

/**
 * some useful method when processing Label
 */
public class LabelUtils extends TupleUtils {
  
  private static Tuple tuple;

  /**
   * This function compares a label with another label in respective field, and
   * returns:
   * <p>
   * 0        if the two are equal,
   * 1        if the label is greater,
   * -1        if the label is smaller,
   *
   * @param fldType   the type of the field being compared.
   * @param l1        one label.
   * @param l2        another label.
   * @param l1_fld_no the field numbers in the labels to be compared.
   * @param l2_fld_no the field numbers in the labels to be compared.
   * @return 0        if the two are equal,
   * 1        if the label is greater,
   * -1        if the label is smaller,
   * @throws UnknowAttrType      don't know the attribute type
   * @throws IOException         some I/O fault
   * @throws LabelUtilsException exception from this class
   */
  public static int CompareLabelWithLabel(
      AttrType fldType,
      Label l1, int l1_fld_no,
      Label l2, int l2_fld_no)
          throws IOException,
          UnknowAttrType,
          LabelUtilsException, FieldNumberOutOfBoundException {
    int l1_i, l2_i;
    float l1_r, l2_r;
    String l1_s, l2_s;

    switch (fldType.attrType) {
      case AttrType.attrInteger:                // Compare two integers.
        l1_i = tuple.getIntFld(l1_fld_no);
        l2_i = tuple.getIntFld(l2_fld_no);
        if (l1_i == l2_i) {
          return 0;
        }
        if (l1_i < l2_i) {
          return -1;
        }
        if (l1_i > l2_i) {
          return 1;
        }

      case AttrType.attrReal:                // Compare two floats
        l1_r = tuple.getFloFld(l1_fld_no);
        l2_r = tuple.getFloFld(l2_fld_no);
        if (l1_r == l2_r) {
          return 0;
        }
        if (l1_r < l2_r) {
          return -1;
        }
        if (l1_r > l2_r) {
          return 1;
        }

      case AttrType.attrString:                // Compare two strings
        l1_s = tuple.getStrFld(l1_fld_no);
        l2_s = tuple.getStrFld(l2_fld_no);

        // Now handle the special case that is posed by the max_values for strings...
        if (l1_s.compareTo(l2_s) > 0) {
          return 1;
        }
        if (l1_s.compareTo(l2_s) < 0) {
          return -1;
        }
        return 0;
      default:

        throw new UnknowAttrType(null, "Don't know how to handle attrSymbol, attrNull");
    }
  }

  /**
   * This function  compares  label1 with another label2 whose
   * field number is same as the label1
   *
   * @param fldType   the type of the field being compared.
   * @param l1        one label
   * @param value     another label.
   * @param l1_fld_no the field numbers in the labels to be compared.
   * @return 0        if the two are equal,
   * 1        if the label is greater,
   * -1        if the label is smaller,
   * @throws UnknowAttrType      don't know the attribute type
   * @throws IOException         some I/O fault
   * @throws LabelUtilsException exception from this class
   */
  public static int CompareLabelWithValue(
      AttrType fldType,
      Label l1, int l1_fld_no,
      Label value)
          throws IOException,
          UnknowAttrType,
          LabelUtilsException, FieldNumberOutOfBoundException {
    return CompareLabelWithLabel(fldType, l1, l1_fld_no, value, l1_fld_no);
  }

  /**
   * This function Compares two Label inn all fields
   *
   * @param l1     the first label
   * @param l2     the secocnd label
   * @param len    the field numbers
   * @return 0        if the two are not equal,
   * 1        if the two are equal,
   * @throws UnknowAttrType      don't know the attribute type
   * @throws IOException         some I/O fault
   * @throws LabelUtilsException exception from this class
   */

  public static boolean Equal(Label l1, Label l2, AttrType types[], int len)
          throws IOException, UnknowAttrType, LabelUtilsException, FieldNumberOutOfBoundException {
    int i;

    for (i = 1; i <= len; i++) {
      if (CompareLabelWithLabel(types[i - 1], l1, i, l2, i) != 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * get the string specified by the field number
   *
   * @param label the label
   * @return the content of the field number
   * @throws IOException         some I/O fault
   * @throws LabelUtilsException exception from this class
   */
  public static String Value(Label label, int fldno)
          throws IOException,
          LabelUtilsException, FieldNumberOutOfBoundException {
    String temp;
    temp = tuple.getStrFld(fldno);
    return temp;
  }

  /**
   * set up a label in specified field from a label
   *
   * @param value   the label to be set
   * @param label   the given label
   * @param fld_no  the field number
   * @param fldType the label attr type
   * @throws UnknowAttrType      don't know the attribute type
   * @throws IOException         some I/O fault
   * @throws LabelUtilsException exception from this class
   */
  public static void SetValue(Label value, Label label, int fld_no, AttrType fldType)
          throws IOException,
          UnknowAttrType,
          LabelUtilsException, FieldNumberOutOfBoundException {

    switch (fldType.attrType) {
      case AttrType.attrInteger:
        value.setIntFld(fld_no, label.getIntFld(fld_no));
        break;
      case AttrType.attrReal:
        value.setFloFld(fld_no, label.getFloFld(fld_no));
        break;
      case AttrType.attrString:
        value.setStrFld(fld_no, label.getStrFld(fld_no));
        break;
      default:
        throw new UnknowAttrType(null, "Don't know how to handle attrSymbol, attrNull");
    }

    return;
  }

  /**
   * set up the Jlabel's attrtype, string size,field number for using join
   *
   * @param Jlabel       reference to an actual label  - no memory has been malloced
   * @param res_attrs    attributes type of result label
   * @param in1          array of the attributes of the label (ok)
   * @param len_in1      num of attributes of in1
   * @param in2          array of the attributes of the label (ok)
   * @param len_in2      num of attributes of in2
   * @param l1_str_sizes shows the length of the string fields in S
   * @param l2_str_sizes shows the length of the string fields in R
   * @param proj_list    shows what input fields go where in the output label
   * @param nOutFlds     number of outer relation fileds
   * @throws IOException         some I/O fault
   * @throws LabelUtilsException exception from this class
   */
  public static short[] setup_op_label(
      Label Jlabel, AttrType[] res_attrs,
      AttrType in1[], int len_in1, AttrType in2[],
      int len_in2, short l1_str_sizes[],
      short l2_str_sizes[],
      FldSpec proj_list[], int nOutFlds)
      throws IOException,
      LabelUtilsException {
    short[] sizesL1 = new short[len_in1];
    short[] sizesL2 = new short[len_in2];
    int i, count = 0;

    for (i = 0; i < len_in1; i++) {
      if (in1[i].attrType == AttrType.attrString) {
        sizesL1[i] = l1_str_sizes[count++];
      }
    }

    for (count = 0, i = 0; i < len_in2; i++) {
      if (in2[i].attrType == AttrType.attrString) {
        sizesL2[i] = l2_str_sizes[count++];
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
        res_str_sizes[count++] = sizesL1[proj_list[i].offset - 1];
      } else if (proj_list[i].relation.key == RelSpec.innerRel &&
          in2[proj_list[i].offset - 1].attrType == AttrType.attrString) {
        res_str_sizes[count++] = sizesL2[proj_list[i].offset - 1];
      }
    }
    try {
      Jlabel.setHdr((short) nOutFlds, res_attrs, res_str_sizes);
    } catch (Exception e) {
      throw new LabelUtilsException(e, "setHdr() failed");
    }
    return res_str_sizes;
  }

  /**
   * set up the Jlabel's attrtype, string size,field number for using project
   *
   * @param Jlabel       reference to an actual label  - no memory has been malloced
   * @param res_attrs    attributes type of result label
   * @param in1          array of the attributes of the label (ok)
   * @param len_in1      num of attributes of in1
   * @param l1_str_sizes shows the length of the string fields in S
   * @param proj_list    shows what input fields go where in the output label
   * @param nOutFlds     number of outer relation fileds
   * @throws IOException         some I/O fault
   * @throws LabelUtilsException exception from this class
   * @throws InvalidRelation     invalid relation
   */

  public static short[] setup_op_label(
      Label Jlabel, AttrType res_attrs[],
      AttrType in1[], int len_in1,
      short l1_str_sizes[],
      FldSpec proj_list[], int nOutFlds)
      throws IOException,
      LabelUtilsException,
      InvalidRelation {
    short[] sizesL1 = new short[len_in1];
    int i, count = 0;

    for (i = 0; i < len_in1; i++) {
      if (in1[i].attrType == AttrType.attrString) {
        sizesL1[i] = l1_str_sizes[count++];
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
        res_str_sizes[count++] = sizesL1[proj_list[i].offset - 1];
      }
    }

    try {
      Jlabel.setHdr((short) nOutFlds, res_attrs, res_str_sizes);
    } catch (Exception e) {
      throw new LabelUtilsException(e, "setHdr() failed");
    }
    return res_str_sizes;
  }
}




