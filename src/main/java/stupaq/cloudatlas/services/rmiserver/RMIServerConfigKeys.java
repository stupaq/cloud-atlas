package stupaq.cloudatlas.services.rmiserver;

public interface RMIServerConfigKeys {
  // Runtime configuration section
  static final String PREFIX = "rmi.";
  // Runtime configuration entries
  static final String HANDLE = PREFIX + "handle";
  static final String HOST = PREFIX + "host";
  static final String HOST_DEFAULT = "127.0.0.1";
  static final String PORT = PREFIX + "port";
  static final int PORT_DEFAULT = 1099;
}
