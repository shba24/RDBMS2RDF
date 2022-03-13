package diskmgr.IndexingSchemes;

import btree.KeyClass;
import btree.StringKey;
import btree.quadbtree.BTreeFile;
import global.AttrType;
import global.QID;
import heap.Quadruple;
import heap.quadrupleheap.QuadrupleHeapFile;
import heap.quadrupleheap.TScan;

public class ConfidenceIndexSchemes implements IndexSchemes{

  @Override
  public void createIndex(BTreeFile bTreeFile, String dbname)
      throws Exception{

    if(bTreeFile != null)
    {
      bTreeFile.close();
      bTreeFile.destroyFile();
      IndexUtils.destroyIndex(dbname+"/Quadruple_BTreeIndex");
    }

    //create new
    int keytype = AttrType.attrString;
    bTreeFile = new BTreeFile(dbname+"/bTreeFile",keytype,255,1);
    bTreeFile.close();

    //scan sorted heap file and insert into btree index
    bTreeFile = new BTreeFile(dbname+"/Quadruple_BTreeIndex");
    QuadrupleHeapFile qHeapFile = new QuadrupleHeapFile(dbname+"/QuadrupleHF");
    TScan scan = new TScan(qHeapFile);
    Quadruple quadruple;
    QID qid = new QID();
    double confidence;
    while((quadruple = scan.getNext(qid)) != null)
    {
      confidence = quadruple.getConfidence();
      String temp = Double.toString(confidence);
      KeyClass key = new StringKey(temp);
      bTreeFile.insert(key,qid);
    }

    scan.closescan();
    bTreeFile.close();
  }
}
