package iterator;

import chainexception.ChainException;

public class QuadrupleUtilsException extends ChainException {
  public QuadrupleUtilsException(String s) {super(null, s);}

  public QuadrupleUtilsException(Exception prev, String s) {super(prev, s);}
}
