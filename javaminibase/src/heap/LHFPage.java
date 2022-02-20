package heap;

import diskmgr.Page;
import global.*;

import java.io.IOException;

/**
 * Class label heap file page.
 * The design assumes that records are kept compacted when
 * deletions are performed.
 */

public class LHFPage {

    //region Private Members

    private HFPage hfpage;

    //endregion

    //region Constructor

    /**
    * Default constructor
    */
    public LHFPage(){}

    /**
     * Constructor of class LHFPage
     * open a LHFPage and make this LHFPage point to the given page
     *
     * @param page the given page in Page type
     */
    public LHFPage(Page page) {
        this.hfpage = new HFPage(page);
    }

    //endregion

    /**
     * Constructor of class LHFPage
     * initialize a new page
     *
     * @throws IOException I/O errors
     * @param    pageNo    the page number of a new page to be initialized
     * @param    apage    the Page to be initialized
     * @see        Page
     */
    public void init(PageId pageNo, Page apage)
            throws IOException {
        this.hfpage.init(pageNo, apage);
    }

    //region Page & Slot Methods

    /**
     * Constructor of class LHFPage
     * open a existed LHFPage
     *
     * @param apage a page in buffer pool
     */
    public void openLHFPage(Page apage) {
        this.hfpage.openHFpage(apage);
    }


    /**
     * @return byte array
     */
    public byte [] getLHFPageArray()
    {
        return this.hfpage.getHFpageArray();
    }

    /**
     * Dump contents of a page
     * @exception IOException I/O errors
     */
    public void dumpPage()
            throws IOException
    {
        hfpage.dumpPage();
    }

    /**
     * @return	PageId of previous page
     * @exception IOException I/O errors
     */
    public PageId getPrevPage()
            throws IOException
    {
        return hfpage.getPrevPage();
    }

    /**
     * sets value of prevPage to pageNo
     * @param       pageNo  page number for previous page
     * @exception IOException I/O errors
     */
    public void setPrevPage(PageId pageNo)
            throws IOException
    {
        hfpage.setPrevPage(pageNo);
    }

    /**
     * @return     page number of next page
     * @exception IOException I/O errors
     */
    public PageId getNextPage()
            throws IOException
    {
        return hfpage.getNextPage();
    }

    /**
     * sets value of nextPage to pageNo
     * @param	pageNo	page number for next page
     * @exception IOException I/O errors
     */
    public void setNextPage(PageId pageNo)
            throws IOException
    {
        hfpage.setNextPage(pageNo);
    }

    /**
     * @return 	page number of current page
     * @exception IOException I/O errors
     */
    public PageId getCurPage()
            throws IOException
    {
        return hfpage.getCurPage();
    }

    /**
     * sets value of curPage to pageNo
     * @param	pageNo	page number for current page
     * @exception IOException I/O errors
     */
    public void setCurPage(PageId pageNo)
            throws IOException
    {
        hfpage.setCurPage((pageNo));
    }

    /**
     * @return 	the ype
     * @exception IOException I/O errors
     */
    public short getType()
            throws IOException
    {
        return hfpage.getType();
    }

    /**
     * sets value of type
     * @param	valtype     an arbitrary value
     * @exception IOException I/O errors
     */
    public void setType(short valtype)
            throws IOException
    {
        hfpage.setType(valtype);
    }

    /**
     * @return 	slotCnt used in this page
     * @exception IOException I/O errors
     */
    public short getSlotCnt()
            throws IOException
    {
        return hfpage.getSlotCnt();
    }

    /**
     * sets slot contents
     * @param       slotno  the slot number
     * @param 	length  length of quadruple the slot contains
     * @param	offset  offset of quadruple
     * @exception IOException I/O errors
     */
    public void setSlot(int slotno, int length, int offset)
            throws IOException
    {
        hfpage.setSlot(slotno, length, offset);
    }

    /**
     * @param	slotno	slot number
     * @exception IOException I/O errors
     * @return	the length of quadruple the given slot contains
     */
    public short getSlotLength(int slotno)
            throws IOException
    {
        return hfpage.getSlotLength(slotno);
    }

    /**
     * @param       slotno  slot number
     * @exception IOException I/O errors
     * @return      the offset of quadruple the given slot contains
     */
    public short getSlotOffset(int slotno)
            throws IOException
    {
        return hfpage.getSlotOffset(slotno);
    }

    /**
     * returns the amount of available space on the page.
     *
     * @return the amount of available space on the page
     * @throws IOException I/O errors
     */
    public int available_space()
            throws IOException {
        return hfpage.available_space();
    }

    /**
     * Determining if the page is empty
     *
     * @return true if the LHFPage is has no records in it, false otherwise
     * @throws IOException I/O errors
     */
    public boolean empty()
            throws IOException {
        return hfpage.empty();
    }

    //endregion

    //region Public label methods

    /**
     * inserts a new Label onto the page, returns LID of this Label
     * @param	label 	a label to be inserted
     * @return	LID of label, null if sufficient space does not exist
     * @exception IOException I/O errors
     * in C++ Status insertLabel(char *recPtr, int recLen, LID& lid)
     */
    public ILID insertLabel (byte [] label)
            throws IOException
    {
        ILID lid = new LID();

        RID rid;
        try {
            rid = hfpage.insertRecord(label);
        } catch (Exception e) {
            System.err.println("Error in inserting Label");
            e.printStackTrace();
            throw e;
        }
        if(rid != null) {
            lid.setSlotNo(rid.slotNo);
            lid.setPageNo(rid.pageNo);
        }
        return lid ;
    }

    /**
     * delete the record with the specified rid
     *
     * @param lid the Label ID
     *                                    in C++ Status deleteRecord(const RID& rid)
     * @throws InvalidSlotNumberException Invalid slot number
     */
    public void deleteLabel(ILID lid)
            throws InvalidSlotNumberException {
        RID rid = new RID();
        rid.pageNo = lid.getPageNo();
        rid.slotNo = lid.getSlotNo();
        try {
            hfpage.deleteRecord(rid);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error in deleting Label");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @return LID of first label on page, null if page contains no labels.
     * @throws IOException I/O errors
     *                     in C++ Status firstLabel(LID& firstLid)
     */
    public ILID firstLabel()
            throws IOException {
        RID rid;
        try {
            rid = hfpage.firstRecord();
        } catch (Exception e) {
            System.err.println("Error in fetching first Label");
            e.printStackTrace();
            throw e;
        }

        ILID lid = new LID();

        lid.setPageNo(rid.pageNo);
        lid.setSlotNo(rid.slotNo);

        return lid;
    }

    /**
     * @param curLid current label ID
     * @return LID of next label on the page, null if no more
     * labels exist on the page
     * @throws IOException I/O errors
     *                     in C++ Status nextRecord (LID curLid, LID& nextLid)
     */
    public ILID nextLID(ILID curLid)
            throws IOException {
        RID curRid = new RID();
        curRid.slotNo = curLid.getSlotNo();
        curRid.pageNo = curLid.getPageNo();

        try {
            curRid = hfpage.nextRecord(curRid);
        } catch (Exception e) {
            System.err.println("Error in fetching next Label");
            e.printStackTrace();
            throw e;
        }

        ILID lid = new LID();
        lid.setSlotNo(curRid.slotNo);
        lid.setPageNo(curRid.pageNo);

        return lid;
    }

    /**
     * copies out Label with LID lid into label pointer.
     * <br>
     * Status getLabel(LID lid, char *recPtr, int& recLen)
     *
     * @param lid the label ID
     * @return a label contains the record
     * @throws InvalidSlotNumberException Invalid slot number
     * @see Tuple
     */
    public Label getLabel(ILID lid)
            throws InvalidSlotNumberException {

        RID rid = new RID();
        rid.pageNo = lid.getPageNo();
        rid.slotNo = lid.getSlotNo();

        Tuple tuple = null;
        try {
            tuple = hfpage.getRecord(rid);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error in fetching Label");
            e.printStackTrace();
            throw e;
        }
        Label label = new Label();
        label.setTuple(tuple);
        label.setLid(lid);
        return label;
    }

    /**
     * returns a tuple in a byte array[pageSize] with given LID lid.
     * <br>
     * in C++	Status returnLabel(LID lid, char*& recPtr, int& recLen)
     *
     * @param lid the label ID
     * @return a Label  with its length and offset in the byte array
     * @throws InvalidSlotNumberException Invalid slot number
     * @see Tuple
     */
    public Label returnLabel(ILID lid)
            throws InvalidSlotNumberException {

        RID rid = new RID();
        rid.pageNo = lid.getPageNo();
        rid.slotNo = lid.getSlotNo();

        Tuple tuple = null;
        try {
            tuple = hfpage.returnRecord(rid);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error in fetching Label in byte array");
            e.printStackTrace();
            throw e;
        }
        Label label = new Label();
        label.setTuple(tuple);
        label.setLid(lid);
        return label;
    }

    //endregion

    //region Protected Methods

    /**
     * Compacts the slot directory on an LHFPage.
     * WARNING -- this will probably lead to a change in the LIDs of
     * records on the page.  You CAN'T DO THIS on most kinds of pages.
     *
     * @throws IOException I/O errors
     */
    protected void compact_slot_dir()
            throws IOException {
        this.hfpage.compact_slot_dir();
    }

    //endregion
}

