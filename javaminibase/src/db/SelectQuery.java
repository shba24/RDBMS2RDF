package db;

import diskmgr.rdf.IStream;
import diskmgr.rdf.RdfDB;
import global.QuadOrder;
import global.SystemDefs;
import heap.Quadruple;

public class SelectQuery extends BaseQuery implements IQuery {
  private IndexOption indexOption;
  private QuadOrder orderType;
  private String subjectFilter;
  private String predicateFilter;
  private String objectFilter;
  private Float confidenceFilter;
  private int numBuf;

  /**
   * Public constructor
   *
   * @param _dbName           database name
   * @param _indexOption      index option as string
   * @param _orderType        order of the output
   * @param _subjectFilter    subject filter, if '*' will be set to null
   * @param _predicateFilter  predicate filter, if '*' will be set to null
   * @param _objectFilter     object filter, if '*' will be set to null
   * @param _confidenceFilter confidence filter, if '*' will be set to null
   * @param _numBuf           number of max buffer pages to read from
   */
  public SelectQuery(
      String _dbName, String _indexOption, String _orderType, String _subjectFilter,
      String _predicateFilter, String _objectFilter, String _confidenceFilter, String _numBuf) {
    super(_dbName);
    indexOption = IndexOption.valueOf(_indexOption);
    orderType = new QuadOrder(Integer.valueOf(_orderType));
    if (_subjectFilter.equals("*") ||
        _subjectFilter.isEmpty()) {
      subjectFilter = null;
    } else {
      subjectFilter = _subjectFilter;
    }
    if (_predicateFilter.equals("*") ||
        _predicateFilter.isEmpty()) {
      predicateFilter = null;
    } else {
      predicateFilter = _predicateFilter;
    }
    if (_objectFilter.equals("*") ||
        _objectFilter.isEmpty()) {
      objectFilter = null;
    } else {
      objectFilter = _objectFilter;
    }
    if (_confidenceFilter.equals("*") || _confidenceFilter.isEmpty()) {
      confidenceFilter = null;
    } else {
      confidenceFilter = Float.parseFloat(_confidenceFilter);
    }
    numBuf = Integer.valueOf(_numBuf);
  }

  /**
   * get index option as IndexOption enum
   *
   * @return
   */
  public IndexOption getIndexOption() {
    return indexOption;
  }

  /**
   * set index option from String
   *
   * @param indexOption String is converted to enum internally
   */
  public void setIndexOption(String indexOption) {
    this.indexOption = IndexOption.valueOf(indexOption);
  }

  /**
   * set index option from IndexOption
   *
   * @param indexOption IndexOption
   */
  public void setIndexOption(IndexOption indexOption) {
    this.indexOption = indexOption;
  }

  /**
   * get the order of the query
   *
   * @return int order of the query
   */
  public QuadOrder getOrderType() {
    return orderType;
  }

  /**
   * set the order of the query
   *
   * @param _orderType int order of the query
   */
  public void setOrder(QuadOrder _orderType) {
    this.orderType = _orderType;
  }

  /**
   * get subject filter
   *
   * @return string format subject filter
   */
  public String getSubjectFilter() {
    return subjectFilter;
  }

  /**
   * set subject filter
   *
   * @param subjectFilter String subject filter
   */
  public void setSubjectFilter(String subjectFilter) {
    if (subjectFilter.equals("*") ||
        subjectFilter.isEmpty()) {
      this.subjectFilter = null;
    } else {
      this.subjectFilter = subjectFilter;
    }
  }

  /**
   * get predicate filter
   *
   * @return String predicate filter
   */
  public String getPredicateFilter() {
    return predicateFilter;
  }

  /**
   * set predicate filter
   *
   * @param predicateFilter String predicate filter
   */
  public void setPredicateFilter(String predicateFilter) {
    if (predicateFilter.equals("*") ||
        predicateFilter.isEmpty()) {
      this.predicateFilter = null;
    } else {
      this.predicateFilter = predicateFilter;
    }
  }

  /**
   * get object filter
   *
   * @return String object filter
   */
  public String getObjectFilter() {
    return objectFilter;
  }

  /**
   * set object filter
   *
   * @param objectFilter String object filter
   */
  public void setObjectFilter(String objectFilter) {
    if (objectFilter.equals("*") ||
        objectFilter.isEmpty()) {
      this.objectFilter = null;
    } else {
      this.objectFilter = objectFilter;
    }
  }

  /**
   * get the confidence filter
   *
   * @return string confidence filter
   */
  public Float getConfidenceFilter() {
    return confidenceFilter;
  }

  /**
   * set confidence filter
   *
   * @param confidenceFilter string confidence filter
   */
  public void setConfidenceFilter(String confidenceFilter) {
    if (confidenceFilter.equals("*") || confidenceFilter.isEmpty()) {
      this.confidenceFilter = null;
    } else {
      this.confidenceFilter = Float.parseFloat(confidenceFilter);
    }
  }

  /**
   * set confidence filter from Float
   *
   * @param confidenceFilter Float confidence filter
   */
  public void setConfidenceFilter(Float confidenceFilter) {
    this.confidenceFilter = confidenceFilter;
  }

  /**
   * get the max number of buffer pages to read
   *
   * @return
   */
  public int getNumBuf() {
    return numBuf;
  }

  /**
   * set the max number of buffer pages to read from
   *
   * @param numBuf
   */
  public void setNumBuf(int numBuf) {
    this.numBuf = numBuf;
  }

  /**
   * Executes the query
   */
  public void execute() throws Exception {
    IStream stream = null;
    try {
      stream = ((RdfDB) SystemDefs.JavabaseDB).openStream(
          orderType,
          numBuf,
          subjectFilter,
          predicateFilter,
          objectFilter,
          confidenceFilter
      );
      Quadruple quad = stream.getNext();
      while (quad != null) {
        quad.print();
        quad = stream.getNext();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      if (stream!=null) stream.closeStream();
    }
  }
}
