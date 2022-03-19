package programs;

import db.QueryFactory;
import db.QueryType;
import db.RDFDatabase;
import db.SelectQuery;

public class Query {
  public static void main(String[] args) throws Exception {
    //    query RDFDBNAME INDEXOPTION ORDER SUBJECTFILTER PREDICATEFILTER OBJECTFILTER CONFIDENCEFILTER NUMBUF

    if(args.length != 8) {
      System.out.println("Please provide all the required arguments.");
      System.out.println("1st argument is the path to the data file");
      System.out.println("2nd argument is the index option.");
      System.out.println("3rd argument is the order of the output.");
      System.out.println("4th argument is the subject filter.");
      System.out.println("5th argument is the predicate filter.");
      System.out.println("6th argument is the object filter.");
      System.out.println("7th argument is the confidence filter.");
      System.out.println("8th argument is the number of buffer pages to read.");
    }
    SelectQuery query = (SelectQuery) QueryFactory.getQuery(QueryType.SELECT, String.join(" ", args));
    RDFDatabase db = new RDFDatabase(query.getDbName(), query.getIndexOption());
    db.select(query);
  }
}
