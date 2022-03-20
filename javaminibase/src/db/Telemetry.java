package db;

import global.GlobalConst;
import java.util.Iterator;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Telemetry {

  private int readCnt;
  private int writeCnt;
  private String dbName;

  public Telemetry(String _dbName) {
    readCnt = 0;
    writeCnt = 0;
    dbName = _dbName;
  }

  public void initialize() throws IOException, ParseException {
    // Initialize it by default values
    readCnt = 0;
    writeCnt = 0;

    // Now check if exists in the telemetry file
    try {
      File newFile = new File(GlobalConst.JSON_FILE);
      if (newFile.exists()) {
        JSONParser jsonParser = new JSONParser();
        JSONObject dbJsonObject = (JSONObject) jsonParser.parse(
            new FileReader(GlobalConst.JSON_FILE));
        if (dbJsonObject.containsKey(dbName)) {
          JSONObject dataObject = (JSONObject) dbJsonObject.get(dbName);
          if (dataObject.containsKey(GlobalConst.READS)) {
            readCnt = Integer.parseInt(dataObject.get(GlobalConst.READS).toString());
          }
          if (dataObject.containsKey(GlobalConst.WRITES)) {
            writeCnt = Integer.parseInt(dataObject.get(GlobalConst.WRITES).toString());
          }
        }
      }
    } catch (Exception e) {
      System.err.println("Failed to parse the telemetry file.");
      e.printStackTrace();
      throw e;
    }
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
    int rCount = 0, wCount = 0;
    if (dataObject.containsKey(GlobalConst.READS)) {
      rCount = Integer.parseInt(dataObject.get(GlobalConst.READS).toString());
    }
    dataObject.put(GlobalConst.READS, rCount + readCnt);
    if (dataObject.containsKey(GlobalConst.WRITES)) {
      wCount = Integer.parseInt(dataObject.get(GlobalConst.WRITES).toString());
    }
    dataObject.put(GlobalConst.WRITES, wCount + writeCnt);
    dbJsonObject.put(dbName, dataObject);
    putTelemetry(dbJsonObject);
  }

  public static void printAllTelemetry() {
    JSONObject telemetry  = getTelemetry();
    for (Iterator iterator = telemetry.keySet().iterator(); iterator.hasNext(); ) {
      String dbName = (String) iterator.next();
      JSONObject dataObject = (JSONObject) telemetry.get(dbName);
      int rCount = 0, wCount = 0;
      if (dataObject.containsKey(GlobalConst.READS)) {
        rCount = Integer.parseInt(dataObject.get(GlobalConst.READS).toString());
      }
      if (dataObject.containsKey(GlobalConst.WRITES)) {
        wCount = Integer.parseInt(dataObject.get(GlobalConst.WRITES).toString());
      }
      System.out.println("DB Name: " + dbName + " Reads: " + rCount + " Writes: " + wCount);
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
