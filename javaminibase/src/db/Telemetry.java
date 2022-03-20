package db;

import global.GlobalConst;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Telemetry implements ITelemetry {

  public JSONObject dbJsonObject;

  @Override
  public void initialize() throws IOException, ParseException {

    File newFile = new File(GlobalConst.JSON_FILE_NAME);
    if (newFile.exists()) {
      JSONParser jsonParser = new JSONParser();
      dbJsonObject = (JSONObject) jsonParser.parse(
          new FileReader(GlobalConst.JSON_FILE_NAME));
    } else {
      dbJsonObject = new JSONObject();
    }

  }

  @Override
  public void addRead(String dbName) {

    if (dbJsonObject.containsKey(dbName)) {
      JSONObject dataObject = (JSONObject) dbJsonObject.get(dbName);

      String rCount = dataObject.get(GlobalConst.READS).toString();
      int rc = Integer.parseInt(rCount);

      dataObject.put(GlobalConst.READS, rc + 1);

      dbJsonObject.put(dbName, dataObject);

      String fileName = Paths.get(
          GlobalConst.ROOT_FOLDER,
          GlobalConst.JSON_FILE
      ).toString();

      try (FileWriter file = new FileWriter(fileName)) {
        file.write(dbJsonObject.toJSONString());
        file.flush();
      } catch (Exception e) {
        e.printStackTrace();
      }

    } else {
      JSONObject dbJSONObject = new JSONObject();

      JSONObject dataObject = new JSONObject();

      dataObject.put(GlobalConst.READS, 1);
      dataObject.put(GlobalConst.WRITES, 0);

      dbJsonObject.put(dbName, dataObject);



      try (FileWriter file = new FileWriter(GlobalConst.JSON_FILE_NAME)) {
        file.write(dbJsonObject.toJSONString());
        file.flush();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void addWrite(String dbName) {

    if (dbJsonObject.containsKey(dbName)) {
      JSONObject dataObject = (JSONObject) dbJsonObject.get(dbName);

      String wCount = dataObject.get(GlobalConst.WRITES).toString();
      int wC = Integer.parseInt(wCount);
      dataObject.put(GlobalConst.WRITES, wC + 1);

      dbJsonObject.put(dbName, dataObject);


      try (FileWriter file = new FileWriter(GlobalConst.JSON_FILE_NAME)) {
        file.write(dbJsonObject.toJSONString());
        file.flush();
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      JSONObject dbJSONObject = new JSONObject();

      JSONObject dataObject = new JSONObject();

      dataObject.put(GlobalConst.READS, 0);
      dataObject.put(GlobalConst.WRITES, 1);

      dbJsonObject.put(dbName, dataObject);


      try (FileWriter file = new FileWriter(GlobalConst.JSON_FILE_NAME)) {
        file.write(dbJsonObject.toJSONString());
        file.flush();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }

  @Override
  public int getRead(String dbName) {
    if (dbJsonObject.containsKey(dbName)) {
      JSONObject dataObject = (JSONObject) dbJsonObject.get(dbName);
      return Integer.parseInt(dataObject.get(GlobalConst.READS).toString());
    } else {
      System.out.println("DB data Not found");
    }

    return 0;
  }

  @Override
  public int getWrite(String dbName) {
    if (dbJsonObject.containsKey(dbName)) {
      JSONObject dataObject = (JSONObject) dbJsonObject.get(dbName);
      return Integer.parseInt(dataObject.get(GlobalConst.WRITES).toString());
    } else {
      System.out.println("DB data Not found");
    }

    return 0;
  }

  @Override
  public void printFile(){
    System.out.println(dbJsonObject.toJSONString());
  }


}
