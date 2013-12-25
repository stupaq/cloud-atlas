package stupaq.cloudatlas.messaging.messages;

import stupaq.cloudatlas.services.busybody.strategies.ContactSelection;

public class ContactSelectionMessage extends Request<Void> {
  private final ContactSelection strategy;

  public ContactSelectionMessage(ContactSelection strategy) {
    this.strategy = strategy;
  }
}
