package diskmgr.IndexingSchemes;

import btree.KeyClass;
import btree.StringKey;
import btree.quadbtree.BTreeFile;
import global.AttrType;
import global.LID;
import global.QID;
import heap.Label;
import heap.Quadruple;
import heap.labelheap.LabelHeapFile;
import heap.quadrupleheap.QuadrupleHeapFile;
import heap.quadrupleheap.TScan;

public class PredicateConfidenceIndexScheme implements IndexSchemes{

  /**
   * Create Index
   * @param bTreeFile BTree Index
   * @param quadrupleHeapFile    QuadrupleHeapFile
   * @throws Exception
   */
  @Override
  public void createIndex(BTreeFile bTreeFile, QuadrupleHeapFile quadrupleHeapFile, LabelHeapFile predicateHeapFile) throws Exception {

    TScan scan = new TScan(quadrupleHeapFile);
    Quadruple quadruple;
    QID qid = new QID();
    double confidence;
    while((quadruple = scan.getNext(qid)) != null)
    {
      confidence = quadruple.getConfidence();
      String temp = Double.toString(confidence);
      Label predicate = predicateHeapFile.getLabel((LID)quadruple.getPredicateID().returnLID());
      KeyClass key = new StringKey(predicate.getLabel()+":"+temp);
      bTreeFile.insert(key,qid);
    }
    scan.closescan();

  }
}
