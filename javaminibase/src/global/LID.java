
package global;

import java.io.*;

public class LID implements ILID{

    private int slotNo;

    public int getSlotNo(){
        return slotNo;
    }

    private PageId pageNo;

    public PageId getPageNo(){
        if(pageNo == null)
        {
            pageNo = new PageId();
        }
        return pageNo;
    }

    public void setPageNo(PageId pageNo) {
        this.pageNo = pageNo;
    }

    public void setSlotNo(int slotNo) {
        this.slotNo = slotNo;
    }

    ///<summary>
    ///Default constructor
    ///</summary>
    public LID() {  }

    ///<summary>
    ///Constructor of class
    ///</summary>
    public LID(PageId pageNo, int slotNo){
        this.pageNo = pageNo;
        this.slotNo = slotNo;
    }


}