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
  private ComboBox<GmailLabel> downloadLabelComboBox;
  @FXML
  private ComboBox<GmailLabel> removeLabelComboBox;
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
        Constants.PRODUCT_NAME + " adds Gmail labels to all emails where\n" +
        "attachments have been downloaded and/or removed."
    );
    customizeLabel.setText(
        "You can edit the labels' settings within Gmail itself.\n" +
        "You can update the label's appearance by pressing\n" +
        "⋮ next to the label's name in the list of labels."
    );
    String downloadedLabelId = controller.getOrCreateDownloadedLabelId();
    String removedLabelId = controller.getOrCreateRemovedLabelId();
    List<GmailLabel> labels = controller.getIdToLabel().entrySet().stream()
            .map(entry -> new GmailLabel(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(GmailLabel::getName))
            .collect(Collectors.toList());
    downloadLabelComboBox.getItems().setAll(labels);
    removeLabelComboBox.getItems().setAll(labels);
    selectLabel(labels, downloadedLabelId, downloadLabelComboBox);
    selectLabel(labels, removedLabelId, removeLabelComboBox);
  }

  private void selectLabel(List<GmailLabel> labels, String labelId, ComboBox<GmailLabel> comboBox) {
    OptionalInt downloadLabelIndex = IntStream.range(0, labels.size())
        .filter(i -> labels.get(i).getId().equals(labelId)).findFirst();
    if (downloadLabelIndex.isPresent()) {
      comboBox.getSelectionModel().select(downloadLabelIndex.getAsInt());
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
    GmailLabel label = removeLabelComboBox.getSelectionModel().getSelectedItem();
    controller.saveRemovedLabelId(label.getId());
    okButton.getScene().getWindow().hide();
  }

}
