package stupaq.cloudatlas.messaging.messages;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.messaging.Message;
import stupaq.compact.CompactSerializer;
import stupaq.compact.CompactSerializers;
import stupaq.compact.TypeDescriptor;

@Immutable
public class FallbackContactsRequest extends Message implements Iterable<CAContact> {
  public static final CompactSerializer<FallbackContactsRequest> SERIALIZER =
      new CompactSerializer<FallbackContactsRequest>() {
        @Override
        public FallbackContactsRequest readInstance(ObjectInput in) throws IOException {
          return new FallbackContactsRequest(CompactSerializers.List(CAContact.SERIALIZER)
              .readInstance(in));
        }

        @Override
        public void writeInstance(ObjectOutput out, FallbackContactsRequest object)
            throws IOException {
          CompactSerializers.List(CAContact.SERIALIZER).writeInstance(out, object.contacts);
        }
      };
  private final List<CAContact> contacts;

  public FallbackContactsRequest(List<CAContact> contacts) {
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
