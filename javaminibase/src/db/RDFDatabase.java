package db;

import global.GlobalConst;
import global.RDFSystemDefs;
import java.nio.file.Paths;

public class RDFDatabase {
  private String dbPath;

  /**
   * Public Constructor
   *
   * @param _dbName
   * @param _indexOption
   */
  public RDFDatabase(String _dbName, IndexOption _indexOption) {
    /**
     * Given these quadruples, the name of the rdf database
     * that will be created in the database will be RDFDBNAME_INDEXOPTION.
     */
    dbPath = getFullDbName(_dbName, _indexOption);
    RDFSystemDefs.init(dbPath, _indexOption, GlobalConst.NUMBUF);
  }

  /**
   * Gets the full db path
   * @param dbName
   * @param indexOption
   * @return
   */
  private String getFullDbName(String dbName, IndexOption indexOption) {
    return Paths.get(
        GlobalConst.ROOT_FOLDER,
        dbName.toLowerCase(),
        indexOption.name().toLowerCase(),
        GlobalConst.DATA_FILE
    ).toString();
  }

  /**
   * Returns dbPath
   *
   * @return
   */
  public String getDbPath() {
    return dbPath;
  }

  /**
   * Does select query on the database
   *
   * @param query
   * @throws Exception
   */
  public void select(SelectQuery query) throws Exception {
    query.execute();
  }

  /**
   * Does insert on the database
   *
   * @param query
   * @throws Exception
   */
  public void insert(InsertQuery query) throws Exception {
    query.execute();
  }
}
