package diskmgr.IndexingSchemes;

import btree.ConstructPageException;
import btree.GetFileEntryException;
import btree.PinPageException;
import btree.StringKey;
import global.GlobalConst;
import global.QID;
import heap.Quadruple;
import heap.labelheap.LabelHeapFile;

public class PredicateIndexScheme extends BaseIndexScheme {

  public PredicateIndexScheme(String bTreeFilePath)
      throws ConstructPageException, GetFileEntryException, PinPageException {
    super(bTreeFilePath);
  }

  public static String getFilePath(String rootFolderPath) {
    String[] tokens = new String[]{
        GlobalConst.BTREE_FILE_IDENTIFIER,
        GlobalConst.PREDICATE_IDENTIFIER
    };
    return generateFilePath(rootFolderPath, tokens);
  }

  @Override
  public StringKey getKey(
      Quadruple quadruple, QID qid, LabelHeapFile entityHeapFile, LabelHeapFile predicateHeapFile) throws Exception {
    return new StringKey(
        entityHeapFile.getLabel(quadruple.getPredicateID().returnLID()).getLabel());
  }
}
