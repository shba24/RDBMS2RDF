package programs;

import db.InsertQuery;
import db.InsertQueryExecutor;
import db.InsertQueryParser;
import db.ParserFactory;
import db.QueryExecutorFactory;
import db.QueryType;

public class BatchInsert {
  public static void main(String[] args) {
//    DATAFILENAME INDEXOPTION RDFDBNAME

    if(args.length != 3) {
      System.out.println("Please provide all the required arguments.");
      System.out.println("1st argument is the path to the data file");
      System.out.println("2nd argument is the index option.");
      System.out.println("3rd argument is the RDF DB name.");
    }

    InsertQueryParser parser = (InsertQueryParser) ParserFactory.getParser(QueryType.INSERT);
    String query = String.join(" ", args);
    InsertQuery insertQuery = (InsertQuery) parser.parse(query);
    InsertQueryExecutor executor = (InsertQueryExecutor) QueryExecutorFactory.getQueryExecutor(QueryType.INSERT);
    executor.execute(insertQuery);
  }
}


