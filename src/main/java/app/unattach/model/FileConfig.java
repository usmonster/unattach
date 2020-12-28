package app.unattach.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileConfig extends BaseConfig {
  private static final Logger LOGGER = Logger.getLogger(FileConfig.class.getName());

  @Override
  public void loadConfig() {
    File configFile = getConfigPath().toFile();
    if (configFile.exists()) {
      try (FileInputStream in = new FileInputStream(configFile)) {
        config.load(in);
      } catch (IOException e) {
        LOGGER.log(Level.SEVERE, "Failed to load the config file.", e);
      }
    }
  }

  @Override
  public void saveConfig() {
    File configFile = getConfigPath().toFile();
    try (FileOutputStream out = new FileOutputStream(configFile)) {
      config.store(out, null);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Failed to save the config file.", e);
    }
  }

  private static Path getConfigPath() {
    String userHome = System.getProperty("user.home");
    return Paths.get(userHome, "." + Constants.PRODUCT_NAME.toLowerCase() + ".properties");
  }
}
