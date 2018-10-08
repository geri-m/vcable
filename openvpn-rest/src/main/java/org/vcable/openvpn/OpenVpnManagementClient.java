package org.vcable.openvpn;

import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.commons.net.SocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vcable.openvpn.responses.Pid;
import org.vcable.openvpn.responses.ResponseParseException;
import org.vcable.openvpn.responses.Version;
import org.vcable.openvpn.responses.Welcome;

public class OpenVpnManagementClient extends SocketClient implements Transceiver {

  private static final Logger LOGGER = LoggerFactory.getLogger(OpenVpnManagementClient.class);
  private static final int OPEN_VPN_READ_TIMEOUT_IN_MS = 2000;
  private static final int MAX_AMOUNT_OF_CHARS_TO_READ = 2048;

  // Some Constant strings for the management Console
  private static final String CMD_END = "END";

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
   * @return {@link OpenVpnManagementClient} Instance
   * @throws IOException            Exception thrown if the communication failed.
   * @throws ResponseParseException Exception throw if the welcome message was not parsed correctly.
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
        LOGGER.debug("End found: '{}'", endFound);
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
   * @return result from the management console
   */

  @Override
  public synchronized String transceiverMultiLine(final OpenVpnCommandEnum command) throws IOException {
    // we do a readMultiLineWithEnd first, in order to "clean" the in buffer
    // readMultiLineWithEnd();

    // now we write stuff and flush
    _output_.write((command.getCommand() + NETASCII_EOL).getBytes());
    _output_.flush();
    return readMultiLineWithEnd();
  }

  /**
   * Sends Command (String) to Management Console and reads only one line
   *
   * @param command to be sent to the management console
   * @return result from the management console
   */

  @Override
  public String transceiverSingleLine(final OpenVpnCommandEnum command) throws IOException {
    // now we write stuff and flush
    _output_.write((command.getCommand() + NETASCII_EOL).getBytes());
    _output_.flush();
    return readSingleLineWithoutEnd();
  }

  /**
   * Return the {@link Welcome} Object that was created during Connecting
   *
   * @return Welcome Object
   */

  public Welcome getWelcome() {
    return welcome;
  }

  /**
   * Return the {@link Version} that was received from Management Console
   *
   * @return {@link Version} Object
   * @throws ResponseParseException Exception that is thrown if communication failed.
   */

  public Version getVersion() throws ResponseParseException {
    return Version.getInstance(this);
  }

  /**
   * Return the {@link Pid} that was received from Management Console
   *
   * @return {@link Pid} Object
   * @throws ResponseParseException Exception that is thrown if communication failed.
   */

  public Pid getPid() throws ResponseParseException {
    return Pid.getInstance(this);
  }
}
