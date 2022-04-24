package programs;

import db.InsertQuery;
import db.QueryFactory;
import db.QueryType;
import db.RDFDatabase;
import db.Telemetry;
import global.GlobalConst;
import global.SystemDefs;

public class BatchInsert {
  public static void main(String[] args) throws Exception {
    //    DATAFILENAME INDEXOPTION RDFDBNAME

    if(args.length != 3) {
      System.out.println("Please provide all the required arguments.");
      System.out.println("1st argument is the path to the data file");
      System.out.println("2nd argument is the index option.");
      System.out.println("3rd argument is the RDF DB name.");
    }
    InsertQuery query = (InsertQuery) QueryFactory.getQuery(QueryType.INSERT, String.join(" ", args));
    RDFDatabase db = new RDFDatabase(query.getDbName(), query.getIndexOption(), GlobalConst.NUMBUF);
    db.insert(query);
    db.close();

    // Print reads and writes
    Telemetry.printAllTelemetry(SystemDefs.JavabaseDBName);
  }
}


