package stupaq.commons.net;

import com.google.common.net.HostAndPort;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import stupaq.commons.base.ForwardingWrapper;

@Immutable
public class HostWithPort extends ForwardingWrapper<HostAndPort>
    implements Comparable<HostWithPort> {

  public HostWithPort(@Nonnull HostAndPort value) {
    super(value);
  }

  public HostWithPort(@Nonnull HostWithPort value) {
    super(value.get());
  }

  @Override
  public int compareTo(@Nonnull HostWithPort other) {
    return toString().compareTo(other.toString());
  }

  public String getHost() {
    return get().getHostText();
  }

  public int getPort() {
    return get().getPort();
  }
}
