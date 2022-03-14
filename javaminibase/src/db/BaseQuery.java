package db;

public class BaseQuery {
  private final String dbName;

  /**
   * Public constructor
   *
   * @param _dbName
   */
  public BaseQuery(String _dbName) {
    dbName = _dbName;
  }

  /**
   * Get db name
   *
   * @return String db name
   */
  public String getDbName() {
    return dbName;
  }
}
