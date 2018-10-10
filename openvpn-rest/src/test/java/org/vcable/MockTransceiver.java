package org.vcable;

import org.vcable.openvpn.OpenVpnCommandEnum;
import org.vcable.openvpn.Transceiver;

public class MockTransceiver implements Transceiver {

  final String singleLine;
  final String multiLine;

  public MockTransceiver(final String singleLine, final String multiLine) {
    this.singleLine = singleLine;
    this.multiLine = multiLine;
  }


  @Override
  public String transceiverMultiLine(final OpenVpnCommandEnum command) {
    return multiLine;
  }

  @Override
  public String transceiverSingleLine(final OpenVpnCommandEnum command) {
    return singleLine;
  }
}
