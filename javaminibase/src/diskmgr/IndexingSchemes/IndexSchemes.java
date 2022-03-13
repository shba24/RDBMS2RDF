package diskmgr.IndexingSchemes;

import btree.AddFileEntryException;
import btree.ConstructPageException;
import btree.ConvertException;
import btree.DeleteFileEntryException;
import btree.DeleteRecException;
import btree.FreePageException;
import btree.GetFileEntryException;
import btree.IndexInsertRecException;
import btree.IndexSearchException;
import btree.InsertException;
import btree.IteratorException;
import btree.KeyNotMatchException;
import btree.KeyTooLongException;
import btree.LeafDeleteException;
import btree.LeafInsertRecException;
import btree.NodeNotMatchException;
import btree.PinPageException;
import btree.UnpinPageException;
import btree.quadbtree.BTreeFile;
import bufmgr.HashEntryNotFoundException;
import bufmgr.InvalidFrameNumberException;
import bufmgr.PageUnpinnedException;
import bufmgr.ReplacerException;
import heap.FieldNumberOutOfBoundException;
import heap.quadrupleheap.QuadrupleHeapFile;
import java.io.IOException;

public interface IndexSchemes {

  /**
   * Create Index method implemented using different index schemes on Unclustered BTree File.
   *
   * @param bTreeFile BTree Index
   * @param dbname    DB Name
   * @throws Exception
   */
  void createIndex(BTreeFile bTreeFile, String dbname)
      throws Exception;

}
