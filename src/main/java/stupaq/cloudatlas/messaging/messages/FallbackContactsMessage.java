package stupaq.cloudatlas.messaging.messages;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.messaging.Message;
import stupaq.compact.CompactSerializable;
import stupaq.compact.CompactSerializer;
import stupaq.compact.CompactSerializers;
import stupaq.compact.TypeDescriptor;

@Immutable
public class FallbackContactsMessage extends Message
    implements CompactSerializable, Iterable<CAContact> {
  public static final CompactSerializer<FallbackContactsMessage> SERIALIZER =
      new CompactSerializer<FallbackContactsMessage>() {
        @Override
        public FallbackContactsMessage readInstance(ObjectInput in) throws IOException {
          return new FallbackContactsMessage(
              CompactSerializers.List(CAContact.SERIALIZER).readInstance(in));
        }

        @Override
        public void writeInstance(ObjectOutput out, FallbackContactsMessage object)
            throws IOException {
          CompactSerializers.List(CAContact.SERIALIZER).writeInstance(out, object.contacts);
        }
      };
  private final List<CAContact> contacts;

  public FallbackContactsMessage(List<CAContact> contacts) {
    this.contacts = contacts;
  }

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.FallbackContactsRequest;
  }

  @Override
  public Iterator<CAContact> iterator() {
    return contacts.iterator();
  }
}
