package org.vcable.openvpn.responses;

import java.net.InetSocketAddress;
import java.util.Date;

public class ClientConnected {

  private final String name;
  private final InetSocketAddress address;
  private final int received;
  private final int sent;
  private final Date lastRef;

  /**
   * Pojo for each Client Connected line in the Status Response
   *
   * @param name     Name of the Client
   * @param address  IP Address with Port of the Client
   * @param received Amount of Bytes Received
   * @param sent     Amount of Bytes Sent
   * @param lastRef  The of the last contact
   */

  public ClientConnected(final String name, final InetSocketAddress address, final int received, final int sent, final Date lastRef) {
    this.name = name;
    this.address = address;
    this.received = received;
    this.sent = sent;
    this.lastRef = new Date(lastRef.getTime());
  }

  public String getName() {
    return name;
  }

  public InetSocketAddress getAddress() {
    return address;
  }

  public int getReceived() {
    return received;
  }

  public int getSent() {
    return sent;
  }

  public Date getLastRef() {
    return new Date(lastRef.getTime());
  }

  @Override
  public String toString() {
    return "ClientConnected{" + "name='" + name + '\'' + ", address=" + address + ", received=" + received + ", sent=" + sent + ", lastRef=" + lastRef + '}';
  }
}
