package iterator;

import global.AttrType;
import heap.BasicPatternClass;
import heap.FieldNumberOutOfBoundException;

import heap.labelheap.LabelHeapFile;
import java.io.IOException;
import java.util.Arrays;

/**
 * some useful method when processing BP
 */
public class BPUtils {
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

  private static String getValid(BasicPatternClass bp, int fld_no) throws Exception {
    byte[] value = bp.getBytesFld(fld_no);
    if (Arrays.equals(maxi, value)) {
      return String.valueOf(Character.MAX_VALUE);
    } else if (Arrays.equals(mini, value)) {
      return String.valueOf(Character.MIN_VALUE);
    }
    return bp.getNodeLabel(fld_no);
  }

  /**
   * Compares BP with BP
   *
   * @param bp1
   * @param bp2
   * @param fld_no
   * @return
   * @throws TupleUtilsException
   */
  private static int CompareBPWithBP(
      BasicPatternClass bp1,
      BasicPatternClass bp2,
      int fld_no) throws TupleUtilsException {
    if (fld_no==1) {
      try {
        Float confidence1 = bp1.getConfidence();
        Float confidence2 = bp2.getConfidence();
        int cmp = confidence1.compareTo(confidence2);
        if (cmp<0) return -1;
        else if (cmp==0) return 0;
        else return 1;
      } catch (Exception e) {
        System.err.println("Error comparing confidence.");
        e.printStackTrace();
      }
    } else {
      try {
        String nodeLabel1 = bp1.getNodeLabel(fld_no);
        String nodeLabel2 = getValid(bp2, fld_no);
        return nodeLabel1.compareTo(nodeLabel2);
      } catch (Exception e) {
        System.err.println("Error comparing node.");
        e.printStackTrace();
      }
    }
    throw new TupleUtilsException("Wrong field number fld_no="+fld_no);
  }

  /**
   * Compares field bp1_fld_no of bp1 with bp1_fld_no bp2
   *
   * @param fldType
   * @param bp1
   * @param bp1_fld_no
   * @param bp2
   * @param bp2_fld_no
   * @return
   * @throws TupleUtilsException
   */
  public static int CompareBPWithBP(
      AttrType fldType,
      BasicPatternClass bp1, int bp1_fld_no,
      BasicPatternClass bp2, int bp2_fld_no) throws TupleUtilsException {
    if (bp1_fld_no!=bp2_fld_no) {
      throw new TupleUtilsException(
          "Wrong field number bp1_fld_no="+bp1_fld_no+" bp2_fld_no="+bp2_fld_no);
    }
    return CompareBPWithBP(bp1, bp2, bp1_fld_no);
  }

  /**
   * Compares field bp1_fld_no of bp1 with bp2
   *
   * @param fldType
   * @param bp1
   * @param bp1_fld_no
   * @param bp2
   * @return
   * @throws TupleUtilsException
   */
  public static int CompareBPWithValue(
      AttrType fldType,
      BasicPatternClass bp1, int bp1_fld_no,
      BasicPatternClass bp2)
      throws TupleUtilsException {
    return CompareBPWithBP(fldType, bp1, bp1_fld_no, bp2, bp1_fld_no);
  }

  /**
   * Checks equality of the BP
   *
   * @param bp1
   * @param bp2
   * @param types
   * @param len
   * @return
   * @throws Exception
   */
  public static boolean Equal(BasicPatternClass bp1, BasicPatternClass bp2, AttrType types[], int len)
      throws Exception {
    int i;

    for (i = 1; i <= len; i++) {
      if (CompareBPWithBP(types[i - 1], bp1, i, bp2, i) != 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * Sets the fld_no in bp1 of type fldType from bp2 at fld_no
   *
   * @param bp1
   * @param bp2
   * @param fld_no
   * @param fldType
   * @throws IOException
   * @throws UnknowAttrType
   * @throws FieldNumberOutOfBoundException
   */
  public static void SetValue(BasicPatternClass bp1, BasicPatternClass bp2, int fld_no, AttrType fldType)
      throws IOException,
      UnknowAttrType,
      FieldNumberOutOfBoundException {

    switch (fldType.attrType) {
      case AttrType.attrInteger:
        bp1.setIntFld(fld_no, bp2.getIntFld(fld_no));
        break;
      case AttrType.attrReal:
        bp1.setFloFld(fld_no, bp2.getFloFld(fld_no));
        break;
      case AttrType.attrString:
        bp1.setStrFld(fld_no, bp2.getStrFld(fld_no));
        break;
      case AttrType.attrBytes:
        bp1.setBytesFld(fld_no, bp2.getBytesFld(fld_no));
        break;
      default:
        throw new UnknowAttrType(null, "Don't know how to handle attrSymbol, attrNull");
    }

    return;
  }
}




