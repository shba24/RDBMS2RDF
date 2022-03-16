package diskmgr.IndexingSchemes;

import btree.ConstructPageException;
import btree.GetFileEntryException;
import btree.PinPageException;
import btree.StringKey;
import global.GlobalConst;
import global.QID;
import heap.Quadruple;
import heap.labelheap.LabelHeapFile;

public class SubjectPredicateObjectScheme extends BaseIndexScheme {

  public SubjectPredicateObjectScheme(String bTreeFilePath)
      throws ConstructPageException, GetFileEntryException, PinPageException {
    super(bTreeFilePath);
  }

  public static String getFilePath(String rootFolderPath) {
    String[] tokens = new String[]{
        GlobalConst.BTREE_FILE_IDENTIFIER,
        GlobalConst.SUBJECT_IDENTIFIER,
        GlobalConst.PREDICATE_IDENTIFIER,
        GlobalConst.OBJECT_IDENTIFIER
    };
    return generateFilePath(rootFolderPath, tokens);
  }

  @Override
  public StringKey getKey(
      Quadruple quadruple, QID qid, LabelHeapFile entityHeapFile, LabelHeapFile predicateHeapFile) throws Exception {
    String subject = entityHeapFile.getLabel(quadruple.getSubjectID().returnLID()).getLabel();
    String object = entityHeapFile.getLabel(quadruple.getObjectID().returnLID()).getLabel();
    String predicate = predicateHeapFile.getLabel(quadruple.getPredicateID().returnLID()).getLabel();
    return new StringKey(subject+":"+predicate+":"+object);
  }
}
