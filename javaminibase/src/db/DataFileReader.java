package db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

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
    while (line != null) {
      String[] tokens = Arrays.stream(line.split("[ \t]")).map(
          str -> str.replaceFirst("^:", "")
      ).toArray(String[]::new);
      String[] tks = new String[4];
      if (tokens[0].trim().isEmpty()) {
        line = readLine();
        continue;
      }
      tks[0] = tokens[0].trim();
      if (tokens[1].trim().isEmpty()) {
        line = readLine();
        continue;
      }
      tks[1] = tokens[1].trim();
      if (tokens[2].trim().isEmpty()) {
        line = readLine();
        continue;
      }
      tks[2] = tokens[2].trim();
      if (tokens[3].trim().isEmpty()) {
        if (tokens[4].trim().isEmpty()) {
          line = readLine();
          continue;
        }
        tks[3] = tokens[4].trim();
      } else {
        tks[3] = tokens[3].trim();
      }
      /*
        System.out.println("-------------------");
        for (int i=0;i<tokens.length;i++) {
          System.out.println("@"+tokens[i]+"@");
        }
      */
      return tks;
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
