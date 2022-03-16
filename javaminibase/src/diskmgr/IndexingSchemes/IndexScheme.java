package diskmgr.IndexingSchemes;

import btree.StringKey;
import btree.quadbtree.BTreeFile;
import global.QID;
import heap.FieldNumberOutOfBoundException;
import heap.Quadruple;
import heap.labelheap.LabelHeapFile;
import heap.quadrupleheap.QuadrupleHeapFile;
import java.io.IOException;

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
}
