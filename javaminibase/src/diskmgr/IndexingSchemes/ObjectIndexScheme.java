package diskmgr.IndexingSchemes;

import static diskmgr.IndexingSchemes.IndexUtils.destroyIndex;

import btree.KeyClass;
import btree.KeyDataEntry;
import btree.StringKey;
import btree.quadbtree.*;
import global.AttrType;
import global.LID;
import global.QID;
import heap.Label;
import heap.Quadruple;
import heap.labelheap.LabelHeapFile;
import heap.quadrupleheap.QuadrupleHeapFile;
import heap.quadrupleheap.TScan;

public class ObjectIndexScheme implements IndexSchemes {

  /**
   * * Unclustered BTree Index on Object
   *
   * @param QuadBTreeIndex
   * @param quadrupleHeapFile
   */
  @Override
  public void createIndex(BTreeFile QuadBTreeIndex, QuadrupleHeapFile quadrupleHeapFile, LabelHeapFile entityHeapFile) {

    try{
      TScan am = new TScan(quadrupleHeapFile);
      Quadruple quadruple = null;
      QID qid = new QID();
      KeyDataEntry entry = null;
      BTFileScan scan = null;

      while ((quadruple = am.getNext(qid)) != null) {
        Label subject = entityHeapFile.getLabel((LID) quadruple.getObjectID().returnLID());
        KeyClass key = new StringKey(subject.getLabel());

        QuadBTreeIndex.insert(key, qid);

      }
      am.closescan();

    } catch (Exception e) {
      System.err.println("*** Error creating Index for Subject " + e);
      e.printStackTrace();
      Runtime.getRuntime().exit(1);
    }
  }
}
