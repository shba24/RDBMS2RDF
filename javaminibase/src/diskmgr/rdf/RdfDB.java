package diskmgr.rdf;


import btree.ConstructPageException;
import btree.GetFileEntryException;
import btree.IteratorException;
import btree.KeyNotMatchException;
import btree.PinPageException;
import btree.ScanIteratorException;
import btree.StringKey;
import btree.UnpinPageException;
import bufmgr.HashEntryNotFoundException;
import bufmgr.InvalidFrameNumberException;
import bufmgr.PageUnpinnedException;
import bufmgr.ReplacerException;
import db.IndexOption;
import diskmgr.DB;
import diskmgr.IndexingSchemes.IndexScheme;
import diskmgr.IndexingSchemes.IndexSchemeFactory;
import global.EID;
import global.GlobalConst;
import global.LID;
import global.PID;
import global.QID;
import heap.FieldNumberOutOfBoundException;
import heap.InvalidTupleSizeException;
import heap.InvalidTypeException;
import heap.Label;
import heap.Quadruple;
import heap.labelheap.LabelHeapFile;
import heap.quadrupleheap.QuadrupleHeapFile;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * This class is an abstraction of the RDF database.
 * Responsibilities of this class include
 * 1. Creating and managing storage files (heap files for entities, predicates and quadruples) and
 *    indexes (btree index)
 * 2. Maintaining counts of quadruples, entities, predicates, subjects,
 *    objects in the database.
 * 3. Insertion and deletion of entities, predicates, quadruples into their
 *    respective files.
 */
public class RdfDB {
  private DB baseDb;
  private IndexOption indexOption;
  private String rdfDBPath;

  /**
   * Storage files for the database.
   */
  private QuadrupleHeapFile quadrupleHeapFile;
  private LabelHeapFile entityHeapFile;
  private LabelHeapFile predicateHeapFile;

  private btree.lablebtree.BTreeFile predicateBtreeFile;
  private btree.lablebtree.BTreeFile entityBtreeFile;

  /**
   * quadIndexScheme is created to check the duplicate
   * quadruples in the database.
   * As mentioned in the project description, we will
   * have to detect duplicate quadruples.
   */
  private IndexScheme quadIndexScheme;
  private IndexScheme indexScheme;

  /**
   * Default Constructor.
   * Needs to call initRdfDB() to initialize the database
   *
   * @param _rdfDBPath
   * @param _numberOfPages
   * @param _indexOption
   */
  public RdfDB(String _rdfDBPath, int _numberOfPages, IndexOption _indexOption) {
    baseDb = new DB();
    rdfDBPath = _rdfDBPath;
    indexOption = _indexOption;
    try {
      this.baseDb.openDB(rdfDBPath, _numberOfPages);
    } catch (Exception e) {
      System.err.println("Error in opening the DB.");
      e.printStackTrace();
    }
  }

  /**
   * Needs to be called after the constructor is called
   * to initialize the database.
   *
   * @throws ConstructPageException
   * @throws GetFileEntryException
   * @throws PinPageException
   */
  public void initRdfDB() throws ConstructPageException, GetFileEntryException, PinPageException {
    initHeapFiles();
    initIndexFiles();
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
    String[] quadFileNameToken = new String[]{
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
    String[] predicateFileNameToken = new String[]{
        GlobalConst.HEAP_FILE_IDENTIFIER,
        GlobalConst.PREDICATE_IDENTIFIER
    };
    predicateHeapFile = createLabelHeapFile(predicateFileNameToken);
  }

  private btree.lablebtree.BTreeFile createLabelBTreeFile(String[] fileNameTokens) {
    btree.lablebtree.BTreeFile btreeFile = null;
    try {
      btreeFile = new btree.lablebtree.BTreeFile(generateFilePath(fileNameTokens));
    } catch (Exception e) {
      System.err.println("Error while creating BTree File.");
      e.printStackTrace();
    }
    return btreeFile;
  }

  private btree.quadbtree.BTreeFile createQuadBTreeFile(String[] fileNameTokens) {
    btree.quadbtree.BTreeFile btreeFile = null;
    try {
      btreeFile = new btree.quadbtree.BTreeFile(generateFilePath(fileNameTokens));
    } catch (Exception e) {
      System.err.println("Error while creating BTree File.");
      e.printStackTrace();
    }
    return btreeFile;
  }

  /**
   * Default indexes that we need to create to get
   * the predicate and Entity names.
   *    - Predicate Name Index
   *    - Entity Name Index
   *    - Quadruple (Subject Predicate Object) Index
   * Additionally,
   * Creates indexes according to the indexOption
   * 1. IndexOption.Confidence
   *      - ConfidenceIndexScheme
   * 2. IndexOption.Object
   *      - ObjectIndexScheme
   * 3. IndexOption.Subject
   *      - SubjectIndexScheme
   * 4. IndexOption.Predicate
   *      - PredicateIndexScheme
   * 5. IndexOption.SubjectPredicateObjectConfidence
   *      - SubjectPredicateObjectConfidenceIndexScheme
   */
  private void initIndexFiles() throws ConstructPageException, GetFileEntryException, PinPageException {
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
    quadIndexScheme = IndexSchemeFactory.createIndexScheme(IndexOption.SubjectPredicateObject, rdfDBPath);

    /**
     * Create BTreeFile According to Index Scheme
     */
    indexScheme = IndexSchemeFactory.createIndexScheme(indexOption, rdfDBPath);
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
      throws IteratorException, ConstructPageException, KeyNotMatchException, PinPageException, IOException,
      UnpinPageException, ScanIteratorException, HashEntryNotFoundException, InvalidFrameNumberException,
      PageUnpinnedException, ReplacerException {
    StringKey lo_key = new StringKey(entityLabelName);
    StringKey hi_key = new StringKey(entityLabelName);
    btree.lablebtree.BTFileScan scan = entityBtreeFile.new_scan(lo_key, hi_key);
    btree.lablebtree.KeyDataEntry entry = scan.get_next();
    scan.DestroyBTreeFileScan();
    if (entry != null) {
      return ((btree.lablebtree.LeafData)entry.data).getData().getEntityID();
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
      throws FieldNumberOutOfBoundException, InvalidTupleSizeException, IOException, InvalidTypeException,
      IteratorException, ConstructPageException, KeyNotMatchException, ScanIteratorException, PinPageException,
      UnpinPageException, HashEntryNotFoundException, InvalidFrameNumberException, PageUnpinnedException,
      ReplacerException {
    EID eid = getEntity(entityLabelName);
    if (eid == null) {
      Label entityLabel = new Label(entityLabelName);
      LID entityLabelId = null;

      byte[] entityLabelBytes = entityLabel.returnTupleByteArray();
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
   *
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
      LID lid = ((btree.lablebtree.LeafData)entry.data).getData();
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
      throws IteratorException, ConstructPageException, KeyNotMatchException, PinPageException, IOException,
      UnpinPageException, ScanIteratorException, HashEntryNotFoundException, InvalidFrameNumberException,
      PageUnpinnedException, ReplacerException {
    StringKey lo_key = new StringKey(predicateLabelName);
    StringKey hi_key = new StringKey(predicateLabelName);
    btree.lablebtree.BTFileScan scan = predicateBtreeFile.new_scan(lo_key, hi_key);
    btree.lablebtree.KeyDataEntry entry = scan.get_next();
    scan.DestroyBTreeFileScan();
    if (entry != null) {
      return ((btree.lablebtree.LeafData)entry.data).getData().getPredicateID();
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
      throws FieldNumberOutOfBoundException, InvalidTupleSizeException, IOException, InvalidTypeException,
      IteratorException, ConstructPageException, KeyNotMatchException, ScanIteratorException, PinPageException,
      UnpinPageException, HashEntryNotFoundException, InvalidFrameNumberException, PageUnpinnedException,
      ReplacerException {
    PID pid = getPredicate(predicateLabelName);
    if (pid == null) {
      Label predicateLabel = new Label(predicateLabelName);
      LID predicateLabelId = null;

      byte[] predicateLabelBytes = predicateLabel.returnTupleByteArray();
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
      LID lid = ((btree.lablebtree.LeafData)entry.data).getData();
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
    String key = quadIndexScheme.getKey(quadruple, null, entityHeapFile, predicateHeapFile).getKey();
    StringKey lo_key = new StringKey(key);
    StringKey hi_key = new StringKey(key);
    btree.quadbtree.BTFileScan scan = quadIndexScheme.getBtreeFile().new_scan(lo_key, hi_key);
    btree.quadbtree.KeyDataEntry entry = scan.get_next();
    if (entry != null) {
      return ((btree.quadbtree.LeafData)entry.data).getData();
    }
    return null;
  }

  /**
   * Deletes the quadruples from all places it exists.
   *  - quadrupleHeapFile
   *  - quadIndexScheme
   *  - indexScheme
   *
   * @param qid
   * @param quadruple
   * @throws Exception
   */
  private void deleteQuadrupleInternal(QID qid, Quadruple quadruple) throws Exception {
    quadrupleHeapFile.deleteQuadruple(qid);
    StringKey key1 = quadIndexScheme.getKey(quadruple, null, entityHeapFile, predicateHeapFile);
    quadIndexScheme.getBtreeFile().Delete(key1, qid);
    StringKey key2 = indexScheme.getKey(quadruple, null, entityHeapFile, predicateHeapFile);
    indexScheme.getBtreeFile().Delete(key2, qid);
  }

  /**
   * Creates a new quadruple and pushes in the quadrupleHeapFile
   * as well as adds to the index that we maintain,
   * i.e. quadIndexScheme and indexScheme.
   * @param quadruple
   * @param quadrupleBytes
   * @return
   * @throws Exception
   */
  private QID createQuadruple(Quadruple quadruple, byte[] quadrupleBytes) throws Exception {
    QID newQid = quadrupleHeapFile.insertQuadruple(quadrupleBytes);
    quadIndexScheme.insert(quadruple, newQid, entityHeapFile, predicateHeapFile);
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
   * @param quadrupleBytes
   * @return
   */
  public QID insertQuadruple(byte[] quadrupleBytes) throws Exception {
    Quadruple quadruple = new Quadruple(quadrupleBytes, 0, quadrupleBytes.length);
    QID oldQid = getQuadrupleWithoutConfidence(quadruple);
    if (oldQid != null) {
      Quadruple oldQuad = quadrupleHeapFile.getQuadruple(oldQid);
      if (oldQuad.getConfidence() < quadruple.getConfidence()) {
        deleteQuadrupleInternal(oldQid, oldQuad);
        return createQuadruple(quadruple, quadrupleBytes);
      }
      return oldQid;
    } else {
      return createQuadruple(quadruple, quadrupleBytes);
    }
  }

  /**
   * Deletes Quadruple from the Quadruple heap and
   * current index
   *
   * @param quadrupleBytes
   * @return
   */
  public boolean deleteQuadruple(byte[] quadrupleBytes) throws Exception {
    Quadruple quadruple = new Quadruple(quadrupleBytes, 0, quadrupleBytes.length);
    QID oldQid = getQuadrupleWithoutConfidence(quadruple);
    if (oldQid != null) {
      Quadruple oldQuad = quadrupleHeapFile.getQuadruple(oldQid);
      // As there is only one tuple with same <Subject, Predicate, Object>
      if (oldQuad.getConfidence() == quadruple.getConfidence()) {
        deleteQuadrupleInternal(oldQid, oldQuad);
        return true;
      }
    }
    return false;
  }

  private String generateFilePath(String[] identifiers) {
    StringBuilder fileName = new StringBuilder();

    for (int i = 0; i < identifiers.length - 1; i++) {
      fileName.append(identifiers[i]);
      fileName.append("-");
    }

    fileName.append(identifiers[identifiers.length - 1]);

    return Paths.get(rdfDBPath, fileName.toString()).toString();
  }

}
