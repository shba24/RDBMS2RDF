package diskmgr.IndexingSchemes;


import btree.KeyClass;
import btree.StringKey;
import btree.quadbtree.*;
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
  public void createIndex(BTreeFile QuadBTreeIndex, QuadrupleHeapFile quadrupleHeapFile,
      LabelHeapFile entityHeapFile) {
    try {
      TScan am = new TScan(quadrupleHeapFile);
      Quadruple quadruple = null;
      QID qid = new QID();
      double confidence = 0.0;

      while ((quadruple = am.getNext(qid)) != null) {
        confidence = quadruple.getConfidence();
        String temp = Double.toString(confidence);
        Label subject = entityHeapFile.getLabel((LID) quadruple.getSubjectID().returnLID());
        KeyClass key = new StringKey(subject.getLabel() + ":" + temp);

        QuadBTreeIndex.insert(key, qid);

      }
      am.closescan();

    } catch (Exception e) {
      System.err.println("*** Error creating Index for Subject Confidence" + e);
      e.printStackTrace();
    }
  }
}
