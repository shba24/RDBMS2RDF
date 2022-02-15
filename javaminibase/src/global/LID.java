
package global;

import java.io.*;

public class LID implements ILID{

    private int slotNo;

    /**
     *
     * @return slotNo
     */
    public int getSlotNo(){
        return slotNo;
    }

    private PageId pageNo;

    /**
     *
     * @return PageNo
     */
    public PageId getPageNo(){
        if(pageNo == null)
        {
            pageNo = new PageId();
        }
        return pageNo;
    }

    /**
     * sets the PageNo
     * @param pageNo contains PageId object to be assigned
     */
    public void setPageNo(PageId pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * sets the slot no
     * @param slotNo contains slotNo to be assigned.
     */
    public void setSlotNo(int slotNo) {
        this.slotNo = slotNo;
    }

    /**
     * Empty Constructor
     */
    public LID() {  }

    /**
     * parameterized constructor
     * @param pageNo contains PageId to be assigned
     * @param slotNo cotains slotNo to be assigned.
     */
    public LID(PageId pageNo, int slotNo){
        this.pageNo = pageNo;
        this.slotNo = slotNo;
    }
}