package diskmgr.rdf;

import heap.BasicPatternClass;

public interface IJStream {
  BasicPatternClass getNext() throws Exception;
  void closeStream() throws Exception;
}