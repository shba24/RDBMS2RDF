package tests;
import db.IndexOption;
import db.RDFDatabase;
import diskmgr.rdf.RdfDB;
import java.lang.*;


public class Report {
  public static void main(String[] args) throws Exception {
    String rdfDbFileName = null;
    String indexOption = null;
    String rdfDBFilePath=null;
    int numberOfPages=0;


    if (args.length != 2) {
      System.out.println(
          "Incorrect number of arguments passed. Expected format `java programs.Query [rdfDBname] [indexoption]`");
      System.exit(201);
    }

    rdfDbFileName = args[0];
    indexOption=args[1];

    //getting rdfdb filepath
    RDFDatabase rdfDataBase = new RDFDatabase(rdfDbFileName, IndexOption.valueOf(indexOption));
    rdfDBFilePath=rdfDataBase.getDbPath();

    //initializing rdfDB with filepath, indexoption.
    RdfDB rdfDB=new RdfDB( rdfDBFilePath,numberOfPages, IndexOption.valueOf(indexOption) );
    rdfDB.initRdfDB();



    //getting all the stats.
    Integer quadrupleCnt = rdfDB.getQuadrupleCount();
    Integer entityCnt = rdfDB.getEntityCount();
    Integer predicateCnt = rdfDB.getPredicateCount();
    Integer subjectCnt = rdfDB.getSubjectCount();
    Integer objectCnt = rdfDB.getObjectCount();

    System.out.println("\n\n\n******************** Report - RDF DB Statistics ******************");
    System.out.printf("DataBase name: %s\n", rdfDbFileName);
    System.out.printf("Index type: %d\n", indexOption);
    System.out.printf("Quadruple Count: %d\n", quadrupleCnt);
    System.out.printf("Entity Count: %d\n", entityCnt);
    System.out.printf("\tSubject Count: %d\n", subjectCnt);
    System.out.printf("\tObject Count: %d\n", objectCnt);
    System.out.printf("Predicate Count: %d\n", predicateCnt);
    System.out.println("------------------------");
  }
}

