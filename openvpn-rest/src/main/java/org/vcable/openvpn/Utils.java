package org.vcable.openvpn;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.vcable.openvpn.responses.ResponseParseException;

public class Utils {

  private static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss yyyy";

  // DateFormater must be local as it is not thread save.
  private static final DateFormat FORMATTER_DATE_LONG = new SimpleDateFormat(DATE_FORMAT, Locale.US);


  public static InetSocketAddress createInetSocketAddressFromString(final String socket) throws ResponseParseException {
    final InetAddress address;
    final String[] splitAddress = socket.trim()
        .split(":");
    try {

      address = InetAddress.getByName(splitAddress[0].replace("/", ""));
    } catch (final UnknownHostException e) {
      throw new ResponseParseException("Unable to parse '" + splitAddress[0] + "' as host/IP. Exception: " + e.toString());
    }

    final int port;
    try {
      port = Integer.parseInt(splitAddress[1]);
    } catch (final NumberFormatException e) {
      throw new ResponseParseException("Unable to parse '" + splitAddress[1] + "' as Port. Exception: " + e.toString());
    }

    final InetSocketAddress result;
    try {
      result = new InetSocketAddress(address, port);
    } catch (final IllegalArgumentException e) {
      throw new ResponseParseException("Unable to create InetSocketAddress: " + e.toString());
    }

    return result;
  }

  /**
   * Helper Method to create out of String Sun Apr 21 18:07:19 2013 (Format EEE MMM d HH:mm:ss yyyy) a Date Object
   *
   * @param dateString Date-Time as String in the Format EEE MMM d HH:mm:ss yyyy
   * @return Date Object
   * @throws ResponseParseException Thrown if Parsing was not possible
   */

  public static Date createDateFromLongString(final String dateString) throws ResponseParseException {
    try {
      return FORMATTER_DATE_LONG.parse(dateString);
    } catch (final Exception e) {
      throw new ResponseParseException(e);
    }
  }
}
