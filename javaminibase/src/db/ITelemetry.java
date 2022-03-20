package db;

public interface ITelemetry {

  /**
   * Initializes telemetry class
   * by reading the already existing file
   * if it exits, otherwise by creating the file.
   */
  void initialize();

  /**
   * Increases the read counter (read from the file)
   * for this db by 1 and updates the file.
   * @param dbName
   */
  void addRead(String dbName);
  /**
   * Increases the write counter (read from the file)
   * for this db by 1 and updates the file.
   * @param dbName
   */
  void addWrite(String dbName);

  /**
   * Reads the read counter for the db from the file.
   * If dbName doesn't exists in the file, returns 0.
   * @param dbName
   */
  int getRead(String dbName);
  /**
   * Reads the write counter for the db from the file.
   * If dbName doesn't exists in the file, returns 0.
   * @param dbName
   */
  int getWrite(String dbName);
}
