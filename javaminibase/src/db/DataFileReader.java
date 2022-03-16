package db;

import heap.Quadruple;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

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

  public String[] read_next() {
    String line = readLine();
    if (line != null) {
      String[] tokens = Arrays.stream(line.split(" ")).map(
          str -> str.replaceFirst("^:", "")
      ).toArray(String[]::new);
      line = readLine();
      return tokens;
    }
    return null;
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
