package iterator;

import btree.quadbtree.BTFileScan;
import btree.quadbtree.KeyDataEntry;
import bufmgr.PageNotReadException;
import diskmgr.rdf.SelectFilter;
import global.EID;
import global.QID;
import heap.BasicPatternClass;
import heap.FieldNumberOutOfBoundException;
import heap.InvalidTupleSizeException;
import heap.InvalidTypeException;
import heap.Quadruple;
import heap.quadrupleheap.QuadrupleHeapFile;
import index.IndexException;
import java.io.IOException;

/**
 * Opens a Heapfile and according to the condition expression to get
 * output file, call get_next to get all quadruples
 */
public class BPIndexScan extends BPIterator {
  private BTFileScan scan;
  private QuadrupleHeapFile quadrupleHeapFile;
  private Quadruple quadruple1;
  private SelectFilter selectFilter;

  /**
   * Public constructor
   *
   * @param  _scan                btfilescan
   * @param _selectFilter  select expressions
   * @throws IOException             some I/O fault
   * @throws FileScanException       exception from this class
   * @throws QuadrupleUtilsException exception from this class
   * @throws InvalidRelation         invalid relation
   */
  public BPIndexScan(
      BTFileScan _scan,
      QuadrupleHeapFile _quadrupleHeapFile,
      SelectFilter _selectFilter)
      throws InvalidTupleSizeException, IOException, InvalidTypeException {
    selectFilter = _selectFilter;
    quadruple1 = new Quadruple();
    quadrupleHeapFile = _quadrupleHeapFile;
    scan = _scan;
  }

  /**
   * @return the result quadruple
   * @throws JoinsException                 some join exception
   * @throws IOException                    I/O errors
   * @throws InvalidTupleSizeException      invalid quadruple size
   * @throws InvalidTypeException           quadruple type not valid
   * @throws PageNotReadException           exception from lower layer
   * @throws PredEvalException              exception from PredEval class
   * @throws UnknowAttrType                 attribute type unknown
   * @throws FieldNumberOutOfBoundException array out of bounds
   * @throws WrongPermat                    exception for wrong FldSpec argument
   */
  @Override
  public BasicPatternClass get_next()
      throws Exception {
    KeyDataEntry entry = scan.get_next();
    while (entry != null) {
      QID qid = ((btree.quadbtree.LeafData) entry.data).getData();
      quadruple1 = quadrupleHeapFile.getQuadruple(qid);
      quadruple1.setDefaultHeader();
      if (QuadPredEval.Eval(selectFilter, quadruple1)) {
        EID subjectId = quadruple1.getSubjectID();
        EID objectId = quadruple1.getObjectID();
        Float confidence = quadruple1.getConfidence();
        EID[] nodeIds = new EID[]{subjectId, objectId};
        return new BasicPatternClass(nodeIds, confidence);
      }
      entry = scan.get_next();
    }
    return null;
  }

  /**
   * implement the abstract method close() from super class Iterator
   * to finish cleaning up
   */
  @Override
  public void close() throws IndexException {

    if (!closeFlag) {
      try {
        scan.DestroyBTreeFileScan();
      } catch (Exception e) {
        throw new IndexException(e, "BTree error in destroying index scan.");
      }
      closeFlag = true;
    }
  }
}