package programs;

public class BatchInsert {
  public static void main(String[] args) {
//    DATAFILENAME INDEXOPTION RDFDBNAME

    if(args.length != 3) {
      System.out.println("Please provide all the required arguments.");
      System.out.println("1st argument is the path to the data file");
      System.out.println("2nd argument is the index option.");
      System.out.println("3rd argument is the RDF DB name.");
    }
  }
}


