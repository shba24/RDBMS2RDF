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

public class SubjectConfidenceIndexScheme implements IndexSchemes {

  /**
   * Unclustered BTree Index on subject and confidence.
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
      double confidence = 0.0;

      while ((quadruple = am.getNext(qid)) != null) {
        confidence = quadruple.getConfidence();
        String temp = Double.toString(confidence);
        Label subject = entityHeapFile.getLabel((LID) quadruple.getSubjectID().returnLID());
        KeyClass key = new StringKey(subject.getLabel() + ":" + temp);

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
