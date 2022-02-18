package global;

/**
 * Enum for specifying the three types of label - subject, predicate and object.
 */
public enum LabelType {

    SUBJECT('S'),
    PREDICATE('P'),
    OBJECT('O');

    private final char asChar;

    LabelType(char asChar) {
        this.asChar = asChar;
    }

    public char getAsChar() {
        return asChar;
    }
}