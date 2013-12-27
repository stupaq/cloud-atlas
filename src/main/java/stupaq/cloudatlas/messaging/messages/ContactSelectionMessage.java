package stupaq.cloudatlas.messaging.messages;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.services.busybody.strategies.ContactSelection;

@Immutable
public class ContactSelectionMessage extends Message {
  private final ContactSelection strategy;

  public ContactSelectionMessage(ContactSelection strategy) {
    this.strategy = strategy;
  }

  public ContactSelection getStrategy() {
    return strategy;
  }
}
