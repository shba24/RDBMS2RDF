package diskmgr.IndexingSchemes;

import btree.ConstructPageException;
import btree.GetFileEntryException;
import btree.PinPageException;
import db.IndexOption;

public class IndexSchemeFactory {
  public static IndexScheme createIndexScheme(IndexOption indexOption, String rootFolder)
      throws ConstructPageException, GetFileEntryException, PinPageException {
    IndexScheme indexScheme = null;
    switch (indexOption) {
      case Confidence: {
        indexScheme = new ConfidenceIndexScheme(rootFolder);
        break;
      }
      case Subject: {
        indexScheme = new SubjectIndexScheme(rootFolder);
        break;
      }
      case Predicate: {
        indexScheme = new PredicateIndexScheme(rootFolder);
        break;
      }
      case Object: {
        indexScheme = new ObjectIndexScheme(rootFolder);
        break;
      }
      case SubjectPredicateObjectConfidence: {
        indexScheme = new SubjectPredicateObjectConfidenceScheme(rootFolder);
        break;
      }
      case SubjectPredicateObject: {
        indexScheme = new SubjectPredicateObjectScheme(rootFolder);
        break;
      }
    };
    return indexScheme;
  }
}
