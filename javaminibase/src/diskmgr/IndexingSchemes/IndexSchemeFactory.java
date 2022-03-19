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
  public static IndexScheme createIndexScheme(IndexOption indexOption)
      throws ConstructPageException, GetFileEntryException, PinPageException, AddFileEntryException, IOException {
    IndexScheme indexScheme = null;
    switch (indexOption) {
      case Confidence: {
        indexScheme = new ConfidenceIndexScheme();
        break;
      }
      case Subject: {
        indexScheme = new SubjectIndexScheme();
        break;
      }
      case Predicate: {
        indexScheme = new PredicateIndexScheme();
        break;
      }
      case Object: {
        indexScheme = new ObjectIndexScheme();
        break;
      }
      case SubjectPredicateObjectConfidence: {
        indexScheme = new SubjectPredicateObjectConfidenceScheme();
        break;
      }
      case SubjectPredicateObject: {
        indexScheme = new SubjectPredicateObjectScheme();
        break;
      }
    };
    return indexScheme;
  }
}
