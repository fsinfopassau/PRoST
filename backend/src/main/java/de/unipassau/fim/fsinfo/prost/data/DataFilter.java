package de.unipassau.fim.fsinfo.prost.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataFilter {

  private static final String EMAIL_PATTERN = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\\.+[a-zA-Z0-9.-]+$";
  final static int MAX_NAME_LENGTH = 30;

  public static boolean isValidString(String value, String name) {
    if (value == null || value.isBlank()) {
      System.out.println("[DF] :: " + name + " is empty");
      return false;
    }
    if (value.length() > MAX_NAME_LENGTH) {
      System.out.println("[DF] :: " + name + " size to large");
      return false;
    }
    return true;
  }

  /**
   * Filters all kind of ids that have to be readable to be lowercase and without spaces
   *
   * @param id the previous id
   * @return the filtered id
   */
  public static String filterNameId(String id) {
    StringBuilder filtered = new StringBuilder();
    for (char c : id.toLowerCase().toCharArray()) {
      if (Character.isLowerCase(c) || Character.isDigit(c)) {
        filtered.append(c);
      }
    }

    return filtered.toString();
  }

  public static boolean isValidEmail(String email) {
    if (email == null) {
      return false;
    }

    Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    Matcher matcher = pattern.matcher(email);
    return matcher.matches();
  }

}
