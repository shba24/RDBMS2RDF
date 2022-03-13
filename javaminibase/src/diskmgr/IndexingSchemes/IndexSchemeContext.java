package diskmgr.IndexingSchemes;

import btree.quadbtree.BTreeFile;

public class IndexSchemeContext {
  private IndexSchemes indexSchemes;

  public IndexSchemeContext(IndexSchemes indexSchemes){
    this.indexSchemes=indexSchemes;
  }
  public void executeIndexScheme(BTreeFile bTreeFile, String dbname) throws Exception {
    indexSchemes.createIndex(bTreeFile,dbname);
  }

}
