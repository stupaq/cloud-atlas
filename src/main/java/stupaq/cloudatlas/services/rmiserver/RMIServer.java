package stupaq.cloudatlas.services.rmiserver;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AbstractIdleService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.configuration.CAConfiguration;
import stupaq.cloudatlas.configuration.StartIfPresent;
import stupaq.cloudatlas.messaging.MessageBus;
import stupaq.cloudatlas.services.rmiserver.handler.LocalClientHandler;

@StartIfPresent(section = "rmi")
public class RMIServer extends AbstractIdleService implements RMIServerConfigKeys {
  private static final Log LOG = LogFactory.getLog(RMIServer.class);

  static {
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new SecurityManager());
    }
  }

  private final List<Remote> stubs = new ArrayList<>();
  private final MessageBus bus;
  private final String context;
  private Registry registry;

  public RMIServer(BootstrapConfiguration config) {
    this.bus = config.bus();
    this.context = contextHandle(config);
    Preconditions.checkNotNull(context);
  }

  @Override
  protected void startUp() throws RemoteException, AlreadyBoundException {
    registry = LocateRegistry.getRegistry();
    bind(new LocalClientHandler(bus));
  }

  @Override
  protected void shutDown() throws Exception {
    for (Remote stub : stubs) {
      unbindForce(stub);
    }
  }

  private void bind(Remote handler) throws RemoteException, AlreadyBoundException {
    Remote stub = UnicastRemoteObject.exportObject(handler, 0);
    registry.rebind(exportedName(handler.getClass(), context), stub);
    stubs.add(stub);
  }

  private void unbindForce(Remote handler) {
    try {
      registry.unbind(exportedName(handler.getClass(), context));
    } catch (NotBoundException | RemoteException ignored) {
      LOG.error("Failed to unbind stub " + handler);
    }
    try {
      UnicastRemoteObject.unexportObject(handler, true);
    } catch (NoSuchObjectException ignored) {
      LOG.error("Failed to unexport stub " + handler);
    }
  }

  public static String exportedName(Class<? extends Remote> clazz, String context) {
    return clazz.getSimpleName().replace("Handler", "Protocol") + "@" + context;
  }

  @SuppressWarnings("unchecked")
  public static <Protocol extends Remote> Protocol createClient(Class<Protocol> protocol,
      CAConfiguration config) throws RemoteException, NotBoundException {
    return (Protocol) LocateRegistry.getRegistry(config.getString(HOST, HOST_DEFAULT),
        config.getInt(PORT, PORT_DEFAULT)).lookup(exportedName(protocol, contextHandle(config)));
  }

  private static String contextHandle(CAConfiguration config) {
    return config.containsKey(HANDLE) ? config.getString(HANDLE) : config.findCharacteristicZone();
  }
}
