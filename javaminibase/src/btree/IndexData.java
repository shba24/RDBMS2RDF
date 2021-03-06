package btree;

import global.PageId;

/**
 * IndexData: It extends the DataClass.
 * It defines the data "pageNo" for index node in B++ tree.
 */
public class IndexData extends DataClass {
  private PageId pageId;

  /**
   * Class constructor
   *
   * @param pageNo the page number
   */
  public IndexData(PageId pageNo) {pageId = new PageId(pageNo.pid);}

  /**
   * Class constructor
   *
   * @param pageNo the page number
   */
  public IndexData(int pageNo) {pageId = new PageId(pageNo);}

  ;

  public String toString() {
    return (new Integer(pageId.pid)).toString();
  }

  ;

  /**
   * get a copy of the pageNo
   *
   * @return the reference of the copy
   */
  public PageId getData() {return new PageId(pageId.pid);}

  ;

  /**
   * set the pageNo
   */
  public void setData(PageId pageNo) {pageId = new PageId(pageNo.pid);}

  ;
}   
