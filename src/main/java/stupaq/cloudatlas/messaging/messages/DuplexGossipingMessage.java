package stupaq.cloudatlas.messaging.messages;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.services.busybody.sessions.SessionId;

@Immutable
public class DuplexGossipingMessage extends Message {
  private final CAContact contact;
  private final SessionId sessionId;

  public DuplexGossipingMessage(CAContact contact, SessionId sessionId) {
    this.contact = contact;
    this.sessionId = sessionId;
  }

  public CAContact getContact() {
    return contact;
  }

  public SessionId getSessionId() {
    return sessionId;
  }
}
