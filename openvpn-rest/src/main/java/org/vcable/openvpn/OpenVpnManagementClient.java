package org.vcable.openvpn;

import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.commons.net.SocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenVpnManagementClient extends SocketClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(OpenVpnManagementClient.class);
  private static final int OPEN_VPN_READ_TIMEOUT_IN_MS = 2000;

  private static OpenVpnManagementClient instance;
  private final InetSocketAddress managementAddress;

  /**
   * Private Constructor. If Connection is not possbile this throws an IOException. Not nice.
   *
   * @param managementAddress Address to connect to
   * @throws IOException Exception is thrown is the connection was not established.
   */

  private OpenVpnManagementClient(final InetSocketAddress managementAddress) throws IOException {
    super();
    this.managementAddress = managementAddress;
    setDefaultPort(managementAddress.getPort());
    setDefaultTimeout(OPEN_VPN_READ_TIMEOUT_IN_MS);

    // establish connection to host input and output stream are no available;
    connect(managementAddress.getHostName());

    // in case there is input for 2 seconds, come back InputStream. only available _AFTER_ connect.
    setSoTimeout(OPEN_VPN_READ_TIMEOUT_IN_MS);
  }


  /**
   * GetInstance to get the Singleton of the Management Connection, as there can only be one Connection to the OpenVPN Management Connection
   *
   * @param managementAddress Address containing host and port. Will usually be localhost 7505
   */

  public static synchronized OpenVpnManagementClient getInstance(final InetSocketAddress managementAddress) throws IOException {

    if (instance == null) {
      instance = new OpenVpnManagementClient(managementAddress);
    }
    return instance;
  }


}
