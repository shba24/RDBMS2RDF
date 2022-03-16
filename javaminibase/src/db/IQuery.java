package db;

import global.RDFSystemDefs;

/**
 * Implements the interface for Command behavioural
 * design pattern
 */
public interface IQuery {
  // needs to inject rdfdb as dependency here
  void execute(RDFSystemDefs rdfSystemDefs) throws Exception;
}
