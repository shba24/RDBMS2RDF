package db;

import global.GlobalConst;
import global.RDFSystemDefs;
import java.nio.file.Paths;

public class RDFDatabase {
  private String dbPath;
  private RDFSystemDefs rdfSystemDefs;

  public RDFDatabase(String _dbName, IndexOption _indexOption) {
    /**
     * Given these quadruples, the name of the rdf database
     * that will be created in the database will be RDFDBNAME_INDEXOPTION.
     */
    dbPath = getFullDbName(_dbName, _indexOption);
    rdfSystemDefs = new RDFSystemDefs(dbPath, _indexOption);
    rdfSystemDefs.initRdfDB();
  }

  private String getFullDbName(String dbName, IndexOption indexOption) {
    return Paths.get(
        Paths.get(System.getProperty(GlobalConst.CURR_DIR_ENV)).toString(),
        GlobalConst.ROOT_FOLDER,
        dbName.toLowerCase(),
        indexOption.name().toLowerCase()
    ).toString();
  }

  public String getDbPath() {
    return dbPath;
  }

  public void select(SelectQuery query) {

  }

  public void insert(InsertQuery query) throws Exception {
    query.execute(rdfSystemDefs);
  }
}
