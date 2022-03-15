package db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DataFileReader {

  BufferedReader reader = null;

  public DataFileReader(String dataFilePath) {
    InputStream inputStream;

    try {
      File file = new File(dataFilePath);
      inputStream = new FileInputStream(file);
      reader = new BufferedReader(new InputStreamReader(inputStream));

    } catch(FileNotFoundException e) {
      System.out.println("Could not find the data file at the given path - " + dataFilePath);
    }
  }

  public String readLine() {
    String line = null;
    try {
      line = reader.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
    if(line != null){
      return line;
    } else {
      return null;
    }
  }
}
