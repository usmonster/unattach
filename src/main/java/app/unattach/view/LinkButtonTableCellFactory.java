package app.unattach.view;

import app.unattach.controller.Controller;
import app.unattach.controller.ControllerFactory;
import app.unattach.model.Email;
import app.unattach.model.service.GmailServiceException;
import app.unattach.utils.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class LinkButtonTableCellFactory
    implements Callback<TableColumn.CellDataFeatures<Email, Button>, ObservableValue<Button>> {
  private static final Logger logger = Logger.get();

  private final Controller controller;
  private String emailAddress;

  public LinkButtonTableCellFactory() {
    controller = ControllerFactory.getDefaultController();
    try {
      emailAddress = controller.getEmailAddress();
    } catch (GmailServiceException e) {
      logger.error("Failed to get the user's email address.", e);
    }
  }

  @Override
  public ObservableValue<Button> call(TableColumn.CellDataFeatures<Email, Button> cellDataFeatures) {
    Email email = cellDataFeatures.getValue();
    Button button = new Button();
    button.setText("Open");
    addOnActionHandler(email, button);
    return new SimpleObjectProperty<>(button);
  }

  private void addOnActionHandler(Email email, Button button) {
    button.setOnAction(event -> {
      String url = String.format("https://mail.google.com/mail/u/%s/#inbox/%s", emailAddress, email.getGmailId());
      controller.openWebPage(url);
    });
  }
}