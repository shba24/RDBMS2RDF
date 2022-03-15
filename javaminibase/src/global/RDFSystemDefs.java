package global;

import bufmgr.BufMgr;
import db.IndexOption;
import diskmgr.rdf.RdfDB;

public class RDFSystemDefs extends SystemDefs {
  /**
   *
   * @param rdfDBName
   * @param num_pgs
   * @param bufpoolsize
   * @param replacement_policy
   * @param indexOption
   */
  public RDFSystemDefs(String rdfDBName, int num_pgs, int bufpoolsize, String replacement_policy, IndexOption indexOption)
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
        ((RdfDB)JavabaseDB).openRdfDB(JavabaseDBName, indexOption); //open exisiting rdf database
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
        ((RdfDB)JavabaseDB).createRdfDB(rdfDBName, numberOfPages, indexOption);
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
