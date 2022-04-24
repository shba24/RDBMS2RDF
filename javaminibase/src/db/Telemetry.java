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
  private int pageSize;
  private int bufferSize;
  private int noOfUniqueEntities;
  private int noOfUniquePredicates;
  private int noOfTotalQuadruples;
  private String dbName;

  public Telemetry(String _dbName, int _pageSize, int _bufferSize) {
    readCnt = 0;
    writeCnt = 0;
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
    dbJsonObject.put(dbName, dataObject);
    putTelemetry(dbJsonObject);
  }

  public static void printAllTelemetry(String db) {
    JSONObject telemetry  = getTelemetry();
    for (Iterator iterator = telemetry.keySet().iterator(); iterator.hasNext(); ) {
      String dbName = (String) iterator.next();
      if (db!=null && !db.equalsIgnoreCase(dbName)) continue;
      JSONObject dataObject = (JSONObject) telemetry.get(dbName);
      int rCount = 0, wCount = 0, pSize = 0, bSize = 0, entSize = 0, predSize = 0, quadSize = 0;
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
      System.out.println("DB Name: " + dbName + " Reads: " + rCount + " Writes: " + wCount + " PageSize: " + pSize +
          " BuffSize: " + bSize + " noOfUniqueEntities: " + entSize + " noOfUniquePredicates: " + predSize + " " +
          "noOfTotalQuadruples: " + quadSize);
    }
  }

  /**
   * Increases the read counter by 1.
   *
   */
  public void addRead() { readCnt++; try { flush(); } catch (Exception e) {} }

  /**
   * Increases the write counter by 1.
   *
   */
  public void addWrite() { writeCnt++; try { flush(); } catch (Exception e) {} }

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
