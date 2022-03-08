package btree.lablebtree;

import btree.DataClass;
import global.LID;

/**
 * IndexData: It extends the DataClass.
 * It defines the data "lid" for leaf node in B++ tree.
 */
public class LeafData extends DataClass {
  private LID myLid;

  /**
   * Class constructor
   *
   * @param lid the data lid
   */
  LeafData(LID lid) {myLid = new LID(lid.pageNo, lid.slotNo);}

  public String toString() {
    String s;
    s = "[ " + (new Integer(myLid.pageNo.pid)).toString() + " "
        + (new Integer(myLid.slotNo)).toString() + " ]";
    return s;
  }

  ;

  /**
   * get a copy of the lid
   *
   * @return the reference of the copy
   */
  public LID getData() {return new LID(myLid.pageNo, myLid.slotNo);}

  ;

  /**
   * set the lid
   */
  public void setData(LID lid) {myLid = new LID(lid.pageNo, lid.slotNo);}

  ;
}   
