package tests;
import db.Telemetry;
import java.io.IOException;
import java.lang.*;
import org.json.simple.parser.ParseException;


public class Report {
  protected static void printReportStat(ReportStats reportStats) throws IOException, ParseException {

    System.out.println("\n\n\n******************** Report - RDF DB Statistics ******************");
    System.out.printf("Quadruple Count: %d\n", reportStats.getQuadrupleCnt());
    System.out.printf("Entity Count: %d\n", reportStats.getEntityCnt());
    System.out.printf("Remaining buffer space: %d\n", reportStats.getRemainingBuffSpace());
    System.out.println("------------------------");

    Telemetry Telemetry = new Telemetry();
    System.out.println("-----------Read and Write Counts--------------");
    Telemetry.printFile();
  }
}

