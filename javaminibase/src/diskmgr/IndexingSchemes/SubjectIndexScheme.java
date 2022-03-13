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

public class SubjectIndexScheme implements IndexSchemes {

  /**
   * Unclustered BTree Index on Subject
   *
   * @param QuadBTreeIndex
   * @param curr_dbname
   */
  @Override
  public void createIndex(BTreeFile QuadBTreeIndex, String curr_dbname) {
    //Unclustered BTree Index file on subject
    try {
      //destroy existing index first
      if (QuadBTreeIndex != null) {
        QuadBTreeIndex.close();
        QuadBTreeIndex.destroyFile();
        destroyIndex(curr_dbname + "/QuadBTreeIndex");
      }

      //create new
      int keytype = AttrType.attrString;
      QuadBTreeIndex = new BTreeFile(curr_dbname + "/QuadBTreeIndex", keytype, 255, 1);

      //scan sorted heap file and insert into btree index
      QuadrupleHeapFile QuadrupleHF = new QuadrupleHeapFile(curr_dbname + "/quadrupleHF");

      LabelHeapFile Entity_HF = new LabelHeapFile(curr_dbname + "/entityHF");
      TScan am = new TScan(QuadrupleHF);
      Quadruple quadruple = null;
      QID qid = new QID();
      KeyDataEntry entry = null;
      BTFileScan scan = null;

      while ((quadruple = am.getNext(qid)) != null) {
        Label subject = Entity_HF.getLabel((LID) quadruple.getSubjectID().returnLID());
        KeyClass key = new StringKey(subject.getLabel());

        QuadBTreeIndex.insert(key, qid);

      }
      am.closescan();
      QuadBTreeIndex.close();
    } catch (Exception e) {
      System.err.println("*** Error creating Index for Subject " + e);
      e.printStackTrace();
      Runtime.getRuntime().exit(1);
    }
  }
}
