package diskmgr.rdf;

import btree.AddFileEntryException;
import btree.ConstructPageException;
import btree.GetFileEntryException;
import btree.IteratorException;
import btree.KeyNotMatchException;
import btree.PinPageException;
import btree.ScanIteratorException;
import btree.StringKey;
import btree.UnpinPageException;
import db.IndexOption;
import diskmgr.DB;
import diskmgr.IndexingSchemes.IndexScheme;
import diskmgr.IndexingSchemes.IndexSchemeFactory;
import global.AttrType;
import global.EID;
import global.GlobalConst;
import global.LID;
import global.PID;
import global.QID;
import global.QuadOrder;
import heap.FieldNumberOutOfBoundException;
import heap.InvalidTupleSizeException;
import heap.InvalidTypeException;
import heap.Label;
import heap.Quadruple;
import heap.labelheap.LabelHeapFile;
import heap.quadrupleheap.QuadrupleHeapFile;
import iterator.QuadrupleUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class is an abstraction of the RDF database.
 * Responsibilities of this class include
 * 1. Creating and managing storage files (heap files for entities, predicates and quadruples) and
 * indexes (btree index)
 * 2. Maintaining counts of quadruples, entities, predicates, subjects,
 * objects in the database.
 * 3. Insertion and deletion of entities, predicates, quadruples into their
 * respective files.
 */
public class RdfDB extends DB {
  private IndexOption indexOption;
  private String rdfDBPath;

  /**
   * Storage files for the database.
   */
  private QuadrupleHeapFile quadrupleHeapFile;
  private LabelHeapFile entityHeapFile;
  private LabelHeapFile predicateHeapFile;

  /**
   * Index file for the predicate and entity.
   * This helps with checking the duplicate
   * predicate and entity.
   */
  private btree.lablebtree.BTreeFile predicateBtreeFile;
  private btree.lablebtree.BTreeFile entityBtreeFile;
  /**
   * quadIndexScheme is created to check the duplicate
   * quadruples in the database.
   * As mentioned in the project description, we will
   * have to detect duplicate quadruples.
   */
  private btree.quadbtree.BTreeFile quadBtreeFile;

  /**
   * indexScheme is the option provided for the database during
   * the insert of the quadruples
   */
  private IndexScheme indexScheme;

  /**
   * Default Constructor.
   * Needs to call initRdfDB() to initialize the database
   *
   * @param _rdfDBPath
   * @param _numberOfPages
   * @param _indexOption
   */
  public RdfDB(String _rdfDBPath, int _numberOfPages, IndexOption _indexOption) throws IOException {
    rdfDBPath = _rdfDBPath;
    indexOption = _indexOption;
    num_pages = _numberOfPages;
  }

  /**
   * Opens the DB with num_pgs
   * @param num_pgs
   * @throws IOException
   */
  public void openDB(int num_pgs) throws IOException {
    Path dbPath = Paths.get(rdfDBPath);
    if (!Files.exists(dbPath.getParent())) {
      Files.createDirectories(dbPath.getParent());
    }
    try {
      this.openDB(rdfDBPath, num_pgs);
    } catch (Exception e) {
      System.err.println("Error in opening the DB.");
      e.printStackTrace();
    }
  }

  /**
   * Does clean up of the index files and heap files.
   *
   * @throws Exception
   */
  public void close()
      throws Exception {
    predicateBtreeFile.close();
    entityBtreeFile.close();
    quadBtreeFile.close();
    indexScheme.close();
  }

  /**
   * Needs to be called after the constructor is called
   * to initialize the database.
   *
   * @throws ConstructPageException
   * @throws GetFileEntryException
   * @throws PinPageException
   */
  public void initRdfDB()
      throws ConstructPageException, GetFileEntryException, PinPageException, IOException, AddFileEntryException {
    initHeapFiles();
    initIndexFiles();
    QuadrupleUtils.init(entityHeapFile, predicateHeapFile);
  }

  /**
   * Creates Quadruple heap files
   *
   * @param fileNameTokens
   * @return
   */
  private QuadrupleHeapFile createQuadHeapFile(String[] fileNameTokens) {
    QuadrupleHeapFile heapFile = null;
    try {
      heapFile = new QuadrupleHeapFile(generateFilePath(fileNameTokens));
    } catch (Exception e) {
      System.err.println("Error while creating Quadruple Heap File.");
      e.printStackTrace();
    }
    return heapFile;
  }

  /**
   * Creates label heap files
   *
   * @param fileNameTokens
   * @return
   */
  private LabelHeapFile createLabelHeapFile(String[] fileNameTokens) {
    LabelHeapFile heapFile = null;
    try {
      heapFile = new LabelHeapFile(generateFilePath(fileNameTokens));
    } catch (Exception e) {
      System.err.println("Error while creating Label Heap File.");
      e.printStackTrace();
    }
    return heapFile;
  }

  /**
   * Creates three heap files
   * 1. Quadruple Heap File
   * 2. Entity Label Heap File
   * 3. Predicate Label Heap File
   */
  private void initHeapFiles() {
    /**
     * Create Quadruple heap file
     */
    String[] quadFileNameToken = new String[] {
        GlobalConst.HEAP_FILE_IDENTIFIER,
        GlobalConst.QUADRUPLE_IDENTIFIER
    };
    quadrupleHeapFile = createQuadHeapFile(quadFileNameToken);

    /**
     * Create Entity Label file
     */
    String[] entityFileNameToken = new String[] {
        GlobalConst.HEAP_FILE_IDENTIFIER,
        GlobalConst.ENTITY_IDENTIFIER
    };
    entityHeapFile = createLabelHeapFile(entityFileNameToken);

    /**
     * Create Predicate Label file
     */
    String[] predicateFileNameToken = new String[] {
        GlobalConst.HEAP_FILE_IDENTIFIER,
        GlobalConst.PREDICATE_IDENTIFIER
    };
    predicateHeapFile = createLabelHeapFile(predicateFileNameToken);
  }

  /**
   * Creates Label Btree File
   *
   * @param fileNameTokens
   * @return
   */
  private btree.lablebtree.BTreeFile createLabelBTreeFile(String[] fileNameTokens) {
    btree.lablebtree.BTreeFile btreeFile = null;
    try {
      btreeFile = new btree.lablebtree.BTreeFile(
          generateFilePath(fileNameTokens),
          AttrType.attrString,
          GlobalConst.DEFAULT_KEY_SIZE,
          GlobalConst.NAIVE_DELETE_FASHION);
    } catch (Exception e) {
      System.err.println("Error while creating BTree File.");
      e.printStackTrace();
    }
    return btreeFile;
  }

  /**
   * Creates Quadruple Btree file
   *
   * @param fileNameTokens
   * @return
   */
  private btree.quadbtree.BTreeFile createQuadBTreeFile(String[] fileNameTokens) {
    btree.quadbtree.BTreeFile btreeFile = null;
    try {
      btreeFile = new btree.quadbtree.BTreeFile(
          generateFilePath(fileNameTokens),
          AttrType.attrString,
          GlobalConst.DEFAULT_KEY_SIZE,
          GlobalConst.NAIVE_DELETE_FASHION);
    } catch (Exception e) {
      System.err.println("Error while creating BTree File.");
      e.printStackTrace();
    }
    return btreeFile;
  }

  /**
   * Default indexes that we need to create to get
   * the predicate and Entity names.
   * - Predicate Name Index
   * - Entity Name Index
   * - Quadruple (Subject Predicate Object) Index
   * Additionally,
   * Creates indexes according to the indexOption
   * 1. IndexOption.Confidence
   * - ConfidenceIndexScheme
   * 2. IndexOption.Object
   * - ObjectIndexScheme
   * 3. IndexOption.Subject
   * - SubjectIndexScheme
   * 4. IndexOption.Predicate
   * - PredicateIndexScheme
   * 5. IndexOption.SubjectPredicateObjectConfidence
   * - SubjectPredicateObjectConfidenceIndexScheme
   */
  private void initIndexFiles()
      throws ConstructPageException, GetFileEntryException, PinPageException, AddFileEntryException, IOException {
    /**
     * Create Default BTree Files
     */
    String[] predicateBtreeFileToken = new String[] {
        GlobalConst.BTREE_FILE_IDENTIFIER,
        GlobalConst.PREDICATE_IDENTIFIER
    };
    predicateBtreeFile = createLabelBTreeFile(predicateBtreeFileToken);

    String[] entityBtreeFileToken = new String[] {
        GlobalConst.BTREE_FILE_IDENTIFIER,
        GlobalConst.ENTITY_IDENTIFIER
    };
    entityBtreeFile = createLabelBTreeFile(entityBtreeFileToken);
    /**
     * Create index on quadruples to check the duplicate quadruples
     */
    String[] quadBtreeFileToken = new String[]{
        GlobalConst.BTREE_FILE_IDENTIFIER,
        GlobalConst.QUADRUPLE_IDENTIFIER
    };
    quadBtreeFile = createQuadBTreeFile(quadBtreeFileToken);

    /**
     * Create BTreeFile According to Index Scheme
     */
    indexScheme = IndexSchemeFactory.createIndexScheme(indexOption);
  }

  /**
   * Get the quadruple count in the database.
   *
   * @return
   */
  public int getQuadrupleCount() {
    int quadrupleCount = -1;
    try {
      if (quadrupleHeapFile != null) {
        quadrupleCount = quadrupleHeapFile.getQuadrupleCnt();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return quadrupleCount;
  }

  /**
   * Get the entity count in the database.
   *
   * @return
   */
  public int getEntityCount() {
    int entityCount = -1;
    try {
      if (entityHeapFile != null) {
        entityCount = entityHeapFile.getRecCnt();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return entityCount;
  }

  /**
   * Get Predicate count in the database.
   *
   * @return
   */
  public int getPredicateCount() {
    int predicatesCount = -1;
    try {
      if (predicateHeapFile != null) {
        predicatesCount = predicateHeapFile.getRecCnt();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return predicatesCount;
  }

  /**
   * Get count of the unique Subject count from the RDFDB.
   *
   * @return
   */
  public int getSubjectCount() {
    return getEntityCount();
  }

  /**
   * Returns unique object count from RDFDB
   *
   * @return
   */
  public int getObjectCount() {
    return getEntityCount();
  }

  /**
   * Gets the Entity if already exists.
   *
   * @param entityLabelName
   * @return
   * @throws IteratorException
   * @throws ConstructPageException
   * @throws KeyNotMatchException
   * @throws PinPageException
   * @throws IOException
   * @throws UnpinPageException
   * @throws ScanIteratorException
   */
  private EID getEntity(String entityLabelName)
      throws Exception {
    StringKey lo_key = new StringKey(entityLabelName);
    StringKey hi_key = new StringKey(entityLabelName);
    btree.lablebtree.BTFileScan scan = entityBtreeFile.new_scan(lo_key, hi_key);
    btree.lablebtree.KeyDataEntry entry = scan.get_next();
    scan.DestroyBTreeFileScan();
    if (entry != null) {
      return ((btree.lablebtree.LeafData) entry.data).getData().getEntityID();
    } else {
      return null;
    }
  }

  /**
   * Insert the Entity in the EntityHeapFile.
   * Also returns the existing entity id if
   * already exists.
   *
   * @param entityLabelName
   * @return
   * @throws FieldNumberOutOfBoundException
   * @throws InvalidTupleSizeException
   * @throws IOException
   * @throws InvalidTypeException
   */
  public EID insertEntity(String entityLabelName)
      throws Exception {
    EID eid = getEntity(entityLabelName);
    if (eid == null) {
      Label entityLabel = new Label(entityLabelName);
      LID entityLabelId = null;

      byte[] entityLabelBytes = entityLabel.getTupleByteArray();
      try {
        entityLabelId = entityHeapFile.insertLabel(entityLabelBytes);
        entityBtreeFile.insert(new StringKey(entityLabelName), entityLabelId);
      } catch (Exception e) {
        System.err.println("Error while inserting entity into the database.");
        e.printStackTrace();
      }
      return entityLabelId.getEntityID();
    }
    return eid;
  }

  /**
   * Deletes the entity with entityLabelName
   * if it exists in the entityBtreeFile.
   * <p>
   * Returns true if the entity was found and deleted
   * Otherwise, returns false if the entity was not found.
   *
   * @param entityLabelName
   * @return
   */
  public boolean deleteEntity(String entityLabelName)
      throws Exception {
    StringKey lo_key = new StringKey(entityLabelName);
    StringKey hi_key = new StringKey(entityLabelName);
    btree.lablebtree.BTFileScan scan = entityBtreeFile.new_scan(lo_key, hi_key);
    btree.lablebtree.KeyDataEntry entry = scan.get_next();
    if (entry != null) {
      LID lid = ((btree.lablebtree.LeafData) entry.data).getData();
      entityHeapFile.deleteLabel(lid);
      scan.delete_current();
      scan.DestroyBTreeFileScan();
      return true;
    }
    return false;
  }

  /**
   * Gets the Predicate if already exists.
   *
   * @param predicateLabelName
   * @return
   * @throws IteratorException
   * @throws ConstructPageException
   * @throws KeyNotMatchException
   * @throws PinPageException
   * @throws IOException
   * @throws UnpinPageException
   * @throws ScanIteratorException
   */
  private PID getPredicate(String predicateLabelName)
      throws Exception {
    StringKey lo_key = new StringKey(predicateLabelName);
    StringKey hi_key = new StringKey(predicateLabelName);
    btree.lablebtree.BTFileScan scan = predicateBtreeFile.new_scan(lo_key, hi_key);
    btree.lablebtree.KeyDataEntry entry = scan.get_next();
    scan.DestroyBTreeFileScan();
    if (entry != null) {
      return ((btree.lablebtree.LeafData) entry.data).getData().getPredicateID();
    } else {
      return null;
    }
  }

  /**
   * Insert the Predicate in the PredicateHeapFile.
   * Also returns the existing predicate id if
   * already exists.
   *
   * @param predicateLabelName
   * @return
   * @throws FieldNumberOutOfBoundException
   * @throws InvalidTupleSizeException
   * @throws IOException
   * @throws InvalidTypeException
   */
  public PID insertPredicate(String predicateLabelName)
      throws Exception {
    PID pid = getPredicate(predicateLabelName);
    if (pid == null) {
      Label predicateLabel = new Label(predicateLabelName);
      LID predicateLabelId = null;

      byte[] predicateLabelBytes = predicateLabel.getTupleByteArray();
      try {
        predicateLabelId = predicateHeapFile.insertLabel(predicateLabelBytes);
        predicateBtreeFile.insert(new StringKey(predicateLabelName), predicateLabelId);
      } catch (Exception e) {
        System.err.println("Error while inserting entity into the database.");
        e.printStackTrace();
      }
      return predicateLabelId.getPredicateID();
    }
    return pid;
  }

  /**
   * Deletes the predicate with predicateLabelName
   * if it exists in the predicateBtreeFile.
   *
   * @param predicateLabelName
   * @return
   */
  public boolean deletePredicate(String predicateLabelName)
      throws Exception {
    StringKey lo_key = new StringKey(predicateLabelName);
    StringKey hi_key = new StringKey(predicateLabelName);
    btree.lablebtree.BTFileScan scan = predicateBtreeFile.new_scan(lo_key, hi_key);
    btree.lablebtree.KeyDataEntry entry = scan.get_next();
    if (entry != null) {
      LID lid = ((btree.lablebtree.LeafData) entry.data).getData();
      predicateHeapFile.deleteLabel(lid);
      scan.delete_current();
      scan.DestroyBTreeFileScan();
      return true;
    }
    return false;
  }

  /**
   * Searches for the quadruple with the <Subject, Predicate, Object>
   * Doesn't include Confidence in the search as only one pair
   * exits with same <Subject, Predicate, Object> as mentioned in the
   * project details.
   *
   * @param quadruple
   * @return
   * @throws Exception
   */
  private QID getQuadrupleWithoutConfidence(Quadruple quadruple)
      throws Exception {
    byte[] key = quadruple.getQuadWithoutConfidence();
    StringKey lo_key = new StringKey(new String(key));
    StringKey hi_key = new StringKey(new String(key));
    btree.quadbtree.BTFileScan scan = quadBtreeFile.new_scan(lo_key, hi_key);
    btree.quadbtree.KeyDataEntry entry = scan.get_next();
    if (entry != null) {
      QID qid = ((btree.quadbtree.LeafData) entry.data).getData();
      scan.DestroyBTreeFileScan();
      return qid;
    }
    return null;
  }

  /**
   * Creates a new quadruple and pushes in the quadrupleHeapFile
   * as well as adds to the index that we maintain,
   * i.e. quadIndexScheme and indexScheme.
   *
   * @param quadruple
   * @return
   * @throws Exception
   */
  private QID createQuadruple(Quadruple quadruple) throws Exception {
    StringKey quadKey = new StringKey(new String(quadruple.getQuadWithoutConfidence()));
    QID newQid = quadrupleHeapFile.insertQuadruple(quadruple.getTupleByteArray());
    quadBtreeFile.insert(quadKey, newQid);
    indexScheme.insert(quadruple, newQid, entityHeapFile, predicateHeapFile);
    return newQid;
  }

  /**
   * Inserts the quadruple bytes into quadruple heap file.
   * Checks if quadruples already exists with less confidence
   * if it does, it deletes the old one and inserts the new one
   * into quadrupleHeapFile, quadIndexScheme and indexScheme.
   * If it doesn't, then it creates the new one and inserts.
   *
   * @param quadruple
   * @return
   */
  public QID insertQuadruple(Quadruple quadruple) throws Exception {
    QID oldQid = getQuadrupleWithoutConfidence(quadruple);
    if (oldQid != null) {
      Quadruple oldQuad = quadrupleHeapFile.getQuadruple(oldQid);
      oldQuad.setDefaultHeader();
      if (oldQuad.getConfidence() < quadruple.getConfidence()) {
        quadrupleHeapFile.updateQuadruple(oldQid, quadruple);
      }
      return oldQid;
    } else {
      return createQuadruple(quadruple);
    }
  }

  /**
   * Initialize a stream of quadruples, where the subject
   * label matches subjectFilter, predicate label matches
   * predicateFilter, object label matches objectFilter,
   * and confidence is greater than or equal to the
   * confidenceFilter. If any of the filters are null strings
   * or 0, then that filter is not considered (e.g., if
   * subjectFilter is null, then all subject labels are OK).
   *
   * @param orderType
   * @param subjectFilter
   * @param predicateFilter
   * @param objectFilter
   * @param confidenceFilter
   * @return
   * @throws Exception
   */
  public IStream openStream(
      QuadOrder orderType,
      int numBuf,
      String subjectFilter,
      String predicateFilter,
      String objectFilter,
      Float confidenceFilter) throws Exception {
    return indexScheme.getStream(
        orderType,
        numBuf,
        subjectFilter,
        predicateFilter,
        objectFilter,
        confidenceFilter,
        quadrupleHeapFile,
        entityHeapFile,
        predicateHeapFile);
  }

  private String generateFilePath(String[] identifiers) {
    StringBuilder fileName = new StringBuilder();

    for (int i = 0; i < identifiers.length - 1; i++) {
      fileName.append(identifiers[i]);
      fileName.append("-");
    }

    fileName.append(identifiers[identifiers.length - 1]);

    return Paths.get(fileName.toString()).toString();
  }
}
