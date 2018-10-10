package org.vcable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.vcable.openvpn.Transceiver;
import org.vcable.openvpn.responses.ResponseParseException;
import org.vcable.openvpn.responses.Version;

public class VersionTest {

  @Test
  public void versionOkayTest() throws ResponseParseException {
    final Transceiver t = new MockTransceiver(null,
        "OpenVPN Version: OpenVPN 2.4.6 x86_64-alpine-linux-musl [SSL (OpenSSL)] [LZO] [LZ4] [EPOLL] [MH/PKTINFO] [AEAD] built on " + "Jul" + "  8 2018\nManagement " + "Version:" +
            " " + "1\nEND");
    final Version version = Version.getInstance(t);
    assertEquals(1, version.getVersionOfInterface());
    assertEquals("OpenVPN 2.4.6 x86_64-alpine-linux-musl [SSL (OpenSSL)] [LZO] [LZ4] [EPOLL] [MH/PKTINFO] [AEAD] built on Jul  8 2018", version.getOpenVpnVersion());
  }

  @Test
  public void versionNotOkayTest() {
    final Transceiver t = new MockTransceiver(null,
        "OpenVPN Version: OpenVPN 2.4.6 x86_64-alpine-linux-musl [SSL (OpenSSL)] [LZO] [LZ4] [EPOLL] [MH/PKTINFO] [AEAD] built on " + "Jul" + "  8 2018\nManagement " + "Version:" +
            " " + "x\nEND");
    try {
      Version.getInstance(t);
      fail();
    } catch (final ResponseParseException e) {
      assertEquals("java.lang.NumberFormatException: For input string: \"\"", e.getMessage());
    }
  }

  @Test
  public void incorrectTest() {
    final Transceiver t = new MockTransceiver(null, "NO DATA");
    try {
      Version.getInstance(t);
      fail();
    } catch (final ResponseParseException e) {
      assertEquals("Unable to parse Version Object", e.getMessage());
    }
  }
}
