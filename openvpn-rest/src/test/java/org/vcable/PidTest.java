package org.vcable;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.vcable.openvpn.Transceiver;
import org.vcable.openvpn.responses.Pid;
import org.vcable.openvpn.responses.ResponseParseException;


public class PidTest {

  @Test
  public void pidOkayTest() throws ResponseParseException {
    final Transceiver t = new MockTransceiver("SUCCESS: pid=1", null);
    final Pid pid = Pid.getInstance(t);
    assertEquals(1, pid.getPid());
  }

  @Test
  public void pidNotOkayTest() {
    final Transceiver t = new MockTransceiver("SUCCESS: pid=d", null);
    try {
      Pid.getInstance(t);
      fail();
    } catch (final ResponseParseException e) {
      assertEquals("Unable to parse Pid Object", e.getMessage());
    }
  }

  @Test
  public void pidIncorrectTest() {
    final Transceiver t = new MockTransceiver("NO DATA", null);
    try {
      Pid.getInstance(t);
      fail();
    } catch (final ResponseParseException e) {
      assertEquals("Unable to parse Pid Object", e.getMessage());
    }
  }
}
