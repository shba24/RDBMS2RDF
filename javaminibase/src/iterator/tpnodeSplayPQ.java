package iterator;

import global.AttrType;
import global.TupleOrder;
import heap.Quadruple;
import java.io.IOException;

/**
 * Extends pnodeSplayPQ to use QuadrupleUtils for
 * the pnode compare
 */
public class tpnodeSplayPQ extends pnodeSplayPQ{
  public tpnodeSplayPQ() {
    super();
  }

  public tpnodeSplayPQ(int fldNo, AttrType fldType, TupleOrder order) {
    super(fldNo, fldType, order);
  }

  @Override
  public int pnodeCMP(pnode a, pnode b)
      throws IOException, UnknowAttrType, TupleUtilsException {
    int ans = QuadrupleUtils.CompareQuadrupleWithQuadruple(
        fld_type, new Quadruple(a.tuple), fld_no, new Quadruple(b.tuple), fld_no);
    return ans;
  }
}
