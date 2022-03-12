package db;

import global.SystemDefs;

public class InsertQueryExecutor implements  IQueryExecutor {

  @Override
  public void execute(IQuery query) {
    InsertQuery insertQuery = (InsertQuery) query;
    DataFileReader reader = new DataFileReader(insertQuery.getDataFileName());

    String rdfDBName = insertQuery.getDbName();
    int numberOfPages = 10000;
    int bufferPoolSize = 1000;
    String replacementPolicy = "Clock";
    IndexOption indexOption = insertQuery.getIndexOption();

    SystemDefs sysdef = new SystemDefs(rdfDBName, numberOfPages, bufferPoolSize, replacementPolicy, indexOption);

    sysdef.JavabaseDB.getEntityCount();
  }
}
