package app.unattach.view;

import app.unattach.controller.Controller;
import app.unattach.controller.ControllerFactory;
import app.unattach.model.Constants;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GmailLabelController {
  private Controller controller;

  @FXML
  private Label introductionLabel;
  @FXML
  private ComboBox<GmailLabel> labelComboBox;
  @FXML
  private Label customizeLabel;
  @FXML
  private Button cancelButton;
  @FXML
  private Button okButton;

  @FXML
  public void initialize() {
    controller = ControllerFactory.getDefaultController();
    introductionLabel.setText(
        Constants.PRODUCT_NAME + " adds a Gmail label to all emails where\n" +
        "attachments have been removed."
    );
    customizeLabel.setText(
        "You can edit the label settings within Gmail itself,\n" +
        "while you can edit the label appearance by pressing\n" +
        "⋮ next to the label name in the list of labels."
    );
    String removedLabelId = controller.getOrCreateRemovedLabelId();
    List<GmailLabel> labels = controller.getIdToLabel().entrySet().stream()
            .map(entry -> new GmailLabel(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(label -> label.name))
            .collect(Collectors.toList());
    labelComboBox.getItems().setAll(labels);
    OptionalInt index = IntStream.range(0, labels.size())
            .filter(i -> labels.get(i).id.equals(removedLabelId)).findFirst();
    if (index.isPresent()) {
      labelComboBox.getSelectionModel().select(index.getAsInt());
    }
  }

  @FXML
  private void onEditLabelsInGmailButtonPressed() {
    controller.openWebPage("https://mail.google.com/mail/u/0/#settings/labels");
  }

  @FXML
  private void onCancelButtonPressed() {
    cancelButton.getScene().getWindow().hide();
  }

  @FXML
  private void onOkButtonPressed() {
    GmailLabel label = labelComboBox.getSelectionModel().getSelectedItem();
    controller.saveRemovedLabelId(label.id);
    okButton.getScene().getWindow().hide();
  }

  private static class GmailLabel {
    private final String id;
    private final String name;

    private GmailLabel(String id, String name) {
      this.id = id;
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }
}
