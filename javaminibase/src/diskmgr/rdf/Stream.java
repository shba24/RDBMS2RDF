package diskmgr.rdf;

import db.SelectQuery;
import global.QID;
import heap.Quadruple;

public class Stream {
  RdfDB rdfDB;
  int orderType;
  String subjectFilter;
  String predicateFilter;
  String objectFilter;
  double confidenceFilter;

  public Stream(SelectQuery selectQuery) {

  }

  public void closeStream() {

  }

  Quadruple getNext(QID quadrupleID) {
    return null;
  }

}
