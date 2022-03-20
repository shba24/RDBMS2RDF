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
import global.GlobalConst;
import global.QID;
import global.QuadOrder;
import heap.Quadruple;
import heap.labelheap.LabelHeapFile;
import heap.quadrupleheap.QuadrupleHeapFile;
import java.io.IOException;

public class SubjectIndexScheme extends BaseIndexScheme {

  /**
   * Public Constructor
   *
   * @throws ConstructPageException
   * @throws GetFileEntryException
   * @throws PinPageException
   * @throws AddFileEntryException
   * @throws IOException
   */
  public SubjectIndexScheme()
      throws ConstructPageException, GetFileEntryException, PinPageException, AddFileEntryException, IOException {
    super(getFilePath());
  }

  /**
   * Returns the file path of the btree index file
   *
   * @return
   */
  public static String getFilePath() {
    String[] tokens = new String[]{
        GlobalConst.BTREE_FILE_IDENTIFIER,
        GlobalConst.SUBJECT_IDENTIFIER
    };
    return generateFilePath(tokens);
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
    return new StringKey(
        entityHeapFile.getLabel(quadruple.getSubjectID().returnLID()).getLabel());
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
      LabelHeapFile predicateHeapFile)
      throws Exception {
    if (subjectFilter == null) {
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
      KeyClass lo_key = new StringKey(subjectFilter);
      KeyClass hi_key = new StringKey(subjectFilter);
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
