package diskmgr.rdf;


import btree.KeyClass;
import btree.StringKey;
import btree.lablebtree.BTFileScan;
import btree.lablebtree.BTreeFile;
import btree.lablebtree.KeyDataEntry;
import btree.lablebtree.LeafData;
import db.IndexOption;
import diskmgr.DB;
import global.AttrType;
import global.EID;
import global.GlobalConst;
import global.LID;
import global.PID;
import global.PageId;
import global.QID;
import heap.Label;
import heap.labelheap.LabelHeapFile;
import heap.quadrupleheap.QuadrupleHeapFile;

/**
 * This class is an abstraction of the RDF database. Responsibilities of this class include 1.
 * Creating and managing storage files (heap files for entities, predicates and quadruples) and
 * indexes (btree index) 2. Maintaining counts of quadruples, entities, predicates, subjects,
 * objects in the database. 3. Insertion and deletion of entities, predicates, quadruples into their
 * respective files.
 */
public class RdfDB extends DB implements GlobalConst {
//  private QuadrupleHeapFile tempQuadrupleHeapFile; //TEMPORARY HEAP FILE FOR SORTING


  private final String HEAP_FILE_IDENTIFIER = "heapfile";
  private final String BTREE_FILE_IDENTIFIER = "btreefile";
  private final String QUADRUPLE_IDENTIFIER = "quadruple";
  private final String ENTITY_IDENTIFIER = "entity";
  private final String PREDICATE_IDENTIFIER = "predicate";
  private final String LABEL_IDENTIFIER = "label";
  private final String SUBJECT_IDENTIFIER = "subject";
  private final String OBJECT_IDENTIFIER = "object";
  IndexOption indexOption;
  private String rdfDBName;

  /**
   * Storage files for the database.
   */
  private String quadrupleHeapFileName;
  private String entityHeapFileName;
  private String predicateHeapFileName;
  private QuadrupleHeapFile quadrupleHeapFile;
  private LabelHeapFile entityHeapFile;
  private LabelHeapFile predicateHeapFile;

  private String quadrupleBTreeFileName;
  private btree.quadbtree.BTreeFile quadrupleBTreeFile;


  private String entityBTreeFileName;
  private BTreeFile entityBTreeFile;
  private String dublicateSubjectBTreeFileName;
  private BTreeFile dublicateSubjectBTreeFile;

  private String dublicateObjectBTreeFileName;
  private BTreeFile dublicateObjectBTreeFile;

  /** TODO
   *
   */
//  private LabelBTreeFile entitiesBTreeFile;  		//BTree Index file on Entity Heap file
//  private LabelBTreeFile predicatesBTreeFile; 	//BTree Predicate file on Predicate Heap file
//  private QuadrupleBTreeFile quadruplesBTreeFile; 		//BTree Predicate file on Predicate Heap file

//  private LabelBTreeFile dup_tree;        	//BTree file for duplicate subjects
//  private LabelBTreeFile dup_Objtree;     	//BTree file for duplicate objects
  /**
   * To store the counts of quadruples, subjects, predicates, objects and entities. We have to store
   * the count of distinct subjects and objects.
   */

  /**
   * Default Constructor
   */
  public RdfDB() {
  }

  public void createRdfDB(String rdfDBName, int numberOfPages, IndexOption indexOption) {
    this.rdfDBName = rdfDBName;
    this.indexOption = indexOption;

    try {
      openDB(this.rdfDBName, numberOfPages);

    } catch (Exception e) {
      System.err.println("" + e);
      e.printStackTrace();
      Runtime.getRuntime().exit(1);
    }

    initRdfDB(indexOption);
  }

  public void initRdfDB(IndexOption indexOption) {
    initHeapFiles();
    initIndexFiles(indexOption);
  }

  private void initHeapFiles() {
    //Create QuadrupleS heap file
    try {
      String[] quadHFNameIdentifiers = new String[]{
          rdfDBName,
          HEAP_FILE_IDENTIFIER,
          QUADRUPLE_IDENTIFIER
      };
      quadrupleHeapFileName = generateFileName(quadHFNameIdentifiers);
      quadrupleHeapFile = new QuadrupleHeapFile(quadrupleHeapFileName);

    } catch (Exception e) {
      System.err.println("" + e);
      e.printStackTrace();
      Runtime.getRuntime().exit(1);
    }

    try {
      String[] entitiesHFNameIdentifiers = new String[]{
          rdfDBName,
          HEAP_FILE_IDENTIFIER,
          ENTITY_IDENTIFIER
      };
      entityHeapFileName = generateFileName(entitiesHFNameIdentifiers);
      entityHeapFile = new LabelHeapFile(entityHeapFileName);

    } catch (Exception e) {
      System.err.println("" + e);
      e.printStackTrace();
      Runtime.getRuntime().exit(1);
    }

    try {
      String[] predicatesHFNameIdentifiers = new String[]{
          rdfDBName,
          HEAP_FILE_IDENTIFIER,
          PREDICATE_IDENTIFIER
      };
      predicateHeapFileName = generateFileName(predicatesHFNameIdentifiers);
      predicateHeapFile = new LabelHeapFile(predicateHeapFileName);

    } catch (Exception e) {
      System.err.println("" + e);
      e.printStackTrace();
      Runtime.getRuntime().exit(1);
    }
  }

  private void initIndexFiles(IndexOption indexOption) {
    try {
      String[] entityBTreeFileNameIdentifiers = new String[]{
          rdfDBName,
          BTREE_FILE_IDENTIFIER,
          ENTITY_IDENTIFIER
      };
      entityBTreeFileName = generateFileName(entityBTreeFileNameIdentifiers);
      entityBTreeFile = new BTreeFile(entityBTreeFileName);

    } catch (Exception e) {
      System.err.println("" + e);
      e.printStackTrace();
      Runtime.getRuntime().exit(1);
    }

    try {
      String[] quadrupleBTreeNameIdentifiers = new String[]{
          rdfDBName,
          BTREE_FILE_IDENTIFIER,
          QUADRUPLE_IDENTIFIER
      };
      quadrupleBTreeFileName = generateFileName(quadrupleBTreeNameIdentifiers);
      quadrupleBTreeFile = new btree.quadbtree.BTreeFile(quadrupleBTreeFileName);

    } catch (Exception e) {
      System.err.println("" + e);
      e.printStackTrace();
      Runtime.getRuntime().exit(1);
    }

    try {
      String[] dupSubjectBTreeNameIdentifiers = new String[]{
          rdfDBName,
          BTREE_FILE_IDENTIFIER,
          "Duplicate",
          SUBJECT_IDENTIFIER
      };
      dublicateSubjectBTreeFileName = generateFileName(dupSubjectBTreeNameIdentifiers);
      dublicateSubjectBTreeFile = new BTreeFile(dublicateSubjectBTreeFileName);

    } catch (Exception e) {
      System.err.println("" + e);
      e.printStackTrace();
      Runtime.getRuntime().exit(1);
    }

    try {
      String[] dupObjectBTreeNameIdentifiers = new String[]{
          rdfDBName,
          BTREE_FILE_IDENTIFIER,
          "Duplicate",
          OBJECT_IDENTIFIER
      };
      dublicateObjectBTreeFileName = generateFileName(dupObjectBTreeNameIdentifiers);
      dublicateObjectBTreeFile = new BTreeFile(dublicateObjectBTreeFileName);

    } catch (Exception e) {
      System.err.println("" + e);
      e.printStackTrace();
      Runtime.getRuntime().exit(1);
    }

  }

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
   * get count of the unique Subject count from the RDFDB.
   *
   * @return
   */
  public int getSubjectCount() {
    int subjectCount = -1;
    btree.quadbtree.KeyDataEntry entry = null;
    KeyDataEntry duplicateEntry = null;

    try {
      int keyType = AttrType.attrString;
      btree.quadbtree.BTFileScan scan = quadrupleBTreeFile.new_scan(null, null);
      do {
        entry = scan.get_next();
        if (entry != null) {
          String quadrupleKey = ((StringKey) (entry.key)).getKey();
          String[] temp = quadrupleKey.split(":");

          String subject = temp[0] + temp[1];

          KeyClass lowKey = new StringKey(subject);
          KeyClass highKey = new StringKey(subject);

          BTFileScan duplicateLabelBTScan = dublicateSubjectBTreeFile.new_scan(lowKey, highKey);
          duplicateEntry = duplicateLabelBTScan.get_next();

          if (duplicateEntry == null) {
            dublicateSubjectBTreeFile.insert(lowKey,
                new LID(new PageId(Integer.parseInt(temp[1])), Integer.parseInt(temp[0])));
          }

          duplicateLabelBTScan.DestroyBTreeFileScan();
        }
      } while (entry != null);
      scan.DestroyBTreeFileScan();
      quadrupleBTreeFile.close();

      KeyClass lowKey = null;
      KeyClass highKey = null;

      BTFileScan duplicateLabelBTScan = dublicateSubjectBTreeFile.new_scan(lowKey, highKey);

      do {
        duplicateEntry = duplicateLabelBTScan.get_next();
        if (duplicateEntry != null) {
          subjectCount++;
        }
      } while (duplicateEntry != null);
      duplicateLabelBTScan.DestroyBTreeFileScan();
      dublicateSubjectBTreeFile.close();
    } catch (Exception e) {
      System.err.println("" + e);
      e.printStackTrace();
      Runtime.getRuntime().exit(1);


    }

    return subjectCount;


  }

  /**
   * Returns unique object count from RDFDB
   *
   * @return
   */
  public int getObjectCount() {
    int objectCount = -1;

    btree.quadbtree.KeyDataEntry quadrupleBTreeEntry = null;
    KeyDataEntry duplicateLabelBTreeEntry = null;

    try {
      int keyType = AttrType.attrString;
      btree.quadbtree.BTFileScan quadrupleScan = quadrupleBTreeFile.new_scan(null, null);
      do {
        quadrupleBTreeEntry = quadrupleScan.get_next();
        if (quadrupleBTreeEntry != null) {
          String quadrupleKey = ((StringKey) (quadrupleBTreeEntry.key)).getKey();
          String[] temp = quadrupleKey.split(":");
          String object = temp[4] + temp[5];

          KeyClass lowKey = new StringKey(object);
          KeyClass highKey = new StringKey(object);

          BTFileScan duplicateObjectBTFileScan = dublicateObjectBTreeFile.new_scan(lowKey, highKey);
          duplicateLabelBTreeEntry = duplicateObjectBTFileScan.get_next();
          if (duplicateLabelBTreeEntry != null) {
            dublicateObjectBTreeFile.insert(lowKey,
                new LID(new PageId(Integer.parseInt(temp[4])), Integer.parseInt(temp[5])));

          }
          duplicateObjectBTFileScan.DestroyBTreeFileScan();
        }
      } while (quadrupleBTreeEntry != null);
      quadrupleScan.DestroyBTreeFileScan();
      quadrupleBTreeFile.close();

      KeyClass lowKey = null;
      KeyClass highKey = null;

      BTFileScan duplicateScan = dublicateObjectBTreeFile.new_scan(lowKey, highKey);

      do {
        duplicateLabelBTreeEntry = duplicateScan.get_next();
        if (duplicateLabelBTreeEntry != null) {
          objectCount++;
        }

      } while (duplicateLabelBTreeEntry != null);
      duplicateScan.DestroyBTreeFileScan();
      dublicateObjectBTreeFile.close();
    } catch (Exception e) {
      System.err.println("" + e);
      e.printStackTrace();
      Runtime.getRuntime().exit(1);

    }

    return objectCount;
  }

  EID insertEntity(String entityLabelName) {
    Label entityLabel = new Label(entityLabelName);
    LID entityLabelId = null;

    byte[] entityLabelBytes = entityLabel.returnTupleByteArray();
    try {
      entityLabelId = entityHeapFile.insertLabel(entityLabelBytes);
    } catch (Exception e) {
      System.err.println("");
      return null;
    }

    EID entityId = new EID(entityLabelId);
    return entityId;
  }

  /**
   * Delete
   *
   * @param entityLabelName
   * @return
   */
  boolean deleteEntity(String entityLabelName) {
    Label entityLabel = new Label(entityLabelName);
    LID entityLabelId = new LID();

//    entityHeapFile.deleteLabel(entityLabel);

//    entityHeapFile.deleteLabel();

    boolean success = false;

    try {
      KeyClass lowKey = new StringKey(entityLabelName);
      KeyClass highKey = new StringKey(entityLabelName);

      KeyDataEntry entry = null;

      BTFileScan scan = entityBTreeFile.new_scan(lowKey, highKey);
      entry = scan.get_next();

      if (entry != null) {
        if (entityLabel.equals(((StringKey) (entry.key)).getKey())) {
          entityLabelId = ((LeafData) entry.data).getData();
          success = entityHeapFile.deleteLabel(entityLabelId) && entityBTreeFile.Delete(lowKey,
              entityLabelId);
        }
        scan.DestroyBTreeFileScan();
        entityBTreeFile.close();
      }
    } catch (Exception e) {
      System.err.println("*** Error deleting entity " + e);
      e.printStackTrace();
    }

    return true;
  }


  public PID insertPredicate(String predicateLabelName) {
    Label predicateLabel = new Label(predicateLabelName);
    LID predicateLabelId = null;

    byte[] predicateLabelBytes = predicateLabel.returnTupleByteArray();
    try {
      predicateLabelId = predicateHeapFile.insertLabel(predicateLabelBytes);
    } catch (Exception e) {
      System.err.println("");
      return null;
    }

    PID predicateId = new PID(predicateLabelId);
    return predicateId;
  }

  boolean deletePredicate(String predicateLabelName) {
    Label predicateLabel = new Label(predicateLabelName);
    LID predicateLabelId = new LID();

    try {
      predicateHeapFile.deleteLabel(predicateLabelId);
    } catch (Exception e) {
      System.err.println("");
    }
    return false;
  }

  public QID insertQuadruple(byte[] quadrupleBytes) {
    QID quadrupleId = null;
    try {
      quadrupleId = quadrupleHeapFile.insertQuadruple(quadrupleBytes);
    } catch (Exception e) {

    }
    return quadrupleId;
  }

  boolean deleteQuadruple(byte[] quadrupleBytes) {
//    quadrupleHeapFile.delete
    return false;
  }

  private String generateFileName(String[] identifiers) {
    StringBuilder fileName = new StringBuilder();

    for (int i = 0; i < identifiers.length - 1; i++) {
      fileName.append(identifiers[i]);
      fileName.append("-");
    }

    fileName.append(identifiers[identifiers.length - 1]);

    return fileName.toString();
  }

  /**
   * TODO
   * Opens an existing RDF database.
   */
  public void openRdfDB(String rdfDBName, IndexOption indexOption) {
    this.rdfDBName = new String(rdfDBName);
    try {
      openDB(this.rdfDBName);
    } catch (Exception e) {
      System.err.println("" + e);
      e.printStackTrace();
      Runtime.getRuntime().exit(1);
    }
    initRdfDB(indexOption);
  }

}
