/* File hferr.java  */

package labelheap;

import chainexception.ChainException;

public class LHFDiskMgrException extends ChainException {

  public LHFDiskMgrException() {
    super();
  }

  public LHFDiskMgrException(Exception ex, String name) {
    super(ex, name);
  }
}
