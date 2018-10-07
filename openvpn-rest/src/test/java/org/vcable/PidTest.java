package org.vcable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vcable.openvpn.OpenVpnCommandEnum;
import org.vcable.openvpn.Transceiver;
import org.vcable.openvpn.responses.Pid;
import org.vcable.openvpn.responses.ResponseParseException;
import junit.framework.TestCase;

public class PidTest extends TestCase {
  private static final Logger LOGGER = LoggerFactory.getLogger(PidTest.class);

  public void test01_PidOkayTest() {

    final Transceiver t = new Transceiver() {
      @Override
      public String transceiverMultiLine(final OpenVpnCommandEnum command) {
        return null;
      }

      @Override
      public String transceiverSingleLine(final OpenVpnCommandEnum command) {
        return "SUCCESS: pid=1";
      }
    };

    try {
      final Pid pid = Pid.getInstance(t);
      assertEquals(1, pid.getPid());
    } catch (final ResponseParseException e) {
      LOGGER.error("Error: {}", e.toString());
      fail();
    }
  }

  public void test02_PidNotOkayTest() {
    final Transceiver t = new Transceiver() {
      @Override
      public String transceiverMultiLine(final OpenVpnCommandEnum command) {
        return null;
      }

      @Override
      public String transceiverSingleLine(final OpenVpnCommandEnum command) {
        return "SUCCESS: pid=d";
      }
    };

    try {
      Pid.getInstance(t);
      fail();
    } catch (final ResponseParseException e) {
      assertEquals("Unable to parse Pid Object", e.getMessage());
    }
  }

  public void test03_PidIncorrectTest() {
    final Transceiver t = new Transceiver() {
      @Override
      public String transceiverMultiLine(final OpenVpnCommandEnum command) {
        return null;
      }

      @Override
      public String transceiverSingleLine(final OpenVpnCommandEnum command) {
        return "NO DATA";
      }
    };

    try {
      Pid.getInstance(t);
      fail();
    } catch (final ResponseParseException e) {
      assertEquals("Unable to parse Pid Object", e.getMessage());
    }
  }

}
