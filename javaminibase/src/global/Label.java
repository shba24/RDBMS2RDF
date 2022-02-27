package global;

import lombok.Data;

/**
 * Label
 * Tuple is modified into label which can be used by subject, predicate or object
 */
@Data
public class Label {

    /**
     * label name
     */
    private String labelName;

    /**
     * Constructor to label class
     */
    public Label() {

    }

    /**
     * Gets a label
     *
     * @return
     */
    public String getLabel() {
        return this.getLabelName();
    }

    /**
     * sets the label
     *
     * @param labelName given label
     * @return
     */
    public Label setLabel(String labelName) {

        Label newLabel = new Label();
        newLabel.setLabelName(labelName);

        return newLabel;
    }

    /**
     * print the label
     */
    public void print() {
        System.out.println(this.getLabel());
    }
}
