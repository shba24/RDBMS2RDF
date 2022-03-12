package db;

public class InsertQueryParser implements IParser {
  public IQuery parse(String query) {
    /**
     * DATAFILENAME INDEXOPTION RDFDBNAME
     */
    String[] tokens = query.split(" ");
    return new InsertQuery(
        tokens[0],  /* = dataFileName*/
        tokens[1],  /* = indexOption */
        tokens[2]); /* = rdfDBName */
  }
}
