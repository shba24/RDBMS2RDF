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

public class PredicateConfidence implements IndexSchemes{

  /**
   * Create Index
   * @param bTreeFile BTree Index
   * @param dbname    DB Name
   * @throws Exception
   */
  @Override
  public void createIndex(BTreeFile bTreeFile, String dbname) throws Exception {
    //destroy existing index first
    if(bTreeFile != null)
    {
      bTreeFile.close();
      bTreeFile.destroyFile();
      IndexUtils.destroyIndex(dbname+"/Quadruple_BTreeIndex");
    }

    //create new
    int keytype = AttrType.attrString;
    bTreeFile = new BTreeFile(dbname+"/Triple_BTreeIndex",keytype,255,1);
    bTreeFile.close();

    //scan sorted heap file and insert into btree index
    bTreeFile = new BTreeFile(dbname+"/Quadruple_BTreeIndex");
    QuadrupleHeapFile qHeapFile = new QuadrupleHeapFile(dbname+"/QuadrupleHF");
    LabelHeapFile predHeapFile = new LabelHeapFile(dbname+"/predicateHF");
    TScan scan = new TScan(qHeapFile);
    Quadruple quadruple;
    QID qid = new QID();
    double confidence;
    while((quadruple = scan.getNext(qid)) != null)
    {
      confidence = quadruple.getConfidence();
      String temp = Double.toString(confidence);
      Label predicate = predHeapFile.getLabel((LID)quadruple.getPredicateID().returnLID());
      KeyClass key = new StringKey(predicate.getLabel()+":"+temp);
      bTreeFile.insert(key,qid);
    }
    scan.closescan();
    bTreeFile.close();
  }
}
