package iterator;

import global.AttrType;
import global.TupleOrder;
import heap.BasicPatternClass;
import heap.InvalidTupleSizeException;
import heap.InvalidTypeException;
import java.io.IOException;

/**
 * Extends pnodeSplayPQ to compare BasicPatternClass for
 * the pnode compare
 */
public class bppnodeSplayPQ extends pnodeSplayPQ{
  private int num_fields;
  public bppnodeSplayPQ() {
    super();
  }

  public bppnodeSplayPQ(int fldNo, AttrType fldType, TupleOrder order, int _num_fields) {
    super(fldNo, fldType, order);
    num_fields = _num_fields;
  }

  @Override
  public int pnodeCMP(pnode a, pnode b)
      throws TupleUtilsException {
    BasicPatternClass bpa = new BasicPatternClass(a.tuple);
    BasicPatternClass bpb = new BasicPatternClass(b.tuple);
    try {
      bpa.setDefaultHeader(num_fields);
      bpb.setDefaultHeader(num_fields);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return BPUtils.CompareBPWithBP(fld_type, bpa, fld_no, bpb, fld_no);
  }
}