package stupaq.cloudatlas.messaging.messages;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.annotation.concurrent.Immutable;

import stupaq.cloudatlas.messaging.Response;
import stupaq.cloudatlas.naming.LocalName;
import stupaq.cloudatlas.services.zonemanager.hierarchy.ZoneHierarchy;
import stupaq.compact.CompactSerializable;
import stupaq.compact.CompactSerializer;
import stupaq.compact.TypeDescriptor;

@Immutable
public class KnownZonesResponse extends Response<KnownZonesRequest> implements CompactSerializable {
  public static final CompactSerializer<KnownZonesResponse> SERIALIZER =
      new CompactSerializer<KnownZonesResponse>() {
        @Override
        public KnownZonesResponse readInstance(ObjectInput in) throws IOException {
          return new KnownZonesResponse(
              ZoneHierarchy.Serializer(LocalName.SERIALIZER).readInstance(in));
        }

        @Override
        public void writeInstance(ObjectOutput out, KnownZonesResponse object) throws IOException {
          ZoneHierarchy.Serializer(LocalName.SERIALIZER).writeInstance(out, object.zones);
        }
      };
  private final ZoneHierarchy<LocalName> zones;

  public KnownZonesResponse(ZoneHierarchy<LocalName> zones) {
    this.zones = zones;
  }

  @Override
  public TypeDescriptor descriptor() {
    return TypeDescriptor.ZoneReportResponse;
  }
}
