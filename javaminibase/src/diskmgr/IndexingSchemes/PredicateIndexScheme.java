package diskmgr.IndexingSchemes;

import btree.AddFileEntryException;
import btree.ConstructPageException;
import btree.GetFileEntryException;
import btree.KeyClass;
import btree.PinPageException;
import btree.StringKey;
import diskmgr.rdf.BTStream;
import diskmgr.rdf.IStream;
import diskmgr.rdf.TStream;
import global.Convert;
import global.EID;
import global.GlobalConst;
import global.PID;
import global.QID;
import global.QuadOrder;
import heap.FieldNumberOutOfBoundException;
import heap.Quadruple;
import heap.labelheap.LabelHeapFile;
import heap.quadrupleheap.QuadrupleHeapFile;
import java.io.IOException;

public class PredicateIndexScheme extends BaseIndexScheme {

  public PredicateIndexScheme()
      throws ConstructPageException, GetFileEntryException, PinPageException, AddFileEntryException, IOException {
    super();
  }

  @Override
  public StringKey getKey(Quadruple quadruple) throws Exception {
    byte[] buffer = new byte[GlobalConst.MAX_EID_OBJ_SIZE];
    try {
      Convert.setBytesValue(quadruple.getPredicateID().returnByteArray(), 0, buffer);
      return new StringKey(new String(buffer));
    } catch (FieldNumberOutOfBoundException e) {
      System.err.println("[PredicateIndexScheme] Error in getting key.");
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * Returns the IStream object of two different kinds
   * depending on the filter provided the indexing
   * scheme it was asked for.
   * - TStream
   * - BTStream
   *
   * @param orderType
   * @param subjectID
   * @param predicateID
   * @param objectID
   * @param confidenceFilter
   * @param quadrupleHeapFile
   * @param entityHeapFile
   * @param predicateHeapFile
   * @return
   * @throws Exception
   */
  @Override
  public IStream getStream(
      QuadOrder orderType,
      int numBuf,
      EID subjectID,
      PID predicateID,
      EID objectID,
      Float confidenceFilter,
      QuadrupleHeapFile quadrupleHeapFile,
      LabelHeapFile entityHeapFile,
      LabelHeapFile predicateHeapFile) throws Exception {
    if (predicateID == null) {
      return new TStream(
          orderType,
          numBuf,
          quadrupleHeapFile,
          subjectID,
          predicateID,
          objectID,
          confidenceFilter
      );
    } else {
      Quadruple quad = new Quadruple();
      quad.setPredicateID(predicateID);
      StringKey lo_key = getKey(quad);
      KeyClass hi_key = new StringKey(lo_key.getKey());
      return new BTStream(
          orderType,
          numBuf,
          bTreeFile.new_scan(lo_key, hi_key),
          subjectID,
          predicateID,
          objectID,
          confidenceFilter,
          quadrupleHeapFile
      );
    }
  }
}
