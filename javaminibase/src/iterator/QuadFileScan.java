package iterator;

import bufmgr.PageNotReadException;
import diskmgr.rdf.SelectFilter;
import global.QID;
import heap.FieldNumberOutOfBoundException;
import heap.InvalidTupleSizeException;
import heap.InvalidTypeException;
import heap.Quadruple;
import heap.quadrupleheap.QuadrupleHeapFile;
import heap.quadrupleheap.TScan;

import java.io.IOException;

/**
 * open a heapfile and according to the condition expression to get
 * output file, call get_next to get all quadruples
 */
public class QuadFileScan extends Iterator {
  private QuadrupleHeapFile f;
  private TScan tScan;
  private Quadruple quadruple1;
  private SelectFilter selectFilter;

  /**
   * constructor
   *
   * @param quadrupleHeapFile  heapfile
   * @param _selectFilter  select expressions
   * @throws IOException             some I/O fault
   * @throws FileScanException       exception from this class
   * @throws QuadrupleUtilsException exception from this class
   * @throws InvalidRelation         invalid relation
   */
  public QuadFileScan(
      QuadrupleHeapFile quadrupleHeapFile,
      SelectFilter _selectFilter)
      throws IOException,
      FileScanException,
      InvalidTupleSizeException, InvalidTypeException {
    selectFilter = _selectFilter;
    quadruple1 = new Quadruple();
    quadruple1.setDefaultHeader();
    f = quadrupleHeapFile;
    try {
      tScan = f.openTScan();
    } catch (Exception e) {
      throw new FileScanException(e, "openScan() failed");
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
  public Quadruple get_next()
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
      if (QuadPredEval.Eval(selectFilter, quadruple1) == true) {
        return quadruple1;
      }
    }
  }

  /**
   * implement the abstract method close() from super class Iterator
   * to finish cleaning up
   */
  public void close() {

    if (!closeFlag) {
      tScan.closescan();
      closeFlag = true;
    }
  }
}