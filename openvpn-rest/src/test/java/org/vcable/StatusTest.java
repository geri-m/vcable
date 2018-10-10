package org.vcable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.text.SimpleDateFormat;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vcable.openvpn.Transceiver;
import org.vcable.openvpn.responses.ResponseParseException;
import org.vcable.openvpn.responses.Status;

public class StatusTest {

  private static final String TEST_01 = "OpenVPN CLIENT LIST\nUpdated,Sun Apr 21 17:52:33 2013\nCommon Name,Real Address,Bytes Received,Bytes Sent,Connected Since\nROUTING " +
      "TABLE\nVirtual Address,Common Name,Real Address,Last Ref\nGLOBAL STATS\nMax bcast/mcast queue length,0\nEND\n";
  private static final String TEST_02 = "OpenVPN CLIENT LIST\nUpdated,Sun Apr 21 18:07:23 2013\nCommon Name,Real Address,Bytes Received,Bytes Sent,Connected Since\nUNDEF,84" +
      ".112.155.68:46633,1404,5889,Sun Apr 21 18:07:19 2013\nROUTING TABLE\nVirtual Address,Common Name,Real Address,Last Ref\nGLOBAL STATS\nMax bcast/mcast queue " + "length," + "0\nEND\n";
  private static final String TEST_03 = "OpenVPN CLIENT LIST\nUpdated,Sun Apr 21 18:08:21 2013\nCommon Name,Real Address,Bytes Received,Bytes Sent,Connected " + "Since" +
      "\nvcable0003.vcable.org,84.112.155.68:43241,7452,9556,Sun Apr 21 18:07:44 2013\nROUTING TABLE\nVirtual Address,Common Name,Real Address,Last Ref\nGLOBAL " + "STATS\nMax " + "bcast/mcast queue length,0\nEND\n";
  private static final String TEST_04 = "OpenVPN CLIENT LIST\nUpdated,Sun Apr 21 18:12:08 2013\nCommon Name,Real Address,Bytes Received,Bytes Sent,Connected " + "Since" +
      "\nvcable0003.vcable.org,84.112.155.68:34951,7452,9556,Sun Apr 21 18:11:51 2013\nROUTING TABLE\nVirtual Address,Common Name,Real Address,Last Ref\n10.8.117" + ".6," +
      "vpnCh8TestClient,192.168.0.104:1194,Tue May 17 23:27:22 2011\nGLOBAL STATS\nMax bcast/mcast queue length,0\nEND\n";
  private static final String TEST_05 = "OpenVPN CLIENT LIST\nUpdated,Thu Mar  3 17:24:54 2005\nCommon Name,Real Address,Bytes Received,Bytes Sent,Connected Since\nother" +
      ".common.name,1.2.3.4:21370,86559,87369,Thu Mar  3 13:39:10 2005\nROUTING TABLE\nVirtual Address,Common Name,Real Address,Last Ref\n192.168.57.14,other.common.name,7" +
      ".8.9.1:54836,Thu Mar  3 17:23:10 2005\n192.168.57.10,common.name,1.2.3.4:21370,Thu Mar  3 13:39:11 2005\nGLOBAL STATS\nMax bcast/mcast queue length,0\nEND\n";
  private static final String TEST_06_FAIL_IP =
      "OpenVPN CLIENT LIST\nUpdated,Thu Mar  3 17:24:54 2005\nCommon Name,Real Address,Bytes Received,Bytes Sent,Connected " + "Since" + "\nother.common.name,444.442.443" +
          ".444:21370,86559,87369,Thu Mar  3 13:39:10 2005\nROUTING TABLE\nVirtual Address,Common Name,Real Address,Last Ref\n192.168.57" + ".14,other" + ".common.name,7.8.9" +
          ".1:54836,Thu Mar  3 17:23:10 2005\n192.168.57.10,common.name,1.2.3.4:21370,Thu Mar  3 13:39:11 2005\nGLOBAL STATS\nMax bcast/mcast queue " + "length," + "0\nEND\n";
  private static final String TEST_07 = "OpenVPN CLIENT LIST\nUpdated,Thu May  2 22:20:34 2013\nCommon Name,Real Address,Bytes Received,Bytes Sent,Connected " + "Since" +
      "\nvcable0002.vcable.org,89.144.192.56:7816,786658,435503,Thu May  2 22:06:42 2013\nvcable0003.vcable.org,89.144.192.60:57926,432139,789780,Thu May  2 22:06:44 " + "2013" + "\nROUTING TABLE\nVirtual Address,Common Name,Real Address,Last Ref\n00:a0:de:71:64:f6,vcable0002.vcable.org,89.144.192.56:7816,Thu May 2 22:06:52 " + "2013\nb8:27:eb:88" + ":09:7b,vcable0003.vcable.org,89.144.192.60:57926,Thu May  2 22:08:53 2013\n00:21:6a:17:7d:aa,vcable0002.vcable.org,89.144.192.56:7816,Thu May 2 " + "22:06:54 " + "2013\n7c:c3:a1:89:28:e5,vcable0003.vcable.org,89.144.192.60:57926,Thu May  2 22:20:34 2013\n00:00:24:cc:45:c0,vcable0002.vcable.org,89.144.192.56:7816,Thu " + "May 2 " + "22:20:33 2013\nce:69:b2:c2:e0:e8,vcable0002.vcable.org,89.144.192.56:7816,Thu May 2 22:06:52 2013\nb8:27:eb:a8:46:79,vcable0002.vcable.org,89.144.192.56:7816," + "Thu May" + " 2 22:08:54 2013\n00:24:01:2e:45:43,vcable0002.vcable.org,89.144.192.56:7816,Thu May 2 22:09:32 2013\n00:06:dc:80:80:66,vcable0002.vcable.org,89.144.192" + ".56:7816,Thu " + "May 2 22:06:59 2013\nGLOBAL STATS\nMax bcast/mcast queue length,4\nEND\n";
  private static final String TEST_08 = "OpenVPN CLIENT LIST\nUpdated,Sat Jun 01 14:16:44 2013\nCommon Name,Real Address,Bytes Received,Bytes Sent,Connected " + "Since" +
      "\nvcable0001.vcable.org,127.0.0.1:51549,1000,5000,Sat Jun 01 14:16:44 2013\nROUTING TABLE\nVirtual Address,Common Name,Real Address,Last Ref\n1.2.3.4," + "vcable0001" +
      ".vcable.org,127.0.0.1:51548,Sat Jun 01 14:16:44 2013\nGLOBAL STATS\nMax bcast/mcast queue length,0\nEND\n";
  private static final String TEST_09 = "OpenVPN CLIENT LIST\nUpdated,Sat Jul 06 21:56:45 2013\nCommon Name,Real Address,Bytes Received,Bytes Sent,Connected Since\nROUTING " +
      "TABLE\nVirtual Address,Common Name,Real Address,Last Ref\nGLOBAL STATS\nMax bcast/mcast queue length,0\nEND\n";

  private static final Logger LOGGER = LoggerFactory.getLogger(StatusTest.class);
  private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Test
  public void statusEmptyListOkayTest() throws ResponseParseException {
    final Transceiver t = new MockTransceiver(null, TEST_01);
    final Status s = Status.getInstance(t);
    assertEquals(0, s.getClientsConnectedList()
        .size());
    assertEquals(0, s.getRoutingTableEntryList()
        .size());
    assertEquals("2013-04-21 17:52:33", SDF.format(s.getUpdated()));

  }

  @Test
  public void statusEmptyListOkayTest02() throws ResponseParseException {
    final Transceiver t = new MockTransceiver(null, TEST_02);


    final Status s = Status.getInstance(t);
    assertEquals(1, s.getClientsConnectedList()
        .size());
    // Get a single Client Connected Element
    assertEquals("UNDEF", s.getClientsConnectedList()
        .get(0)
        .getName());
    assertEquals("84.112.155.68:46633", s.getClientsConnectedList()
        .get(0)
        .getAddress()
        .toString()
        .replace("/", ""));
    assertEquals(1404, s.getClientsConnectedList()
        .get(0)
        .getReceived());
    assertEquals(5889, s.getClientsConnectedList()
        .get(0)
        .getSent());
    assertEquals("2013-04-21 18:07:19", SDF.format(s.getClientsConnectedList()
        .get(0)
        .getLastRef()));

    assertEquals(0, s.getRoutingTableEntryList()
        .size());
    assertEquals("2013-04-21 18:07:23", SDF.format(s.getUpdated()));
    LOGGER.info(TEST_02);
    LOGGER.info(s.toString());
  }

  @Test
  public void statusEmptyListOkayTest03() throws ResponseParseException {
    final Transceiver t = new MockTransceiver(null, TEST_03);
    Status.getInstance(t);
  }

  @Test
  public void statusEmptyListOkayTest04() throws ResponseParseException {
    final Transceiver t = new MockTransceiver(null, TEST_04);
    Status.getInstance(t);
  }

  @Test
  public void statusEmptyListOkayTest05() throws ResponseParseException {
    final Transceiver t = new MockTransceiver(null, TEST_05);
    Status.getInstance(t);
  }

  @Test
  public void statusEmptyListFailTest06() {
    final Transceiver t = new MockTransceiver(null, TEST_06_FAIL_IP);
    try {
      // IP Address is incorrect. Throw Exception.
      Status.getInstance(t);
      fail();
    } catch (final ResponseParseException e) {
      // Fix: OpenJDK and Oracle JDK have different Error Messages for the UnknownHostException
      assertTrue(e.getMessage()
          .startsWith("Unable to parse '444.442.443.444' as host/IP. Exception: java.net.UnknownHostException: 444.442.443.444:"));
    }
  }

  @Test
  public void statusEmptyListFailTest07() throws ResponseParseException {
    final Transceiver t = new MockTransceiver(null, TEST_07);
    Status.getInstance(t);
  }

  @Test
  public void statusEmptyListFailTest08() throws ResponseParseException {
    final Transceiver t = new MockTransceiver(null, TEST_08);
    Status.getInstance(t);
  }

  @Test
  public void statusEmptyListFailTest09() throws ResponseParseException {
    final Transceiver t = new MockTransceiver(null, TEST_09);
    Status.getInstance(t);
  }
}
