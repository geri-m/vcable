package org.vcable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vcable.openvpn.OpenVpnCommandEnum;
import org.vcable.openvpn.Transceiver;
import org.vcable.openvpn.responses.ResponseParseException;
import org.vcable.openvpn.responses.Version;
import junit.framework.TestCase;

public class VersionTest extends TestCase {

  private static final Logger LOGGER = LoggerFactory.getLogger(VersionTest.class);

  public void test01_VersionOkayTest() {

    final Transceiver t = new Transceiver() {
      @Override
      public String transceiverMultiLine(final OpenVpnCommandEnum command) {
        return "OpenVPN Version: OpenVPN 2.4.6 x86_64-alpine-linux-musl [SSL (OpenSSL)] [LZO] [LZ4] [EPOLL] [MH/PKTINFO] [AEAD] built on Jul  8 2018\nManagement " + "Version: " + "1\nEND";
      }

      @Override
      public String transceiverSingleLine(final OpenVpnCommandEnum command) {
        return null;
      }
    };

    try {
      final Version version = Version.getInstance(t);
      assertEquals(1, version.getVersionOfInterface());
      assertEquals("OpenVPN 2.4.6 x86_64-alpine-linux-musl [SSL (OpenSSL)] [LZO] [LZ4] [EPOLL] [MH/PKTINFO] [AEAD] built on Jul  8 2018", version.getOpenVpnVersion());
    } catch (final ResponseParseException e) {
      LOGGER.error("Error: {}", e.toString());
      fail();
    }
  }

  public void test02_VersionNotOkayTest() {
    final Transceiver t = new Transceiver() {
      @Override
      public String transceiverMultiLine(final OpenVpnCommandEnum command) {
        return "OpenVPN Version: OpenVPN 2.4.6 x86_64-alpine-linux-musl [SSL (OpenSSL)] [LZO] [LZ4] [EPOLL] [MH/PKTINFO] [AEAD] built on Jul  8 2018\nManagement " + "Version: " + "x\nEND";
      }

      @Override
      public String transceiverSingleLine(final OpenVpnCommandEnum command) {
        return null;
      }
    };

    try {
      Version.getInstance(t);
      fail();
    } catch (final ResponseParseException e) {
      assertEquals("java.lang.NumberFormatException: For input string: \"\"", e.getMessage());
    }
  }

  public void test03_VersionIncorrectTest() {
    final Transceiver t = new Transceiver() {
      @Override
      public String transceiverMultiLine(final OpenVpnCommandEnum command) {
        return "NO DATA";
      }

      @Override
      public String transceiverSingleLine(final OpenVpnCommandEnum command) {
        return null;
      }
    };

    try {
      Version.getInstance(t);
      fail();
    } catch (final ResponseParseException e) {
      assertEquals("Unable to parse Version Object", e.getMessage());
    }
  }
}
