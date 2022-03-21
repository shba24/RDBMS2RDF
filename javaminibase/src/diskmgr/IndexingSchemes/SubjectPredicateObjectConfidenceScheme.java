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
import global.QID;
import global.QuadOrder;
import heap.Quadruple;
import heap.labelheap.LabelHeapFile;
import heap.quadrupleheap.QuadrupleHeapFile;
import java.io.IOException;

public class SubjectPredicateObjectConfidenceScheme extends BaseIndexScheme {

  public SubjectPredicateObjectConfidenceScheme()
      throws ConstructPageException, GetFileEntryException, PinPageException, AddFileEntryException, IOException {
    super(getFilePath());
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
    double confidence = quadruple.getConfidence();
    return new StringKey(subject+":"+predicate+":"+object+":"+confidence);
  }

  /**
   * Returns the IStream object of two different kinds
   * depending on the filter provided the indexing
   * scheme it was asked for.
   * - TStream
   * - BTStream
   *
   * @param orderType
   * @param subjectFilter
   * @param predicateFilter
   * @param objectFilter
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
      String subjectFilter,
      String predicateFilter,
      String objectFilter,
      Float confidenceFilter,
      QuadrupleHeapFile quadrupleHeapFile,
      LabelHeapFile entityHeapFile,
      LabelHeapFile predicateHeapFile) throws Exception {
    if (subjectFilter == null ||
        predicateFilter == null ||
        objectFilter == null ||
        confidenceFilter == null) {
      return new TStream(
          orderType,
          numBuf,
          quadrupleHeapFile,
          subjectFilter,
          predicateFilter,
          objectFilter,
          confidenceFilter
      );
    } else {
      String filterKey = subjectFilter+":"+predicateFilter+":"+objectFilter+":"+confidenceFilter;
      KeyClass lo_key = new StringKey(filterKey);
      KeyClass hi_key = new StringKey(filterKey);
      return new BTStream(
          orderType,
          numBuf,
          bTreeFile.new_scan(lo_key, hi_key),
          subjectFilter,
          predicateFilter,
          objectFilter,
          confidenceFilter,
          quadrupleHeapFile
      );
    }
  }
}
