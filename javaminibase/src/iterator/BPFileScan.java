package iterator;

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
import heap.quadrupleheap.TScan;
import java.io.IOException;

/**
 * Opens a Heapfile and according to the condition expression to get
 * output file, call get_next to get all quadruples
 */
public class BPFileScan extends BPIterator {
  private QuadrupleHeapFile f;
  private TScan tScan;
  private Quadruple quadruple1;
  private SelectFilter selectFilter;

  /**
   * Public constructor
   *
   * @param  quadrupleHeapFile                heapfile
   * @param _selectFilter  select expressions
   * @throws IOException             some I/O fault
   * @throws FileScanException       exception from this class
   * @throws QuadrupleUtilsException exception from this class
   * @throws InvalidRelation         invalid relation
   */
  public BPFileScan(
      QuadrupleHeapFile quadrupleHeapFile,
      SelectFilter _selectFilter)
      throws FileScanException, InvalidTupleSizeException, IOException, InvalidTypeException {
    selectFilter = _selectFilter;
    quadruple1 = new Quadruple();
    f = quadrupleHeapFile;
    try {
      tScan = f.openTScan();
    } catch (Exception e) {
      throw new FileScanException(e, "openTScan() failed");
    }
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
      throws JoinsException,
      IOException,
      InvalidTupleSizeException,
      InvalidTypeException,
      PageNotReadException,
      PredEvalException,
      UnknowAttrType,
      FieldNumberOutOfBoundException,
      WrongPermat {
    QID qid = new QID();

    while (true) {
      if ((quadruple1 = tScan.getNext(qid)) == null) {
        return null;
      }
      quadruple1.setDefaultHeader();
      if (QuadPredEval.Eval(selectFilter, quadruple1)) {
        EID subjectId = quadruple1.getSubjectID();
        EID objectId = quadruple1.getObjectID();
        Float confidence = quadruple1.getConfidence();
        EID[] nodeIds = new EID[]{subjectId, objectId};
        return new BasicPatternClass(nodeIds, confidence);
      }
    }
  }

  /**
   * implement the abstract method close() from super class Iterator
   * to finish cleaning up
   */
  @Override
  public void close() {

    if (!closeFlag) {
      tScan.closescan();
      closeFlag = true;
    }
  }
}