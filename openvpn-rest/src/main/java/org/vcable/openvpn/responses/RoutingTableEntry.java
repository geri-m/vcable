package org.vcable.openvpn.responses;

import java.net.InetSocketAddress;
import java.util.Date;

public class RoutingTableEntry {

  private final String virtualAddress;
  private final String commonName;
  private final InetSocketAddress realAddress;
  private final Date lastRef;

  /**
   * PoJo for each Routing Table Entry in the {@link Status}
   *
   * @param virtualAddress Virutal Address (MAC or IP!)
   * @param commonName     CN of the Certificate
   * @param realAddress    Real IP Address with Port
   * @param lastRef        Last Timestamp of Contact
   */

  public RoutingTableEntry(final String virtualAddress, final String commonName, final InetSocketAddress realAddress, final Date lastRef) {
    this.virtualAddress = virtualAddress;
    this.commonName = commonName;
    this.realAddress = realAddress;
    this.lastRef = new Date(lastRef.getTime());
  }

  public String getVirtualAddress() {
    return virtualAddress;
  }

  public String getCommonName() {
    return commonName;
  }

  public InetSocketAddress getRealAddress() {
    return realAddress;
  }

  public Date getLastRef() {
    return lastRef;
  }

  @Override
  public String toString() {
    return "RoutingTableEntry{" + "virtualAddress='" + virtualAddress + '\'' + ", commonName='" + commonName + '\'' + ", realAddress=" + realAddress + ", lastRef=" + lastRef + '}';
  }
}
