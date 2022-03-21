package diskmgr.IndexingSchemes;

import btree.AddFileEntryException;
import btree.ConstructPageException;
import btree.GetFileEntryException;
import btree.PinPageException;
import db.IndexOption;
import java.io.IOException;

/**
 * Factory class for the Index Scheme.
 */
public class IndexSchemeFactory {
  public static IndexScheme createIndexScheme(IndexOption indexOption, String filename)
      throws ConstructPageException, GetFileEntryException, PinPageException, AddFileEntryException, IOException {
    IndexScheme indexScheme = null;
    switch (indexOption) {
      case Confidence: {
        indexScheme = new ConfidenceIndexScheme(filename);
        break;
      }
      case Subject: {
        indexScheme = new SubjectIndexScheme(filename);
        break;
      }
      case Predicate: {
        indexScheme = new PredicateIndexScheme(filename);
        break;
      }
      case Object: {
        indexScheme = new ObjectIndexScheme(filename);
        break;
      }
      case SubjectPredicateObjectConfidence: {
        indexScheme = new SubjectPredicateObjectConfidenceScheme(filename);
        break;
      }
      case SubjectPredicateObject: {
        indexScheme = new SubjectPredicateObjectScheme(filename);
        break;
      }
    };
    return indexScheme;
  }
}
