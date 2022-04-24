package db;

import java.io.IOException;

/**
 * Factory design pattern for Query
 * <p>
 * Can provide the concrete objects
 * for :-
 * 1. InsertQuery
 * 2. SelectQuery
 */
public class QueryFactory {
  public static IQuery getQuery(QueryType queryType, String query) throws IOException {
    if (queryType == null) {
      return null;
    }

    IParser parser = ParserFactory.getParser(queryType);
    if (queryType == QueryType.INSERT) {
      return parser.parse(query);
    } else if (queryType == QueryType.SELECT) {
      return parser.parse(query);
    } else if (queryType == QueryType.JOIN) {
      return parser.parse(query);
    }
    return null;
  }
}
