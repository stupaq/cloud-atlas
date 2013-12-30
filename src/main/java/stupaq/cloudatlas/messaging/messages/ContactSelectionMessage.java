package stupaq.cloudatlas.messaging.messages;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.services.busybody.sessions.SessionId;
import stupaq.cloudatlas.services.busybody.strategies.ContactSelection;

@Immutable
public class ContactSelectionMessage extends Message {
  private final ContactSelection strategy;
  private final SessionId sessionId;

  public ContactSelectionMessage(ContactSelection strategy, SessionId sessionId) {
    this.strategy = strategy;
    this.sessionId = sessionId;
  }

  public ContactSelection getStrategy() {
    return strategy;
  }

  public SessionId getSessionId() {
    return sessionId;
  }
}
