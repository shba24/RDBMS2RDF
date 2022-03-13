package diskmgr.IndexingSchemes;

import btree.quadbtree.BTreeFile;

/**
 * IndexScheme uses strategy pattern, this class used to change the strategy.
 */
public class IndexSchemeContext {

  private IndexSchemes indexSchemes;

  /**
   * Constructor
   * @param indexSchemes type of index scheme
   */
  public IndexSchemeContext(IndexSchemes indexSchemes) {
    this.indexSchemes = indexSchemes;
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
