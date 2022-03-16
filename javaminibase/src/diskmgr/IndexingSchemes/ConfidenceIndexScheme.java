package diskmgr.IndexingSchemes;

import btree.ConstructPageException;
import btree.GetFileEntryException;
import btree.PinPageException;
import btree.StringKey;
import global.GlobalConst;
import global.QID;
import heap.FieldNumberOutOfBoundException;
import heap.Quadruple;
import heap.labelheap.LabelHeapFile;
import java.io.IOException;

public class ConfidenceIndexScheme extends BaseIndexScheme {

  public ConfidenceIndexScheme(String rootFolderPath)
      throws ConstructPageException, GetFileEntryException, PinPageException {
    super(getFilePath(rootFolderPath));
  }

  public static String getFilePath(String rootFolderPath) {
    String[] tokens = new String[]{
        GlobalConst.BTREE_FILE_IDENTIFIER,
        GlobalConst.CONFIDENCE_IDENTIFIER
    };
    return generateFilePath(rootFolderPath, tokens);
  }

  @Override
  public StringKey getKey(
      Quadruple quadruple,
      QID qid,
      LabelHeapFile entityHeapFile,
      LabelHeapFile predicateHeapFile) throws FieldNumberOutOfBoundException, IOException {
    return new StringKey(Double.toString(quadruple.getConfidence()));
  }
}
