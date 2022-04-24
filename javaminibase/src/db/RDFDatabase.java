package db;

import global.GlobalConst;
import global.JoinStrategy;
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
  public RDFDatabase(String _dbName, IndexOption _indexOption, int numBuf) {
    /**
     * Given these quadruples, the name of the rdf database
     * that will be created in the database will be RDFDBNAME_INDEXOPTION.
     */
    dbPath = getFullDbName(_dbName, _indexOption);
    RDFSystemDefs.init(dbPath, _indexOption, numBuf);
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

  public void join(JoinQuery query, JoinStrategy js) throws Exception {
    query.execute(js);
  }

  /**
   * Cleans up RSFSystemDef
   * @throws Exception
   */
  public void close() throws Exception {
    RDFSystemDefs.close();
  }
}
