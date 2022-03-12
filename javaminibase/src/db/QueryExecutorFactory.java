package db;

public class QueryExecutorFactory {
  public static IQueryExecutor getQueryExecutor(QueryType queryType) {
    if (queryType == null) {
      return null;
    }

    if (queryType == QueryType.INSERT) {
      return new InsertQueryExecutor();
    } else if (queryType == QueryType.SELECT) {
      return new SelectQueryExecutor();
    }
    return null;
  }
}
