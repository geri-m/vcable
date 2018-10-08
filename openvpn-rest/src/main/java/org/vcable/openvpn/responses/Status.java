package org.vcable.openvpn.responses;

import java.io.IOException;
import java.net.InetSocketAddress;
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
  // private static final String CLIENT_ITEM = ".*," + IP_ADDRESS_PATTERN + ":\\d+,\\d+,\\d+," + DATE_PATTERN + "\\s";
  private static final String CLIENT_LIST_GROUPED = "(?<name>.*),(?<ip>" + IP_ADDRESS_PATTERN + ":\\d+),(?<received>\\d+),(?<sent>\\d+),(?<lastRef>" + DATE_PATTERN + ")\\s";
  // \r\n192.168.57.14,other.common.name,7.8.9.1:54836,Thu Mar 3 17:23:10 2018
  // \r\n192.168.57.10,common.name,1.2.3.4:21370,Thu Mar 3 13:39:11 2018\r\n
  private static final String ROUTING_TABLE_GROUPED =
      "(?<virtualAddress>" + IP_ADDRESS_PATTERN + "|" + MAC_ADDRESS_PATTERN + "),(?<commonName>[a-zA-Z0-9.]*),(?<realAddress>" + IP_ADDRESS_PATTERN + ":\\d+),(?<lastRefRoute>" + DATE_PATTERN + ")\\s";
  private static final Pattern STATUS_PATTERN =
      Pattern.compile(HEADLINE_RESPONSE + UPDATED_PATTERN + HEADLINE_CLIENT + "(?<clientsConnected>(" + CLIENT_LIST_GROUPED + ")*)" + HEADLINE_ROUTINGTABLE + HEADLINE_ROUTINGTABLE_ENTRYS + "(?<routingTableEntry>(" + ROUTING_TABLE_GROUPED + ")*)" + HEADLINE_GLOBAL_STATS + HEADLINE_GLOBAL_STATS_ENTRYS + "END\\s", Pattern.MULTILINE);
  private static final Pattern CLIENT_PATTERN = Pattern.compile(CLIENT_LIST_GROUPED);
  private static final Pattern ROUTING_TABLE_ENTRY_PATTERN = Pattern.compile(ROUTING_TABLE_GROUPED);

  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(Status.class);

  // Routing List is currently not used in GUI.
  private static final long serialVersionUID = 1;

  private final List<ClientConnected> clientsConnectedList;
  private final List<RoutingTableEntry> routingTableEntryList;
  private final Date updated;

  private Status(final Date updated, final List<ClientConnected> clientsConnectedList, final List<RoutingTableEntry> routingTableEntryList) {
    this.updated = updated;
    this.clientsConnectedList = new ArrayList<>(clientsConnectedList);
    this.routingTableEntryList = new ArrayList<>(routingTableEntryList);
  }

  /**
   * Singleton to fire command and generated Object from Response
   *
   * @param transceiver Link to Management Console
   * @return Object to Create
   * @throws ResponseParseException Exception if communication was not successful
   */

  public static synchronized Status getInstance(final Transceiver transceiver) throws ResponseParseException {
    try {
      final Matcher matcherStatus = STATUS_PATTERN.matcher(transceiver.transceiverMultiLine(OpenVpnCommandEnum.STATUS));

      if (matcherStatus.matches()) {
        final Date update = Utils.createDateFromLongString(matcherStatus.group("updateDate"));
        final String clientsConnected = matcherStatus.group("clientsConnected");
        final String routingTableEntries = matcherStatus.group("routingTableEntry");
        LOGGER.info("RoutingTableList: {}", routingTableEntries);
        return new Status(update, createClientsConnectList(clientsConnected), createRoutingTableList(routingTableEntries));
      }

    } catch (final IOException | NumberFormatException e) {
      throw new ResponseParseException(e);
    }
    throw new ResponseParseException("Unable to parse Status Object");
  }

  /**
   * Private Method to create a list of {@link ClientConnected} Objects out of the Substring of the Status
   *
   * @param input Substring of the {@link Status} with the Clients connected
   * @return Returns an {@link ArrayList} of {@link ClientConnected} Objects or an empty list
   * @throws ResponseParseException Exception is thrown if a the String parsing failed
   */

  private static List<ClientConnected> createClientsConnectList(final String input) throws ResponseParseException {
    final List<ClientConnected> result = new ArrayList<>();
    if ((input != null) && !input.isEmpty()) {
      final Matcher clientMatcher = CLIENT_PATTERN.matcher(input);
      // Iterating over the string and create an object for each match.
      while (clientMatcher.find()) {
        final String name = clientMatcher.group("name");
        final InetSocketAddress address = Utils.createInetSocketAddressFromString(clientMatcher.group("ip"));
        final int received = Integer.parseInt(clientMatcher.group("received"));
        final int sent = Integer.parseInt(clientMatcher.group("sent"));
        final Date lastRef = Utils.createDateFromLongString(clientMatcher.group("lastRef"));
        result.add(new ClientConnected(name, address, received, sent, lastRef));
      }
    }
    return result;
  }

  /**
   * Private Method to create a list of {@link RoutingTableEntry} Objects ouf the Substring of the Status
   *
   * @param input Substring of the {@link Status} with the Routing Table
   * @return Returns an {@link ArrayList} of {@link RoutingTableEntry} Objects or an empty list
   * @throws ResponseParseException Exception is thrown if a the String parsing failed
   */

  private static List<RoutingTableEntry> createRoutingTableList(final String input) throws ResponseParseException {
    final List<RoutingTableEntry> result = new ArrayList<>();
    if ((input != null) && !input.isEmpty()) {
      final Matcher routingTableEntry = ROUTING_TABLE_ENTRY_PATTERN.matcher(input);
      // Iterating over the string and create an object for each match.
      while (routingTableEntry.find()) {
        final String virtualAddress = routingTableEntry.group("virtualAddress");
        final String commonName = routingTableEntry.group("commonName");
        final InetSocketAddress realAddress = Utils.createInetSocketAddressFromString(routingTableEntry.group("realAddress"));
        final Date lastRef = Utils.createDateFromLongString(routingTableEntry.group("lastRefRoute"));
        result.add(new RoutingTableEntry(virtualAddress, commonName, realAddress, lastRef));
      }
    }
    return result;
  }


  /**
   * Getting a list of Connected Client out of the String
   *
   * @return {@link ArrayList} of {@link ClientConnected} Objects or an empty list
   */

  public List<ClientConnected> getClientsConnectedList() {
    return new ArrayList<>(clientsConnectedList);
  }

  /**
   * Getting a list of Entries of a Routing Table ut of the String
   *
   * @return {@link ArrayList} of {@link RoutingTableEntry} Objects or an empty list
   */


  public List<RoutingTableEntry> getRoutingTableEntryList() {
    return new ArrayList<>(routingTableEntryList);
  }

  /**
   * Returns the date, when the state was updated
   *
   * @return {@link Date} when the status was updated
   */

  public Date getUpdated() {
    return new Date(updated.getTime());
  }

  @Override
  public String toString() {
    return "Status{" + "clientsConnectedList=" + clientsConnectedList + ", routingTableEntryList=" + routingTableEntryList + ", updated=" + updated + '}';
  }
}
