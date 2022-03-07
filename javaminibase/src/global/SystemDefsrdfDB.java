package global;

import bufmgr.BufMgr;
import catalog.Catalog;
import diskmgr.*;

public class SystemDefsrdfDB {
  public static BufMgr JavabaseBM;
  public static rdfDB JavabaserdfDB;
  public static Catalog JavabaseCatalog;

  public static String JavabaserdfDBName;
  public static String JavabaseLogName;
  public static boolean MINIBASE_RESTART_FLAG = false;
  public static String MINIBASE_RDFDBNAME;

  public SystemDefsrdfDB() {
  }

  ;

  public SystemDefsrdfDB(
      String rdfDBName, int num_pgs, int bufpoolsize,
      String replacement_policy) {
    int logsize;

    String real_logname = new String(rdfDBName);
    String real_dbname = new String(rdfDBName);

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
    JavabaserdfDB = null;
    JavabaserdfDBName = null;
    JavabaseLogName = null;
    JavabaseCatalog = null;

    try {
      JavabaseBM = new BufMgr(bufpoolsize, replacement_policy);
      JavabaserdfDB = new rdfDB();
/*
	JavabaseCatalog = new Catalog();
*/
    } catch (Exception e) {
      System.err.println("" + e);
      e.printStackTrace();
      Runtime.getRuntime().exit(1);
    }

    JavabaserdfDBName = new String(dbname);
    JavabaseLogName = new String(logname);
    MINIBASE_RDFDBNAME = new String(JavabaserdfDBName);

    // create or open the DB

    if ((MINIBASE_RESTART_FLAG) || (num_pgs == 0)) {//open an existing database
      try {
        JavabaserdfDB.openDB(dbname);
      } catch (Exception e) {
        System.err.println("" + e);
        e.printStackTrace();
        Runtime.getRuntime().exit(1);
      }
    } else {
      try {
        JavabaserdfDB.openDB(dbname, num_pgs);
        JavabaseBM.flushAllPages();
      } catch (Exception e) {
        System.err.println("" + e);
        e.printStackTrace();
        Runtime.getRuntime().exit(1);
      }
    }
  }

}
