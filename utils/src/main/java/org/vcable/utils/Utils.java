package org.vcable.utils;

import java.util.Iterator;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Various Utils Methods
 */
public class Utils {

  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

  public static String bytesToHexString(final byte[] bytes) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < bytes.length; i++) {
      String hex = Integer.toHexString(0xFF & bytes[i]);
      if (hex.length() == 1) {
        sb.append('0');
      }
      sb.append(hex);
    }
    return sb.toString();
  }


  public static String printMap(Map<String, String[]> map) {
    StringBuilder sb = new StringBuilder();
    Iterator<Map.Entry<String, String[]>> iter = map.entrySet()
        .iterator();
    while (iter.hasNext()) {
      try {
        Map.Entry<String, String[]> entry = iter.next();
        String[] values = entry.getValue();
        sb.append(entry.getKey());
        sb.append('=')
            .append('"');


        sb.append(printArray(values));

        sb.append('"');
        if (iter.hasNext()) {
          sb.append(',')
              .append(' ');
        }

        LOGGER.info("Key: '{}', value: '{}'", entry.getKey(), printArray(entry.getValue()));

      } catch (Exception e) {
        LOGGER.error("Error Parsing Element: {}", e.toString());
      }

    }
    return sb.toString();
  }

  private static String printArray(String[] stringArray) {
    StringBuilder sb = new StringBuilder();
    for (String s : stringArray) {
      sb.append(s)
          .append(",");
    }
    // removing last ","
    sb.deleteCharAt(sb.lastIndexOf(","));

    return sb.toString();
  }


}




