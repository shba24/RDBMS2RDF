package db;

public class InsertQuery extends BaseQuery implements IQuery {
  private String dataFileName;
  private IndexOption indexOption;

  /**
   * Public constructor
   *
   * @param _datafile
   * @param _indexOption
   * @param _dbname
   */
  public InsertQuery(
      String _datafile,
      String _indexOption,
      String _dbname) {
    super(_dbname);
    dataFileName = _datafile;
    indexOption = IndexOption.valueOf(_indexOption);
  }

  /**
   * get data file name
   *
   * @return  String data file name
   */
  public String getDataFileName() {
    return dataFileName;
  }

  /**
   * set the data file name
   *
   * @param dataFileName String data file name
   */
  public void setDataFileName(String dataFileName) {
    this.dataFileName = dataFileName;
  }

  /**
   * get indexOption
   *
   * @return  IndexOption
   */
  public IndexOption getIndexOption() {
    return indexOption;
  }

  /**
   * set IndexOption from string
   *
   * @param indexOption String indexOption
   */
  public void setIndexOption(String indexOption) {
    this.indexOption = IndexOption.valueOf(indexOption);
  }

  /**
   * set IndexOption
   *
   * @param indexOption IndexOption
   */
  public void setIndexOption(IndexOption indexOption) {
    this.indexOption = indexOption;
  }

  /**
   * Execute the query
   *
   */
  public void execute() {
    // to be implemented
  }
}
