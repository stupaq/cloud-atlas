package stupaq.cloudatlas.runnable.tools;

import com.google.common.base.Optional;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.AbstractExecutionThreadService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import stupaq.cloudatlas.naming.AttributeName;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.services.rmiserver.RMIServerConfigKeys;
import stupaq.cloudatlas.services.rmiserver.protocol.LocalClientProtocol;

@SuppressWarnings("unused")
public class CAQueriesRemovalTool extends AbstractExecutionThreadService
    implements RMIServerConfigKeys {
  private static final Log LOG = LogFactory.getLog(CAQueriesRemovalTool.class);
  private static final int RMI_PORT_DEFAULT = 1099;
  private final List<HostAndPort> registries = new ArrayList<>();

  public CAQueriesRemovalTool(String[] args) {
    for (String address : args) {
      registries.add(HostAndPort.fromString(address).withDefaultPort(RMI_PORT_DEFAULT));
    }
  }

  @Override
  protected void run() {
    for (HostAndPort address : registries) {
      Registry registry;
      String[] names;
      try {
        registry = LocateRegistry.getRegistry(address.getHostText(), address.getPort());
        names = registry.list();
      } catch (Throwable e) {
        LOG.warn("Failed to reach registry: " + address, e);
        continue;
      }
      for (String name : names) {
        try {
          LocalClientProtocol client = (LocalClientProtocol) registry.lookup(name);
          client.removeQuery(Optional.<AttributeName>absent(), Optional.<List<GlobalName>>absent());
        } catch (Throwable e) {
          LOG.warn("Failed to lookup registry: " + registry + " with entry: " + name, e);
        }
      }
    }
  }
}
