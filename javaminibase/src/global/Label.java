package global;

import lombok.Data;
import java.io.IOException;

/**
 * Label
 * Tuple is modified into label which can be used by subject, predicate or object
 */
@Data
public class Label {

    private LID lid;

    /**
     * label name
     */
    private String label;

    private LabelType labelType;

    /**
     * Constructor to label class
     */
    public Label() {

    }

    /**
     * insert label and return LID
     * @param label - label to be inserted
     * @return
     */
    public LID insertLabel(Label label) {

        Label newLabel = new Label();
        newLabel.setLabel(label.label);
        newLabel.setLabelType(label.labelType);

        return newLabel.getLid();
    }

    /**
     * Update label and return if success
     * @param lid - lid of label to be updated
     * @param newLabel - label object
     * @return
     */
    public boolean updateLabel(LID lid, Label newLabel) {
        if(this.getLid().equals(lid)) {
            this.setLabel(newLabel.label);
            return true;
        }
        return false;
    }

    /**
     * Delete label with given lid and returns if success
     * @param lid given lid
     * @return
     */
    public boolean deleteLabel(LID lid) {
        if(this.getLid().equals(lid)) {
            this.setLabel("");
            return true;
        }
        return false;
    }

    /**
     * Gets a label
     * @return
     */
    public Label getLabel() {
        return this;
    }

    /**
     * sets the label
     * @param label given label
     * @return
     */
    public Label setLabel(String label) {

        this.label = label;
        return this;
    }

    /**
     * print the label
     */
    public void print() {
        System.out.println(this);
    }
}
