package stupaq.cloudatlas.messaging.messages;

import com.google.common.base.Optional;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import stupaq.cloudatlas.attribute.values.CAContact;
import stupaq.cloudatlas.attribute.values.CATime;
import stupaq.cloudatlas.naming.GlobalName;

public class ZonesInterestMessage extends Message implements Iterable<Entry<GlobalName, CATime>> {
  private final GlobalName leaf;
  private final Map<GlobalName, CATime> timestamps;
  private final CAContact contact;

  public ZonesInterestMessage(CAContact contact, GlobalName leaf,
      Map<GlobalName, CATime> timestamps) {
    this.leaf = leaf;
    this.timestamps = timestamps;
    this.contact = contact;
  }

  @Override
  public Iterator<Entry<GlobalName, CATime>> iterator() {
    return timestamps.entrySet().iterator();
  }

  public CATime getTimestamp(GlobalName name) {
    return Optional.of(timestamps.get(name)).or(new CATime());
  }

  public GlobalName getLeaf() {
    return leaf;
  }

  public CAContact getContact() {
    return contact;
  }
}
