package iterator;

import diskmgr.rdf.SelectFilter;
import heap.Quadruple;

public class QuadPredEval extends PredEval {
  /**
   * predicate evaluate, according to the condition ConExpr, judge if
   * the two quadruple can join. if so, return true, otherwise false
   *
   * @param t1    compared quadruple1
   * @return true or false
   */
  public static boolean Eval(SelectFilter filter, Quadruple t1)
      throws PredEvalException {
      try {
        String subject = t1.getSubjectLabel();
        String predicate = t1.getPredicateLabel();
        String object = t1.getObjectLabel();
        Float confidence = t1.getConfidence();
        if ((filter.subjectFilter==null || subject.equals(filter.subjectFilter)) &&
            (filter.predicateFilter==null || predicate.equals(filter.predicateFilter)) &&
            (filter.objectFilter==null || object.equals(filter.objectFilter)) &&
            (filter.confidenceFilter==null || filter.confidenceFilter <= confidence)) {
          return true;
        }
      } catch (Exception e) {
        throw new PredEvalException(e, "QuadrupleUtilsException is caught by PredEval.java");
      }

    return false;
  }
}