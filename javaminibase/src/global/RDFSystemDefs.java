package global;

import bufmgr.BufMgr;
import db.IndexOption;
import diskmgr.rdf.RdfDB;
import java.nio.file.Paths;

/**
 * Implements the RDFSystemDefs using the Builder
 * design Pattern.
 *
 */
public class RDFSystemDefs {
  private String dbPath;
  private IndexOption indexOption;
  private String logFilePath;
  private int numPgs = GlobalConst.DEFAULT_DB_PAGES;
  private int logSize = 3 * GlobalConst.DEFAULT_DB_PAGES;
  private int bufPoolSize = GlobalConst.NUMBUF;
  private String replacementPolicy = GlobalConst.DEFAULT_REPLACEMENT_POLICY;

  /**
   * Global Constants
   */
  public BufMgr bufMgr;
  public RdfDB rdfDB;

  /**
   * Default constructor for the RDFSystemDefs
   *
   * @param rdfDbPath
   * @param _indexOption
   */
  public RDFSystemDefs(
      String rdfDbPath,
      IndexOption _indexOption) {
    dbPath = rdfDbPath;
    indexOption = _indexOption;
    logFilePath = Paths.get(
        dbPath,
        GlobalConst.DEFAULT_LOG_FILENAME
    ).toString();
  }

  public RDFSystemDefs setNumPages(int _numPgs) {
    numPgs = _numPgs;
    return this;
  }

  public RDFSystemDefs setLogSize(int _logSize) {
    logSize = _logSize;
    return this;
  }

  public RDFSystemDefs setBufPoolSize(int _bufPoolSize) {
    bufPoolSize = _bufPoolSize;
    return this;
  }

  public RDFSystemDefs setReplacementPolicy(String _replacementPolicy) {
    replacementPolicy = _replacementPolicy;
    return this;
  }

  public void initRdfDB() {
    try {
      bufMgr = new BufMgr(bufPoolSize, replacementPolicy);
      rdfDB = new RdfDB(dbPath, numPgs, indexOption);
      rdfDB.initRdfDB();
    } catch (Exception e) {
      System.err.println("Error while initializing RDFSystemDef");
      e.printStackTrace();
    }
  }
}
