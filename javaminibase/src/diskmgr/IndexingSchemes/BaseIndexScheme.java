package diskmgr.IndexingSchemes;

import btree.AddFileEntryException;
import btree.ConstructPageException;
import btree.GetFileEntryException;
import btree.PinPageException;
import btree.StringKey;
import btree.quadbtree.BTreeFile;
import global.AttrType;
import global.GlobalConst;
import global.QID;
import heap.Quadruple;
import heap.labelheap.LabelHeapFile;
import heap.quadrupleheap.QuadrupleHeapFile;
import heap.quadrupleheap.TScan;
import java.io.IOException;
import java.nio.file.Paths;

public abstract class BaseIndexScheme implements IndexScheme {
  protected final BTreeFile bTreeFile;

  public BaseIndexScheme()
      throws ConstructPageException, GetFileEntryException, PinPageException, AddFileEntryException, IOException {
    bTreeFile = new BTreeFile(getFilePath(), AttrType.attrString, GlobalConst.DEFAULT_KEY_SIZE, GlobalConst.NAIVE_DELETE_FASHION);
  }

  public void close() throws Exception {
    bTreeFile.close();
  }

  /**
   * Creates the file name for the btree file
   * in which indexing is done.
   * @return
   */
  protected static String getFilePath() {
    String[] tokens = new String[]{
        GlobalConst.BTREE_FILE_IDENTIFIER,
        GlobalConst.INDEX_IDENTIFIER
    };
    return generateFilePath(tokens);
  }

  /**
   * Gets the Btree file
   * @return
   */
  public BTreeFile getBtreeFile() {
    return bTreeFile;
  }

  @Override
  public void insert(Quadruple quadruple, QID qid, LabelHeapFile entityHeapFile, LabelHeapFile predicateHeapFile) {
    try {
      StringKey key = getKey(quadruple, qid, entityHeapFile, predicateHeapFile);
      bTreeFile.insert(key, qid);
    } catch (Exception e) {
      System.err.println("Error creating Index for Quadruple.");
      e.printStackTrace();
    }
  }

  @Override
  public void batchInsert(QuadrupleHeapFile quadrupleHeapFile, LabelHeapFile entityHeapFile, LabelHeapFile predicateHeapFile) {
    try {
      TScan scan = new TScan(quadrupleHeapFile);
      Quadruple quadruple = null;
      QID qid = new QID();
      while ((quadruple = scan.getNext(qid)) != null) {
        insert(quadruple, qid, entityHeapFile, predicateHeapFile);
      }
      scan.closescan();
    } catch (Exception e) {
      System.err.println("Error doing batch insert.");
      e.printStackTrace();
    }
  }

  public static String generateFilePath(String[] identifiers) {
    StringBuilder fileName = new StringBuilder();

    for (int i = 0; i < identifiers.length - 1; i++) {
      fileName.append(identifiers[i]);
      fileName.append("-");
    }

    fileName.append(identifiers[identifiers.length - 1]);

    return Paths.get(fileName.toString()).toString();
  }
}
