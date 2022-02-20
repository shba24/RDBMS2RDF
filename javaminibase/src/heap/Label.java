package heap;

import global.*;

public class Label {

    private ILID lid;

    private Tuple tuple;

    /**
     * Sets the tuple member
     * @param  tuple to be assigned
     */
    public void setTuple(Tuple tuple)
    {
        this.tuple = tuple;
    }

    /**
     * Sets the lid
     * @param lid to be assigned
     */
    public void setLid(ILID lid){
        this.lid.setPageNo(lid.getPageNo());
        this.lid.setSlotNo(lid.getSlotNo());
    }

    /**
     * Default constructor of class
     */
    public Label(){}
}
