package heap.quadrupleheap;

import diskmgr.Page;
import diskmgr.rdf.RdfDB;
import global.QID;
import global.RID;
import global.SystemDefs;
import heap.HFPage;
import heap.InvalidSlotNumberException;
import heap.Quadruple;
import heap.Tuple;

import java.io.IOException;

/**
 * THFPage is the implementation of the quadruple heap file page. It uses adapter design pattern to
 * deliver its functionality by using HFPage object as a member of this class.
 */
public class THFPage extends HFPage {
  /**
   * Default constructor. No operations take place inside this constructor.
   */
  public THFPage() {
  }

  /**
   * Constructor to initialize a quadruple heap file page from a Page object.
   *
   * @param page page to construct the THFPage from
   */
  public THFPage(Page page) {
    super(page);
  }

  /**
   * Inserts a new quadruple onto the page. Returns the generated QID for the quadruple being
   * stored.
   *
   * @param quadruple a quadruple to be inserted
   * @return QID of quadruple, null if sufficient space does not exist
   * @throws IOException              I/O errors in C++ Status insertRecord(char *recPtr, int
   *                                  recLen, RID& rid)
   * @throws IllegalArgumentException thrown when the byte array is empty
   */
  public QID insertQuadruple(byte[] quadruple) throws IOException {

    RID rid;

    try {
      rid = this.insertRecord(quadruple);
    } catch (Exception e) {
      System.err.println("[THFPage] I/O error in inserting quadruple.");
      e.printStackTrace();
      throw e;
    }

    /*
      rid is null when insertion of quadruple fails. When QHeapfile calls insertQuadruple,
      it will receive null qid. In that case, QHeapfile should throw new QHeapfileException,
      the way it is done in Heapfile.
     */
    if (rid != null) {
      return new QID(rid.pageNo, rid.slotNo);
    } else {
      return null;
    }
  }

  /**
   * Delete the quadruple with the specified qid.
   *
   * @param qid qid of the quadruple to be deleted
   * @throws IOException                I/O exception when deleting quadruple from the page
   * @throws InvalidSlotNumberException Invalid slot number
   * @throws NullPointerException       NPE when qid is null
   */
  public void deleteQuadruple(QID qid) throws IOException, InvalidSlotNumberException {
    RID rid = new RID();
    rid.pageNo = qid.getPageNo();
    rid.slotNo = qid.getSlotNo();

    try {
      this.deleteRecord(rid);
    } catch (Exception e) {
      System.err.println("[THFPage] Error in deleting Quadruple.");
      throw e;
    }
  }

  /**
   * Returns the first quadruple stored on the page. In case there are no quadruples, it returns
   * null.
   *
   * @return qid of the first quadruple in the page
   * @throws IOException I/O exception when reading the first quadruple from the page
   */
  public QID firstQuadruple() throws IOException {
    RID rid;

    try {
      rid = this.firstRecord();
    } catch (Exception e) {
      System.err.println("[THFPage] I/O exception in retrieving first quadruple.");
      throw e;
    }

    if (rid == null) {
      return null;
    } else {
      return new QID(rid.pageNo, rid.slotNo);
    }
  }

  /**
   * Returns the next quadruple stored on the page given the QID of the current quadruple.
   *
   * @param curQuad qid of the current quadruple
   * @return qid of the next quadruple
   * @throws IOException          I/O exception when reading the next quadruple
   * @throws NullPointerException NPE when curQuad is null from the page
   */
  public QID nextQuadruple(QID curQuad) throws IOException, NullPointerException {
    RID curRid = new RID();
    curRid.pageNo = curQuad.getPageNo();
    curRid.slotNo = curQuad.getSlotNo();

    RID nextRid;

    try {
      nextRid = this.nextRecord(curRid);
    } catch (Exception e) {
      System.err.println("[THFPage] Exception in retrieving next quadruple.");
      throw e;
    }

    if (nextRid == null) {
      return null;
    } else {
      return new QID(nextRid.pageNo, nextRid.slotNo);
    }
  }

  /**
   * Get the quadruple with the specified QID.
   *
   * @param qid qid of the quadruple to be fetched
   * @return quadruple with the specified QID
   * @throws IOException                I/O exception when reading the quadruple from the page
   * @throws InvalidSlotNumberException Invalid slot number
   * @throws NullPointerException       thrown when qid is null
   */
  public Quadruple getQuadruple(QID qid)
      throws Exception {
    RID rid = new RID();

    rid.pageNo = qid.getPageNo();
    rid.slotNo = qid.getSlotNo();

    Tuple tuple;

    try {
      tuple = this.getRecord(rid);
    } catch (Exception e) {
      System.err.println("[THFPage] Exception in getting quadruple.");
      throw e;
    }

    // tuple will not be null. When it will be, exception is thrown.
    if (tuple != null) {
      SystemDefs.telemetry.readQuad();
      return new Quadruple(tuple.getTupleByteArray(), tuple.getOffset(), tuple.getLength());
    } else {
      return null;
    }
  }

  /**
   * Returns the quadruple for the specified QID. Null is returned if the quadruple is not found.
   *
   * @param qid qid of the quadruple to be fetched
   * @return quadruple to be fetched
   * @throws InvalidSlotNumberException invalid slot number
   * @throws IOException                I/O exception when reading quadruple from the page
   * @throws NullPointerException       NPE when qid is null
   */
  public Quadruple returnQuadruple(QID qid)
      throws Exception {
    RID rid = new RID();
    rid.pageNo.pid = qid.getPageNo().pid;
    rid.slotNo = qid.getSlotNo();
    Tuple tuple;

    try {
      tuple = this.returnRecord(rid);
    } catch (IOException | InvalidSlotNumberException e) {
      System.err.println("[THFPage] I/O exception in returning quadruple.");
      throw e;
    }

    if (tuple != null) {
      return new Quadruple(tuple.returnTupleByteArray(), tuple.getOffset(), tuple.getLength());
    } else {
      return null;
    }
  }
}
