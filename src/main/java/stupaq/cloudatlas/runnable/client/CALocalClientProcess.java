package stupaq.cloudatlas.runnable.client;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.ServiceManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import stupaq.cloudatlas.configuration.BootstrapConfiguration;
import stupaq.cloudatlas.configuration.BootstrapConfiguration.Builder;
import stupaq.cloudatlas.configuration.ServicesList;
import stupaq.cloudatlas.services.collector.AttributesCollector;
import stupaq.cloudatlas.services.installer.QueriesInstaller;
import stupaq.cloudatlas.services.rmiserver.RMIServerConfigKeys;
import stupaq.cloudatlas.services.scribe.AttributesScribe;
import stupaq.cloudatlas.threading.SingleThreadModel;

import static stupaq.cloudatlas.configuration.ConfigurationDiscovery.forLocalClient;

@SuppressWarnings("unused")
public class CALocalClientProcess extends AbstractIdleService implements RMIServerConfigKeys {
  private static final Log LOG = LogFactory.getLog(CALocalClientProcess.class);
  private ServiceManager manager;

  public CALocalClientProcess(String[] args) {
  }

  @Override
  protected void startUp()
      throws NotBoundException, RemoteException, InvocationTargetException, NoSuchMethodException,
             InstantiationException, IllegalAccessException {
    // Configuration for client
    BootstrapConfiguration config =
        new Builder().configuration(forLocalClient()).threadModel(new SingleThreadModel()).create();
    // Create and start all services
    ServicesList services = new ServicesList();
    services.addWith(config, AttributesCollector.class);
    services.addWith(config, AttributesScribe.class);
    services.addWith(config, QueriesInstaller.class);
    manager = new ServiceManager(services);
    manager.startAsync().awaitHealthy();
  }

  @Override
  protected void shutDown() throws Exception {
    manager.stopAsync().awaitStopped();
  }
}
