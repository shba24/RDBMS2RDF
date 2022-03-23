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
import global.EID;
import global.PID;
import global.QID;
import global.QuadOrder;
import heap.Quadruple;
import heap.labelheap.LabelHeapFile;
import heap.quadrupleheap.QuadrupleHeapFile;
import java.io.IOException;

public class SubjectPredicateObjectScheme extends BaseIndexScheme {

  public SubjectPredicateObjectScheme()
      throws ConstructPageException, GetFileEntryException, PinPageException, AddFileEntryException, IOException {
    super();
  }

  /**
   * Returns the key for the Quadruple according
   * to this scheme.
   *
   * @param quadruple
   * @param qid
   * @param entityHeapFile
   * @param predicateHeapFile
   * @return
   * @throws Exception
   */
  @Override
  public StringKey getKey(
      Quadruple quadruple, QID qid, LabelHeapFile entityHeapFile, LabelHeapFile predicateHeapFile) throws Exception {
    String subject = entityHeapFile.getLabel(quadruple.getSubjectID().returnLID()).getLabel();
    String object = entityHeapFile.getLabel(quadruple.getObjectID().returnLID()).getLabel();
    String predicate = predicateHeapFile.getLabel(quadruple.getPredicateID().returnLID()).getLabel();
    return new StringKey(subject+":"+predicate+":"+object);
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
    if (subjectID == null ||
        predicateID == null ||
        objectID == null) {
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
      String subjectFilter = entityHeapFile.getLabel(subjectID.returnLID()).getLabel();
      String predicateFilter = predicateHeapFile.getLabel(predicateID.returnLID()).getLabel();
      String objectFilter = entityHeapFile.getLabel(objectID.returnLID()).getLabel();
      String filterKey = subjectFilter +":"+ predicateFilter +":"+ objectFilter;
      KeyClass lo_key = new StringKey(filterKey);
      KeyClass hi_key = new StringKey(filterKey);
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
