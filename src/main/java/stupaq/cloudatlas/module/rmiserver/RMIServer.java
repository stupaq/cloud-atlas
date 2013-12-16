package stupaq.cloudatlas.module.rmiserver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import stupaq.cloudatlas.module.Module;
import stupaq.cloudatlas.module.rmiserver.handler.LocalClientRMIHandler;

public class RMIServer implements Module {
  private static final Log LOG = LogFactory.getLog(RMIServer.class);

  static {
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new SecurityManager());
    }
  }

  private final Registry registry;
  private List<Remote> stubs = new ArrayList<>();

  public RMIServer() throws RemoteException {
    this.registry = LocateRegistry.getRegistry();
    bind(new LocalClientRMIHandler());
  }

  private void bind(Remote handler) throws RemoteException {
    try {
      Remote stub = UnicastRemoteObject.exportObject(handler, 0);
      registry.rebind(exportedName(handler.getClass()), stub);
      stubs.add(stub);
    } catch (Exception e) {
      LOG.fatal("Encountered exception during setup", e);
      throw e;
    }
  }

  @Override
  public void close() {
    for (Remote stub : stubs) {
      try {
        UnicastRemoteObject.unexportObject(stub, true);
      } catch (RemoteException e) {
        // Ignored
      }
    }
  }

  public static String exportedName(Class<? extends Remote> clazz) {
    return clazz.getSimpleName().replace("Handler", "Protocol");
  }
}
