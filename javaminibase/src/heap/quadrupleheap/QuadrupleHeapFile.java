package heap.quadrupleheap;


import diskmgr.Page;
import global.PageId;
import global.QID;
import global.SystemDefs;
import heap.*;


import heap.SpaceNotAvailableException;

import java.io.IOException;


interface Filetype {

  int TEMP = 0;
  int ORDINARY = 1;
} // end of Filetype

public class QuadrupleHeapFile extends Heapfile {
  /**
   * Initialize.  A null name produces a temporary heapfile which will be deleted by the destructor.
   *  If the name already denotes a file, the file is opened; otherwise, a new empty file is
   * created.
   *
   * @throws HFException        heapfile exception
   * @throws HFBufMgrException  exception thrown from bufmgr layer
   * @throws HFDiskMgrException exception thrown from diskmgr layer
   * @throws IOException        I/O errors
   */
  public QuadrupleHeapFile(String name)
      throws HFDiskMgrException, HFException, HFBufMgrException, IOException {
    super();
    // Give us a prayer of destructing cleanly if construction fails.
    _file_deleted = true;
    _fileName = null;
    if (name == null) {
      // If the name is NULL, allocate a temporary name
      // and no logging is required.
      _fileName = "tempQuadrupleHeapFile";
      String useId = new String("user.name");
      String userAccName;
      userAccName = System.getProperty(useId);
      _fileName = _fileName + userAccName;

      String filenum = Integer.toString(tempfilecount);
      _fileName = _fileName + filenum;
      _ftype = TEMP;
      tempfilecount++;
    } else {
      _fileName = name;
      _ftype = ORDINARY;
    }

    // The constructor gets run in two different cases.
    // In the first case, the file is new and the header page
    // must be initialized.  This case is detected via a failure
    // in the db->get_file_entry() call.  In the second case, the
    // file already exists and all that must be done is to fetch
    // the header page into the buffer pool

    // try to open the file

    Page apage = new Page();
    _firstDirPageId = null;
    if (_ftype == ORDINARY) {
      _firstDirPageId = get_file_entry(_fileName);
    }

    if (_firstDirPageId == null) {
      // file doesn't exist. First create it.
      _firstDirPageId = newPage(apage, 1);
      // check error
      if (_firstDirPageId == null) {
        throw new HFException(null, "can't new page");
      }

      add_file_entry(_fileName, _firstDirPageId);
      // check error(new exception: Could not add file entry

      THFPage firstDirPage = new THFPage();
      firstDirPage.init(_firstDirPageId, apage);
      PageId pageId = new PageId(INVALID_PAGE);

      firstDirPage.setNextPage(pageId);
      firstDirPage.setPrevPage(pageId);
      unpinPage(_firstDirPageId, true /*dirty*/);
    }
    _file_deleted = false;
    // ASSERTIONS:
    // - ALL private data members of class QuadrupleHeapfile are valid:
    //
    //  - _firstDirPageId valid
    //  - _fileName valid
    //  - no datapage pinned yet

  }// end of constructor

  private THFPage _newDatapage(DataPageInfo dpinfop) throws HFException,
      HFBufMgrException,
      IOException {
    Page apage = new Page();
    PageId pageId = new PageId();
    pageId = newPage(apage, 1);

    if (pageId == null) {
      throw new HFException(null, "can't new create page");
    }

    // initialize internal values of the new page:

    THFPage thfpage = new THFPage();
    thfpage.init(pageId, apage);

    dpinfop.pageId.pid = pageId.pid;
    dpinfop.recct = 0;
    dpinfop.availspace = thfpage.available_space();

    return thfpage;

  }// end of _newDatapage

  /* Internal QuadrupleHeapFile function (used in getQuadruple and updateQuadruple):
    returns pinned directory page and pinned data page of the specified
    user quadruple(qid) and true if quadruple is found.
    If the user quadruple cannot be found, return false.
 */
  private boolean _findDataPage(
      QID qid,
      PageId dirPageId, THFPage dirpage,
      PageId dataPageId, THFPage datapage,
      QID rpDataPageQid)
      throws Exception {
    PageId currentDirPageId = new PageId(_firstDirPageId.pid);

    THFPage currentDirPage = new THFPage();
    THFPage currentDataPage = new THFPage();
    QID currentDataPageQid = new QID();
    PageId nextDirPageId = new PageId();
    // datapageId is stored in dpinfo.pageId

    pinPage(currentDirPageId, currentDirPage, false/*read disk*/);

    Quadruple aquadruple = null;

    while (currentDirPageId.pid != INVALID_PAGE) {// Start While01
      // ASSERTIONS:
      //  currentDirPage, currentDirPageId valid and pinned and Locked.

      for (currentDataPageQid = currentDirPage.firstQuadruple();
          currentDataPageQid != null;
          currentDataPageQid = currentDirPage.nextQuadruple(currentDataPageQid)) {
        try {
          aquadruple = currentDirPage.getQuadruple(currentDataPageQid);
        } catch (InvalidSlotNumberException e)// check error! return false(done)
        {
          return false;
        }

        DataPageInfo dpinfo = new DataPageInfo(aquadruple);
        try {
          pinPage(dpinfo.pageId, currentDataPage, false/*Rddisk*/);

          //check error;need unpin currentDirPage
        } catch (Exception e) {
          unpinPage(currentDirPageId, false/*undirty*/);
          dirpage = null;
          datapage = null;
          throw e;
        }

        // ASSERTIONS:
        // - currentDataPage, currentDataPageQid, dpinfo valid
        // - currentDataPage pinned

        if (dpinfo.pageId.pid == qid.getPageNo().pid) {
          aquadruple = currentDataPage.returnQuadruple(qid);
          // found user's Quadruple on the current datapage which itself
          // is indexed on the current dirpage.  Return both of these.

          dirpage.setpage(currentDirPage.getpage());
          dirPageId.pid = currentDirPageId.pid;

          datapage.setpage(currentDataPage.getpage());
          dataPageId.pid = dpinfo.pageId.pid;

          rpDataPageQid.setPageNo(new PageId(currentDataPageQid.getPageNo().pid));
          rpDataPageQid.setSlotNo(currentDataPageQid.getSlotNo());
          return true;
        } else {
          // user Quadruple not found on this datapage; unpin it
          // and try the next one
          unpinPage(dpinfo.pageId, false /*undirty*/);
        }
      }

      // if we would have found the correct datapage on the current
      // directory page we would have already returned.
      // therefore:
      // read in next directory page:

      nextDirPageId = currentDirPage.getNextPage();
      try {
        unpinPage(currentDirPageId, false /*undirty*/);
      } catch (Exception e) {
        throw new HFException(e, "heapfile,_find,unpinpage failed");
      }

      currentDirPageId.pid = nextDirPageId.pid;
      if (currentDirPageId.pid != INVALID_PAGE) {
        pinPage(currentDirPageId, currentDirPage, false/*Rdisk*/);
        if (currentDirPage == null) {
          throw new HFException(null, "pinPage return null page");
        }
      }
    } // end of While01
    // checked all dir pages and all data pages; user Quadruple not found:(
    dirPageId.pid = dataPageId.pid = INVALID_PAGE;
    return false;

  }// end of _findDatapage

  public int getQuadrupleCnt() throws Exception {
    int answer = 0;
    PageId currentDirPageId = new PageId(_firstDirPageId.pid);
    PageId nextDirPageId = new PageId(0);

    THFPage currentDirPage = new THFPage();
    Page pageinbuffer = new Page();

    while (currentDirPageId.pid != INVALID_PAGE) {
      pinPage(currentDirPageId, currentDirPage, false);

      QID qid = new QID();
      Quadruple aquadruple;

      for (qid = currentDirPage.firstQuadruple(); qid != null;
          qid = currentDirPage.nextQuadruple(qid)) {
        aquadruple = currentDirPage.getQuadruple(qid);
        DataPageInfo dataPageInfo = new DataPageInfo(aquadruple);
        answer += dataPageInfo.recct;
      }
      // ASSERTIONS: no more Quadruple
      // - we have read all datapage Quadruples on
      //   the current directory page.

      nextDirPageId = currentDirPage.getNextPage();
      unpinPage(currentDirPageId, false);
      currentDirPageId.pid = nextDirPageId.pid;
    }

    // ASSERTIONS:
    // - if error, exceptions
    // - if end of heapfile reached: currentDirPageId == INVALID_PAGE
    // - if not yet end of heapfile: currentDirPageId valid

    return answer;
  }//end of getQuadrupleCnt

  /**
   * Insert quadruple into file, return its Qid.
   *
   * @param quadruplePtr pointer of the quadruple
   * @return the Qid of the quadruple
   * @throws InvalidSlotNumberException invalid slot number
   * @throws InvalidTupleSizeException  invalid tuple size
   * @throws SpaceNotAvailableException no space left
   * @throws HFException                heapfile exception
   * @throws HFBufMgrException          exception thrown from bufmgr layer
   * @throws HFDiskMgrException         exception thrown from diskmgr layer
   * @throws IOException                I/O errors
   */
  public QID insertQuadruple(byte[] quadruplePtr) throws Exception {
    int dpinfoLen = 0;
    int quadLen = quadruplePtr.length;
    boolean found;

    QID currentDataPageQid = new QID();
    Page pageinbuffer = new Page();

    THFPage currentDirPage = new THFPage();
    THFPage currentDataPage = new THFPage();

    THFPage nextDirPage = new THFPage();
    PageId currentDirPageId = new PageId(_firstDirPageId.pid);
    PageId nextDirPageId = new PageId();  // OK

    pinPage(currentDirPageId, currentDirPage, false/*Rdisk*/);

    found = false;
    Quadruple quadruple;
    DataPageInfo dpinfo = new DataPageInfo();

    while (found == false) {//Start While01
      // look for suitable dpinfo-struct
      for (currentDataPageQid = currentDirPage.firstQuadruple();
          currentDataPageQid != null;
          currentDataPageQid =
              currentDirPage.nextQuadruple(currentDataPageQid)) {
        quadruple = currentDirPage.getQuadruple(currentDataPageQid);
        dpinfo = new DataPageInfo(quadruple);
        // need check the Quadruple length == QuadDataPageInfo'slength

        if (quadLen <= dpinfo.availspace) {
          found = true;
          break;
        }

      }
      // two cases:
      // (1) found == true:
      //     currentDirPage has a datapageQuadruple which can accomodate
      //     the Quadruple which we have to insert
      // (2) found == false:
      //     there is no datapageQuadruple on the current directory page
      //     whose corresponding datapage has enough space free
      //     several subcases: see below
      if (found == false) {//Start IF01
        // case (2)

        //System.out.println("no datapageQuadruple on the current directory is OK");
        //System.out.println("dirpage availspace "+currentDirPage.available_space());

        // on the current directory page is no datapageQuadruple which has
        // enough free space
        //
        // two cases:
        //
        // - (2.1) (currentDirPage->available_space() >= sizeof(DataPageInfo):
        //         if there is enough space on the current directory page
        //         to accomodate a new datapageQuadruple (type DataPageInfo),
        //         then insert a new DataPageInfo on the current directory
        //         page
        // - (2.2) (currentDirPage->available_space() <= sizeof(DataPageInfo):
        //         look at the next directory page, if necessary, create it.
        if (currentDirPage.available_space() >= dpinfo.size) {
          //Start IF02
          // case (2.1) : add a new data page Quadruple into the
          //              current directory page
          currentDataPage = _newDatapage(dpinfo);
          // currentDataPage is pinned! and dpinfo->pageId is also locked
          // in the exclusive mode
          // didn't check if currentDataPage==NULL, auto exception

          // currentDataPage is pinned: insert its Quadruple
          // calling a HFPage function

          Tuple tuple = dpinfo.convertToTuple();
          byte[] tmpdata = tuple.getTupleByteArray();

          currentDataPageQid = currentDirPage.insertQuadruple(tmpdata);

          QID tmpQid = currentDirPage.firstQuadruple();

          if (currentDataPageQid == null) {
            throw new HFException(null, "no space to insert Quadruple.");
          }

          // end the loop, because a new datapage with its Quadruple
          // in the current directorypage was created and inserted into
          // the heapfile; the new datapage has enough space for the
          // Quadruple which the user wants to insert

          found = true;
        }//end of IF02
        else {//Start else 02
          // case (2.2)
          nextDirPageId = currentDirPage.getNextPage();
          // two sub-cases:
          //
          // (2.2.1) nextDirPageId != INVALID_PAGE:
          //         get the next directory page from the buffer manager
          //         and do another look
          // (2.2.2) nextDirPageId == INVALID_PAGE:
          //         append a new directory page at the end of the current
          //         page and then do another loop

          if (nextDirPageId.pid != INVALID_PAGE) {//Start IF03
            // case (2.2.1): there is another directory page:
            unpinPage(currentDirPageId, false);
            currentDirPageId.pid = nextDirPageId.pid;

            pinPage(currentDirPageId,
                currentDirPage, false);
            // now go back to the beginning of the outer while-loop and
            // search on the current directory page for a suitable datapage
          }//End of IF03
          else {//Start Else03
            // case (2.2): append a new directory page after currentDirPage
            //             since it is the last directory page

            nextDirPageId = newPage(pageinbuffer, 1);
            // need check error!
            if (nextDirPageId == null) {
              throw new HFException(null, "can't new pae");
            }

            // initialize new directory page
            nextDirPage.init(nextDirPageId, pageinbuffer);
            PageId temppid = new PageId(INVALID_PAGE);
            nextDirPage.setNextPage(temppid);
            nextDirPage.setPrevPage(currentDirPageId);

            // update current directory page and unpin it
            // currentDirPage is already locked in the Exclusive mode
            currentDirPage.setNextPage(nextDirPageId);
            unpinPage(currentDirPageId, true/*dirty*/);

            currentDirPageId.pid = nextDirPageId.pid;
            currentDirPage = new THFPage(nextDirPage);

            // remark that MINIBASE_BM->newPage already
            // pinned the new directory page!
            // Now back to the beginning of the while-loop, using the
            // newly created directory page.

          }//End of else03
        }// End of else02
        // ASSERTIONS:
        // - if found == true: search will end and see assertions below
        // - if found == false: currentDirPage, currentDirPageId
        //   valid and pinned
      }//end IF01
      else {//Start else01
        // found == true:
        // we have found a datapage with enough space,
        // but we have not yet pinned the datapage:

        // ASSERTIONS:
        // - dpinfo valid

        pinPage(dpinfo.pageId, currentDataPage, false);
        //currentDataPage.openHFpage(pageinbuffer);

      }//End else01
    }//end of While01
    // ASSERTIONS:
    // - currentDirPageId, currentDirPage valid and pinned
    // - dpinfo.pageId, currentDataPageQid valid
    // - currentDataPage is pinned!
    if ((dpinfo.pageId).pid == INVALID_PAGE) // check error!
    {
      throw new HFException(null, "invalid PageId");
    }

    if (!(currentDataPage.available_space() >= quadLen)) {
      throw new SpaceNotAvailableException(null, "no available space");
    }

    if (currentDataPage == null) {
      throw new HFException(null, "can't find Data page");
    }

    QID qid;
    qid = currentDataPage.insertQuadruple(quadruplePtr);

    dpinfo.recct++;
    dpinfo.availspace = currentDataPage.available_space();

    unpinPage(dpinfo.pageId, true /* = DIRTY */);

    // DataPage is now released
    quadruple = currentDirPage.returnQuadruple(currentDataPageQid);
    DataPageInfo dpinfo_ondirpage = new DataPageInfo(quadruple);

    dpinfo_ondirpage.availspace = dpinfo.availspace;
    dpinfo_ondirpage.recct = dpinfo.recct;
    dpinfo_ondirpage.pageId.pid = dpinfo.pageId.pid;
    dpinfo_ondirpage.flushToTuple();

    unpinPage(currentDirPageId, true /* = DIRTY */);
    return qid;

  }//end of insert Quadruple

  /**
   * Delete quadruple from file with given Qid.
   *
   * @return true Quadruple deleted  false:Quadruple not found
   * @throws InvalidSlotNumberException invalid slot number
   * @throws InvalidTupleSizeException  invalid tuple size
   * @throws HFException                heapfile exception
   * @throws HFBufMgrException          exception thrown from bufmgr layer
   * @throws HFDiskMgrException         exception thrown from diskmgr layer
   * @throws Exception                  other exception
   */
  public boolean deleteQuadruple(QID qid) throws Exception {
    boolean status;
    THFPage currentDirPage = new THFPage();
    PageId currentDirPageId = new PageId();
    THFPage currentDataPage = new THFPage();
    PageId currentDataPageId = new PageId();
    QID currentDataPageQid = new QID();

    status = _findDataPage(qid,
        currentDirPageId, currentDirPage,
        currentDataPageId, currentDataPage,
        currentDataPageQid);

    if (status != true) {
      return status;  // Quadruple not found
    }

    // ASSERTIONS:
    // - currentDirPage, currentDirPageId valid and pinned
    // - currentDataPage, currentDataPageid valid and pinned

    // get datapageinfo from the current directory page:

    Quadruple quadruple;

    quadruple = currentDirPage.returnQuadruple(currentDataPageQid);
    DataPageInfo pdpinfo = new DataPageInfo(quadruple);

    // delete the quadruple on the datapage
    currentDataPage.deleteQuadruple(qid);

    pdpinfo.recct--;
    pdpinfo.flushToTuple();  //Write to the buffer pool

    if (pdpinfo.recct >= 1) {
      // more Quadruples remain on datapage so it still hangs around.
      // we just need to modify its directory entry

      pdpinfo.availspace = currentDataPage.available_space();
      pdpinfo.flushToTuple();
      unpinPage(currentDataPageId, true /* = DIRTY*/);

      unpinPage(currentDirPageId, true /* = DIRTY */);
    } else {
      // the quadruple is already deleted:
      // we're removing the last quadruple on datapage so free datapage
      // also, free the directory page if
      //   a) it's not the first directory page, and
      //   b) we've removed the last DataPageInfo quadruple on it.

      // delete empty datapage: (does it get unpinned automatically? -NO, Ranjani)
      unpinPage(currentDataPageId, false /*undirty*/);

      freePage(currentDataPageId);

      // delete corresponding QuadDataPageInfo-entry on the directory page:
      // currentDataPageQid points to datapage (from for loop above)

      currentDirPage.deleteQuadruple(currentDataPageQid);

      // ASSERTIONS:
      // - currentDataPage, currentDataPageId invalid
      // - empty datapage unpinned and deleted

      // now check whether the directory page is empty:

      currentDataPageQid = currentDirPage.firstQuadruple();

      // st == OK: we still found a Quaddatapageinfo quadruple on this directory page
      PageId pageId;
      pageId = currentDirPage.getPrevPage();
      if ((currentDataPageQid == null) && (pageId.pid != INVALID_PAGE)) {
        // the directory-page is not the first directory page and it is empty:
        // delete it

        // point previous page around deleted page:

        THFPage prevDirPage = new THFPage();
        pinPage(pageId, prevDirPage, false);

        pageId = currentDirPage.getNextPage();
        prevDirPage.setNextPage(pageId);
        pageId = currentDirPage.getPrevPage();
        unpinPage(pageId, true /* = DIRTY */);

        // set prevPage-pointer of next Page
        pageId = currentDirPage.getNextPage();
        if (pageId.pid != INVALID_PAGE) {
          THFPage nextDirPage = new THFPage();
          pageId = currentDirPage.getNextPage();
          pinPage(pageId, nextDirPage, false);

          //nextDirPage.openHFpage(apage);

          pageId = currentDirPage.getPrevPage();
          nextDirPage.setPrevPage(pageId);
          pageId = currentDirPage.getNextPage();
          unpinPage(pageId, true /* = DIRTY */);
        }

        // delete empty directory page: (automatically unpinned?)
        unpinPage(currentDirPageId, false/*undirty*/);
        freePage(currentDirPageId);
      } else {
        // either (the directory page has at least one more datapageQuadruple
        // entry) or (it is the first directory page):
        // in both cases we do not delete it, but we have to unpin it:

        unpinPage(currentDirPageId, true /* == DIRTY */);
      }
    }
    return true;
  }

  /**
   * Updates the specified Quadruple in the heapfile.
   *
   * @param qid:          the Quadruple which needs update
   * @param newquadruple: the new content of the Quadruple
   * @return ture:update success   false: can't find the Quadruple
   * @throws InvalidSlotNumberException invalid slot number
   * @throws InvalidUpdateException     invalid update on Quadruple
   * @throws InvalidTupleSizeException  invalid tuple size
   * @throws HFException                heapfile exception
   * @throws HFBufMgrException          exception thrown from bufmgr layer
   * @throws HFDiskMgrException         exception thrown from diskmgr layer
   * @throws Exception                  other exception
   */
  public boolean updateQuadruple(QID qid, Quadruple newquadruple)
      throws InvalidUpdateException,
      InvalidTupleSizeException,
      HFBufMgrException,
      Exception {
    boolean status;
    THFPage dirPage = new THFPage();
    PageId currentDirPageId = new PageId();
    THFPage dataPage = new THFPage();
    PageId currentDataPageId = new PageId();
    QID currentDataPageQid = new QID();

    status = _findDataPage(qid,
        currentDirPageId, dirPage,
        currentDataPageId, dataPage,
        currentDataPageQid);

    if (status != true) {
      return status;  // Quadruple not found
    }
    Quadruple quadruple = new Quadruple();
    quadruple = dataPage.returnQuadruple(qid);
    quadruple.setDefaultHeader();

    // Assume update a Quadruple with a Quadruple whose length is equal to
    // the original Quadruple

    if (newquadruple.size() != quadruple.size()) {
      unpinPage(currentDataPageId, false /*undirty*/);
      unpinPage(currentDirPageId, false /*undirty*/);

      throw new InvalidUpdateException(null, "invalid Quadruple update");
    }

    // new copy of this Quadruple fits in old space;
    quadruple.quadrupleCopy(newquadruple);
    unpinPage(currentDataPageId, true /* = DIRTY */);

    unpinPage(currentDirPageId, false /*undirty*/);

    return true;
  }

  /**
   * Read quadruple from file, returning pointer and length.
   *
   * @param qid Quadruple ID
   * @return a Quadruple. if Quadruple==null, no more Quadruple
   * @throws InvalidSlotNumberException invalid slot number
   * @throws InvalidTupleSizeException  invalid tuple size
   * @throws SpaceNotAvailableException no space left
   * @throws HFException                heapfile exception
   * @throws HFBufMgrException          exception thrown from bufmgr layer
   * @throws HFDiskMgrException         exception thrown from diskmgr layer
   * @throws Exception                  other exception
   */
  public Quadruple getQuadruple(QID qid)
      throws HFBufMgrException,
      Exception {
    boolean status;
    THFPage dirPage = new THFPage();
    PageId currentDirPageId = new PageId();
    THFPage dataPage = new THFPage();
    PageId currentDataPageId = new PageId();
    QID currentDataPageQid = new QID();

    status = _findDataPage(qid,
        currentDirPageId, dirPage,
        currentDataPageId, dataPage,
        currentDataPageQid);

    if (status != true) {
      return null; // Quadruple not found
    }

    Quadruple quadruple = dataPage.getQuadruple(qid);

    /*
     * getQuadruple has copied the contents of qid into recPtr and fixed up
     * quadLen also.  We simply have to unpin dirpage and datapage which
     * were originally pinned by _findDataPage.
     */

    unpinPage(currentDataPageId, false /*undirty*/);

    unpinPage(currentDirPageId, false /*undirty*/);

    return quadruple;  //(true?)OK, but the caller need check if atuple==NULL
  }

  /**
   * Delete file from the DataBase
   */
  @Override
  public void deleteFile() throws Exception {

    if (_file_deleted) {
      throw new FileAlreadyDeletedException(null, "file alread deleted");
    }

    // Mark the deleted flag (even if it doesn't get all the way done).
    _file_deleted = true;

    // Deallocate all data pages
    PageId currentDirPageId = new PageId();
    currentDirPageId.pid = _firstDirPageId.pid;
    PageId nextDirPageId = new PageId();
    nextDirPageId.pid = 0;
    Page pageinbuffer = new Page();
    THFPage currentDirPage = new THFPage();
    Quadruple quadruple;

    pinPage(currentDirPageId, currentDirPage, false);
    //currentDirPage.openHFpage(pageinbuffer);

    QID qid = new QID();
    while (currentDirPageId.pid != INVALID_PAGE) {
      for (qid = currentDirPage.firstQuadruple();
          qid != null;
          qid = currentDirPage.nextQuadruple(qid)) {
        quadruple = currentDirPage.getQuadruple(qid);
        DataPageInfo dpinfo = new DataPageInfo(quadruple);
        //int dpinfoLen = aQuadruple.length;

        freePage(dpinfo.pageId);
      }
      // ASSERTIONS:
      // - we have freePage()'d all data pages referenced by
      // the current directory page.

      nextDirPageId = currentDirPage.getNextPage();
      freePage(currentDirPageId);

      currentDirPageId.pid = nextDirPageId.pid;
      if (nextDirPageId.pid != INVALID_PAGE) {
        pinPage(currentDirPageId, currentDirPage, false);
        //currentDirPage.openHFpage(pageinbuffer);
      }
    }

    delete_file_entry(_fileName);
  }

  /**
   * Creates a new TScan Object and returns it.
   * @return TScan
   * @throws InvalidTupleSizeException
   * @throws IOException
   */
  public TScan openTScan() throws InvalidTupleSizeException, IOException {
    TScan newscan = new TScan(this);
    return newscan;

  }

  /**
   * Returns firstDirPageId from the HeapFile class.
   * @return _firstDirPageId
   */
  public PageId getFirstDirPageId() {
    return _firstDirPageId;
  }

}
