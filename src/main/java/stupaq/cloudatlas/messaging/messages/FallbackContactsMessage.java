package stupaq.cloudatlas.messaging.messages;

import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.messaging.Message;

@Immutable
public class FallbackContactsMessage extends Message implements Iterable<CAContact> {
  private final List<CAContact> contacts;

  public FallbackContactsMessage(List<CAContact> contacts) {
    this.contacts = contacts;
  }

  @Override
  public Iterator<CAContact> iterator() {
    return contacts.iterator();
  }
}
