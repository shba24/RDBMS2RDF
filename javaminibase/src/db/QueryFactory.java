package db;

/**
 * Factory design pattern for Query
 * <p>
 * Can provide the concrete objects
 * for :-
 * 1. InsertQuery
 * 2. SelectQuery
 */
public class QueryFactory {
  public static IQuery getQuery(String type, String query) {
    if (type == null) {
      return null;
    }

    IParser parser = ParserFactory.getParser(type);
    if (type.equalsIgnoreCase("INSERT")) {
      return parser.parse(query);
    } else if (type.equalsIgnoreCase("SELECT")) {
      return parser.parse(query);
    }
    return null;
  }
}
