package db;

public class SelectQueryParser implements IParser {
  public IQuery parse(String query) throws IllegalArgumentException {
    /**
     * RDFDBNAME INDEXOPTION ORDER SUBJECTFILTER PREDICATEFILTER OBJECTFILTER CONFIDENCEFILTER NUMBUF
     */
    String[] tokens = query.split(" ");
    if (tokens.length != 8) {
      throw new IllegalArgumentException("Number of arguments are not equal to 8");
    }
    return new SelectQuery(
        tokens[0],  /* = rdfDBName*/
        tokens[1],  /* = indexOption */
        tokens[2],  /* = order */
        tokens[3],  /* = subjectFilter */
        tokens[4],  /* = predicateFilter */
        tokens[5],  /* = objectFilter */
        tokens[6],  /* = confidenceFilter */
        tokens[7]); /* = numBuf */
  }
}
