package org.vcable.openvpn.responses;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.vcable.openvpn.OpenVpnCommandEnum;
import org.vcable.openvpn.Transceiver;

public class Pid implements Response {

  /*
   * SUCCESS: pid=1
   */

  private static final String REGEX = "SUCCESS: pid=(\\d+)";
  private static final Pattern PATTERN = Pattern.compile(REGEX, Pattern.DOTALL);

  private final int pid;

  /**
   * Response Object from the Management interface on a 'version' command.
   *
   * @param pid ID of Process
   */

  private Pid(final int pid) {
    this.pid = pid;
  }

  /**
   * Singleton to fire command and generated Object from Response
   *
   * @param transceiver Link to Management Console
   * @return Object to Create
   * @throws ResponseParseException Exception if communication was not successful
   */

  public static synchronized Pid getInstance(final Transceiver transceiver) throws ResponseParseException {
    final Matcher matcher;
    try {
      matcher = PATTERN.matcher(transceiver.transceiverSingleLine(OpenVpnCommandEnum.PID));
      if (matcher.matches()) {
        return new Pid(Integer.parseInt(matcher.group(1)
            .trim()));
      }
    } catch (final IOException | NumberFormatException e) {
      throw new ResponseParseException(e);
    }
    throw new ResponseParseException("Unable to parse Pid Object");
  }

  /**
   * Get the ID Number
   *
   * @return pid ID
   */

  public int getPid() {
    return pid;
  }
}
