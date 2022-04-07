package iterator;

import diskmgr.rdf.SelectFilter;
import global.EID;
import global.PID;
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
        EID subject = (EID) t1.getSubjectID();
        PID predicate = (PID) t1.getPredicateID();
        EID object = (EID) t1.getObjectID();
        Float confidence = t1.getConfidence();
        if ((filter.subjectID==null || subject.equals(filter.subjectID)) &&
            (filter.predicateID==null || predicate.equals(filter.predicateID)) &&
            (filter.objectID==null || object.equals(filter.objectID)) &&
            (filter.confidenceFilter==null || filter.confidenceFilter <= confidence)) {
          return true;
        }
      } catch (Exception e) {
        throw new PredEvalException(e, "QuadrupleUtilsException is caught by PredEval.java");
      }

    return false;
  }
}