package db;

public class SelectQuery implements IQuery {
  private String dbName;
  private IndexOption indexOption;
  private String order;
  private String subjectFilter;
  private String predicateFilter;
  private String objectFilter;
  private String confidenceFilter;
  private int numBuf;

  public SelectQuery(String _dbName, String _indexOption, String _order, String _subjectFilter,
      String _predicateFilter, String _objectFilter, String _confidenceFilter, int _numBuf) {
    dbName = _dbName;
    indexOption = IndexOption.valueOf(_indexOption);
    order = _order;
    subjectFilter = _subjectFilter;
    predicateFilter = _predicateFilter;
    objectFilter = _objectFilter;
    confidenceFilter = _confidenceFilter;
    numBuf = _numBuf;
  }

  public String getDbName() {
    return dbName;
  }

  public void setDbName(String dbName) {
    this.dbName = dbName;
  }

  public IndexOption getIndexOption() {
    return indexOption;
  }

  public void setIndexOption(String indexOption) {
    this.indexOption = IndexOption.valueOf(indexOption);
  }

  public void setIndexOption(IndexOption indexOption) {
    this.indexOption = indexOption;
  }

  public String getOrder() {
    return order;
  }

  public void setOrder(String order) {
    this.order = order;
  }

  public String getSubjectFilter() {
    return subjectFilter;
  }

  public void setSubjectFilter(String subjectFilter) {
    this.subjectFilter = subjectFilter;
  }

  public String getPredicateFilter() {
    return predicateFilter;
  }

  public void setPredicateFilter(String predicateFilter) {
    this.predicateFilter = predicateFilter;
  }

  public String getObjectFilter() {
    return objectFilter;
  }

  public void setObjectFilter(String objectFilter) {
    this.objectFilter = objectFilter;
  }

  public String getConfidenceFilter() {
    return confidenceFilter;
  }

  public void setConfidenceFilter(String confidenceFilter) {
    this.confidenceFilter = confidenceFilter;
  }

  public int getNumBuf() {
    return numBuf;
  }

  public void setNumBuf(int numBuf) {
    this.numBuf = numBuf;
  }

  public void execute() {
    // to be implemented
  }
}
