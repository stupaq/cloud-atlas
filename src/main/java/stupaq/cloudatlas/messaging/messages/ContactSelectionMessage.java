package stupaq.cloudatlas.messaging.messages;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.services.busybody.strategies.ContactSelection;

@Immutable
public class ContactSelectionMessage extends Message {
  private final ContactSelection strategy;
  private final CAContact self;

  public ContactSelectionMessage(ContactSelection strategy, CAContact self) {
    this.strategy = strategy;
    this.self = self;
  }

  public ContactSelection getStrategy() {
    return strategy;
  }

  public CAContact getSelf() {
    return self;
  }
}
