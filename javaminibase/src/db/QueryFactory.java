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
  private static InsertQueryParser insertQParser;
  private static SelectQueryParser selectQParser;
  public static IQuery getQuery(String type, String query) {
    if (type == null) {
      return null;
    }

    if (type.equalsIgnoreCase("INSERT")) {
      return insertQParser.parse(query);
    } else if (type.equalsIgnoreCase("SELECT")) {
      return selectQParser.parse(query);
    }
    return null;
  }
}
