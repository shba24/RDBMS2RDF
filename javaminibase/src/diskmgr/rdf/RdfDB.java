package diskmgr.rdf;


import db.IndexOption;
import diskmgr.DB;
import global.EID;
import global.GlobalConst;
import global.LID;
import global.PID;
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

  }

  public int getQuadrupleCount() {
    int quadrupleCount = -1;
    try {
      if(quadrupleHeapFile != null) {
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
      if(entityHeapFile != null) {
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
      if(predicateHeapFile != null) {
        predicatesCount = predicateHeapFile.getRecCnt();
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
    return predicatesCount;
  }

  /**
   * TODO
   *
   * @return
   */
  public int getSubjectCount() {
    return -1;
  }

  /**
   * TODO
   *
   * @return
   */
  public int getObjectCount() {
    return -1;
  }

  EID insertEntity(String entityLabelName) {
    Label entityLabel = new Label(entityLabelName);
    LID entityLabelId = null;

    byte[] entityLabelBytes = entityLabel.returnTupleByteArray();
    try {
      entityLabelId = entityHeapFile.insertLabel(entityLabelBytes);
    } catch(Exception e) {
      System.err.println("");
      return null;
    }

    EID entityId = new EID(entityLabelId);
    return entityId;
  }

  boolean deleteEntity(String entityLabelName) {
    Label entityLabel = new Label(entityLabelName);
    LID entityLabelId = new LID();

//    entityHeapFile.deleteLabel(entityLabel);

//    entityHeapFile.deleteLabel();
    return false;
  }

  PID insertPredicate(String predicateLabelName) {
    Label predicateLabel = new Label(predicateLabelName);
    LID predicateLabelId = null;

    byte[] predicateLabelBytes = predicateLabel.returnTupleByteArray();
    try {
      predicateLabelId = predicateHeapFile.insertLabel(predicateLabelBytes);
    } catch(Exception e) {
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
    } catch(Exception e) {
      System.err.println("");
    }
    return false;
  }

  public QID insertQuadruple(byte[] quadrupleBytes) {
    QID quadrupleId = null;
    try {
      quadrupleId = quadrupleHeapFile.insertQuadruple(quadrupleBytes);
    } catch(Exception e) {

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
