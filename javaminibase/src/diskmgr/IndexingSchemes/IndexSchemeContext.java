package diskmgr.IndexingSchemes;

import btree.quadbtree.BTreeFile;
import diskmgr.IndexOption;

/**
 * IndexScheme uses strategy pattern, this class used to change the strategy.
 */
public class IndexSchemeContext {

  private IndexSchemes indexSchemes;

  /**
   * Constructor
   * @param indexOption enum type of index scheme
   */
  public IndexSchemeContext(IndexOption indexOption) {
    switch(indexOption) {
      case Object:
        this.indexSchemes = new ObjectIndexScheme();
        break;
      case Subject:
        this.indexSchemes = new SubjectIndexScheme();
        break;
      case Confidence:
        this.indexSchemes = new ConfidenceIndexSchemes();
        break;
      case PredicateConfidence:
        this.indexSchemes = new PredicateConfidence();
        break;
      case SubjectConfidence:
        this.indexSchemes = new SubjectConfidenceIndexScheme();
        break;
      case ObjectConfidence:
        this.indexSchemes = new ObjectConfidenceIndexSchemes();
        break;
      default:
        //incorrect type
        break;
    }
  }

  /**
   * Execute Index scheme
   * @param bTreeFile BTree Index
   * @param dbname dbname
   * @throws Exception
   */
  public void executeIndexScheme(BTreeFile bTreeFile, String dbname) throws Exception {
    indexSchemes.createIndex(bTreeFile, dbname);
  }
}
