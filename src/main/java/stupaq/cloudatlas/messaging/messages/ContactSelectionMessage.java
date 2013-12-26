package stupaq.cloudatlas.messaging.messages;

import stupaq.cloudatlas.messaging.Message;
import stupaq.cloudatlas.services.busybody.strategies.ContactSelection;

public class ContactSelectionMessage extends Message {
  private final ContactSelection strategy;

  public ContactSelectionMessage(ContactSelection strategy) {
    this.strategy = strategy;
  }

  public ContactSelection getStrategy() {
    return strategy;
  }
}
