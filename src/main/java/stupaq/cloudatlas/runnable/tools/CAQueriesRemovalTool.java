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
  private final List<HostAndPort> repositories = new ArrayList<>();

  public CAQueriesRemovalTool(String[] args) {
    for (String address : args) {
      repositories.add(HostAndPort.fromString(address).withDefaultPort(RMI_PORT_DEFAULT));
    }
  }

  @Override
  protected void run() throws Exception {
    for (HostAndPort address : repositories) {
      try {
        Registry registry = LocateRegistry.getRegistry(address.getHostText(), address.getPort());
        for (String name : registry.list()) {
          try {
            LocalClientProtocol client = (LocalClientProtocol) registry.lookup(name);
            client
                .removeQuery(Optional.<AttributeName>absent(), Optional.<List<GlobalName>>absent());
          } catch (Throwable e) {
            LOG.warn("Failed for registry: " + registry + " with entry: " + name, e);
          }
        }
      } catch (Throwable e) {
        LOG.warn("Failed for repository: " + address, e);
      }
    }
  }
}
