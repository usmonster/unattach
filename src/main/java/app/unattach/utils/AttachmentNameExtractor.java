package app.unattach.utils;

import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AttachmentNameExtractor {
  public static List<String> getAttachmentNames(Message message) {
    Set<String> attachmentNames = new TreeSet<>();
    getAttachmentNamesRecursive(attachmentNames, message.getPayload());
    return new ArrayList<>(attachmentNames);
  }

  private static void getAttachmentNamesRecursive(Set<String> attachmentNames, MessagePart part) {
    String attachmentName = part.getFilename();
    if (!StringUtils.isEmpty(attachmentName)) {
      attachmentNames.add(attachmentName);
    }
    List<MessagePart> subParts = part.getParts();
    if (subParts != null) {
      for (MessagePart subPart : subParts) {
        getAttachmentNamesRecursive(attachmentNames, subPart);
      }
    }
  }
}
