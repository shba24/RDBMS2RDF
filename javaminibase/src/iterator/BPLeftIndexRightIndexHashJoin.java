package iterator;

import btree.StringKey;
import btree.quadbtree.BTFileScan;
import bufmgr.PageNotReadException;
import diskmgr.IndexingSchemes.IndexScheme;
import diskmgr.rdf.SelectFilter;
import heap.BasicPatternClass;
import heap.InvalidTupleSizeException;
import heap.InvalidTypeException;
import heap.Quadruple;
import heap.quadrupleheap.QuadrupleHeapFile;
import index.IndexException;
import index.QuadIndexScan;
import java.io.IOException;

public class BPLeftIndexRightIndexHashJoin extends BPIterator {
  private final int amtOfMem;
  private final int numLeftNodes;
  private final BPIterator leftItr;
  private final int bpJoinNodePosition;
  private final int joinOnSubjectOrObject;
  private final SelectFilter rightSelectFilter;
  private final IndexScheme indexScheme;
  private final QuadrupleHeapFile quadrupleHeapFile;
  private final int[] leftOutNodePositions;
  private final int outputRightSubject;
  private final int outputRightObject;
  private BasicPatternClass outerBp;
  private Quadruple innerQuad;
  private boolean done, get_from_outer;
  private QuadIndexScan rightItr;

  /**
   * @param _amtOfMem
   * @param _numLeftNodes
   * @param _leftItr               access method for left i/p to join
   * @param _bpJoinNodePosition
   * @param _joinOnSubjectOrObject
   * @param _leftOutNodePositions
   * @param _outputRightSubject
   * @param _outputRightObject
   * @throws InvalidTupleSizeException
   * @throws IOException
   * @throws InvalidTypeException
   * @throws NestedLoopException
   */
  public BPLeftIndexRightIndexHashJoin(
      int _amtOfMem,
      int _numLeftNodes,
      BPIterator _leftItr,
      int _bpJoinNodePosition,
      int _joinOnSubjectOrObject,
      SelectFilter _rightSelectFilter,
      IndexScheme _indexScheme,
      QuadrupleHeapFile _quadrupleHeapFile,
      int[] _leftOutNodePositions,
      int _outputRightSubject,
      int _outputRightObject) throws Exception {
    amtOfMem = _amtOfMem;
    numLeftNodes = _numLeftNodes;
    leftItr = _leftItr;
    bpJoinNodePosition = _bpJoinNodePosition;
    joinOnSubjectOrObject = _joinOnSubjectOrObject;
    rightSelectFilter = _rightSelectFilter;
    indexScheme = _indexScheme;
    quadrupleHeapFile = _quadrupleHeapFile;
    leftOutNodePositions = _leftOutNodePositions;
    outputRightSubject = _outputRightSubject;
    outputRightObject = _outputRightObject;
    innerQuad = new Quadruple(); // Inner BP Tuple
    outerBp = new BasicPatternClass(); // Output BP Tuple
    done = false;
    get_from_outer = true;
    rightItr = null;
  }

  /**
   * abstract method, every subclass must implement it.
   *
   * @return the result tuple
   * @throws IOException               I/O errors
   * @throws JoinsException            some join exception
   * @throws IndexException            exception from super class
   * @throws InvalidTupleSizeException invalid tuple size
   * @throws InvalidTypeException      tuple type not valid
   * @throws PageNotReadException      exception from lower layer
   * @throws TupleUtilsException       exception from using tuple utilities
   * @throws PredEvalException         exception from PredEval class
   * @throws SortException             sort exception
   * @throws LowMemException           memory error
   * @throws UnknowAttrType            attribute type unknown
   * @throws UnknownKeyTypeException   key type unknown
   * @throws Exception                 other exceptions
   */
  @Override
  public BasicPatternClass get_next()
      throws IOException, JoinsException, IndexException, InvalidTupleSizeException, InvalidTypeException,
      PageNotReadException, TupleUtilsException, PredEvalException, SortException, LowMemException, UnknowAttrType,
      UnknownKeyTypeException, Exception {

    if (done) {
      return null;
    }

    do {
      // If get_from_outer is true, Get a bp from the outer, delete
      // an existing stream on the file, and reopen a new stream on the file.
      // If a get_next on the outer returns DONE?, then the nested loops
      // join is done too.

      if (get_from_outer == true) {
        get_from_outer = false;
        if (rightItr != null)     // If this not the first time,
        {
          rightItr.close();
          rightItr = null;
        }

        if ((outerBp = leftItr.get_next()) == null) {
          done = true;
          return null;
        }

        try {
          Quadruple quad = new Quadruple();
          quad.setSubjectID(rightSelectFilter.subjectID)
              .setPredicateID(rightSelectFilter.predicateID)
              .setObjectID(rightSelectFilter.objectID)
              .setConfidence(rightSelectFilter.confidenceFilter);
          if (joinOnSubjectOrObject==0) {
            quad.setSubjectID(outerBp.getNodeId(bpJoinNodePosition));
          } else {
            quad.setObjectID(outerBp.getNodeId(bpJoinNodePosition));
          }
          StringKey lo_key = indexScheme.getKey(quad);
          StringKey hi_key = new StringKey(lo_key.getKey());
          BTFileScan scan = indexScheme.getBtreeFile().new_scan(lo_key, hi_key);
          rightItr = new QuadIndexScan(rightSelectFilter, scan, quadrupleHeapFile);
        } catch (Exception e) {
          throw new NestedLoopException(e, "openScan failed");
        }
      }  // ENDS: if (get_from_outer == TRUE)

      // The next step is to get a tuple from the inner,
      // while the inner is not completely scanned && there
      // is no match (with pred),get a tuple from the inner.
      if ((innerQuad = rightItr.get_next()) != null) {
        int noOfNodeIds = leftOutNodePositions.length + outputRightSubject + outputRightObject + 1 /*confidence*/;
        BasicPatternClass bpOutput = new BasicPatternClass();
        bpOutput.setDefaultHeader(noOfNodeIds);
        bpOutput.setConfidence(Math.min(outerBp.getConfidence(), innerQuad.getConfidence()));
        int i = 2;
        for (; i < leftOutNodePositions.length + 2; i++) {
          bpOutput.setNodeId(outerBp.getNodeId(leftOutNodePositions[i - 2]), i);
        }
        if (outputRightSubject == 1) {
          bpOutput.setNodeId(innerQuad.getSubjectID(), i++);
        }
        if (outputRightObject == 1) {
          bpOutput.setNodeId(innerQuad.getObjectID(), i++);
        }
        return bpOutput;
      }

      // There has been no match. (otherwise, we would have
      //returned from t//he while loop. Hence, inner is
      //exhausted, => set get_from_outer = TRUE, go to top of loop

      get_from_outer = true; // Loop back to top and get next outer tuple.
    } while (true);
  }

  /**
   * @throws IOException    I/O errors
   * @throws JoinsException some join exception
   * @throws IndexException exception from Index class
   * @throws SortException  exception Sort class
   */
  @Override
  public void close() throws IOException, JoinsException, SortException, IndexException {
    if (!closeFlag) {

      try {
        leftItr.close();
        if (rightItr != null) {
          rightItr.close();
        }
      } catch (Exception e) {
        throw new JoinsException(e, "BPJava.java: error in closing iterator.");
      }
      closeFlag = true;
    }
  }
}

