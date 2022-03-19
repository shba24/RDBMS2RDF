package diskmgr.rdf;

import heap.Quadruple;

public interface IStream {
  Quadruple getNext() throws Exception;
  void closeStream() throws Exception;
}
