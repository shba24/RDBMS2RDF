package global;

import bufmgr.BufMgr;
import catalog.Catalog;
import db.IndexOption;
import diskmgr.DB;
import diskmgr.rdf.RdfDB;

public class SystemDefs {

  public static BufMgr JavabaseBM;
  public static RdfDB JavabaseDB;
  public static Catalog JavabaseCatalog;

  public static String JavabaseDBName;
  public static String JavabaseLogName;
  public static boolean MINIBASE_RESTART_FLAG = false;
  public static String MINIBASE_DBNAME;

  public SystemDefs() {
  }

  public SystemDefs(
      String dbname, int num_pgs, int bufpoolsize,
      String replacement_policy) {
    int logsize;

    String real_logname = new String(dbname);
    String real_dbname = new String(dbname);

    if (num_pgs == 0) {
      logsize = 500;
    } else {
      logsize = 3 * num_pgs;
    }

    if (replacement_policy == null) {
      replacement_policy = new String("Clock");
    }

    init(real_dbname, real_logname, num_pgs, logsize,
        bufpoolsize, replacement_policy);
  }

  public void init(
      String dbname, String logname,
      int num_pgs, int maxlogsize,
      int bufpoolsize, String replacement_policy) {

    boolean status = true;
    JavabaseBM = null;
    JavabaseDB = null;
    JavabaseDBName = null;
    JavabaseLogName = null;
    JavabaseCatalog = null;

    try {
      JavabaseBM = new BufMgr(bufpoolsize, replacement_policy);
      JavabaseDB = (RdfDB) new DB();

//	JavabaseCatalog = new Catalog();

    } catch (Exception e) {
      System.err.println("" + e);
      e.printStackTrace();
      Runtime.getRuntime().exit(1);
    }

    JavabaseDBName = new String(dbname);
    JavabaseLogName = new String(logname);
    MINIBASE_DBNAME = new String(JavabaseDBName);

    // create or open the DB

    if ((MINIBASE_RESTART_FLAG) || (num_pgs == 0)) {//open an existing database
      try {
        JavabaseDB.openDB(dbname);
      } catch (Exception e) {
        System.err.println("" + e);
        e.printStackTrace();
        Runtime.getRuntime().exit(1);
      }
    } else {
      try {
        JavabaseDB.openDB(dbname, num_pgs);
        JavabaseBM.flushAllPages();
      } catch (Exception e) {
        System.err.println("" + e);
        e.printStackTrace();
        Runtime.getRuntime().exit(1);
      }
    }
  }


  /**
   *
   * @param rdfDBName
   * @param num_pgs
   * @param bufpoolsize
   * @param replacement_policy
   * @param indexOption
   */
  public SystemDefs(String rdfDBName, int num_pgs, int bufpoolsize, String replacement_policy, IndexOption indexOption)
  {
    int logsize;

    String logFileName = new String(rdfDBName);

    System.out.println(rdfDBName);

    if (num_pgs == 0) {
      logsize = 500;
    }
    else {
      logsize = 3 * num_pgs;
    }

    if (replacement_policy == null) {
      replacement_policy = new String("Clock");
    }

    initRdfDB(rdfDBName, logFileName, num_pgs, logsize, bufpoolsize, replacement_policy, indexOption);
  }

  public void initRdfDB(String rdfDBName, String logFileName, int numberOfPages, int maxLogSize, int bufferPoolSize, String replacementPolicy, IndexOption indexOption)
  {

    boolean status = true;
    JavabaseBM = null;
    JavabaseDB = null;
    JavabaseDBName = null;
    JavabaseLogName = null;
    JavabaseCatalog = null;

    try {
      JavabaseBM = new BufMgr(bufferPoolSize, replacementPolicy);
      JavabaseDB = new RdfDB();
			/*
			   JavabaseCatalog = new Catalog();
			 */
    }
    catch (Exception e) {
      System.err.println (""+e);
      e.printStackTrace();
      Runtime.getRuntime().exit(1);
    }

    JavabaseDBName = new String(rdfDBName);
    JavabaseLogName = new String(logFileName);
    MINIBASE_DBNAME = new String(JavabaseDBName);

    // create or open the DB

    if ((MINIBASE_RESTART_FLAG)||(numberOfPages == 0)){//open an existing database
      try {
        System.out.println("***Opening existing database***");
        JavabaseDB.openRdfDB(JavabaseDBName, indexOption); //open exisiting rdf database
      }
      catch (Exception e) {
        System.err.println (""+e);
        e.printStackTrace();
        Runtime.getRuntime().exit(1);
      }
    }
    else {
      try {
        System.out.println("***Creating new database***");
        JavabaseDB.createRdfDB(rdfDBName, numberOfPages, indexOption);
        JavabaseBM.flushAllPages();
      }
      catch (Exception e) {
        System.err.println (""+e);
        e.printStackTrace();
        Runtime.getRuntime().exit(1);
      }
    }
  }
}
