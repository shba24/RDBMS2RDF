package diskmgr.rdf;

import global.QuadOrder;
import iterator.Iterator;
import iterator.QSort;

public abstract class BaseStream implements IStream {
  protected Iterator init(
      QuadOrder _orderType,
      int _numBuf,
      Iterator am) throws Exception {
    Iterator iter = null;
    switch (_orderType.quadOrder) {
      case QuadOrder.SubjectPredicateObjectConfidence:
      {
        Iterator i2 = new QSort(am, 4, _numBuf);
        Iterator i3 = new QSort(i2, 3, _numBuf);
        Iterator i4 = new QSort(i3, 2, _numBuf);
        iter = new QSort(i4, 1, _numBuf);
        break;
      }
      case QuadOrder.PredicateSubjectObjectConfidence:
      {
        Iterator i2 = new QSort(am, 4, _numBuf);
        Iterator i3 = new QSort(i2, 3, _numBuf);
        Iterator i4 = new QSort(i3, 1, _numBuf);
        iter = new QSort(i4, 2, _numBuf);
        break;
      }
      case QuadOrder.SubjectConfidence:
      {
        Iterator i2 = new QSort(am, 4, _numBuf);
        iter = new QSort(i2, 1, _numBuf);
        break;
      }
      case QuadOrder.PredicateConfidence:
      {
        Iterator i2 = new QSort(am, 4, _numBuf);
        iter = new QSort(i2, 2, _numBuf);
        break;
      }
      case QuadOrder.ObjectConfidence:
      {
        Iterator i2 = new QSort(am, 4, _numBuf);
        iter = new QSort(i2, 3, _numBuf);
        break;
      }
      case QuadOrder.Confidence:
      {
        iter = new QSort(am, 4, _numBuf);
        break;
      }
      default:
        iter = am;
    }
    return iter;
  }
}
