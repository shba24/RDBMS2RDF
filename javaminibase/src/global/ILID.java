package global;

public interface ILID {
    PageId getPageNo();

    int getSlotNo();

    void setSlotNo(int slotNo);

    void setPageNo(PageId pageId);
}
