package diskmgr.rdf;



import global.GlobalConst;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PCounter {

  public static int readCounter;
  public static int writeCounter;


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
  public static void write(String dbName, String qName) throws IOException, ParseException {

    String fileName = Paths.get(
        Paths.get(System.getProperty(GlobalConst.CURR_DIR_ENV)).toString(),
        GlobalConst.ROOT_FOLDER,
        "pCounter.json"
    ).toString();

    File newFile = new File(fileName);

    if (newFile.length() != 0) {
      JSONParser parser = new JSONParser();
      JSONObject obj = (JSONObject) parser.parse(
          new FileReader(fileName));

      Boolean isNewDBEntry = true;
      for (Object keyStr : obj.keySet()) {

        if (keyStr.equals(dbName)) {
          isNewDBEntry = false;
          JSONObject dbObj = (JSONObject) obj.get(keyStr);
          Boolean isNewQuery = true;
          for (Object queryKey : dbObj.keySet()) {
            if (queryKey.equals(qName)) {
              JSONObject queryObject = (JSONObject) dbObj.get(queryKey);
              queryObject.put("Reads", readCounter + 1);
              queryObject.put("Writes", writeCounter + 1);
              isNewQuery = false;
            }
          }
          if (isNewQuery == true) {
            JSONObject queryObject = new JSONObject();
            queryObject.put("Reads", readCounter);
            queryObject.put("Writes", readCounter);
            obj.put(qName, queryObject);
            break;
          }
        }
      }
      if (isNewDBEntry) {
        JSONObject innerObj = new JSONObject();
        JSONObject dataObj = new JSONObject();
        dataObj.put("Reads", readCounter);
        dataObj.put("Writes", writeCounter);
        innerObj.put(qName, dataObj);
        obj.put(dbName, innerObj);
      }
      try (FileWriter file = new FileWriter(fileName)) {
        file.write(obj.toJSONString());
        file.flush();
      } catch (Exception e) {
        e.printStackTrace();
      }
      System.out.println(obj);

    } else {
      JSONObject object = new JSONObject();
      JSONObject querObject = new JSONObject();
      JSONObject dataObject = new JSONObject();
      dataObject.put("Reads", readCounter);
      dataObject.put("Writes", writeCounter);
      querObject.put(qName, dataObject);
      object.put(dbName, querObject);

      try (FileWriter file = new FileWriter(fileName)) {
        file.write(object.toJSONString());
        file.flush();
      } catch (Exception e) {
        e.printStackTrace();
      }

    }


  }
}
