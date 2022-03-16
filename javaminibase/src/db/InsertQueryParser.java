package db;

public class InsertQueryParser implements IParser {
  public IQuery parse(String query) throws IllegalArgumentException {
    /**
     * DATAFILENAME INDEXOPTION RDFDBNAME
     */
    String[] tokens = query.split(" ");
    if (tokens.length!=3) {
      throw new IllegalArgumentException("Number of arguments are not equal to 3");
    }
    return new InsertQuery(
        tokens[0],  /* = dataFileName*/
        tokens[1],  /* = indexOption */
        tokens[2]); /* = rdfDBName */
  }
}
