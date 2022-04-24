package iterator;

import global.AttrType;
import global.TupleOrder;
import heap.BasicPatternClass;
import heap.Heapfile;
import heap.Tuple;
import java.io.IOException;

public class BPSort extends Sort {
  private final int numNodes;
  /**
   * Class constructor, take information about the BP, and set up
   * the sorting
   *
   * @param  am              an iterator for accessing the BP
   * @param  sortOrder       sorting order of the output
   * @param  sortNodeIdPos   the field number of the field to sort on
   * @param  nPages          amount of memory (in pages) available for sorting
   * @throws IOException     from lower layers
   * @throws SortException   something went wrong in the lower layer.
   */
  public BPSort(
      BPIterator am,
      int _numNodes,
      TupleOrder sortOrder,
      int sortNodeIdPos,
      int nPages
  ) throws IOException, SortException {
    super(
        BasicPatternClass.getDefaultAttrTypes(_numNodes),
        (short) _numNodes,
        BasicPatternClass.getDefaultAttrSizes(_numNodes),
        am, sortNodeIdPos, sortOrder,
        (sortNodeIdPos==1)?4:MAX_EID_OBJ_SIZE, nPages);

    numNodes = _numNodes;
    Q = new bppnodeSplayPQ(sortNodeIdPos, _in[sortNodeIdPos - 1], sortOrder, numNodes);
  }

  /**
   * Generate sorted runs.
   * Using heap sort.
   *
   * @param max_elems   maximum number of elements in heap
   * @param sortFldType attribute type of the sort field
   * @param sortFldLen  length of the sort field
   * @return number of runs generated
   * @throws Exception    from lower layers
   */
  @Override
  protected int generate_runs(int max_elems, AttrType sortFldType, int sortFldLen)
      throws Exception {
    BasicPatternClass bp;
    pnode cur_node;
    pnodeSplayPQ Q1 = new bppnodeSplayPQ(_sort_fld, sortFldType, order, numNodes);
    pnodeSplayPQ Q2 = new bppnodeSplayPQ(_sort_fld, sortFldType, order, numNodes);
    pnodeSplayPQ pcurr_Q = Q1;
    pnodeSplayPQ pother_Q = Q2;
    BasicPatternClass lastElem = new BasicPatternClass(tuple_size);  // need tuple.java
    lastElem.setDefaultHeader(numNodes);
    try {
      lastElem.setHdr(n_cols, _in, str_lens);
    } catch (Exception e) {
      throw new SortException(e, "BPSort.java: setHdr() failed");
    }

    int run_num = 0;  // keeps track of the number of runs

    // number of elements in Q
    //    int nelems_Q1 = 0;
    //    int nelems_Q2 = 0;
    int p_elems_curr_Q = 0;
    int p_elems_other_Q = 0;

    int comp_res;

    // set the lastElem to be the minimum value for the sort field
    if (order.tupleOrder == TupleOrder.Ascending) {
      try {
        MIN_VAL(lastElem, sortFldType);
      } catch (UnknowAttrType e) {
        throw new SortException(e, "Sort.java: UnknowAttrType caught from MIN_VAL()");
      } catch (Exception e) {
        throw new SortException(e, "MIN_VAL failed");
      }
    } else {
      try {
        MAX_VAL(lastElem, sortFldType);
      } catch (UnknowAttrType e) {
        throw new SortException(e, "Sort.java: UnknowAttrType caught from MAX_VAL()");
      } catch (Exception e) {
        throw new SortException(e, "MIN_VAL failed");
      }
    }

    // maintain a fixed maximum number of elements in the heap
    while ((p_elems_curr_Q + p_elems_other_Q) < max_elems) {
      try {
        bp = (BasicPatternClass) _am.get_next();  // according to Iterator.java
      } catch (Exception e) {
        e.printStackTrace();
        throw new SortException(e, "Sort.java: get_next() failed");
      }

      if (bp == null) {
        break;
      }
      cur_node = new pnode();
      cur_node.tuple = new BasicPatternClass(bp); // tuple copy needed --  Bingjie 4/29/98

      pcurr_Q.enq(cur_node);
      p_elems_curr_Q++;
    }

    // now the queue is full, starting writing to file while keep trying
    // to add new tuples to the queue. The ones that does not fit are put
    // on the other queue temperarily
    while (true) {
      cur_node = pcurr_Q.deq();
      if (cur_node == null) {
        break;
      }
      p_elems_curr_Q--;

      comp_res =
          BPUtils.CompareBPWithValue(sortFldType, (BasicPatternClass) cur_node.tuple, _sort_fld, lastElem);
      // need
      // tuple_utils.java

      if ((comp_res < 0 && order.tupleOrder == TupleOrder.Ascending) ||
          (comp_res > 0 && order.tupleOrder == TupleOrder.Descending)) {
        // doesn't fit in current run, put into the other queue
        try {
          pother_Q.enq(cur_node);
        } catch (UnknowAttrType e) {
          throw new SortException(e, "Sort.java: UnknowAttrType caught from Q.enq()");
        }
        p_elems_other_Q++;
      } else {
        // set lastElem to have the value of the current quadruple,
        // need quadruple_utils.java
        BPUtils.SetValue(lastElem, (BasicPatternClass) cur_node.tuple, _sort_fld, sortFldType);
        // write tuple to output file, need io_bufs.java, type cast???
        //	System.out.println("Putting tuple into run " + (run_num + 1));
        //	cur_node.tuple.print(_in);

        o_buf.Put(cur_node.tuple);
      }

      // check whether the other queue is full
      if (p_elems_other_Q == max_elems) {
        // close current run and start next run
        n_tuples[run_num] = (int) o_buf.flush();  // need io_bufs.java
        run_num++;

        // check to see whether need to expand the array
        if (run_num == n_tempfiles) {
          Heapfile[] temp1 = new Heapfile[2 * n_tempfiles];
          for (int i = 0; i < n_tempfiles; i++) {
            temp1[i] = temp_files[i];
          }
          temp_files = temp1;
          n_tempfiles *= 2;

          int[] temp2 = new int[2 * n_runs];
          for (int j = 0; j < n_runs; j++) {
            temp2[j] = n_tuples[j];
          }
          n_tuples = temp2;
          n_runs *= 2;
        }

        try {
          temp_files[run_num] = new Heapfile(null);
        } catch (Exception e) {
          throw new SortException(e, "Sort.java: create Heapfile failed");
        }

        // need io_bufs.java
        o_buf.init(bufs, _n_pages, tuple_size, temp_files[run_num], false);

        // set the last Elem to be the minimum value for the sort field
        if (order.tupleOrder == TupleOrder.Ascending) {
          try {
            MIN_VAL(lastElem, sortFldType);
          } catch (UnknowAttrType e) {
            throw new SortException(e, "Sort.java: UnknowAttrType caught from MIN_VAL()");
          } catch (Exception e) {
            throw new SortException(e, "MIN_VAL failed");
          }
        } else {
          try {
            MAX_VAL(lastElem, sortFldType);
          } catch (UnknowAttrType e) {
            throw new SortException(e, "Sort.java: UnknowAttrType caught from MAX_VAL()");
          } catch (Exception e) {
            throw new SortException(e, "MIN_VAL failed");
          }
        }

        // switch the current heap and the other heap
        pnodeSplayPQ tempQ = pcurr_Q;
        pcurr_Q = pother_Q;
        pother_Q = tempQ;
        int tempelems = p_elems_curr_Q;
        p_elems_curr_Q = p_elems_other_Q;
        p_elems_other_Q = tempelems;
      }

      // now check whether the current queue is empty
      else if (p_elems_curr_Q == 0) {
        while ((p_elems_curr_Q + p_elems_other_Q) < max_elems) {
          try {
            bp = (BasicPatternClass) _am.get_next();  // according to Iterator.java
          } catch (Exception e) {
            throw new SortException(e, "get_next() failed");
          }

          if (bp == null) {
            break;
          }
          cur_node = new pnode();
          cur_node.tuple = new BasicPatternClass(bp); // tuple copy needed --  Bingjie 4/29/98

          try {
            pcurr_Q.enq(cur_node);
          } catch (UnknowAttrType e) {
            throw new SortException(e, "Sort.java: UnknowAttrType caught from Q.enq()");
          }
          p_elems_curr_Q++;
        }
      }

      // Check if we are done
      if (p_elems_curr_Q == 0) {
        // current queue empty despite our attemps to fill in
        // indicating no more tuples from input
        if (p_elems_other_Q == 0) {
          // other queue is also empty, no more tuples to write out, done
          break; // of the while(true) loop
        } else {
          // generate one more run for all tuples in the other queue
          // close current run and start next run
          n_tuples[run_num] = (int) o_buf.flush();  // need io_bufs.java
          run_num++;

          // check to see whether need to expand the array
          if (run_num == n_tempfiles) {
            Heapfile[] temp1 = new Heapfile[2 * n_tempfiles];
            for (int i = 0; i < n_tempfiles; i++) {
              temp1[i] = temp_files[i];
            }
            temp_files = temp1;
            n_tempfiles *= 2;

            int[] temp2 = new int[2 * n_runs];
            for (int j = 0; j < n_runs; j++) {
              temp2[j] = n_tuples[j];
            }
            n_tuples = temp2;
            n_runs *= 2;
          }

          try {
            temp_files[run_num] = new Heapfile(null);
          } catch (Exception e) {
            throw new SortException(e, "Sort.java: create Heapfile failed");
          }

          // need io_bufs.java
          o_buf.init(bufs, _n_pages, tuple_size, temp_files[run_num], false);

          // set the last Elem to be the minimum value for the sort field
          if (order.tupleOrder == TupleOrder.Ascending) {
            try {
              MIN_VAL(lastElem, sortFldType);
            } catch (UnknowAttrType e) {
              throw new SortException(e, "Sort.java: UnknowAttrType caught from MIN_VAL()");
            } catch (Exception e) {
              throw new SortException(e, "MIN_VAL failed");
            }
          } else {
            try {
              MAX_VAL(lastElem, sortFldType);
            } catch (UnknowAttrType e) {
              throw new SortException(e, "Sort.java: UnknowAttrType caught from MAX_VAL()");
            } catch (Exception e) {
              throw new SortException(e, "MIN_VAL failed");
            }
          }

          // switch the current heap and the other heap
          pnodeSplayPQ tempQ = pcurr_Q;
          pcurr_Q = pother_Q;
          pother_Q = tempQ;
          int tempelems = p_elems_curr_Q;
          p_elems_curr_Q = p_elems_other_Q;
          p_elems_other_Q = tempelems;
        }
      } // end of if (p_elems_curr_Q == 0)
    } // end of while (true)

    // close the last run
    n_tuples[run_num] = (int) o_buf.flush();
    run_num++;

    return run_num;
  }

  /**
   * Returns the next BP in sorted order.
   * Note: You need to copy out the content of the BP, otherwise it
   * will be overwritten by the next <code>get_next()</code> call.
   *
   * @return the next BP, null if all BP exhausted
   * @throws IOException     from lower layers
   * @throws SortException   something went wrong in the lower layer.
   * @throws JoinsException  from <code>generate_runs()</code>.
   * @throws UnknowAttrType  attribute type unknown
   * @throws LowMemException memory low exception
   * @throws Exception       other exceptions
   */
  public BasicPatternClass get_next()
      throws IOException,
      SortException,
      UnknowAttrType,
      LowMemException,
      JoinsException,
      Exception {
    Tuple tuple = super.get_next();
    if (tuple!=null) {
      BasicPatternClass bp = new BasicPatternClass(tuple);
      bp.setDefaultHeader(numNodes);
      return bp;
    }
    return null;
  }
}
