package diskmgr.IndexingSchemes;

import btree.ConstructPageException;
import btree.GetFileEntryException;
import btree.PinPageException;
import btree.StringKey;
import btree.quadbtree.BTreeFile;
import global.QID;
import heap.Quadruple;
import heap.labelheap.LabelHeapFile;
import heap.quadrupleheap.QuadrupleHeapFile;
import heap.quadrupleheap.TScan;
import java.nio.file.Paths;

public abstract class BaseIndexScheme implements IndexScheme {
  protected final BTreeFile bTreeFile;

  public BaseIndexScheme(String bTreeFilePath)
      throws ConstructPageException, GetFileEntryException, PinPageException {
    bTreeFile = new BTreeFile(bTreeFilePath);
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

  public static String generateFilePath(String rootFolderPath, String[] identifiers) {
    StringBuilder fileName = new StringBuilder();

    for (int i = 0; i < identifiers.length - 1; i++) {
      fileName.append(identifiers[i]);
      fileName.append("-");
    }

    fileName.append(identifiers[identifiers.length - 1]);

    return Paths.get(rootFolderPath, fileName.toString()).toString();
  }
}
