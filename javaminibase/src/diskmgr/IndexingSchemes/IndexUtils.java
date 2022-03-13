package diskmgr.IndexingSchemes;

import btree.GetFileEntryException;
import btree.KeyClass;
import btree.quadbtree.BTFileScan;
import btree.quadbtree.BTreeFile;
import btree.quadbtree.KeyDataEntry;
import btree.quadbtree.LeafData;
import global.QID;
import java.util.ArrayList;

public class IndexUtils {

  /**
   * Destroys the existing indexfile
   *
   * @param fileName
   */
  public static void destroyIndex(String fileName) {
    try {
      if (fileName != null) {

        BTreeFile bfile = new BTreeFile(fileName);

        BTFileScan scan = bfile.new_scan(null, null);
        QID qid = null;
        KeyDataEntry entry = null;
        ArrayList<KeyClass> keys = new ArrayList<KeyClass>();
        ArrayList<QID> qids = new ArrayList<QID>();
        int count = 0;

        while ((entry = scan.get_next()) != null) {
          qid = ((LeafData) entry.data).getData();
          keys.add(entry.key);
          qids.add(qid);
          count++;
        }
        scan.DestroyBTreeFileScan();

        for (int i = 0; i < count; i++) {
          //System.out.println("Deleting record having Key : " + keys.get(i) + " TID " + tids.get(i));
          bfile.Delete(keys.get(i), qids.get(i));
        }

        bfile.close();

      }
    } catch (GetFileEntryException e1) {
      System.out.println("Firsttime No index present.. Expected");
    } catch (Exception e) {
      System.err.println("*** Error destroying Index " + e);
      e.printStackTrace();
      Runtime.getRuntime().exit(1);
    }

  }

}
