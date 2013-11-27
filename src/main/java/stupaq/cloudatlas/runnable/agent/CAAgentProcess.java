package stupaq.cloudatlas.runnable.agent;

import com.google.common.base.Preconditions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import stupaq.cloudatlas.module.Module;
import stupaq.cloudatlas.module.rmiserver.RMIServer;
import stupaq.cloudatlas.naming.GlobalName;
import stupaq.cloudatlas.serialization.CATypeRegistry;
import stupaq.guava.base.Exceptions;

public final class CAAgentProcess implements AutoCloseable {
  private static final Log LOG = LogFactory.getLog(CAAgentProcess.class);
  private final CountDownLatch closing = new CountDownLatch(1);
  private final List<Module> modules = new ArrayList<>();

  public CAAgentProcess(GlobalName zoneName) throws RemoteException {
    modules.add(new RMIServer());
  }

  @Override
  public void close() {
    // We close modules in reverse order
    Collections.reverse(modules);
    Exceptions.cleanup(LOG, modules);
    // We've finished closing
    closing.countDown();
  }

  public void awaitClose() throws InterruptedException {
    closing.await();
  }

  public static void main(String[] args) {
    CATypeRegistry.registerCATypes();
    try {
      // Arguments
      Preconditions.checkArgument(args.length >= 1, "Missing arguments: zoneName");
      GlobalName zoneName = GlobalName.parse(args[0]);
      // The process
      final CAAgentProcess process = new CAAgentProcess(zoneName);
      Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
        @Override
        public void run() {
          process.close();
        }
      }));
      // Wait
      process.awaitClose();
    } catch (Exception e) {
      LOG.fatal("Exception in main() thread", e);
      System.exit(-1);
    }
  }
}

