package org.vcable.openvpn;

import java.io.IOException;
import java.net.InetSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenVpn implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(OpenVpn.class);
  private OpenVpnManagementClient client;
  private InetSocketAddress address;

  public OpenVpn(final String host, final int port) {
    address = new InetSocketAddress(host, port);
  }

  public static void main(final String args[]) {
    // creating a new Thread with the SocketClient
    new Thread(new OpenVpn("localhost", 7505)).start();
  }

  @Override
  public void run() {
    try {
      client = OpenVpnManagementClient.getInstance(address);
    } catch (final IOException e) {
      LOGGER.error("Failed to Start Client: {}", e.getMessage());
    }
  }
}
