package org.vcable.openvpn;

import java.io.IOException;

public interface Transceiver {

  /**
   * Sending a command to the management interface and receiving multiple lines back
   *
   * @param command String to send
   * @return Response from the Management Interface
   * @throws IOException Exception thrown if the communication failed
   */

  String transceiverMultiLine(final OpenVpnCommandEnum command) throws IOException;

  /**
   * Sending a command to the management interface and receiving a single line back
   *
   * @param command String to send
   * @return Response from the Management Interface
   * @throws IOException Exception thrown if the communication failed
   */

  String transceiverSingleLine(final OpenVpnCommandEnum command) throws IOException;

}
