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
  public static IParser getParser(String type) {
    if (type == null) {
      return null;
    }

    if (type.equalsIgnoreCase("INSERT")) {
      return new InsertQueryParser();
    } else if (type.equalsIgnoreCase("SELECT")) {
      return new SelectQueryParser();
    }
    return null;
  }
}
