package global;

import bufmgr.BufMgr;
import db.IndexOption;
import db.Telemetry;
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
    telemetry = new Telemetry(rdfDbPath);
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

  public static void close() throws Exception {
    /**
     * Do the cleanup of the DB first to clean up
     * Page pins then flush all buffer manager pages
     * so that in-memory changes are persisted in files.
     * Also flushes telemetry to the file.
     */
    telemetry.flush();
    ((RdfDB)SystemDefs.JavabaseDB).close();
    SystemDefs.JavabaseBM.printPinnedBuffer();
    SystemDefs.JavabaseBM.flushAllPages();
  }
}
