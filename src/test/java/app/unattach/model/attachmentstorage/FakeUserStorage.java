package app.unattach.model.attachmentstorage;

import org.apache.commons.io.IOUtils;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class FakeUserStorage implements UserStorage {
  private final Map<String, byte[]> subPathToAttachment = new TreeMap<>();
  private final Map<String, MimeMessage> targetSubPathToMessage = new TreeMap<>();

  public Map<String, byte[]> getSubPathToAttachment() {
    return Collections.unmodifiableMap(subPathToAttachment);
  }

  public Map<String, MimeMessage> getTargetSubPathToMessage() {
    return targetSubPathToMessage;
  }

  @Override
  public void saveAttachment(InputStream inputStream, File targetDirectory, String targetSubPath, long targetTimestamp)
      throws IOException {
    subPathToAttachment.put(targetSubPath, IOUtils.toByteArray(inputStream));
  }

  @Override
  public void saveMessage(MimeMessage mimeMessage, File targetDirectory, String targetSubPath) {
    targetSubPathToMessage.put(targetSubPath, mimeMessage);
  }
}
