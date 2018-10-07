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
  private static final String DATE_FORMAT_SHORT = "yyyy-MM-dd HH:mm:ss";
  private static final String TIME_FORMAT = "HH:mm:ss";
  private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

  // DateFormater must be local as it is not thread save.
  private static final DateFormat FORMATTER_DATE_LONG = new SimpleDateFormat(DATE_FORMAT, Locale.US);
  private static final DateFormat SIMPLE_DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT_SHORT, Locale.US);
  private static final DateFormat SIMPLE_TIME_FORMATTER = new SimpleDateFormat(TIME_FORMAT, Locale.US);

  public static InetSocketAddress createInetSocketAddressFromString(final String socket) throws ResponseParseException {

    InetAddress address = null;
    int port = 0;

    InetSocketAddress result = null;

    String split[] = socket.trim()
        .split(":");
    try {

      address = InetAddress.getByName(split[0].replace("/", ""));
    } catch (final UnknownHostException e) {
      throw new ResponseParseException("Unable to parse '" + split[0] + "' as host. Exception: " + e.toString());
    }

    try {
      port = Integer.parseInt(split[1]);
    } catch (final NumberFormatException e) {
      throw new ResponseParseException("Unable to parse '" + split[1] + "' as Port. Exception: " + e.toString());
    }

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

  /**
   * Helper Method to create out of String of Format yyyy-MM-dd HH:mm:ss a Date Object
   *
   * @param dateString Date-Time as String in the  yyyy-MM-dd HH:mm:s
   * @return Date Object
   * @throws ResponseParseException Thrown if Parsing was not possible
   */

  public static Date createDateFromShortString(final String dateString) throws ResponseParseException {
    try {
      return SIMPLE_DATE_FORMATTER.parse(dateString);
    } catch (final Exception e) {
      throw new ResponseParseException(e);
    }
  }

  public static Date createDateFromTime(final String timeString) throws ResponseParseException {


    Date result = null;


    try {
      return SIMPLE_TIME_FORMATTER.parse(timeString);
    } catch (final Exception e) {
      throw new ResponseParseException(e);
    }
  }
/*
  public static String createDateString(final Date date) {
    DateFormat formatterDateLong = new SimpleDateFormat(DATE_FORMAT, Locale.US);
    return formatterDateLong.format(date);
  }
*/


}
