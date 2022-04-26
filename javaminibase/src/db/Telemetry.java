package db;

import global.GlobalConst;
import java.util.Iterator;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.PrimitiveIterator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Telemetry {

  private int readCnt;
  private int writeCnt;
  private int readQCnt;
  private int pageSize;
  private int bufferSize;
  private int pageFaults;
  private int lookupCnt;
  private int bufferReads;
  private int noOfUniqueEntities;
  private int noOfUniquePredicates;
  private int noOfTotalQuadruples;
  private String dbName;

  public Telemetry(String _dbName, int _pageSize, int _bufferSize) {
    readCnt = 0;
    writeCnt = 0;
    readQCnt = 0;
    noOfUniqueEntities = 0;
    noOfUniquePredicates = 0;
    noOfTotalQuadruples = 0;
    pageSize = _pageSize;
    bufferSize = _bufferSize;
    dbName = _dbName;
  }

  public void setNoOfUniqueEntities(int num) {
    noOfUniqueEntities = num;
  }

  public void setNoOfUniquePredicates(int num) {
    noOfUniquePredicates = num;
  }

  public void setNoOfTotalQuadruples(int num) {
    noOfTotalQuadruples = num;
  }

  private static JSONObject getTelemetry() {
    JSONObject dbJsonObject = new JSONObject();
    try {
      File telemetryFile = new File(GlobalConst.JSON_FILE);
      if (telemetryFile.exists()) {
        JSONParser jsonParser = new JSONParser();
        dbJsonObject = (JSONObject) jsonParser.parse(
            new FileReader(GlobalConst.JSON_FILE));
      }
    } catch (Exception e) {
      // catch all json parsing exception
      e.printStackTrace();
    }
    return dbJsonObject;
  }

  private void putTelemetry(JSONObject dbJsonObject) {
    File telemetryFile = new File(GlobalConst.JSON_FILE);
    try (FileWriter file = new FileWriter(telemetryFile)) {
      file.write(dbJsonObject.toJSONString());
      file.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Flushes the telemetry updates to the file.
   */
  public void flush() throws IOException, ParseException {
    JSONObject dbJsonObject = getTelemetry();
    JSONObject dataObject = new JSONObject();
    if (dbJsonObject.containsKey(dbName)) {
      dataObject = (JSONObject) dbJsonObject.get(dbName);
    }
    dataObject.put(GlobalConst.READS, readCnt);
    dataObject.put(GlobalConst.WRITES, writeCnt);
    dataObject.put(GlobalConst.PAGE_SIZE, pageSize);
    dataObject.put(GlobalConst.BUFF_SIZE, bufferSize);
    dataObject.put(GlobalConst.UNIQUE_ENTITIES, noOfUniqueEntities);
    dataObject.put(GlobalConst.UNIQUE_PREDICATES, noOfUniquePredicates);
    dataObject.put(GlobalConst.TOTAL_QUADRUPLES, noOfTotalQuadruples);
    dataObject.put(GlobalConst.QUAD_READS, readQCnt);
    dataObject.put(GlobalConst.BUFFER_READ_PAGES, bufferReads);
    dataObject.put(GlobalConst.LOOKUP_COUNT, lookupCnt);
    dataObject.put(GlobalConst.PAGE_FAULTS, pageFaults);
    dbJsonObject.put(dbName, dataObject);
    putTelemetry(dbJsonObject);
  }

  public static void printAllTelemetry(String db) {
    JSONObject telemetry  = getTelemetry();
    for (Iterator iterator = telemetry.keySet().iterator(); iterator.hasNext(); ) {
      String dbName = (String) iterator.next();
      if (db!=null && !db.equalsIgnoreCase(dbName)) continue;
      JSONObject dataObject = (JSONObject) telemetry.get(dbName);
      int rCount = 0, wCount = 0, pSize = 0, bSize = 0, entSize = 0, predSize = 0, quadSize = 0, quadReads = 0;
      int bufferReadCnt = 0, indexLookupCnt = 0, noOfPageFaults = 0;
      if (dataObject.containsKey(GlobalConst.READS)) {
        rCount = Integer.parseInt(dataObject.get(GlobalConst.READS).toString());
      }
      if (dataObject.containsKey(GlobalConst.WRITES)) {
        wCount = Integer.parseInt(dataObject.get(GlobalConst.WRITES).toString());
      }
      if (dataObject.containsKey(GlobalConst.PAGE_SIZE)) {
        pSize = Integer.parseInt(dataObject.get(GlobalConst.PAGE_SIZE).toString());
      }
      if (dataObject.containsKey(GlobalConst.BUFF_SIZE)) {
        bSize = Integer.parseInt(dataObject.get(GlobalConst.BUFF_SIZE).toString());
      }
      if (dataObject.containsKey(GlobalConst.UNIQUE_ENTITIES)) {
        entSize = Integer.parseInt(dataObject.get(GlobalConst.UNIQUE_ENTITIES).toString());
      }
      if (dataObject.containsKey(GlobalConst.UNIQUE_PREDICATES)) {
        predSize = Integer.parseInt(dataObject.get(GlobalConst.UNIQUE_PREDICATES).toString());
      }
      if (dataObject.containsKey(GlobalConst.TOTAL_QUADRUPLES)) {
        quadSize = Integer.parseInt(dataObject.get(GlobalConst.TOTAL_QUADRUPLES).toString());
      }
      if (dataObject.containsKey(GlobalConst.QUAD_READS)) {
        quadReads = Integer.parseInt(dataObject.get(GlobalConst.QUAD_READS).toString());
      }
      if (dataObject.containsKey(GlobalConst.BUFFER_READ_PAGES)) {
        bufferReadCnt = Integer.parseInt(dataObject.get(GlobalConst.BUFFER_READ_PAGES).toString());
      }
      if (dataObject.containsKey(GlobalConst.LOOKUP_COUNT)) {
        indexLookupCnt = Integer.parseInt(dataObject.get(GlobalConst.LOOKUP_COUNT).toString());
      }
      if (dataObject.containsKey(GlobalConst.PAGE_FAULTS)) {
        noOfPageFaults = Integer.parseInt(dataObject.get(GlobalConst.PAGE_FAULTS).toString());
      }
      System.out.println("DB Name: " + dbName + " Reads: " + rCount + " Writes: " + wCount + " PageSize: " + pSize +
          " BuffSize: " + bSize + " noOfUniqueEntities: " + entSize + " noOfUniquePredicates: " + predSize + " " +
          "noOfTotalQuadruples: " + quadSize + " Total Quadruple Read: " + quadReads + " Total Buffer Page Reads: " + bufferReadCnt +
          " Total Index Lookup Count: " + indexLookupCnt + " Total Page Faults : " + noOfPageFaults);
    }
  }

  /**
   * Increases the read counter by 1.
   *
   */
  public void addRead() { readCnt++; }

  /**
   * Increases the write counter by 1.
   *
   */
  public void addWrite() { writeCnt++; }

  /**
   *
   */
  public void readQuad() { readQCnt++; }

  /**
   *
   */
  public void readBufferPage() { bufferReads++; }

  /**
   *
   */
  public void indexLookupCnt() { lookupCnt++; }

  /**
   *
   */
  public void addPageFault() { pageFaults++; }

  /**
   * Returns the read counter.
   *
   */
  public int getRead() { return readCnt; }

  /**
   * Returns the write counter.
   *
   */
  public int getWrite() { return writeCnt; }
}
