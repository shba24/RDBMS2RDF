package db;

public class InsertQuery implements IQuery {
  private String dataFileName;
  private IndexOption indexOption;
  private String dbName;

  public InsertQuery(
      String _datafile,
      String _indexOption,
      String _dbname) {
    dataFileName = _datafile;
    indexOption = IndexOption.valueOf(_indexOption);
    dbName = _dbname;
  }

  public String getDataFileName() {
    return dataFileName;
  }

  public void setDataFileName(String dataFileName) {
    this.dataFileName = dataFileName;
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

  public String getDbName() {
    return dbName;
  }

  public void setDbName(String dbName) {
    this.dbName = dbName;
  }

  public void execute() {
    // to be implemented
  }
}
