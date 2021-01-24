package app.unattach.model.service;

import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.Message;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public interface GmailService {
  void addLabel(String messageIds, String labelId) throws GmailServiceException;
  void batchGetMetadata(List<String> messageIds, JsonBatchCallback<Message> callback) throws GmailServiceException;
  Label createLabel(Label labelIn) throws GmailServiceException;
  void deleteMessage(String messageId, boolean permanentlyDelete) throws GmailServiceException;
  String getEmailAddress() throws GmailServiceException;
  SortedMap<String, String> getIdToLabel() throws GmailServiceException;
  Message getUniqueIdAndHeaders(Message message) throws GmailServiceException;
  Message getRawMessage(String messageId) throws GmailServiceException;
  Message insertMessage(Message message) throws GmailServiceException;
  List<Message> search(String query) throws GmailServiceException;

  static SortedMap<String, String> labelsResponseToMap(ListLabelsResponse response) {
    SortedMap<String, String> labelToId = new TreeMap<>();
    for (Label label : response.getLabels()) {
      labelToId.put(label.getId(), label.getName());
    }
    return labelToId;
  }
}
