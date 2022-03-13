package diskmgr.IndexingSchemes;

import btree.quadbtree.BTreeFile;

/**
 * IndexScheme uses strategy pattern, this class used to change the strategy.
 */
public class IndexSchemeContext {

  private IndexSchemes indexSchemes;

  public IndexSchemeContext(IndexSchemes indexSchemes) {
    this.indexSchemes = indexSchemes;
  }

  public void executeIndexScheme(BTreeFile bTreeFile, String dbname) throws Exception {
    indexSchemes.createIndex(bTreeFile, dbname);
  }
}
