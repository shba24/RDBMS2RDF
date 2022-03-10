package heap.labelheap;

/**
 * JAVA
 * Scan.java-  class Scan
 * <p>
 * Scan.java-  class Scan
 * <p>
 * Scan.java-  class Scan
 * <p>
 * Scan.java-  class Scan
 */
/**
 * Scan.java-  class Scan
 *
 */

import diskmgr.Page;
import global.LID;
import global.PageId;
import global.SystemDefs;
import heap.HFBufMgrException;
import heap.InvalidTupleSizeException;
import heap.Label;
import heap.Scan;

import java.io.IOException;

/**
 * A Scan object is created ONLY through the function openScan
 * of a HeapFile. It supports the getNext interface which will
 * simply retrieve the next label in the heapfile.
 *
 * An object of type scan will always have pinned one directory page
 * of the heapfile.
 */
public class LScan extends Scan {

    /**
     * Note that one label in our way-cool HeapFile implementation is
     * specified by six (6) parameters, some of which can be determined
     * from others:
     */

    /** The heapfile we are using. */
    private LabelHeapFile _lhf;

    /** PageId of current directory page (which is itself an HFPage) */
    private PageId dirpageId = new PageId();

    /** pointer to in-core data of dirpageId (page is pinned) */
    private LHFPage dirpage = new LHFPage();

    /** label ID of the DataPageInfo struct (in the directory page) which
     * describes the data page where our current label lives.
     */
    private LID datePageLid = new LID();

    /** the actual PageId of the data page with the current label */
    private PageId dataPageId = new PageId();

    /** in-core copy (pinned) of the same */
    private LHFPage dataPage = new LHFPage();

    /** label ID of the current label (from the current data page) */
    private LID lid = new LID();

    /** Status of next user status */
    private boolean nextUserStatus;

    /** The constructor pins the first directory page in the file
     * and initializes its private data members from the private
     * data member from hf
     *
     * @exception InvalidTupleSizeException Invalid tuple size
     * @exception IOException I/O errors
     *
     * @param lhf A HeapFile object
     */
    public LScan(LabelHeapFile lhf)
            throws InvalidTupleSizeException,
            IOException {
        super(lhf);
        init(lhf);
    }

    /** Retrieve the next label in a sequential scan
     *
     * @exception InvalidTupleSizeException Invalid tuple size
     * @exception IOException I/O errors
     *
     * @param lid Label ID of the label
     * @return the Tuple of the retrieved label.
     */
    public Label getNext(LID lid)
            throws InvalidTupleSizeException,
            IOException {
        Label label = null;

        if (nextUserStatus != true) {
            nextDataPage();
        }

        if (dataPage == null) {
            return null;
        }

        lid.getPageNo().setPid(this.lid.getPageNo().pid);
        lid.setSlotNo(this.lid.getSlotNo());

        try {
            label = dataPage.getLabel(lid);
        } catch (Exception e) {
            //    System.err.println("SCAN: Error in Scan" + e);
            e.printStackTrace();
        }

        this.lid = dataPage.nextLabel(lid);
        if (this.lid == null) {
            nextUserStatus = false;
        } else {
            nextUserStatus = true;
        }

        return label;
    }

    /** Position the scan cursor to the label with the given lid.
     *
     * @exception InvalidTupleSizeException Invalid tuple size
     * @exception IOException I/O errors
     * @param lid Label ID of the given label
     * @return true if successful,
     *			false otherwise.
     */
    public boolean position(LID lid)
            throws InvalidTupleSizeException,
            IOException {
        LID nxtlid = new LID();
        boolean bst;

        bst = peekNext(nxtlid);

        if (nxtlid.equals(lid) == true) {
            return true;
        }

        // This is kind lame, but otherwise it will take all day.
        PageId pgid = new PageId();
        pgid.setPid(lid.getPageNo().pid);

        if (!dataPageId.equals(pgid)) {

            // reset everything and start over from the beginning
            reset();

            bst = firstDataPage();

            if (bst != true) {
                return bst;
            }

            while (!dataPageId.equals(pgid)) {
                bst = nextDataPage();
                if (bst != true) {
                    return bst;
                }
            }
        }

        // Now we are on the correct page.

        try {
            this.lid = dataPage.firstLabel();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (this.lid == null) {
            bst = false;
            return bst;
        }

        bst = peekNext(nxtlid);

        while ((bst == true) && (nxtlid != lid)) {
            bst = mvNext(nxtlid);
        }

        return bst;
    }

    /** Do all the constructor work
     *
     * @exception InvalidTupleSizeException Invalid tuple size
     * @exception IOException I/O errors
     *
     * @param lhf A HeapFile object
     */
    private void init(LabelHeapFile lhf)
            throws InvalidTupleSizeException,
            IOException {
        _lhf = lhf;

        firstDataPage();
    }

    /** Closes the Scan object */
    public void closescan() {
        reset();
    }

    /** Reset everything and unpin all pages. */
    private void reset() {

        if (dataPage != null) {

            try {
                unpinPage(dataPageId, false);
            } catch (Exception e) {
                // 	System.err.println("SCAN: Error in Scan" + e);
                e.printStackTrace();
            }
        }
        dataPageId.pid = 0;
        dataPage = null;

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
        Label label = null;
        Boolean bst;

        /** copy data about first directory page */

        dirpageId.pid = _lhf.getFirstDirPageId().pid;
        nextUserStatus = true;

        /** get first directory page and pin it */
        try {
            dirpage = new LHFPage();
            pinPage(dirpageId, (Page) dirpage, false);
        } catch (Exception e) {
            //    System.err.println("SCAN Error, try pinpage: " + e);
            e.printStackTrace();
        }

        /** now try to get a pointer to the first datapage */
        LID dataPageLid = dirpage.firstLabel();

        if (dataPageLid != null) {
            /** there is a datapage label on the first directory page: */

            try {
                label = dirpage.getLabel(dataPageLid);
            } catch (Exception e) {
                //	System.err.println("SCAN: Chain Error in Scan: " + e);
                e.printStackTrace();
            }

            dpinfo = new DataPageInfo(label);
            dataPageId.pid = dpinfo.pageId.pid;
        } else {

            /** the first directory page is the only one which can possibly remain
             * empty: therefore try to get the next directory page and
             * check it. The next one has to contain a datapage label, unless
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

                    dirpage = new LHFPage();
                    pinPage(nextDirPageId, (Page) dirpage, false);
                } catch (Exception e) {
                    //  System.err.println("SCAN: Error in 1stdatapage 2 " + e);
                    e.printStackTrace();
                }

                /** now try again to read a data label: */

                try {
                    dataPageLid = dirpage.firstLabel();
                } catch (Exception e) {
                    //  System.err.println("SCAN: Error in 1stdatapg 3 " + e);
                    e.printStackTrace();
                    dataPageId.pid = INVALID_PAGE;
                }

                if (dataPageLid != null) {

                    try {

                        label = dirpage.getLabel(dataPageLid);
                    } catch (Exception e) {
                        //    System.err.println("SCAN: Error getLabel 4: " + e);
                        e.printStackTrace();
                    }

                    if (label.getLength() != DataPageInfo.size) {
                        return false;
                    }

                    dpinfo = new DataPageInfo(label);
                    dataPageId.pid = dpinfo.pageId.pid;
                } else {
                    // heapfile empty
                    dataPageId.pid = INVALID_PAGE;
                }
            }//end if01
            else {// heapfile empty
                dataPageId.pid = INVALID_PAGE;
            }
        }

        dataPage = null;

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
         *    - this->datapage == NULL, this->datapageId, this->datapage Lid valid
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
        Label rectuple = null;

        // ASSERTIONS:
        // - this->dirpageId has Id of current directory page
        // - this->dirpage is valid and pinned
        // (1) if heapfile empty:
        //    - this->datapage==NULL; this->datapageId == INVALID_PAGE
        // (2) if overall first label in heapfile:
        //    - this->datapage==NULL, but this->datapageId valid
        //    - this->datapageLid valid
        //    - current data page unpinned !!!
        // (3) if somewhere in heapfile
        //    - this->datapageId, this->datapage, this->datapageLid valid
        //    - current data page pinned
        // (4)- if the scan had already been done,
        //        dirpage = NULL;  datapageId = INVALID_PAGE

        if ((dirpage == null) && (dataPageId.pid == INVALID_PAGE)) {
            return false;
        }

        if (dataPage == null) {
            if (dataPageId.pid == INVALID_PAGE) {
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
                    dataPage = new LHFPage();
                    pinPage(dataPageId, (Page) dataPage, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    lid = dataPage.firstLabel();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }
        }

        // ASSERTIONS:
        // - this->datapage, this->datapageId, this->datapageLid valid
        // - current datapage pinned

        // unpin the current datapage
        try {
            unpinPage(dataPageId, false /* no dirty */);
            dataPage = null;
        } catch (Exception e) {

        }

        // read next dataPageLabel from current directory page
        // dirPage is set to NULL at the end of scan. Hence

        if (dirpage == null) {
            return false;
        }

        datePageLid = dirpage.nextLabel(datePageLid);

        if (datePageLid == null) {
            nextDataPageStatus = false;
            // we have read all datapage labels on the current directory page

            // get next directory page
            nextDirPageId = dirpage.getNextPage();

            // unpin the current directory page
            try {
                unpinPage(dirpageId, false /* not dirty */);
                dirpage = null;

                dataPageId.pid = INVALID_PAGE;
            } catch (Exception e) {

            }

            if (nextDirPageId.pid == INVALID_PAGE) {
                return false;
            } else {
                // ASSERTION:
                // - nextDirPageId has correct id of the page which is to get

                dirpageId = nextDirPageId;

                try {
                    dirpage = new LHFPage();
                    pinPage(dirpageId, (Page) dirpage, false);
                } catch (Exception e) {

                }

                if (dirpage == null) {
                    return false;
                }

                try {
                    datePageLid = dirpage.firstLabel();
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
        // - this->datapageLid has the Lid of the next datapage to be read
        // - this->datapage, this->datapageId invalid

        // data page is not yet loaded: read its label from the directory page
        try {
            rectuple = dirpage.getLabel(datePageLid);
        } catch (Exception e) {
            System.err.println("HeapFile: Error in Scan" + e);
        }

        if (rectuple.getLength() != DataPageInfo.size) {
            return false;
        }

        dpinfo = new DataPageInfo(rectuple);
        dataPageId.pid = dpinfo.pageId.pid;

        try {
            dataPage = new LHFPage();
            pinPage(dpinfo.pageId, (Page) dataPage, false);
        } catch (Exception e) {
            System.err.println("HeapFile: Error in Scan" + e);
        }

        // - directory page is pinned
        // - datapage is pinned
        // - this->dirpageId, this->dirpage correct
        // - this->datapageId, this->datapage, this->datapageLid correct

        lid = dataPage.firstLabel();

        if (lid == null) {
            nextUserStatus = false;
            return false;
        }

        return true;
    }

    private boolean peekNext(LID lid) {

        lid.getPageNo().setPid(this.lid.getPageNo().pid);
        lid.setSlotNo(this.lid.getSlotNo());
        return true;
    }

    /** Move to the next label in a sequential scan.
     * Also returns the LID of the (new) current label.
     */
    private boolean mvNext(LID lid)
            throws InvalidTupleSizeException,
            IOException {
        LID nextLid;
        boolean status;

        if (dataPage == null) {
            return false;
        }

        nextLid = dataPage.nextLabel(lid);

        if (nextLid != null) {
            this.lid.getPageNo().setPid(nextLid.getPageNo().pid);
            this.lid.setSlotNo(nextLid.getSlotNo());
            return true;
        } else {

            status = nextDataPage();

            if (status == true) {
                lid.getPageNo().setPid(this.lid.getPageNo().pid);
                lid.setSlotNo(this.lid.getSlotNo());
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
     * short cut to access the unpinPage function in bufmgr package.
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
