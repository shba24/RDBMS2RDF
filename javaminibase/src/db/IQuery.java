package db;

public interface IQuery {
  // needs to inject rdfdb as dependency here
  void execute();
}
