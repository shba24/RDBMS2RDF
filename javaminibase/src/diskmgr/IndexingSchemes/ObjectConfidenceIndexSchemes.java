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

public class ObjectConfidenceIndexSchemes implements IndexSchemes{

  @Override
  public void createIndex(BTreeFile bTreeFile, String dbname)
      throws Exception {
    //destroy existing index first
    if(bTreeFile != null)
    {
      bTreeFile.close();
      bTreeFile.destroyFile();
      IndexUtils.destroyIndex(dbname+"/Quadruple_BTreeIndex");
    }

    //create new
    int keytype = AttrType.attrString;
    bTreeFile = new BTreeFile(dbname+"/Quadruple_BTreeIndex",keytype,255,1);
    bTreeFile.close();

    //scan sorted heap file and insert into btree index
    bTreeFile = new BTreeFile(dbname+"/Quadruple_BTreeIndex");
    QuadrupleHeapFile qHeapFile = new QuadrupleHeapFile(dbname+"/QuadrupleHF");
    LabelHeapFile entityHeapFile = new LabelHeapFile(dbname+"/EntityHF");
    TScan scan = new TScan(qHeapFile);
    Quadruple quadruple;
    QID qid = new QID();
    double confidence;
    while((quadruple = scan.getNext(qid)) != null)
    {
      confidence = quadruple.getConfidence();
      String temp = Double.toString(confidence);
      Label label = entityHeapFile.getLabel((LID)quadruple.getObjectID().returnLID());
      KeyClass key = new StringKey(label.getLabel()+":"+temp);
      bTreeFile.insert(key,qid);
    }
    scan.closescan();
    bTreeFile.close();
  }
}
