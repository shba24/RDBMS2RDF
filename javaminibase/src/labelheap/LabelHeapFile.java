package labelheap;

import diskmgr.Page;
import global.*;
import heap.*;
import heap.FileAlreadyDeletedException;

import java.io.IOException;

interface Filetype {
    int TEMP = 0;
    int ORDINARY = 1;
} // end of Filetype

public class LabelHeapFile implements Filetype, GlobalConst {

    private static int tempfilecount = 0;
    PageId _firstDirPageId;   // page number of header page
    int _ftype;
    private boolean _file_deleted;
    private String _fileName;

    public void LabelHeapFile(String name) throws LHFException,
            LHFBufMgrException,
            LHFDiskMgrException,
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

            HFPage firstDirPage = new HFPage();
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

    public int getLabelCnt()
            throws InvalidSlotNumberException,
            InvalidTupleSizeException,
            LHFDiskMgrException,
            LHFBufMgrException,
            IOException {
        int answer = 0;
        PageId currentDirPageId = new PageId(_firstDirPageId.pid);

        PageId nextDirPageId = new PageId(0);

        LHFPage currentDirPage = new LHFPage();
        Page pageinbuffer = new Page();

        while (currentDirPageId.pid != INVALID_PAGE) {
            pinPage(currentDirPageId, currentDirPage, false);

            RID rid = new RID();
            Tuple atuple;
            for (rid = currentDirPage.firstRecord();
                 rid != null;  // rid==NULL means no more record
                 rid = currentDirPage.nextRecord(rid)) {
                atuple = currentDirPage.getRecord(rid);
                DataPageInfo dpinfo = new DataPageInfo(atuple);

                answer += dpinfo.recct;
            }

            // ASSERTIONS: no more record
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
    }

    public void deleteFile()
            throws heap.InvalidSlotNumberException,
            heap.FileAlreadyDeletedException,
            heap.InvalidTupleSizeException,
            HFBufMgrException,
            HFDiskMgrException,
            IOException {
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
        HFPage currentDirPage = new HFPage();
        Tuple atuple;

        pinPage(currentDirPageId, currentDirPage, false);
        //currentDirPage.openHFpage(pageinbuffer);

        RID rid = new RID();
        while (currentDirPageId.pid != INVALID_PAGE) {
            for (rid = currentDirPage.firstRecord();
                 rid != null;
                 rid = currentDirPage.nextRecord(rid)) {
                atuple = currentDirPage.getRecord(rid);
                DataPageInfo dpinfo = new DataPageInfo(atuple);
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

    public boolean deleteLabel(LID lid) {

    }

    private void pinPage(PageId pageno, Page page, boolean emptyPage)
            throws LHFBufMgrException {

        try {
            SystemDefs.JavabaseBM.pinPage(pageno, page, emptyPage);
        } catch (Exception e) {
            throw new LHFBufMgrException(e, "LabelHeapFile.java: pinPage() failed");
        }
    }

    private void unpinPage(PageId pageno, boolean dirty)
            throws LHFBufMgrException {

        try {
            SystemDefs.JavabaseBM.unpinPage(pageno, dirty);
        } catch (Exception e) {
            throw new LHFBufMgrException(e, "LabelHeapFile.java: unpinPage() failed");
        }
    }

    public LScan openScan()
            throws InvalidTupleSizeException,
            IOException {
        LScan newscan = null;
        try {
            newscan = new LScan(this);
        } catch (InvalidTupleSizeException e) {
            e.printStackTrace();
        }
        return newscan;
    }

}
