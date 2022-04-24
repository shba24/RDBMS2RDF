package global;

import btree.DeleteFashion;

public interface GlobalConst {

  public static final int MINIBASE_MAXARRSIZE = 50;
  /**
   * As the initial data for Phase 2 needs
   * more than 50 Buffer pages at a time, we
   * have increased it to 100.
   */
  public static final int NUMBUF = 500;
  /**
   * As the initial data for Phase 2 have
   * 178k quadruples to be inserted into
   * the database, we need at least 200MB
   * to run it properly.
   */
  public static final int DEFAULT_DB_PAGES = 1024 * 10;

  /**
   * Size of page.
   */
  public static final int MINIBASE_PAGESIZE = 1024;           // in bytes

  /**
   * Size of each frame.
   */
  public static final int MINIBASE_BUFFER_POOL_SIZE = 1024;   // in Frames

  public static final int MAX_SPACE = 1024;   // in Frames

  /**
   * in Pages => the DBMS Manager tells the DB how much disk space is available for the database.
   */
  public static final int MINIBASE_DB_SIZE = 10000;
  public static final int MINIBASE_MAX_TRANSACTIONS = 100;
  public static final int MINIBASE_DEFAULT_SHAREDMEM_SIZE = 1000;

  /**
   * also the name of a relation
   */
  public static final int MAXFILENAME = 15;
  public static final int MAXINDEXNAME = 40;
  public static final int MAXATTRNAME = 15;
  /**
   * As the size of the name is not just
   * limited to 50 character in size, we
   * increased it to 300 characters.
   */
  public static final int MAX_NAME = 300;

  public static final int INVALID_PAGE = -1;
  /**
   * ALL_DELETE_FASHION causes problem with the
   * indexing as ALL_DELETE_FASHION actively
   * disposes the pages which makes the Quadruple
   * which stores the page number of entities as invalid
   * when we try to get them later.
   * ALL_DELETE_FASHION also makes the actively deleted
   * pages available for usage which creates the problem
   * of use after free if asked for the deleted page.
   */
  public static final int FULL_DELETE_FASHION = DeleteFashion.FULL_DELETE;
  public static final int NAIVE_DELETE_FASHION = DeleteFashion.NAIVE_DELETE;
  public static final int DEFAULT_KEY_SIZE = 200;

  /**
   * Global constants for directory, path, files and folder names
   */
  public static String DATA_FILE = "data.dat";
  public static String ROOT_FOLDER = "data";
  public static String DEFAULT_LOG_FILENAME = "default.log";
  public static String HEAP_FILE_IDENTIFIER = "heapfile";
  public static String BTREE_FILE_IDENTIFIER = "btreefile";
  public static String QUADRUPLE_IDENTIFIER = "quadruple";
  public static String ENTITY_IDENTIFIER = "entity";
  public static String PREDICATE_IDENTIFIER = "predicate";
  public static String PID_IDENTIFIER = "PID";
  public static String SID_IDENTIFIER = "SID";
  public static String OID_IDENTIFIER = "OID";
  public static String INDEX_IDENTIFIER = "index";
  public static String JSON_FILE = "telemetry.json";
  public static String READS = "Reads";
  public static String WRITES = "Writes";
  public static String PAGE_SIZE = "PageSize";
  public static String BUFF_SIZE = "BuffSize";
  public static String UNIQUE_ENTITIES = "UniqueEntities";
  public static String UNIQUE_PREDICATES = "UniquePredicates";
  public static String TOTAL_QUADRUPLES = "TotalQuadruples";

  /**
   * Global constants for basics
   */
  public static String DEFAULT_REPLACEMENT_POLICY = "Clock";

  /**
   * also maximum object size when stored in byte array
   */
  public static short MAX_PID_OBJ_SIZE = 8;
  public static short MAX_EID_OBJ_SIZE = 8;
  public static short MAX_FLOAT_SIZE = 8;
}
