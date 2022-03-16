package diskmgr.IndexingSchemes;


import btree.quadbtree.BTreeFile;
import heap.labelheap.LabelHeapFile;
import heap.quadrupleheap.QuadrupleHeapFile;


public interface IndexSchemes {

  /**
   * Create Index method implemented using different index schemes on Unclustered BTree File.
   *
   * @param bTreeFile         BTree Index
   * @param quadrupleHeapFile QuadrupleHeapFile
   * @throws Exception
   */
  void createIndex(BTreeFile bTreeFile, QuadrupleHeapFile quadrupleHeapFile,
      LabelHeapFile entityHeapFile)
      throws Exception;

}
