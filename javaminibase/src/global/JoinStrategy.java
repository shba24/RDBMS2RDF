package global;

/**
 * Enumeration class for JoinStrategy
 */

public class JoinStrategy {

  public static final int BPLeftScanRightScanNestedLoopJoin = 0;
  public static final int BPLeftIndexRightIndexNestedLoopJoin = 1;
  public static final int BPLeftIndexRightIndexHashJoin = 2;

  public int joinStrategy;

  public JoinStrategy(int _joinStrategy) {
    joinStrategy = _joinStrategy;
  }

  public String toString() {

    switch (joinStrategy) {
      case BPLeftScanRightScanNestedLoopJoin:
        return "BPLeftScanRightScanNestedLoopJoin";
      case BPLeftIndexRightIndexNestedLoopJoin:
        return "BPLeftIndexRightIndexNestedLoopJoin";
      case BPLeftIndexRightIndexHashJoin:
        return "BPLeftIndexRightIndexHashJoin";
    }
    return ("Unexpected TupleOrder " + joinStrategy);
  }
}
