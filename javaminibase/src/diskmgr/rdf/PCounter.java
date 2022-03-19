package diskmgr.rdf;

import global.GlobalConst;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PCounter {

  public static int readCounter;
  public static int writeCounter;
  public static String fileName = null;

  public static String getFileName() {
    fileName = Paths.get(
        Paths.get(System.getProperty(GlobalConst.CURR_DIR_ENV)).toString(),
        GlobalConst.ROOT_FOLDER,
        "pCounter.json"
    ).toString();
    return fileName;
  }


  /**
   * Initialize the readCounter and writeCounter with 0
   */
  public static void initialize() {
    readCounter = 0;
    writeCounter = 0;
  }

  /**
   * Increments the read counter
   */
  public static void readIncrement() {
    readCounter++;
  }

  /**
   * Increments the write counter
   */
  public static void writeIncrement() {
    writeCounter++;
  }

  /**
   * Writing into the Json file. first checks if the dbentry is present, if dbentry is present then
   * checks queryEntry if both are present updats the data. if dbEntry is not present adds new
   * dbEntry, if QueryEntry is not present adds newQueryEntry and then adds data.
   *
   * @param dbName
   * @param qName
   * @throws IOException
   * @throws ParseException
   */
  public static void writeToJSONFile(String dbName, String qName)
      throws IOException, ParseException {

    fileName = getFileName();
    File newFile = new File(fileName);

    if (newFile.length() != 0) {
      JSONParser jsonParser = new JSONParser();
      JSONObject reportJsonObject = (JSONObject) jsonParser.parse(
          new FileReader(fileName));

      if (reportJsonObject.containsKey(dbName)) {

        JSONObject queryObj = (JSONObject) reportJsonObject.get(dbName);
        if (queryObj.containsKey(qName)) {

          JSONObject dataObj = (JSONObject) ((JSONObject) reportJsonObject.get(dbName)).get(qName);
          dataObj.put("Reads", readCounter);
          dataObj.put("Writes", writeCounter);
          queryObj.put(qName, dataObj);
          reportJsonObject.put(dbName, queryObj);
        } else {
          JSONObject newQueryObj = new JSONObject();
          JSONObject newDataObj = new JSONObject();

          newDataObj.put("Reads", readCounter);
          newDataObj.put("Writes", readCounter);
          newQueryObj.put(qName, newDataObj);
          reportJsonObject.put(dbName, newQueryObj);
        }
      } else {

        JSONObject newQueryObj = new JSONObject();
        JSONObject newDataObj = new JSONObject();

        newDataObj.put("Reads", readCounter);
        newDataObj.put("Writes", readCounter);

        newQueryObj.put(qName, newDataObj);
        reportJsonObject.put(dbName, newQueryObj);
      }

      try (FileWriter file = new FileWriter(fileName)) {
        file.write(reportJsonObject.toJSONString());
        file.flush();
      } catch (Exception e) {
        e.printStackTrace();
      }

    } else {
      JSONObject newReportObj = new JSONObject();
      JSONObject newQueryObj = new JSONObject();
      JSONObject newDataObj = new JSONObject();

      newDataObj.put("Reads", readCounter);
      newDataObj.put("Writes", readCounter);

      newQueryObj.put(qName, newDataObj);
      newReportObj.put(dbName, newQueryObj);

      try (FileWriter file = new FileWriter(fileName)) {
        file.write(newReportObj.toJSONString());
        file.flush();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }


  }

  public static void printReadWriteCount(String dbName, String qName)
      throws IOException, ParseException {
    long rCount = -1;
    long wCount = -1;

    JSONParser jsonParser = new JSONParser();
    JSONObject reportJsonObject = (JSONObject) jsonParser.parse(
        new FileReader(fileName));

    if (reportJsonObject.containsKey(dbName)) {
      JSONObject queryObject = (JSONObject) reportJsonObject.get(dbName);
      if (queryObject.containsKey(qName)) {
        JSONObject dataObject = (JSONObject) queryObject.get(qName);
        rCount = (long) dataObject.get("Reads");
        wCount = (long) dataObject.get("Writes");

        System.out.println("Reads : " + rCount + " Writes : " + wCount);
      } else {
        System.out.println("Query Data not present");
      }
    } else {
      System.out.println("DB Data not present");
    }


  }

  public static void printJsonFile() throws IOException, ParseException {
    JSONParser jsonParser = new JSONParser();
    JSONObject reportJsonObject = (JSONObject) jsonParser.parse(
        new FileReader(fileName));
    System.out.println(reportJsonObject.toJSONString());
  }


}
