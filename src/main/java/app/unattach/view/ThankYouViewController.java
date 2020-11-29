package app.unattach.view;

import app.unattach.controller.Controller;
import app.unattach.controller.ControllerFactory;
import app.unattach.model.DonationOption;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.util.List;

public class ThankYouViewController {
  private Runnable onSubmitFeedbackCallback;
  @FXML
  private ComboBox<DonationOption> buyCoffeeComboBox;
  @FXML
  private ComboBox<String> currencyComboBox;

  @FXML
  public void initialize() {
    Controller controller = ControllerFactory.getDefaultController();
    List<DonationOption> donationOptions = DonationViewUtils.getDonationOptions();
    DonationViewUtils.configureDonationControls(controller, donationOptions, buyCoffeeComboBox, currencyComboBox);
    Platform.runLater(() -> buyCoffeeComboBox.requestFocus());
  }

  public void setOnSubmitFeedbackCallback(Runnable onSubmitFeedbackCallback) {
    this.onSubmitFeedbackCallback = onSubmitFeedbackCallback;
  }

  @FXML
  protected void onSubmitFeedbackButtonPressed() {
    onSubmitFeedbackCallback.run();
  }

  @FXML
  protected void onCloseButtonPressed(ActionEvent actionEvent) {
    Button button = (Button) actionEvent.getSource();
    Stage stage = (Stage) button.getScene().getWindow();
    stage.close();
  }
}
