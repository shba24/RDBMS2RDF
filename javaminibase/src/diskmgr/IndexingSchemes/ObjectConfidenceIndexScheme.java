package diskmgr.IndexingSchemes;

import btree.KeyClass;
import btree.StringKey;
import btree.quadbtree.BTreeFile;
import global.LID;
import global.QID;
import heap.Label;
import heap.Quadruple;
import heap.labelheap.LabelHeapFile;
import heap.quadrupleheap.QuadrupleHeapFile;
import heap.quadrupleheap.TScan;

public class ObjectConfidenceIndexScheme implements IndexSchemes {

  /**
   * Create Index
   *
   * @param bTreeFile         BTree Index
   * @param quadrupleHeapFile QuadrupleHeapFile
   * @throws Exception
   */
  @Override
  public void createIndex(BTreeFile bTreeFile, QuadrupleHeapFile quadrupleHeapFile,
      LabelHeapFile entityHeapFile)
      throws Exception {
    try {
      TScan scan = new TScan(quadrupleHeapFile);
      Quadruple quadruple;
      QID qid = new QID();
      double confidence;
      while ((quadruple = scan.getNext(qid)) != null) {
        confidence = quadruple.getConfidence();
        String temp = Double.toString(confidence);
        Label label = entityHeapFile.getLabel((LID) quadruple.getObjectID().returnLID());
        KeyClass key = new StringKey(label.getLabel() + ":" + temp);
        bTreeFile.insert(key, qid);
      }
      scan.closescan();
    } catch (Exception e) {
      System.err.println("*** Error creating Index for Object Confidence " + e);
      e.printStackTrace();
    }


  }
}
