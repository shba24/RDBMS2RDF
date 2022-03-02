package heap;

import diskmgr.Page;
import global.GlobalConst;
import global.IQID;
import global.QID;
import global.PageId;
import global.SystemDefs;
import java.io.IOException;

public class TScan implements GlobalConst {
/**
 * Note that one record in our way-cool HeapFile implementation is
 * specified by six (6) parameters, some of which can be determined
 * from others:
 */

  /** The Quadruple heapfile we are using. */
  private QuadrupleHeapfile _qhf;

  /** PageId of current directory page (which is itself an HFPage) */
  private PageId dirpageId = new PageId();

  /** pointer to in-core data of dirpageId (page is pinned) */
  private THFPage dirpage = new THFPage();

  /** record ID of the DataPageInfo struct (in the directory page) which
   * describes the data page where our current record lives.
   */
  private IQID datapageQid = new QID();

  /** the actual PageId of the data page with the current record */
  private PageId datapageId = new PageId();

  /** in-core copy (pinned) of the same */
  private THFPage datapage = new THFPage();

  /** record ID of the current record (from the current data page) */
  private IQID userqid = new QID();

  /** Status of next user status */
  private boolean nextUserStatus;

  /** The constructor pins the first directory page in the file
   * and initializes its private data members from the private
   * data member from qhf
   *
   * @exception InvalidTupleSizeException Invalid quadruple size
   * @exception IOException I/O errors
   *
   * @param qhf A HeapFile object
   */
  public TScan(QuadrupleHeapfile qhf)
      throws InvalidTupleSizeException,
      IOException {
    init(qhf);
  }

  /** Retrieve the next record in a sequential scan
   *
   * @exception InvalidTupleSizeException Invalid tuple size
   * @exception IOException I/O errors
   *
   * @param qid Record ID of the record
   * @return the Tuple of the retrieved record.
   */
  public Quadruple getNext(QID qid)
      throws InvalidTupleSizeException,
      IOException {
    Quadruple recptrquadruple = null;

    if (nextUserStatus != true) {
      nextDataPage();
    }

    if (datapage == null) {
      return null;
    }

    qid.getPageNo().pid = userqid.getPageNo().pid;
    qid.setSlotNo(userqid.getSlotNo());

    try {
      recptrquadruple = datapage.getQuadruple(qid);
    } catch (Exception e) {
      //    System.err.println("SCAN: Error in Scan" + e);
      e.printStackTrace();
    }

    userqid = datapage.nextQuadruple(qid);
    if (userqid == null) {
      nextUserStatus = false;
    } else {
      nextUserStatus = true;
    }

    return recptrquadruple;
  }

  /** Position the scan cursor to the record with the given rid.
   *
   * @exception InvalidTupleSizeException Invalid tuple size
   * @exception IOException I/O errors
   * @param qid Record ID of the given record
   * @return true if successful,
   *			false otherwise.
   */
  public boolean position(IQID qid)
      throws InvalidTupleSizeException,
      IOException {
    IQID nxtqid = new QID();
    boolean bst;

    bst = peekNext(nxtqid);

    if (nxtqid.equals(qid) == true) {
      return true;
    }

    // This is kind lame, but otherwise it will take all day.
    PageId pgid = new PageId();
    pgid.pid = qid.getPageNo().pid;

    if (!datapageId.equals(pgid)) {

      // reset everything and start over from the beginning
      reset();

      bst = firstDataPage();

      if (bst != true) {
        return bst;
      }

      while (!datapageId.equals(pgid)) {
        bst = nextDataPage();
        if (bst != true) {
          return bst;
        }
      }
    }

    // Now we are on the correct page.

    try {
      userqid = datapage.firstQuadruple();
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (userqid == null) {
      bst = false;
      return bst;
    }

    bst = peekNext(nxtqid);

    while ((bst == true) && (nxtqid != qid)) {
      bst = mvNext(nxtqid);
    }

    return bst;
  }

  /** Do all the constructor work
   *
   * @exception InvalidTupleSizeException Invalid tuple size
   * @exception IOException I/O errors
   *
   * @param qhf A HeapFile object
   */
  private void init(QuadrupleHeapfile qhf)
      throws InvalidTupleSizeException,
      IOException {
    _qhf = qhf;

    firstDataPage();
  }

  /** Closes the Scan object */
  public void closescan() {
    reset();
  }

  /** Reset everything and unpin all pages. */
  private void reset() {

    if (datapage != null) {

      try {
        unpinPage(datapageId, false);
      } catch (Exception e) {
        // 	System.err.println("SCAN: Error in Scan" + e);
        e.printStackTrace();
      }
    }
    datapageId.pid = 0;
    datapage = null;

    if (dirpage != null) {

      try {
        unpinPage(dirpageId, false);
      } catch (Exception e) {
        //     System.err.println("SCAN: Error in Scan: " + e);
        e.printStackTrace();
      }
    }
    dirpage = null;

    nextUserStatus = true;
  }

  /** Move to the first data page in the file.
   * @exception InvalidTupleSizeException Invalid tuple size
   * @exception IOException I/O errors
   * @return true if successful
   *         false otherwise
   */
  private boolean firstDataPage()
      throws InvalidTupleSizeException,
      IOException {
    DataPageInfo dpinfo;
    Quadruple recQuadruple = null;
    Boolean bst;

    /** copy data about first directory page */

    dirpageId.pid = _qhf._firstDirPageId.pid;
    nextUserStatus = true;

    /** get first directory page and pin it */
    try {
      dirpage = new THFPage();
      pinPage(dirpageId, (Page) dirpage.getHFPage(), false);
    } catch (Exception e) {
      //    System.err.println("SCAN Error, try pinpage: " + e);
      e.printStackTrace();
    }

    /** now try to get a pointer to the first datapage */
    datapageQid = dirpage.firstQuadruple();

    if (datapageQid != null) {
      /** there is a datapage record on the first directory page: */

      try {
        recQuadruple = dirpage.getQuadruple(datapageQid);
      } catch (Exception e) {
        //	System.err.println("SCAN: Chain Error in Scan: " + e);
        e.printStackTrace();
      }

      dpinfo = new DataPageInfo(recQuadruple.getTuple());
      datapageId.pid = dpinfo.pageId.pid;
    } else {

      /** the first directory page is the only one which can possibly remain
       * empty: therefore try to get the next directory page and
       * check it. The next one has to contain a datapage record, unless
       * the heapfile is empty:
       */
      PageId nextDirPageId = new PageId();

      nextDirPageId = dirpage.getNextPage();

      if (nextDirPageId.pid != INVALID_PAGE) {

        try {
          unpinPage(dirpageId, false);
          dirpage = null;
        } catch (Exception e) {
          //	System.err.println("SCAN: Error in 1stdatapage 1 " + e);
          e.printStackTrace();
        }

        try {

          dirpage = new THFPage();
          pinPage(nextDirPageId, (Page) dirpage.getHFPage(), false);
        } catch (Exception e) {
          //  System.err.println("SCAN: Error in 1stdatapage 2 " + e);
          e.printStackTrace();
        }

        /** now try again to read a data record: */

        try {
          datapageQid = dirpage.firstQuadruple();
        } catch (Exception e) {
          //  System.err.println("SCAN: Error in 1stdatapg 3 " + e);
          e.printStackTrace();
          datapageId.pid = INVALID_PAGE;
        }

        if (datapageQid != null) {

          try {

            recQuadruple = dirpage.getQuadruple(datapageQid);
          } catch (Exception e) {
            //    System.err.println("SCAN: Error getRecord 4: " + e);
            e.printStackTrace();
          }

          if (recQuadruple.getTuple().getLength() != DataPageInfo.size) {
            return false;
          }

          dpinfo = new DataPageInfo(recQuadruple.getTuple());
          datapageId.pid = dpinfo.pageId.pid;
        } else {
          // heapfile empty
          datapageId.pid = INVALID_PAGE;
        }
      }//end if01
      else {// heapfile empty
        datapageId.pid = INVALID_PAGE;
      }
    }

    datapage = null;

    try {
      nextDataPage();
    } catch (Exception e) {
      //  System.err.println("SCAN Error: 1st_next 0: " + e);
      e.printStackTrace();
    }

    return true;

    /** ASSERTIONS:
     * - first directory page pinned
     * - this->dirpageId has Id of first directory page
     * - this->dirpage valid
     * - if heapfile empty:
     *    - this->datapage == NULL, this->datapageId==INVALID_PAGE
     * - if heapfile nonempty:
     *    - this->datapage == NULL, this->datapageId, this->datapageRid valid
     *    - first datapage is not yet pinned
     */

  }

  /** Move to the next data page in the file and
   * retrieve the next data page.
   *
   * @return true if successful
   *			false if unsuccessful
   */
  private boolean nextDataPage()
      throws InvalidTupleSizeException,
      IOException {
    DataPageInfo dpinfo;

    boolean nextDataPageStatus;
    PageId nextDirPageId = new PageId();
    Quadruple recQuadruple = null;

    // ASSERTIONS:
    // - this->dirpageId has Id of current directory page
    // - this->dirpage is valid and pinned
    // (1) if heapfile empty:
    //    - this->datapage==NULL; this->datapageId == INVALID_PAGE
    // (2) if overall first record in heapfile:
    //    - this->datapage==NULL, but this->datapageId valid
    //    - this->datapageRid valid
    //    - current data page unpinned !!!
    // (3) if somewhere in heapfile
    //    - this->datapageId, this->datapage, this->datapageRid valid
    //    - current data page pinned
    // (4)- if the scan had already been done,
    //        dirpage = NULL;  datapageId = INVALID_PAGE

    if ((dirpage == null) && (datapageId.pid == INVALID_PAGE)) {
      return false;
    }

    if (datapage == null) {
      if (datapageId.pid == INVALID_PAGE) {
        // heapfile is empty to begin with

        try {
          unpinPage(dirpageId, false);
          dirpage = null;
        } catch (Exception e) {
          //  System.err.println("Scan: Chain Error: " + e);
          e.printStackTrace();
        }
      } else {

        // pin first data page
        try {
          datapage = new THFPage();
          pinPage(datapageId, (Page) datapage.getHFPage(), false);
        } catch (Exception e) {
          e.printStackTrace();
        }

        try {
          userqid = datapage.firstQuadruple();
        } catch (Exception e) {
          e.printStackTrace();
        }

        return true;
      }
    }

    // ASSERTIONS:
    // - this->datapage, this->datapageId, this->datapageRid valid
    // - current datapage pinned

    // unpin the current datapage
    try {
      unpinPage(datapageId, false /* no dirty */);
      datapage = null;
    } catch (Exception e) {

    }

    // read next datapagerecord from current directory page
    // dirpage is set to NULL at the end of scan. Hence

    if (dirpage == null) {
      return false;
    }

    datapageQid = dirpage.nextQuadruple(datapageQid);

    if (datapageQid == null) {
      nextDataPageStatus = false;
      // we have read all datapage records on the current directory page

      // get next directory page
      nextDirPageId = dirpage.getNextPage();

      // unpin the current directory page
      try {
        unpinPage(dirpageId, false /* not dirty */);
        dirpage = null;

        datapageId.pid = INVALID_PAGE;
      } catch (Exception e) {

      }

      if (nextDirPageId.pid == INVALID_PAGE) {
        return false;
      } else {
        // ASSERTION:
        // - nextDirPageId has correct id of the page which is to get

        dirpageId = nextDirPageId;

        try {
          dirpage = new THFPage();
          pinPage(dirpageId, (Page) dirpage.getHFPage(), false);
        } catch (Exception e) {

        }

        if (dirpage == null) {
          return false;
        }

        try {
          datapageQid = dirpage.firstQuadruple();
          nextDataPageStatus = true;
        } catch (Exception e) {
          nextDataPageStatus = false;
          return false;
        }
      }
    }

    // ASSERTION:
    // - this->dirpageId, this->dirpage valid
    // - this->dirpage pinned
    // - the new datapage to be read is on dirpage
    // - this->datapageRid has the Rid of the next datapage to be read
    // - this->datapage, this->datapageId invalid

    // data page is not yet loaded: read its record from the directory page
    try {
      recQuadruple = dirpage.getQuadruple(datapageQid);
    } catch (Exception e) {
      System.err.println("HeapFile: Error in Scan" + e);
    }

    if (recQuadruple.getTuple().getLength() != DataPageInfo.size) {
      return false;
    }

    dpinfo = new DataPageInfo(recQuadruple.getTuple());
    datapageId.pid = dpinfo.pageId.pid;

    try {
      datapage = new THFPage();
      pinPage(dpinfo.pageId, (Page) datapage.getHFPage(), false);
    } catch (Exception e) {
      System.err.println("HeapFile: Error in Scan" + e);
    }

    // - directory page is pinned
    // - datapage is pinned
    // - this->dirpageId, this->dirpage correct
    // - this->datapageId, this->datapage, this->datapageRid correct

    userqid = datapage.firstQuadruple();

    if (userqid == null) {
      nextUserStatus = false;
      return false;
    }

    return true;
  }

  private boolean peekNext(IQID qid) {

    qid.getPageNo().pid = userqid.getPageNo().pid;
    qid.setSlotNo(userqid.getSlotNo());
    return true;
  }

  /** Move to the next record in a sequential scan.
   * Also returns the RID of the (new) current record.
   */
  private boolean mvNext(IQID qid)
      throws InvalidTupleSizeException,
      IOException {
    IQID nextqid;
    boolean status;

    if (datapage == null) {
      return false;
    }

    nextqid = datapage.nextQuadruple(qid);

    if (nextqid != null) {
      userqid.getPageNo().pid = nextqid.getPageNo().pid;
      userqid.setSlotNo(nextqid.getSlotNo());
      return true;
    } else {

      status = nextDataPage();

      if (status == true) {
        qid.getPageNo().pid = userqid.getPageNo().pid;
        qid.setSlotNo(userqid.getSlotNo());
      }
    }
    return true;
  }

  /**
   * short cut to access the pinPage function in bufmgr package.
   */
  private void pinPage(PageId pageno, Page page, boolean emptyPage)
      throws HFBufMgrException {

    try {
      SystemDefs.JavabaseBM.pinPage(pageno, page, emptyPage);
    } catch (Exception e) {
      throw new HFBufMgrException(e, "Scan.java: pinPage() failed");
    }
  } // end of pinPage

  /**
   * short cut to access the unpinPage function in bufmgr package
   */
  private void unpinPage(PageId pageno, boolean dirty)
      throws HFBufMgrException {

    try {
      SystemDefs.JavabaseBM.unpinPage(pageno, dirty);
    } catch (Exception e) {
      throw new HFBufMgrException(e, "Scan.java: unpinPage() failed");
    }
  } // end of unpinPage
}
