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
import heap.FieldNumberOutOfBoundException;
import heap.Quadruple;
import heap.labelheap.LabelHeapFile;
import heap.quadrupleheap.QuadrupleHeapFile;
import java.io.IOException;

public class ConfidenceIndexScheme extends BaseIndexScheme {

  /**
   * Public constructor
   *
   * @throws ConstructPageException
   * @throws GetFileEntryException
   * @throws PinPageException
   * @throws AddFileEntryException
   * @throws IOException
   */
  public ConfidenceIndexScheme(String bTreeFilePath)
      throws ConstructPageException, GetFileEntryException, PinPageException, AddFileEntryException, IOException {
    super(bTreeFilePath);
  }

  /**
   * Gets the key for lookup in the Btree file
   *
   * @param quadruple
   * @param qid
   * @param entityHeapFile
   * @param predicateHeapFile
   * @return
   * @throws FieldNumberOutOfBoundException
   * @throws IOException
   */
  @Override
  public StringKey getKey(
      Quadruple quadruple,
      QID qid,
      LabelHeapFile entityHeapFile,
      LabelHeapFile predicateHeapFile) throws FieldNumberOutOfBoundException, IOException {
    return new StringKey(Double.toString(quadruple.getConfidence()));
  }

  /**
   * Returns the IStream object of two different kinds
   * depending on the filter provided the indexing
   * scheme it was asked for.
   * - TStream
   * - BTStream
   *
   * @param orderType
   * @param numBuf
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
    if (confidenceFilter == null) {
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
      KeyClass lo_key = new StringKey(confidenceFilter.toString());
      KeyClass hi_key = new StringKey(confidenceFilter.toString());
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
