package stupaq.cloudatlas.attribute.values;

import com.google.common.net.HostAndPort;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.attribute.AttributeValue;
import stupaq.cloudatlas.query.errors.UndefinedOperationException;
import stupaq.cloudatlas.query.semantics.ConvertibleValue;
import stupaq.cloudatlas.query.semantics.ConvertibleValue.ConvertibleValueDefault;
import stupaq.cloudatlas.query.semantics.OperableValue;
import stupaq.cloudatlas.query.semantics.OperableValue.OperableValueDefault;
import stupaq.cloudatlas.query.semantics.RelationalValue;
import stupaq.cloudatlas.query.semantics.RelationalValue.RelationalValueDefault;
import stupaq.compact.CompactInput;
import stupaq.compact.CompactOutput;
import stupaq.compact.CompactSerializer;
import stupaq.compact.TypeDescriptor;

import static stupaq.compact.CompactSerializers.String;

@Immutable
public final class CAContact extends AbstractAtomic<InetSocketAddress> {
  public static final CompactSerializer<CAContact> SERIALIZER = new CompactSerializer<CAContact>() {
    @Override
    public CAContact readInstance(CompactInput in) throws IOException {
      return in.readBoolean() ? new CAContact(
          HostAndPort.fromParts(String.readInstance(in), in.readInt())) : new CAContact();
    }

    @Override
    public void writeInstance(CompactOutput out, CAContact object) throws IOException {
      out.writeBoolean(!object.isNull());
      if (!object.isNull()) {
        InetSocketAddress address = object.get();
        String.writeInstance(out, address.getHostString());
        out.writeInt(address.getPort());
      }
    }
  };
  private static final int DEFAULT_PORT = 65535;

  public CAContact() {
    super(null);
  }

  public CAContact(HostAndPort value) {
    this(new InetSocketAddress(value.getHostText(), value.getPortOrDefault(DEFAULT_PORT)));
  }

  public CAContact(String value) {
    this(HostAndPort.fromString(value).withDefaultPort(DEFAULT_PORT));
  }

  public CAContact(InetSocketAddress address) {
    super(address.isUnresolved() ? new InetSocketAddress(address.getHostString(), address.getPort())
        : address);
  }

  public InetSocketAddress address() {
    return get();
  }

  @Override
  public int compareTo(AttributeValue other) {
    throw new UndefinedOperationException("Cannot compare: " + CAContact.class.getSimpleName());
  }

  @Override
  public ConvertibleValue to() {
    return new ConvertibleImplementation();
  }

  @Override
  public OperableValue op() {
    return new OperableValueDefault();
  }

  @Override
  public RelationalValue rel() {
    return new RelationalValueDefault();
  }

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.CAContact;
  }

  private class ConvertibleImplementation extends ConvertibleValueDefault {
    @Override
    public CAContact Contact() {
      return CAContact.this;
    }

    @Override
    public CAString String() {
      return new CAString(isNull() ? null : get().getHostString() + ":" + get().getPort());
    }
  }
}
