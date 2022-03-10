package quadrupleheap;


import diskmgr.Page;
import global.GlobalConst;
import global.IQID;
import global.PageId;
import global.QID;
import global.RID;
import global.SystemDefs;
import global.SystemDefsrdfDB;
import heap.*;
import heap.FileAlreadyDeletedException;
import heap.HFBufMgrException;
import heap.HFDiskMgrException;
import heap.HFException;
import heap.HFPage;
import heap.Heapfile;
import heap.InvalidSlotNumberException;
import heap.InvalidTupleSizeException;
import heap.InvalidUpdateException;
import heap.Quadruple;

import heap.SpaceNotAvailableException;

import java.io.IOException;


interface Filetype {

  int TEMP = 0;
  int ORDINARY = 1;
} // end of Filetype

public class QuadrupleHeapFile implements Filetype, GlobalConst {

  private static int tempfilecount = 0;
  PageId _firstDirPageId;   // page number of header page
  int _ftype;
  private boolean _file_deleted;
  private String _fileName;


  /** Initialize.  A null name produces a temporary heapfile which will be
   * deleted by the destructor.  If the name already denotes a file, the
   * file is opened; otherwise, a new empty file is created.
   *
   * @exception HFException heapfile exception
   * @exception HFBufMgrException exception thrown from bufmgr layer
   * @exception HFDiskMgrException exception thrown from diskmgr layer
   * @exception IOException I/O errors
   */
  public QuadrupleHeapFile(String name)
      throws HFDiskMgrException, HFException, HFBufMgrException, IOException {
    // Give us a prayer of destructing cleanly if construction fails.
    _file_deleted = true;
    _fileName = null;
    if (name == null) {
      // If the name is NULL, allocate a temporary name
      // and no logging is required.
      _fileName = "tempHeapFile";
      String useId = new String("user.name");
      String userAccName;
      userAccName = System.getProperty(useId);
      _fileName = _fileName + userAccName;

      String filenum = Integer.toString(tempfilecount);
      _fileName = _fileName + filenum;
      _ftype = TEMP;
      tempfilecount++;
    }
    else {
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
    // - ALL private data members of class Heapfile are valid:
    //
    //  - _firstDirPageId valid
    //  - _fileName valid
    //  - no datapage pinned yet

  }// end of constructor

  private THFPage _newDatapage(DataPageInfo dpinfop) throws HFException,
      HFBufMgrException,
      HFDiskMgrException,
      IOException{
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
       IQID qid,
       PageId dirPageId, THFPage dirpage,
       PageId dataPageId, THFPage datapage,
       IQID rpDataPageQid)
       throws InvalidSlotNumberException,
       InvalidTupleSizeException,
       HFException,
       HFBufMgrException,
       HFDiskMgrException,
       Exception {
     PageId currentDirPageId = new PageId(_firstDirPageId.pid);

     THFPage currentDirPage = new THFPage();
     THFPage currentDataPage = new THFPage();
     IQID currentDataPageQid = new QID();
     PageId nextDirPageId = new PageId();
     // datapageId is stored in dpinfo.pageId

     pinPage(currentDirPageId, currentDirPage, false/*read disk*/);

     Quadruple aquadruple = new Quadruple();

     while (currentDirPageId.pid != INVALID_PAGE) {// Start While01
       // ASSERTIONS:
       //  currentDirPage, currentDirPageId valid and pinned and Locked.

       for (currentDataPageQid = currentDirPage.firstQuadruple();
           currentDataPageQid != null;
           currentDataPageQid = currentDirPage.nextQuadruple(currentDataPageQid)) {
         try {
           aquadruple = currentDirPage.returnQuadruple(currentDataPageQid);
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
         // - currentDataPage, currentDataPageRid, dpinfo valid
         // - currentDataPage pinned

         if (dpinfo.pageId.pid == qid.getPageNo().pid) {
           aquadruple = currentDataPage.returnQuadruple(qid);
           // found user's Quadruple on the current datapage which itself
           // is indexed on the current dirpage.  Return both of these.

           dirpage.setpage(currentDirPage.getpage());
           dirPageId.pid = currentDirPageId.pid;

           datapage.setpage(currentDataPage.getpage());
           dataPageId.pid = dpinfo.pageId.pid;

           rpDataPageQid.getPageNo().pid = currentDataPageQid.getPageNo().pid;
           rpDataPageQid.setSlotNo(currentDataPageQid.getSlotNo());
           return true;
         } else {
           // user record not found on this datapage; unpin it
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
     // checked all dir pages and all data pages; user record not found:(
     dirPageId.pid = dataPageId.pid = INVALID_PAGE;
     return false;

   }// end of _findDatapage

  public int getQuadrupleCnt() throws Exception {
    int answer = 0;
    PageId currentDirPageId = new PageId(_firstDirPageId.pid);
    PageId nextDirPageId = new PageId(0);

    THFPage currentDirPage = new THFPage();
    Page pageinbuffer = new Page();

    while (currentDirPageId.pid != INVALID_PAGE){
      pinPage(currentDirPageId, currentDirPage, false);

      IQID qid = new QID();
      Quadruple aquadruple;

      for(qid=currentDirPage.firstQuadruple();qid!=null;qid=currentDirPage.nextQuadruple(qid))
      {
        aquadruple= currentDirPage.getQuadruple(qid);
        DataPageInfo dataPageInfo=new DataPageInfo(aquadruple);
        answer+=dataPageInfo.recct;
      }
      // ASSERTIONS: no more record
      // - we have read all datapage records on
      //   the current directory page.

      nextDirPageId=currentDirPage.getNextPage();
      unpinPage(currentDirPageId,false);
      currentDirPageId.pid=nextDirPageId.pid;
    }

    // ASSERTIONS:
    // - if error, exceptions
    // - if end of heapfile reached: currentDirPageId == INVALID_PAGE
    // - if not yet end of heapfile: currentDirPageId valid

    return answer;
  }//end of getRecCnt

  /** Insert quadruple into file, return its Qid.
   *
   * @param quadruplePtr pointer of the quadruple
   *
   *
   * @exception InvalidSlotNumberException invalid slot number
   * @exception InvalidTupleSizeException invalid tuple size
   * @exception SpaceNotAvailableException no space left
   * @exception HFException heapfile exception
   * @exception HFBufMgrException exception thrown from bufmgr layer
   * @exception HFDiskMgrException exception thrown from diskmgr layer
   * @exception IOException I/O errors
   *
   * @return the Qid of the quadruple
   */
  public IQID insertQuadruple(byte[] quadruplePtr) throws Exception {
    int dpinfoLen = 0;
    int quadLen = quadruplePtr.length;
    boolean found;

    IQID currentDataPageQid = new QID();
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

    while (found == false){//Start While01
      // look for suitable dpinfo-struct
      for (currentDataPageQid = currentDirPage.firstQuadruple();
          currentDataPageQid != null;
          currentDataPageQid =
              currentDirPage.nextQuadruple(currentDataPageQid))
      {
        quadruple=currentDirPage.getQuadruple(currentDataPageQid);
        dpinfo=new DataPageInfo(quadruple);
        // need check the record length == DataPageInfo'slength

        if (quadLen <= dpinfo.availspace) {
          found = true;
          break;
        }

      }
      // two cases:
      // (1) found == true:
      //     currentDirPage has a datapagerecord which can accomodate
      //     the record which we have to insert
      // (2) found == false:
      //     there is no datapagerecord on the current directory page
      //     whose corresponding datapage has enough space free
      //     several subcases: see below
      if (found == false){//Start IF01
        // case (2)

        //System.out.println("no datapagerecord on the current directory is OK");
        //System.out.println("dirpage availspace "+currentDirPage.available_space());

        // on the current directory page is no datapagerecord which has
        // enough free space
        //
        // two cases:
        //
        // - (2.1) (currentDirPage->available_space() >= sizeof(DataPageInfo):
        //         if there is enough space on the current directory page
        //         to accomodate a new datapagerecord (type DataPageInfo),
        //         then insert a new DataPageInfo on the current directory
        //         page
        // - (2.2) (currentDirPage->available_space() <= sizeof(DataPageInfo):
        //         look at the next directory page, if necessary, create it.
        if (currentDirPage.available_space() >= dpinfo.size){
          //Start IF02
          // case (2.1) : add a new data page record into the
          //              current directory page
          currentDataPage = _newDatapage(dpinfo);
          // currentDataPage is pinned! and dpinfo->pageId is also locked
          // in the exclusive mode
          // didn't check if currentDataPage==NULL, auto exception

          // currentDataPage is pinned: insert its record
          // calling a HFPage function

          quadruple=dpinfo.convertToQuadruple();
          byte[] tmpdata=quadruple.getQuadrupleByteArray();

          currentDataPageQid=currentDirPage.insertQuadruple(tmpdata);

          IQID tmpQid=currentDirPage.firstQuadruple();

          if(currentDataPageQid==null)
          {
            throw new HFException(null, "no space to insert Quadruple.");
          }

          // end the loop, because a new datapage with its record
          // in the current directorypage was created and inserted into
          // the heapfile; the new datapage has enough space for the
          // record which the user wants to insert

          found = true;
        }//end of IF02
        else{//Start else 02
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

          if (nextDirPageId.pid != INVALID_PAGE){//Start IF03
            // case (2.2.1): there is another directory page:
            unpinPage(currentDirPageId, false);
            currentDirPageId.pid = nextDirPageId.pid;

            pinPage(currentDirPageId,
                currentDirPage, false);
            // now go back to the beginning of the outer while-loop and
            // search on the current directory page for a suitable datapage
          }//End of IF03
          else{//Start Else03
            // case (2.2): append a new directory page after currentDirPage
            //             since it is the last directory page

            nextDirPageId = newPage(pageinbuffer, 1);
            // need check error!
            if (nextDirPageId == null) {
              throw new HFException(null, "can't new pae");
            }

            // initialize new directory page
            nextDirPage.init(nextDirPageId,pageinbuffer);
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
      else{//Start else01
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
    // - dpinfo.pageId, currentDataPageRid valid
    // - currentDataPage is pinned!
    if((dpinfo.pageId).pid == INVALID_PAGE) // check error!
    {
      throw new HFException(null, "invalid PageId");
    }

    if (!(currentDataPage.available_space() >= quadLen)) {
      throw new SpaceNotAvailableException(null, "no available space");
    }

    if (currentDataPage == null) {
      throw new HFException(null, "can't find Data page");
    }

    IQID qid;
    qid=currentDataPage.insertQuadruple(quadruplePtr);

    dpinfo.recct++;
    dpinfo.availspace = currentDataPage.available_space();

    unpinPage(dpinfo.pageId, true /* = DIRTY */);

    // DataPage is now released
    quadruple=currentDirPage.returnQuadruple(currentDataPageQid);
    DataPageInfo dpinfo_ondirpage=new DataPageInfo(quadruple);

    dpinfo_ondirpage.availspace=dpinfo.availspace;
    dpinfo_ondirpage.recct=dpinfo.recct;
    dpinfo_ondirpage.pageId.pid=dpinfo.pageId.pid;
    dpinfo_ondirpage.flushToQuadruple();

    unpinPage(currentDirPageId, true /* = DIRTY */);
    return qid;

  }//end of insert record

  /** Delete quadruple from file with given Qid.
   *
   * @exception InvalidSlotNumberException invalid slot number
   * @exception InvalidTupleSizeException invalid tuple size
   * @exception HFException heapfile exception
   * @exception HFBufMgrException exception thrown from bufmgr layer
   * @exception HFDiskMgrException exception thrown from diskmgr layer
   * @exception Exception other exception
   *
   * @return true Quadruple deleted  false:Quadruple not found
   */
  public boolean deleteQuadruple(IQID qid) throws Exception {
    boolean status;
    THFPage currentDirPage = new THFPage();
    PageId currentDirPageId = new PageId();
    THFPage currentDataPage = new THFPage();
    PageId currentDataPageId = new PageId();
    IQID currentDataPageQid = new QID();

    status = _findDataPage(qid,
        currentDirPageId, currentDirPage,
        currentDataPageId, currentDataPage,
        currentDataPageQid);

    if (status != true) {
      return status;  // record not found
    }

    // ASSERTIONS:
    // - currentDirPage, currentDirPageId valid and pinned
    // - currentDataPage, currentDataPageid valid and pinned

    // get datapageinfo from the current directory page:

    Quadruple quadruple;

    quadruple=currentDirPage.returnQuadruple(currentDataPageQid);
    DataPageInfo pdpinfo = new DataPageInfo(quadruple);

    // delete the quadruple on the datapage
    currentDataPage.deleteQuadruple(qid);

    pdpinfo.recct--;
    pdpinfo.flushToQuadruple();  //Write to the buffer pool

    if (pdpinfo.recct >= 1) {
      // more records remain on datapage so it still hangs around.
      // we just need to modify its directory entry

      pdpinfo.availspace = currentDataPage.available_space();
      pdpinfo.flushToQuadruple();
      unpinPage(currentDataPageId, true /* = DIRTY*/);

      unpinPage(currentDirPageId, true /* = DIRTY */);
    }
    else {
      // the quadruple is already deleted:
      // we're removing the last quadruple on datapage so free datapage
      // also, free the directory page if
      //   a) it's not the first directory page, and
      //   b) we've removed the last DataPageInfo quadruple on it.

      // delete empty datapage: (does it get unpinned automatically? -NO, Ranjani)
      unpinPage(currentDataPageId, false /*undirty*/);

      freePage(currentDataPageId);

      // delete corresponding DataPageInfo-entry on the directory page:
      // currentDataPageQid points to datapage (from for loop above)

      currentDirPage.deleteQuadruple(currentDataPageQid);

      // ASSERTIONS:
      // - currentDataPage, currentDataPageId invalid
      // - empty datapage unpinned and deleted

      // now check whether the directory page is empty:

      currentDataPageQid = currentDirPage.firstQuadruple();

      // st == OK: we still found a datapageinfo quadruple on this directory page
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
        // either (the directory page has at least one more datapagerecord
        // entry) or (it is the first directory page):
        // in both cases we do not delete it, but we have to unpin it:

        unpinPage(currentDirPageId, true /* == DIRTY */);
      }
    }
    return true;
  }

  /** Updates the specified Quadruple in the heapfile.
   * @param qid: the record which needs update
   * @param newquadruple: the new content of the record
   *
   * @exception InvalidSlotNumberException invalid slot number
   * @exception InvalidUpdateException invalid update on record
   * @exception InvalidTupleSizeException invalid tuple size
   * @exception HFException heapfile exception
   * @exception HFBufMgrException exception thrown from bufmgr layer
   * @exception HFDiskMgrException exception thrown from diskmgr layer
   * @exception Exception other exception
   * @return ture:update success   false: can't find the record
   */
  public boolean  updateQuadruple(IQID qid, Quadruple newquadruple)
      throws InvalidSlotNumberException,
      InvalidUpdateException,
      InvalidTupleSizeException,
      HFException,
      HFDiskMgrException,
      HFBufMgrException,
      Exception {
    boolean status;
    THFPage dirPage = new THFPage();
    PageId currentDirPageId = new PageId();
    THFPage dataPage = new THFPage();
    PageId currentDataPageId = new PageId();
    IQID currentDataPageRid = new QID();

    status = _findDataPage(qid,
        currentDirPageId, dirPage,
        currentDataPageId, dataPage,
        currentDataPageRid);

    if (status != true) {
      return status;  // record not found
    }
    Quadruple quadruple = new Quadruple();
    quadruple = dataPage.returnQuadruple(qid);

    // Assume update a Quadruple with a record whose length is equal to
    // the original record

    if (newquadruple.size() != quadruple.size()) {
      unpinPage(currentDataPageId, false /*undirty*/);
      unpinPage(currentDirPageId, false /*undirty*/);

      throw new InvalidUpdateException(null, "invalid record update");
    }

    // new copy of this Quadruple fits in old space;
    quadruple.quadrupleCopy(newquadruple);
    unpinPage(currentDataPageId, true /* = DIRTY */);

    unpinPage(currentDirPageId, false /*undirty*/);

    return true;
  }

  /** Read quadruple from file, returning pointer and length.
   * @param qid Quadruple ID
   *
   * @exception InvalidSlotNumberException invalid slot number
   * @exception InvalidTupleSizeException invalid tuple size
   * @exception SpaceNotAvailableException no space left
   * @exception HFException heapfile exception
   * @exception HFBufMgrException exception thrown from bufmgr layer
   * @exception HFDiskMgrException exception thrown from diskmgr layer
   * @exception Exception other exception
   *
   * @return a Quadruple. if Quadruple==null, no more Quadruple
   */
  public Quadruple getQuadruple(IQID qid)
      throws InvalidSlotNumberException,
      InvalidTupleSizeException,
      HFException,
      HFDiskMgrException,
      HFBufMgrException,
      Exception {
    boolean status;
    THFPage dirPage = new THFPage();
    PageId currentDirPageId = new PageId();
    THFPage dataPage = new THFPage();
    PageId currentDataPageId = new PageId();
    IQID currentDataPageRid = new QID();

    status = _findDataPage(qid,
        currentDirPageId, dirPage,
        currentDataPageId, dataPage,
        currentDataPageRid);

    if (status != true) {
      return null; // record not found
    }

    Quadruple quadruple = new Quadruple();
    quadruple = dataPage.getQuadruple(qid);

    /*
     * getQuadruple has copied the contents of qid into recPtr and fixed up
     * quadLen also.  We simply have to unpin dirpage and datapage which
     * were originally pinned by _findDataPage.
     */

    unpinPage(currentDataPageId, false /*undirty*/);

    unpinPage(currentDirPageId, false /*undirty*/);

    return quadruple;  //(true?)OK, but the caller need check if atuple==NULL
  }

  /** Delete the file from the database.
   *
   * @exception InvalidSlotNumberException invalid slot number
   * @exception InvalidTupleSizeException invalid tuple size
   * @exception FileAlreadyDeletedException file is deleted already
   * @exception HFBufMgrException exception thrown from bufmgr layer
   * @exception HFDiskMgrException exception thrown from diskmgr layer
   * @exception IOException I/O errors
   */
  public void deleteFile()
      throws Exception {
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

    IQID qid = new QID();
    while (currentDirPageId.pid != INVALID_PAGE) {
      for (qid = currentDirPage.firstQuadruple();
          qid != null;
          qid = currentDirPage.nextQuadruple(qid)) {
        quadruple = currentDirPage.getQuadruple(qid);
        DataPageInfo dpinfo = new DataPageInfo(quadruple);
        //int dpinfoLen = arecord.length;

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

  private void delete_file_entry(String fileName) throws HFDiskMgrException {
    try {
      SystemDefs.JavabaseDB.delete_file_entry(fileName);
    } catch (Exception e) {
      throw new HFDiskMgrException(e, "Heapfile.java: delete_file_entry() failed");
    }
  }


  private void freePage(PageId pageno) throws HFBufMgrException {
    try {
      SystemDefsrdfDB.JavabaseBM.freePage(pageno);
    } catch (Exception e) {
      throw new HFBufMgrException(e, "QuadrupleHeapfile.java: freePage() failed");
    }
  }


  private void pinPage(PageId pageNo, THFPage apage, boolean dirty)
      throws HFBufMgrException {
    try {
      Page page=new Page(apage.getpage());
      SystemDefsrdfDB.JavabaseBM.pinPage(pageNo, page, dirty);
    } catch (Exception e) {
      throw new HFBufMgrException(e, "Heapfile.java: pinPage() failed");
    }

  }


  private void unpinPage(PageId pageNo, boolean dirty)
      throws HFBufMgrException{
    try {
      SystemDefsrdfDB.JavabaseBM.unpinPage(pageNo, dirty);
    } catch (Exception e) {
      throw new HFBufMgrException(e, "Heapfile.java: unpinPage() failed");
    }

  }// end of unpinPage

  private PageId newPage(Page apage, int num)
      throws HFBufMgrException{

    PageId tmpId = new PageId();
    try {
      tmpId = SystemDefs.JavabaseBM.newPage(apage, num);
    } catch (Exception e) {
      throw new HFBufMgrException(e, "Heapfile.java: newPage() failed");
    }

    return tmpId;

  }// end of newPage

  private PageId get_file_entry(String fileName)
      throws HFDiskMgrException {
    PageId tmpId = new PageId();

    try {
      tmpId = SystemDefsrdfDB.JavabaserdfDB.get_file_entry(fileName);
    } catch (Exception e) {
      throw new HFDiskMgrException(e, "Heapfile.java: get_file_entry() failed");
    }

    return tmpId;


  }

  private void add_file_entry(String fileName, PageId pageNo)
      throws HFDiskMgrException{
    try {
      SystemDefsrdfDB.JavabaserdfDB.add_file_entry(fileName, pageNo);
    } catch (Exception e) {
      throw new HFDiskMgrException(e, "Heapfile.java: add_file_entry() failed");
    }

  }


  public TScan openScan() throws InvalidTupleSizeException, IOException {
    TScan newscan = new TScan(this);
    return newscan;

  }


}