package org.vcable.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.security.cert.X509Certificate;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vcable.utils.exceptions.ParsingException;

/**
 * Various Methods for Parsing Strings from OpenVPN
 */
public class StringParser {

  public static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss yyyy";
  public static final String DATE_FORMAT_SHORT = "yyyy-MM-dd HH:mm:ss";
  public static final String TIME_FORMAT = "HH:mm:ss";
  private final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

  // DateFormater must be local as it is not thread save.
  // public final static DateFormat formatterDateLong = new SimpleDateFormat(DATE_FORMAT, Locale.US);
  // public final static DateFormat formatterDateShort = new SimpleDateFormat(DATE_FORMAT_SHORT, Locale.US);
  //  public final static DateFormat formatterTime = new SimpleDateFormat(TIME_FORMAT, Locale.US);
  private static final Logger LOGGER = LoggerFactory.getLogger(StringParser.class);

  public static InetSocketAddress createInetSocketAddressFromString(final String socket) throws ParsingException {

    InetAddress address = null;
    int port = 0;

    InetSocketAddress result = null;

    String split[] = socket.trim()
        .split(":");
    try {

      address = InetAddress.getByName(split[0].replace("/", ""));
    } catch (UnknownHostException e) {
      throw new ParsingException("Unable to parse '" + split[0] + "' as host. Exception: " + e.toString());
    }

    try {
      port = Integer.parseInt(split[1]);
    } catch (NumberFormatException e) {
      throw new ParsingException("Unable to parse '" + split[1] + "' as Port. Exception: " + e.toString());
    }

    try {
      result = new InetSocketAddress(address, port);
    } catch (IllegalArgumentException e) {
      throw new ParsingException("Unable to create InetSocketAddress: " + e.toString());
    }

    return result;
  }


  public static Date createDateFromLongString(String s) {
    // Sun Apr 21 18:07:19 2013
    // EEE MMM d HH:mm:ss yyyy";

    Date result = null;
    DateFormat formatterDateLong = new SimpleDateFormat(DATE_FORMAT, Locale.US);

    try {
      result = formatterDateLong.parse(s);
    } catch (Exception e) {
      LOGGER.error("Error createDateFromLongString '{}', '{}'", s, e.toString());
    }

    return result;
  }


  public static Date createDateFromShortString(String s) {
    // yyyy-MM-dd HH:mm:ss
    Date result = null;
    DateFormat formatterDateShort = new SimpleDateFormat(DATE_FORMAT_SHORT, Locale.US);

    try {
      result = formatterDateShort.parse(s);
    } catch (Exception e) {
      LOGGER.error("Error createDateFromShortString '{}', '{}'", s, e.toString());
    }

    return result;
  }

  public static Date createDateFromTime(String s) {


    Date result = null;
    DateFormat formatterTime = new SimpleDateFormat(TIME_FORMAT, Locale.US);

    try {
      result = formatterTime.parse(s);
    } catch (Exception e) {
      LOGGER.error("Error parseable date '{}', Error: {}", s, e.toString());
    }

    return result;

  }

  public static String createDateString(final Date date) {
    final DateFormat formatterDateLong = new SimpleDateFormat(DATE_FORMAT, Locale.US);
    return formatterDateLong.format(date);
  }


  public static HashMap<String, String> parseSubjectDN(String dn) {
    HashMap<String, String> result = new HashMap<String, String>();

    String[] lines = dn.split(",");

    for (int x = 0; x < lines.length; x++) {

      if (lines[x].contains("=")) {
        lines[x] = lines[x].trim();
        String key = lines[x].split("=")[0].trim();
        String value = lines[x].split("=")[1].trim();
        result.put(key, value);
      }
    }

    return result;
  }

  public static String getCommonNameFromSslSession(SSLSession session) throws SSLPeerUnverifiedException, ParsingException {
    return getCommoNameFromX590Certificate(session.getPeerCertificateChain());
  }

  public static String getCommoNameFromX590Certificate(X509Certificate[] certs) throws SSLPeerUnverifiedException, ParsingException {
    if (certs.length > 0) {
      HashMap<String, String> r = parseSubjectDN(certs[0].getSubjectDN()
          .toString());
      if (r.containsKey("CN"))
        return r.get(("CN").trim());
      else
        throw new ParsingException("CN not found in Certificate : " + certs[0].getSubjectDN());
    } else
      throw new ParsingException("No Cerificates attached to session");
  }


  public static String ellipsise(String input, int maxLen) {
    if (input == null)
      return null;
    if ((input.length() < maxLen) || (maxLen < 3))
      return input;
    return input.substring(0, maxLen - 3) + "...";
  }

  public static String createMacAddress(byte[] data) throws InvalidAlgorithmParameterException {

    if (data == null)
      throw new InvalidParameterException("Input Data is null");

    StringBuilder sb = new StringBuilder();

    for (byte aData : data) {
      String bs = Integer.toHexString(aData & 0xFF);
      if (bs.length() == 1) {
        sb.append(0);
      }
      sb.append(bs);
      sb.append(":");

    }

    return sb.toString();
  }


  public static String loadBuildinfoString() {
    InputStream is = StringParser.class.getClassLoader()
        .getResourceAsStream("buildinfo");

    if (is != null) {
      try {
        String info = IOUtils.toString(is);
        LOGGER.debug("Build String: '{}'", info);
        return info;
      } catch (IOException e) {
        return "Failed to read buildinfo file: " + e.getMessage();
      } finally {
        IOUtils.closeQuietly(is);
      }
    } else {
      return "Failed to read buildinfo";
    }
  }


  public static String bytesToHex(final byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    int v;
    for (int j = 0; j < bytes.length; j++) {
      v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }


  public static void sleepSomeMS(int ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
    }
  }

}
