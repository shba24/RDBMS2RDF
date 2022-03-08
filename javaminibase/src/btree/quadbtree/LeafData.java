package btree.quadbtree;

import btree.DataClass;
import global.QID;

/**
 * IndexData: It extends the DataClass.
 * It defines the data "qid" for leaf node in B++ tree.
 */
public class LeafData extends DataClass {
  private QID myQid;

  /**
   * Class constructor
   *
   * @param qid the data qid
   */
  LeafData(QID qid) {myQid = new QID(qid.pageNo, qid.slotNo);}

  public String toString() {
    String s;
    s = "[ " + (new Integer(myQid.pageNo.pid)).toString() + " "
        + (new Integer(myQid.slotNo)).toString() + " ]";
    return s;
  }

  ;

  /**
   * get a copy of the qid
   *
   * @return the reference of the copy
   */
  public QID getData() {return new QID(myQid.pageNo, myQid.slotNo);}

  ;

  /**
   * set the qid
   */
  public void setData(QID qid) {myQid = new QID(qid.pageNo, qid.slotNo);}

  ;
}   
