package db;

import global.EID;
import global.PID;
import global.RDFSystemDefs;
import heap.Quadruple;

public class InsertQuery extends BaseQuery implements IQuery {
  private String dataFileName;
  private IndexOption indexOption;

  /**
   * Public constructor
   *
   * @param _datafile
   * @param _indexOption
   * @param _dbname
   */
  public InsertQuery(
      String _datafile,
      String _indexOption,
      String _dbname) {
    super(_dbname);
    dataFileName = _datafile;
    indexOption = IndexOption.valueOf(_indexOption);
  }

  /**
   * get data file name
   *
   * @return String data file name
   */
  public String getDataFileName() {
    return dataFileName;
  }

  /**
   * set the data file name
   *
   * @param dataFileName String data file name
   */
  public void setDataFileName(String dataFileName) {
    this.dataFileName = dataFileName;
  }

  /**
   * get indexOption
   *
   * @return IndexOption
   */
  public IndexOption getIndexOption() {
    return indexOption;
  }

  /**
   * set IndexOption from string
   *
   * @param indexOption String indexOption
   */
  public void setIndexOption(String indexOption) {
    this.indexOption = IndexOption.valueOf(indexOption);
  }

  /**
   * set IndexOption
   *
   * @param indexOption IndexOption
   */
  public void setIndexOption(IndexOption indexOption) {
    this.indexOption = indexOption;
  }

  /**
   * Executes the query
   */
  public void execute(RDFSystemDefs rdfSystemDefs) throws Exception {
    DataFileReader fileReader = new DataFileReader(dataFileName);
    String[] tokens = fileReader.read_next();
    while (tokens != null) {
      Quadruple quad = new Quadruple();
      EID subjectID = rdfSystemDefs.rdfDB.insertEntity(tokens[0]);
      PID predicateID = rdfSystemDefs.rdfDB.insertPredicate(tokens[1]);
      EID objectID = rdfSystemDefs.rdfDB.insertEntity(tokens[2]);
      double confidence = Double.parseDouble(tokens[3]);
      quad.setSubjectID(subjectID)
          .setPredicateID(predicateID)
          .setObjectID(objectID)
          .setConfidence(confidence);
      rdfSystemDefs.rdfDB.insertQuadruple(quad.getQuadrupleByteArray());
      tokens = fileReader.read_next();
    }
  }
}
