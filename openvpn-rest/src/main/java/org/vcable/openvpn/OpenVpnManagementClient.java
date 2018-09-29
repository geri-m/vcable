package org.vcable.openvpn;

import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.commons.net.SocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vcable.openvpn.responses.ResponseParseException;
import org.vcable.openvpn.responses.Welcome;

public class OpenVpnManagementClient extends SocketClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(OpenVpnManagementClient.class);
  private static final int OPEN_VPN_READ_TIMEOUT_IN_MS = 2000;
  private static final int MAX_AMOUNT_OF_CHARS_TO_READ = 2048;

  // Some Constant strings for the management Console
  private static final String CMD_VERSION = "version";
  private static final String CMD_PID = "pid";
  private static final String CMD_EXIT = "exit";
  private static final String CMD_STATUS = "status";
  private static final String CMD_KILL = "kill";
  private static final String CMD_END = "END";
  private static final String UNKNOWN_CMD = "ERROR: unknown command, enter 'help' for more options";

  private static OpenVpnManagementClient instance;
  private final InetSocketAddress managementAddress;
  private final Welcome welcome;

  /**
   * Private Constructor. If Connection is not possbile this throws an IOException. Not nice.
   *
   * @param managementAddress Address to connect to
   * @throws IOException Exception is thrown is the connection was not established.
   */

  private OpenVpnManagementClient(final InetSocketAddress managementAddress) throws IOException, ResponseParseException {
    super();
    this.managementAddress = managementAddress;
    setDefaultPort(managementAddress.getPort());
    setDefaultTimeout(OPEN_VPN_READ_TIMEOUT_IN_MS);

    // establish connection to host input and output stream are no available;
    connect(managementAddress.getHostName());

    // in case there is input for 2 seconds, come back InputStream. only available _AFTER_ connect.
    setSoTimeout(OPEN_VPN_READ_TIMEOUT_IN_MS);

    welcome = Welcome.getInstance(readSingleLineWithoutEnd());
  }


  /**
   * GetInstance to get the Singleton of the Management Connection, as there can only be one Connection to the OpenVPN Management Connection
   *
   * @param managementAddress Address containing host and port. Will usually be localhost 7505
   */

  public static synchronized OpenVpnManagementClient getInstance(final InetSocketAddress managementAddress) throws IOException, ResponseParseException {

    if (instance == null) {
      instance = new OpenVpnManagementClient(managementAddress);
    }
    return instance;
  }


  /**
   * Separate Method for Reading from the Socket. Reads till "END" or Timeout
   *
   * @return Converted String from the Bytes received thru the socket.
   */

  private synchronized String readMultiLineWithEnd() throws IOException {
    final byte[] buff = new byte[MAX_AMOUNT_OF_CHARS_TO_READ];
    final StringBuilder instr = new StringBuilder();
    boolean endFound = false;
    int amountOfBytesRead;

    // Looping and do at least ONE readMultiLineWithEnd
    LOGGER.debug("Entering Loop");
    do {
      // readMultiLineWithEnd from the buffer, or Time out.
      amountOfBytesRead = super._input_.read(buff);
      if (amountOfBytesRead > 0) {
        instr.append(new String(buff, 0, amountOfBytesRead));
        LOGGER.debug("Appending: '{}'", new String(buff, 0, amountOfBytesRead));
        // check, in the whole string there is somewhere an "END"
        endFound = instr.toString()
            .toUpperCase()
            .contains(CMD_END);
        LOGGER.debug("Endfound: '{}'", endFound);
      } else {
        // not information readMultiLineWithEnd
        LOGGER.debug("Not Information readMultiLineWithEnd, timeout");
      }
    }
    // changed from amountOfBytesRead >= 0
    while ((amountOfBytesRead > 0) && !endFound);

    // remove line feeds and blanks
    return instr.toString()
        .trim();
  }

  /**
   * Method to only read a single line from the console or timeout
   *
   * @return {@code String} at was read from the Console
   * @throws IOException If the connection broke, timeout
   */

  private synchronized String readSingleLineWithoutEnd() throws IOException {
    final byte[] buff = new byte[MAX_AMOUNT_OF_CHARS_TO_READ];
    final StringBuilder instr = new StringBuilder();

    // readMultiLineWithEnd from the buffer, or Time out.
    final int amountOfBytesRead = super._input_.read(buff);
    if (amountOfBytesRead > 0) {
      instr.append(new String(buff, 0, amountOfBytesRead));
      LOGGER.debug("Appending: '{}'", new String(buff, 0, amountOfBytesRead));
    } else {
      // not information readMultiLineWithEnd
      LOGGER.debug("Not Information readSingleLineWithoutEnd, timeout");
    }


    // remove line feeds and blanks
    return instr.toString()
        .trim();
  }


  /**
   * Sends Command (String) to Management Console and reads till "END" String is found
   *
   * @param command to be sent to the management console
   * @return result from the management console but without "END" String
   */

  private synchronized String transive(final String command) throws IOException {
    // we do a readMultiLineWithEnd first, in order to "clean" the in buffer
    readMultiLineWithEnd();

    // now we write stuff and flush
    _output_.write((command + NETASCII_EOL).getBytes());
    _output_.flush();
    return readMultiLineWithEnd();
  }

  public int getVersionOfInterface() {
    return welcome.getVersionOfInterface();
  }


}
