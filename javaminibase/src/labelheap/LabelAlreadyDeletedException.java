package labelheap;

import chainexception.ChainException;

public class LabelAlreadyDeletedException extends ChainException {

    public LabelAlreadyDeletedException() {
        super();
    }

    public LabelAlreadyDeletedException(Exception ex, String name) {
        super(ex, name);
    }
}
