package db;

/**
 * Factory design pattern for Parser
 * <p>
 * Can provide the concrete objects
 * for :-
 * 1. InsertQueryParser
 * 2. SelectQueryParser
 */
public class ParserFactory {
  public static IParser getParser(QueryType queryType) {
    if (queryType == null) {
      return null;
    }

    if (queryType == QueryType.INSERT) {
      return new InsertQueryParser();
    } else if (queryType == QueryType.SELECT) {
      return new SelectQueryParser();
    } else if (queryType == QueryType.JOIN) {
      return new JoinQueryParser();
    }
    return null;
  }
}
