package diskmgr.rdf;

import global.EID;
import global.PID;

/**
 * Hols the Select Filter
 */
public class SelectFilter {
  public EID subjectID;
  public PID predicateID;
  public EID objectID;
  public Float confidenceFilter;
  public SelectFilter(EID _subjectID,
      PID _predicateID,
      EID _objectID,
      Float _confidenceFilter) {
    subjectID = _subjectID;
    predicateID = _predicateID;
    objectID = _objectID;
    confidenceFilter = _confidenceFilter;
  }
}
