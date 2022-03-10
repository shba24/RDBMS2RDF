package heap.labelheap;

import diskmgr.Page;
import global.GlobalConst;
import global.LID;
import global.PageId;
import global.SystemDefs;
import heap.*;

import java.io.IOException;

interface Filetype {
    int TEMP = 0;
    int ORDINARY = 1;
} // end of Filetype

public class LabelHeapFile extends Heapfile {

    /**
     * Initialize.  A null name produces a temporary heapfile which will be
     * deleted by the destructor.  If the name already denotes a file, the
     * file is opened; otherwise, a new empty file is created.
     *
     * @param name
     * @throws HFException        heapfile exception
     * @throws HFBufMgrException  exception thrown from bufmgr layer
     * @throws HFDiskMgrException exception thrown from diskmgr layer
     * @throws IOException        I/O errors
     */
    public LabelHeapFile(String name) throws HFException, HFBufMgrException, HFDiskMgrException, IOException {
        super(name);
    }

    public void labelHeapFile(String name) throws HFException,
            HFBufMgrException,
            HFDiskMgrException,
            IOException {

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
        } else {
            _fileName = name;
            _ftype = ORDINARY;
        }

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

            LHFPage firstDirPage = new LHFPage();
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
    }


    @Override
    public void deleteFile()
            throws FileAlreadyDeletedException,
            HFBufMgrException,
            HFDiskMgrException,
            IOException, InvalidSlotNumberException, InvalidTupleSizeException {
        if (_file_deleted) {
            throw new FileAlreadyDeletedException(null, "file already deleted");
        }

        // Mark the deleted flag (even if it doesn't get all the way done).
        _file_deleted = true;

        // Deallocate all data pages
        PageId currentDirPageId = new PageId();
        currentDirPageId.pid = _firstDirPageId.pid;
        PageId nextDirPageId = new PageId();
        nextDirPageId.pid = 0;
        Page pageinbuffer = new Page();
        LHFPage currentDirPage = new LHFPage();
        Label label;

        pinPage(currentDirPageId, currentDirPage, false);
        //currentDirPage.openHFpage(pageinbuffer);

        LID lid = new LID();
        while (currentDirPageId.pid != INVALID_PAGE) {
            for (lid = currentDirPage.firstLabel();
                 lid != null;
                 lid = currentDirPage.nextLabel(lid)) {
                label = currentDirPage.getLabel(lid);
                DataPageInfo dpinfo = new DataPageInfo(label);

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
     * Return number of records in file.
     *
     * @throws InvalidSlotNumberException invalid slot number
     * @throws InvalidTupleSizeException  invalid tuple size
     * @throws HFBufMgrException         exception thrown from bufmgr layer
     * @throws HFDiskMgrException        exception thrown from diskmgr layer
     * @throws IOException                I/O errors
     */
    public int getRecCnt()
            throws IOException, HFBufMgrException, InvalidSlotNumberException, InvalidTupleSizeException {
        int answer = 0;
        PageId currentDirPageId = new PageId(_firstDirPageId.pid);

        PageId nextDirPageId = new PageId(0);

        LHFPage currentDirPage = new LHFPage();
        Page pageinbuffer = new Page();

        while (currentDirPageId.pid != INVALID_PAGE) {
            pinPage(currentDirPageId, currentDirPage, false);

            LID lid = new LID();
            Label label;
            for (lid = currentDirPage.firstLabel();
                 lid != null;  // lid==NULL means no more label
                 lid = currentDirPage.nextLabel(lid)) {
                label = currentDirPage.getLabel(lid);
                DataPageInfo dpinfo = new DataPageInfo(label);

                answer += dpinfo.recct;
            }

            // ASSERTIONS: no more label
            // - we have read all datapage records on
            //   the current directory page.

            nextDirPageId = currentDirPage.getNextPage();
            unpinPage(currentDirPageId, false /*undirty*/);
            currentDirPageId.pid = nextDirPageId.pid;
        }

        // ASSERTIONS:
        // - if error, exceptions
        // - if end of heapfile reached: currentDirPageId == INVALID_PAGE
        // - if not yet end of heapfile: currentDirPageId valid

        return answer;
    } // end of getRecCnt

    /**
     * Insert label into file, return its Lid.
     *
     * @param recPtr pointer of the label
     * @return the lid of the label
     * @throws InvalidSlotNumberException invalid slot number
     * @throws InvalidTupleSizeException  invalid tuple size
     * @throws SpaceNotAvailableException no space left
     * @throws HFException               heapfile exception
     * @throws HFBufMgrException         exception thrown from bufmgr layer
     * @throws HFDiskMgrException        exception thrown from diskmgr layer
     * @throws IOException                I/O errors
     */
    public LID insertLabel(byte[] recPtr)
            throws HFException,
            IOException, HFBufMgrException, InvalidSlotNumberException, SpaceNotAvailableException, InvalidTupleSizeException {
        int dpinfoLen = 0;
        int recLen = recPtr.length;
        boolean found;
        LID currentDataPageLid = new LID();
        Page pageinbuffer = new Page();
        LHFPage currentDirPage = new LHFPage();
        LHFPage currentDataPage = new LHFPage();

        LHFPage nextDirPage = new LHFPage();
        PageId currentDirPageId = new PageId(_firstDirPageId.pid);
        PageId nextDirPageId = new PageId();  // OK

        pinPage(currentDirPageId, currentDirPage, false/*Rdisk*/);

        found = false;
        Label label;
        DataPageInfo dpinfo = new DataPageInfo();
        while (found == false) { //Start While01
            // look for suitable dpinfo-struct
            for (currentDataPageLid = currentDirPage.firstLabel();
                 currentDataPageLid != null;
                 currentDataPageLid =
                         currentDirPage.nextLabel(currentDataPageLid)) {
                label = currentDirPage.getLabel(currentDataPageLid);

                dpinfo = new DataPageInfo(label);

                // need check the label length == DataPageInfo'slength

                if (recLen <= dpinfo.availspace) {
                    found = true;
                    break;
                }
            }

            // two cases:
            // (1) found == true:
            //     currentDirPage has a datapagerecord which can accomodate
            //     the label which we have to insert
            // (2) found == false:
            //     there is no datapagerecord on the current directory page
            //     whose corresponding datapage has enough space free
            //     several subcases: see below
            if (found == false) { //Start IF01
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

                if (currentDirPage.available_space() >= dpinfo.size) {
                    //Start IF02
                    // case (2.1) : add a new data page label into the
                    //              current directory page
                    currentDataPage = _newDatapage(dpinfo);
                    // currentDataPage is pinned! and dpinfo->pageId is also locked
                    // in the exclusive mode

                    // didn't check if currentDataPage==NULL, auto exception

                    // currentDataPage is pinned: insert its label
                    // calling a HFPage function

                    Label newLabel = dpinfo.convertToLabel();

                    byte[] tmpData = newLabel.returnTupleByteArray();
                    currentDataPageLid = currentDirPage.insertLabel(tmpData);

                    LID tmpLid = currentDirPage.firstLabel();

                    // need catch error here!
                    if (currentDataPageLid == null) {
                        throw new HFException(null, "no space to insert rec.");
                    }

                    // end the loop, because a new datapage with its label
                    // in the current directorypage was created and inserted into
                    // the heapfile; the new datapage has enough space for the
                    // label which the user wants to insert

                    found = true;
                } //end of IF02
                else {  //Start else 02
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

                    if (nextDirPageId.pid != INVALID_PAGE) { //Start IF03
                        // case (2.2.1): there is another directory page:
                        unpinPage(currentDirPageId, false);

                        currentDirPageId.pid = nextDirPageId.pid;

                        pinPage(currentDirPageId,
                                currentDirPage, false);

                        // now go back to the beginning of the outer while-loop and
                        // search on the current directory page for a suitable datapage
                    } //End of IF03
                    else {  //Start Else03
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
                        currentDirPage = new LHFPage(nextDirPage);

                        // remark that MINIBASE_BM->newPage already
                        // pinned the new directory page!
                        // Now back to the beginning of the while-loop, using the
                        // newly created directory page.

                    } //End of else03
                } // End of else02
                // ASSERTIONS:
                // - if found == true: search will end and see assertions below
                // - if found == false: currentDirPage, currentDirPageId
                //   valid and pinned

            }//end IF01
            else { //Start else01
                // found == true:
                // we have found a datapage with enough space,
                // but we have not yet pinned the datapage:

                // ASSERTIONS:
                // - dpinfo valid

                // System.out.println("find the dirpagerecord on current page");

                pinPage(dpinfo.pageId, currentDataPage, false);
                //currentDataPage.openHFpage(pageinbuffer);

            }//End else01
        } //end of While01

        // ASSERTIONS:
        // - currentDirPageId, currentDirPage valid and pinned
        // - dpinfo.pageId, currentDataPageLid valid
        // - currentDataPage is pinned!

        if ((dpinfo.pageId).pid == INVALID_PAGE) // check error!
        {
            throw new HFException(null, "invalid PageId");
        }

        if (!(currentDataPage.available_space() >= recLen)) {
            throw new SpaceNotAvailableException(null, "no available space");
        }

        if (currentDataPage == null) {
            throw new HFException(null, "can't find Data page");
        }

        LID lid;
        lid = currentDataPage.insertLabel(recPtr);

        dpinfo.recct++;
        dpinfo.availspace = currentDataPage.available_space();

        unpinPage(dpinfo.pageId, true /* = DIRTY */);

        // DataPage is now released
        label = currentDirPage.returnLabel(currentDataPageLid);
        DataPageInfo dpinfo_ondirpage = new DataPageInfo(label);

        dpinfo_ondirpage.availspace = dpinfo.availspace;
        dpinfo_ondirpage.recct = dpinfo.recct;
        dpinfo_ondirpage.pageId.pid = dpinfo.pageId.pid;
        dpinfo_ondirpage.flushToTuple();

        unpinPage(currentDirPageId, true /* = DIRTY */);

        return lid;
    }

    /**
     * Delete label from file with given lid.
     *
     * @return true label deleted  false:label not found
     * @throws InvalidSlotNumberException invalid slot number
     * @throws InvalidTupleSizeException  invalid tuple size
     * @throws HFException               heapfile exception
     * @throws HFBufMgrException         exception thrown from bufmgr layer
     * @throws HFDiskMgrException        exception thrown from diskmgr layer
     * @throws Exception                  other exception
     */
    public boolean deleteLabel(LID lid)
            throws InvalidSlotNumberException,
            InvalidTupleSizeException,
            HFException,
            HFBufMgrException,
            HFDiskMgrException,
            Exception {
        boolean status;
        LHFPage currentDirPage = new LHFPage();
        PageId currentDirPageId = new PageId();
        LHFPage currentDataPage = new LHFPage();
        PageId currentDataPageId = new PageId();
        LID currentDataPageLid = new LID();

        status = _findDataPage(lid,
                currentDirPageId, currentDirPage,
                currentDataPageId, currentDataPage,
                currentDataPageLid);

        if (status != true) {
            return status;  // label not found
        }

        // ASSERTIONS:
        // - currentDirPage, currentDirPageId valid and pinned
        // - currentDataPage, currentDataPageid valid and pinned

        // get datapageinfo from the current directory page:
        Label label;

        label = currentDirPage.returnLabel(currentDataPageLid);
        DataPageInfo pdpinfo = new DataPageInfo(label);

        // delete the label on the datapage
        currentDataPage.deleteLabel(lid);

        pdpinfo.recct--;
        pdpinfo.flushToTuple();  //Write to the buffer pool
        if (pdpinfo.recct >= 1) {
            // more records remain on datapage so it still hangs around.
            // we just need to modify its directory entry

            pdpinfo.availspace = currentDataPage.available_space();
            pdpinfo.flushToTuple();
            unpinPage(currentDataPageId, true /* = DIRTY*/);

            unpinPage(currentDirPageId, true /* = DIRTY */);
        } else {
            // the label is already deleted:
            // we're removing the last label on datapage so free datapage
            // also, free the directory page if
            //   a) it's not the first directory page, and
            //   b) we've removed the last DataPageInfo label on it.

            // delete empty datapage: (does it get unpinned automatically? -NO, Ranjani)
            unpinPage(currentDataPageId, false /*undirty*/);

            freePage(currentDataPageId);

            // delete corresponding DataPageInfo-entry on the directory page:
            // currentDataPageLid points to datapage (from for loop above)

            currentDirPage.deleteLabel(currentDataPageLid);

            // ASSERTIONS:
            // - currentDataPage, currentDataPageId invalid
            // - empty datapage unpinned and deleted

            // now check whether the directory page is empty:

            currentDataPageLid = currentDirPage.firstLabel();

            // st == OK: we still found a datapageinfo label on this directory page
            PageId pageId;
            pageId = currentDirPage.getPrevPage();
            if ((currentDataPageLid == null) && (pageId.pid != INVALID_PAGE)) {
                // the directory-page is not the first directory page and it is empty:
                // delete it

                // point previous page around deleted page:

                LHFPage prevDirPage = new LHFPage();
                pinPage(pageId, prevDirPage, false);

                pageId = currentDirPage.getNextPage();
                prevDirPage.setNextPage(pageId);
                pageId = currentDirPage.getPrevPage();
                unpinPage(pageId, true /* = DIRTY */);

                // set prevPage-pointer of next Page
                pageId = currentDirPage.getNextPage();
                if (pageId.pid != INVALID_PAGE) {
                    LHFPage nextDirPage = new LHFPage();
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

    /**
     * Updates the specified label in the heapfile.
     *
     * @param lid:      the label which needs update
     * @param newLabel: the new content of the label
     * @return ture:update success   false: can't find the label
     * @throws InvalidSlotNumberException invalid slot number
     * @throws InvalidUpdateException     invalid update on label
     * @throws InvalidTupleSizeException  invalid tuple size
     * @throws HFException               heapfile exception
     * @throws HFBufMgrException         exception thrown from bufmgr layer
     * @throws HFDiskMgrException        exception thrown from diskmgr layer
     * @throws Exception                  other exception
     */
    public boolean updateLabel(LID lid, Label newLabel)
            throws InvalidSlotNumberException,
            InvalidUpdateException,
            HFException,
            HFDiskMgrException,
            HFBufMgrException,
            Exception {
        boolean status;
        LHFPage dirPage = new LHFPage();
        PageId currentDirPageId = new PageId();
        LHFPage dataPage = new LHFPage();
        PageId currentDataPageId = new PageId();
        LID currentDataPageLid = new LID();

        status = _findDataPage(lid,
                currentDirPageId, dirPage,
                currentDataPageId, dataPage,
                currentDataPageLid);

        if (status != true) {
            return status;  // label not found
        }

        Label label = dataPage.returnLabel(lid);

        // Assume update a label with a label whose length is equal to
        // the original label

        if (newLabel.getLength() != label.getLength()) {
            unpinPage(currentDataPageId, false /*undirty*/);
            unpinPage(currentDirPageId, false /*undirty*/);

            throw new InvalidUpdateException(null, "invalid label update");
        }

        // new copy of this label fits in old space;
        label.labelCopy(newLabel);
        unpinPage(currentDataPageId, true /* = DIRTY */);

        unpinPage(currentDirPageId, false /*undirty*/);

        return true;
    }

    /**
     * Read label from file, returning pointer and length.
     *
     * @param lid Label ID
     * @return a Tuple. if Tuple==null, no more tuple
     * @throws InvalidSlotNumberException invalid slot number
     * @throws InvalidTupleSizeException  invalid tuple size
     * @throws SpaceNotAvailableException no space left
     * @throws HFException               heapfile exception
     * @throws HFBufMgrException         exception thrown from bufmgr layer
     * @throws HFDiskMgrException        exception thrown from diskmgr layer
     * @throws Exception                  other exception
     */
    public Label getLabel(LID lid)
            throws InvalidSlotNumberException,
            HFBufMgrException,
            Exception {
        boolean status;
        LHFPage dirPage = new LHFPage();
        PageId currentDirPageId = new PageId();
        LHFPage dataPage = new LHFPage();
        PageId currentDataPageId = new PageId();
        LID currentDataPageLid = new LID();

        status = _findDataPage(lid,
                currentDirPageId, dirPage,
                currentDataPageId, dataPage,
                currentDataPageLid);

        if (status != true) {
            return null; // label not found
        }

        Label label = dataPage.getLabel(lid);

        /*
         * getLabel has copied the contents of lid into recPtr and fixed up
         * recLen also.  We simply have to unpin dirpage and datapage which
         * were originally pinned by _findDataPage.
         */

        unpinPage(currentDataPageId, false /*undirty*/);

        unpinPage(currentDirPageId, false /*undirty*/);

        return label;  //(true?)OK, but the caller need check if atuple==NULL
    }

    public LScan openScan()
            throws InvalidTupleSizeException,
            IOException {
        LScan newscan = null;
        newscan = new LScan(this);
        return newscan;
    }

    /* get a new datapage from the buffer manager and initialize dpinfo
       @param dpinfop the information in the new HFPage
    */
    private LHFPage _newDatapage(DataPageInfo dpinfop)
            throws IOException, HFBufMgrException, HFException {
        Page apage = new Page();
        PageId pageId = new PageId();
        pageId = newPage(apage, 1);

        if (pageId == null) {
            throw new HFException(null, "can't new pae");
        }

        // initialize internal values of the new page:

        LHFPage lhfPage = new LHFPage();
        lhfPage.init(pageId, apage);

        dpinfop.pageId.pid = pageId.pid;
        dpinfop.recct = 0;
        dpinfop.availspace = lhfPage.available_space();

        return lhfPage;
    } // end of _newDatapage

    /* Internal HeapFile function (used in getRecord and updateRecord):
       returns pinned directory page and pinned data page of the specified
       user record(lid) and true if record is found.
       If the user record cannot be found, return false.
    */
    private boolean _findDataPage(
            LID lid,
            PageId dirPageId, HFPage dirpage,
            PageId dataPageId, HFPage datapage,
            LID rpDataPageLid)
            throws HFBufMgrException, IOException, HFException, InvalidSlotNumberException, InvalidTupleSizeException {
        PageId currentDirPageId = new PageId(_firstDirPageId.pid);

        LHFPage currentDirPage = new LHFPage();
        LHFPage currentDataPage = new LHFPage();
        LID currentDataPageLid = new LID();
        PageId nextDirPageId = new PageId();
        // datapageId is stored in dpinfo.pageId

        pinPage(currentDirPageId, currentDirPage, false/*read disk*/);

        Label label = null;
        while (currentDirPageId.pid != INVALID_PAGE) {// Start While01
            // ASSERTIONS:
            //  currentDirPage, currentDirPageId valid and pinned and Locked.

            for (currentDataPageLid = currentDirPage.firstLabel();
                 currentDataPageLid != null;
                 currentDataPageLid = currentDirPage.nextLabel(currentDataPageLid)) {
                try {
                    label = currentDirPage.getLabel(currentDataPageLid);
                } catch (InvalidSlotNumberException e)// check error! return false(done)
                {
                    return false;
                }

                DataPageInfo dpinfo = new DataPageInfo(label);
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
                // - currentDataPage, currentDataPageLid, dpinfo valid
                // - currentDataPage pinned

                if (dpinfo.pageId.pid == lid.pageNo.pid) {
                    label = currentDataPage.returnLabel(lid);
                    // found user's record on the current datapage which itself
                    // is indexed on the current dirpage.  Return both of these.

                    dirpage.setpage(currentDirPage.getpage());
                    dirPageId.pid = currentDirPageId.pid;

                    datapage.setpage(currentDataPage.getpage());
                    dataPageId.pid = dpinfo.pageId.pid;

                    rpDataPageLid.pageNo.pid = currentDataPageLid.pageNo.pid;
                    rpDataPageLid.slotNo = currentDataPageLid.slotNo;
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
                throw new HFException(e, "labelheapfile,_find,unpinpage failed");
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
    } // end of _findDatapage

    public PageId getFirstDirPageId() {
        return _firstDirPageId;
    }
}
