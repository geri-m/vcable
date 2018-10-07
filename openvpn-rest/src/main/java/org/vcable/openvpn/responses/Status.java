package org.vcable.openvpn.responses;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vcable.openvpn.OpenVpnCommandEnum;
import org.vcable.openvpn.Transceiver;
import org.vcable.openvpn.Utils;

public class Status implements Response {


  /* Empty Status List.
   *
   * OpenVPN CLIENT LIST
   * Updated,Sun Oct  7 12:59:44 2018
   * Common Name,Real Address,Bytes Received,Bytes Sent,Connected Since
   * ROUTING TABLE
   * Virtual Address,Common Name,Real Address,Last Ref
   * GLOBAL STATS
   * Max bcast/mcast queue length,0
   * END
   */

  /* Status List with one Entry
   *
   * OpenVPN CLIENT LIST
   * Updated,Sun Oct  7 12:53:23 2018
   * Common Name,Real Address,Bytes Received,Bytes Sent,Connected Since
   * client_1,172.17.0.1:52717,3325,3147,Sun Oct  7 12:52:56 2018
   * ROUTING TABLE
   * Virtual Address,Common Name,Real Address,Last Ref
   * 192.168.255.6,client_1,172.17.0.1:52717,Sun Oct  7 12:52:56 2018
   * GLOBAL STATS
   * Max bcast/mcast queue length,0
   * END
   *
   */

  // Headline
  private static final String HEADLINE_RESPONSE = "OpenVPN CLIENT LIST\\s";
  private static final String HEADLINE_CLIENT = "Common Name,Real Address,Bytes Received,Bytes Sent,Connected Since\\s";
  private static final String HEADLINE_ROUTINGTABLE = "ROUTING TABLE\\s";
  private static final String HEADLINE_ROUTINGTABLE_ENTRYS = "Virtual Address,Common Name,Real Address,Last Ref\\s";
  private static final String HEADLINE_GLOBAL_STATS = "GLOBAL STATS\\s";
  private static final String HEADLINE_GLOBAL_STATS_ENTRYS = "Max bcast/mcast queue length,[0-9]\\s";

  // Sun Apr 21 17:52:33 2018
  private static final String DATE_PATTERN = "\\D\\D\\D \\D\\D\\D [ |0-3]?[0-9] [ |0-2][0-9]:[ |0-5][0-9]:[ |0-5][0-9] [0-9][0-9][0-9][0-9]";
  // 192.168.57.14
  private static final String IP_ADDRESS_PATTERN = "\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}";
  private static final String HEX_TUPEL = "[a-fA-F0-9]{2}";
  //00:a0:de:71:64:f6
  private static final String MAC_ADDRESS_PATTERN = HEX_TUPEL + ":" + HEX_TUPEL + ":" + HEX_TUPEL + ":" + HEX_TUPEL + ":" + HEX_TUPEL + ":" + HEX_TUPEL;
  // Updated,Sun Apr 21 17:52:33 2018
  private static final String UPDATED_PATTERN = "Updated,(?<updateDate>" + DATE_PATTERN + ")\\s";
  private static final String CLIENT_ITEM = ".*," + IP_ADDRESS_PATTERN + ":\\d+,\\d+,\\d+," + DATE_PATTERN + "\\s";
  private static final String CLIENT_LIST_GROUPED = "(.*),(" + IP_ADDRESS_PATTERN + ":\\d+),(\\d+),(\\d+)," + DATE_PATTERN + "\\s";
  // \r\n192.168.57.14,other.common.name,7.8.9.1:54836,Thu Mar 3 17:23:10 2018
  // \r\n192.168.57.10,common.name,1.2.3.4:21370,Thu Mar 3 13:39:11 2018\r\n
  private static final String ROUTING_TABLE = "(" + IP_ADDRESS_PATTERN + "|" + MAC_ADDRESS_PATTERN + "),([a-zA-Z0-9\\.]*),(" + IP_ADDRESS_PATTERN + ":\\d+)," + DATE_PATTERN +
      "\\s";
  private static final Pattern updatedPattern = Pattern.compile(UPDATED_PATTERN, Pattern.MULTILINE);
  private static final Pattern clientPattern = Pattern.compile(CLIENT_LIST_GROUPED, Pattern.MULTILINE);
  private static final Pattern routingTablePattern = Pattern.compile(ROUTING_TABLE, Pattern.MULTILINE);
  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(Status.class);

  // Routing List is currently not used in GUI.
  private static final long serialVersionUID = 1;

  private final List<ClientConnected> clientConnectedList = new ArrayList<>();
  private final List<RoutingTableEntry> routingTableEntryList = new ArrayList<>();
  private final Date updated;

  private Status(final Date updated) {
    this.updated = updated;

  }

  /**
   * Singleton to fire command and generated Object from Response
   *
   * @param transceiver Link to Management Console
   * @return Object to Create
   * @throws ResponseParseException Exception if communication was not successful
   */

  public static synchronized Status getInstance(final Transceiver transceiver) throws ResponseParseException {
    final Matcher version;

    try {
      final String statusResult = transceiver.transceiverMultiLine(OpenVpnCommandEnum.STATUS);
      LOGGER.info("statusResult: {}", statusResult);
      final Pattern statusPattern =
          Pattern.compile(HEADLINE_RESPONSE + UPDATED_PATTERN + HEADLINE_CLIENT + "(?<clientsConnected>(" + CLIENT_ITEM + ")*)" + HEADLINE_ROUTINGTABLE + HEADLINE_ROUTINGTABLE_ENTRYS + "(?<routingTableEntry>(" + ROUTING_TABLE + ")*)" + HEADLINE_GLOBAL_STATS + HEADLINE_GLOBAL_STATS_ENTRYS + "END\\s", Pattern.MULTILINE);

      final Matcher matcherStatus = statusPattern.matcher(statusResult);

      if (matcherStatus.matches()) {
        final Date update = Utils.createDateFromLongString(matcherStatus.group("updateDate"));
        final String clientsConnected = matcherStatus.group("clientsConnected");
        final String routingTableEntries = matcherStatus.group("routingTableEntry");
        LOGGER.info("Date: {}", update);
        LOGGER.info("Clientlist: {}", clientsConnected);
        LOGGER.info("RoutingTableList: {}", routingTableEntries);
        return new Status(update);
      }

    } catch (final IOException | NumberFormatException e) {
      throw new ResponseParseException(e);
    }
    throw new ResponseParseException("Unable to parse Status Object");
  }
}
