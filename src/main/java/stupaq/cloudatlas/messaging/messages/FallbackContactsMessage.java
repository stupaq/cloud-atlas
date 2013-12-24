package stupaq.cloudatlas.messaging.messages;

import com.google.common.base.Preconditions;

import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.messaging.Message;

@Immutable
public class FallbackContactsMessage implements Iterable<CAContact>, Message {
  private final List<CAContact> contacts;

  public FallbackContactsMessage(List<CAContact> contacts) {
    Preconditions.checkNotNull(contacts);
    this.contacts = contacts;
  }

  @Override
  public Iterator<CAContact> iterator() {
    return contacts.iterator();
  }
}
