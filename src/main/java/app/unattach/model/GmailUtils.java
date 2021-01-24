package app.unattach.model;

import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GmailUtils {
  public static Map<String, String> getHeaderMap(Message message) {
    List<MessagePartHeader> headers = message.getPayload().getHeaders();
    Map<String, String> headerMap = new HashMap<>(headers.size());
    for (MessagePartHeader header : headers) {
      headerMap.put(header.getName().toLowerCase(), header.getValue());
    }
    return headerMap;
  }
}
