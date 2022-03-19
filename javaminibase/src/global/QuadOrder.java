package global;

public class QuadOrder {
  public static final int SubjectPredicateObjectConfidence = 1;
  public static final int PredicateSubjectObjectConfidence = 2;
  public static final int SubjectConfidence = 3;
  public static final int PredicateConfidence = 4;
  public static final int ObjectConfidence = 5;
  public static final int Confidence = 6;

  public int quadOrder;

  /**
   * QuadOrder Constructor
   * <br>
   * A tuple ordering can be defined as
   * <ul>
   * <li>   QuadOrder quadOrder = new QuadOrder(QuadOrder.Random);
   * </ul>
   * and subsequently used as
   * <ul>
   * <li>   if (quadOrder.quadOrder == QuadOrder.Random) ....
   * </ul>
   *
   * @param _quadOrder The possible ordering of the quadruples
   */

  public QuadOrder(int _quadOrder) {
    quadOrder = _quadOrder;
  }

  public String toString() {

    switch (quadOrder) {
      case SubjectPredicateObjectConfidence:
        return "SubjectPredicateObjectConfidence";
      case PredicateSubjectObjectConfidence:
        return "PredicateSubjectObjectConfidence";
      case SubjectConfidence:
        return "SubjectConfidence";
      case PredicateConfidence:
        return "PredicateConfidence";
      case ObjectConfidence:
        return "ObjectConfidence";
      case Confidence:
        return "Confidence";
    }
    return ("Unexpected TupleOrder " + quadOrder);
  }
}
