package tests;

import db.IndexOption;
import global.GlobalConst;
import global.PageId;
import global.RDFSystemDefs;
import global.SystemDefs;
import java.io.IOException;

public class RdfDBTest {

  private static class RdfDBDriver extends TestDriver implements GlobalConst {
    private PageId runStart = new PageId();
    private boolean OK = true;
    private boolean FAIL = false;

    public RdfDBDriver() {
      super("rdfdbtest");
    }

    @Override
    protected String testName() {
      return "RDF DB Disk Space Management";
    }

    @Override
    public boolean runTests() {

      System.out.println("\n" + "Running " + testName() + " tests...." + "\n");

      // Kill anything that might be hanging around
      String newdbpath;
      String newlogpath;
      String remove_logcmd;
      String remove_dbcmd;
      String remove_cmd = "/bin/rm -rf ";

      newdbpath = dbpath;
      newlogpath = logpath;

      remove_logcmd = remove_cmd + logpath;
      remove_dbcmd = remove_cmd + dbpath;

      // Commands here is very machine dependent.  We assume
      // user are on UNIX system here
      try {
        Runtime.getRuntime().exec(remove_logcmd);
        Runtime.getRuntime().exec(remove_dbcmd);
      } catch (IOException e) {
        System.err.println("" + e);
      }

      remove_logcmd = remove_cmd + newlogpath;
      remove_dbcmd = remove_cmd + newdbpath;

      //This step seems redundant for me.  But it's in the original
      //C++ code.  So I am keeping it as of now, just in case I
      //I missed something
      try {
        Runtime.getRuntime().exec(remove_logcmd);
        Runtime.getRuntime().exec(remove_dbcmd);
      } catch (IOException e) {
        System.err.println("" + e);
      }

      //Run the tests. Return type different from C++
      boolean _pass = runAllTests();

      //Clean up again
      try {
        Runtime.getRuntime().exec(remove_logcmd);
        Runtime.getRuntime().exec(remove_dbcmd);
      } catch (IOException e) {
        System.err.println("" + e);
      }

      System.out.print("\n" + "..." + testName() + " tests ");
      System.out.print(_pass == OK ? "completely successfully" : "failed");
      System.out.print(".\n\n");

      return _pass;
    }

    protected boolean runAllTests() {

      boolean _passAll = OK;

      if (!test1()) {
        _passAll = FAIL;
      }
      if (!test2()) {
        _passAll = FAIL;
      }
      if (!test3()) {
        _passAll = FAIL;
      }
      if (!test4()) {
        _passAll = FAIL;
      }
      if (!test5()) {
        _passAll = FAIL;
      }
      if (!test6()) {
        _passAll = FAIL;
      }
      try {
        SystemDefs.JavabaseDB.DBDestroy();
      } catch (IOException e) {
        System.err.println(" DB already destroyed");
      }
      return _passAll;
    }

    /**
     * Creates a new database instance with the given name, number of pages and index option.
     *
     * @return boolean test status
     */
    @Override
    protected boolean test1() {
      String rdfDBName = "/tmp/rdf/test";
      IndexOption indexOption = IndexOption.Confidence;

      RDFSystemDefs.init(rdfDBName, indexOption);
      return true;
    }
  }

  public static void main(String argv[]) {

    RdfDBDriver driver = new RdfDBDriver();

    boolean testsStatus = driver.runTests();

    if (testsStatus) {
      System.out.println("Successfully executed all RDB DB tests.");
    } else {
      System.err.println("Error encountered during RDF DB tests:\n");
      Runtime.getRuntime().exit(1);
    }

    Runtime.getRuntime().exit(0);
  }
}
