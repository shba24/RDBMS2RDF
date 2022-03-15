package diskmgr.IndexingSchemes;

/**
 * IndexScheme uses strategy pattern, this class used to change the strategy.
 */
public class IndexSchemeContext {
  public static IndexSchemes objectIndex = new ObjectIndexScheme();
  public static IndexSchemes subjectIndex = new SubjectIndexScheme();
  public static IndexSchemes confidenceIndex = new ConfidenceIndexScheme();
  public static IndexSchemes predicateConfidenceIndex = new PredicateConfidenceIndexScheme();
  public static IndexSchemes subjectConfidenceIndex = new SubjectConfidenceIndexScheme();
  public static IndexSchemes objectConfidenceIndex = new ObjectConfidenceIndexScheme();
}
