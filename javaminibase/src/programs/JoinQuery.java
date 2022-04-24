package programs;

import db.QueryFactory;
import db.QueryType;
import db.RDFDatabase;
import db.Telemetry;
import global.JoinStrategy;
import global.SystemDefs;

public class JoinQuery {
  public static void main(String[] args) throws Exception {
    db.JoinQuery query = (db.JoinQuery) QueryFactory.getQuery(QueryType.JOIN, String.join(" ", args));
    for (int i=0; i<3; i++) {
      RDFDatabase db = new RDFDatabase(query.getDbName(), query.getIndexOption(), query.getNumBuf());
      JoinStrategy js = new JoinStrategy(i);
      System.out.println("Query Strategy: " + js.toString());
      db.join(query, js);
      db.close();

      // Print reads and writes
      Telemetry.printAllTelemetry(SystemDefs.JavabaseDBName);
    }
  }
}
