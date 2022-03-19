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
public class RDFSystemDefs extends SystemDefs {
  private static IndexOption indexOption;
  private static int numPgs = GlobalConst.DEFAULT_DB_PAGES;
  private static int logSize = 3 * GlobalConst.DEFAULT_DB_PAGES;
  private static int bufPoolSize = GlobalConst.NUMBUF;
  private static String replacementPolicy = GlobalConst.DEFAULT_REPLACEMENT_POLICY;

  /**
   * Default constructor for the RDFSystemDefs
   *
   * @param rdfDbPath
   * @param _indexOption
   */
  public static void init(
      String rdfDbPath,
      IndexOption _indexOption,
      int _bufPoolSize) {
    bufPoolSize = _bufPoolSize;
    JavabaseDBName = rdfDbPath;
    indexOption = _indexOption;
    JavabaseLogName = Paths.get(
        JavabaseDBName,
        GlobalConst.DEFAULT_LOG_FILENAME
    ).toString();
    initRdfDB();
  }

  /**
   * Initializes RDF DB
   */
  public static void initRdfDB() {
    try {
      JavabaseBM = new BufMgr(bufPoolSize, replacementPolicy);
      RdfDB rdfDB = new RdfDB(JavabaseDBName, numPgs, indexOption);
      JavabaseDB = rdfDB;
      rdfDB.openDB(numPgs);
      rdfDB.initRdfDB();
    } catch (Exception e) {
      System.err.println("Error while initializing RDFSystemDef");
      e.printStackTrace();
    }
  }
}
