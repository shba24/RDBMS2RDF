package diskmgr.rdf;

/**
 * Hols the Select Filter
 */
public class SelectFilter {
  public String subjectFilter;
  public String predicateFilter;
  public String objectFilter;
  public Float confidenceFilter;
  public SelectFilter(String _subjectFilter,
      String _predicateFilter,
      String _objectFilter,
      Float _confidenceFilter) {
    subjectFilter = _subjectFilter;
    predicateFilter = _predicateFilter;
    objectFilter = _objectFilter;
    confidenceFilter = _confidenceFilter;
  }
}
