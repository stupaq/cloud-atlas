package stupaq.cloudatlas.module.rmiserver;

import com.google.common.util.concurrent.AbstractIdleService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import stupaq.cloudatlas.module.rmiserver.handler.LocalClientRMIHandler;

public class RMIServer extends AbstractIdleService {
  private static final Log LOG = LogFactory.getLog(RMIServer.class);

  static {
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new SecurityManager());
    }
  }

  private final List<Remote> stubs = new ArrayList<>();
  private Registry registry;

  @Override
  protected void startUp() throws Exception {
    registry = LocateRegistry.getRegistry();
    bind(new LocalClientRMIHandler());
  }

  @Override
  protected void shutDown() throws Exception {
    for (Remote stub : stubs) {
      try {
        UnicastRemoteObject.unexportObject(stub, true);
      } catch (RemoteException e) {
        // Ignored
      }
    }
  }

  private void bind(Remote handler) throws RemoteException {
    Remote stub = UnicastRemoteObject.exportObject(handler, 0);
    registry.rebind(exportedName(handler.getClass()), stub);
    stubs.add(stub);
  }

  public static String exportedName(Class<? extends Remote> clazz) {
    return clazz.getSimpleName().replace("Handler", "Protocol");
  }
}
