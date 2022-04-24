package db;

import java.io.IOException;

public interface IParser {
  /**
   * Parses the query and returns the query object
   * @param query
   * @return  Query object
   */
  public IQuery parse(String query) throws IllegalArgumentException, IOException;
}
