package diskmgr.IndexingSchemes;

import btree.StringKey;
import btree.quadbtree.BTreeFile;
import diskmgr.rdf.IStream;
import global.QID;
import global.QuadOrder;
import heap.Quadruple;
import heap.labelheap.LabelHeapFile;
import heap.quadrupleheap.QuadrupleHeapFile;

public interface IndexScheme {
  /**
   * Inserts a single quadruple to the BTree Index file according to the schema
   * @param quadruple
   * @param qid
   * @param entityHeapFile
   * @param predicateHeapFile
   */
  void insert(Quadruple quadruple, QID qid, LabelHeapFile entityHeapFile, LabelHeapFile predicateHeapFile);
  /**
   * Inserts all quadruples from QuadrupleHeapFile to the
   * BTree Index file according to the schema
   * @param quadrupleHeapFile
   * @param entityHeapFile
   * @param predicateHeapFile
   */
  void batchInsert(QuadrupleHeapFile quadrupleHeapFile, LabelHeapFile entityHeapFile, LabelHeapFile predicateHeapFile);

  StringKey getKey(Quadruple quadruple, QID qid, LabelHeapFile entityHeapFile,
      LabelHeapFile predicateHeapFile) throws Exception;

  /**
   * Returns the btree file.
   *
   * @return
   */
  BTreeFile getBtreeFile();

  void close() throws Exception;

  /**
   * Returns the IStream object of two different kinds
   * depending on the filter provided the indexing
   * scheme it was asked for.
   *  - TStream
   *  - BTStream
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
  IStream getStream(
      QuadOrder orderType,
      int numBuf,
      String subjectFilter,
      String predicateFilter,
      String objectFilter,
      Float confidenceFilter,
      QuadrupleHeapFile quadrupleHeapFile,
      LabelHeapFile entityHeapFile,
      LabelHeapFile predicateHeapFile)
      throws Exception;
}
