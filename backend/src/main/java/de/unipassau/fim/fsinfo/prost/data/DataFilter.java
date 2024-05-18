package de.unipassau.fim.fsinfo.prost.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataFilter {

  private static final String EMAIL_PATTERN = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\\.+[a-zA-Z0-9.-]+$";
  final static int MAX_NAME_LENGTH = 20;

  public static boolean isValidString(String value, String name) {
    if (value == null || value.isBlank()) {
      System.out.println("[DF] :: " + name + " is empty");
      return true;
    }
    if (value.length() > MAX_NAME_LENGTH) {
      System.out.println("[DF] :: " + name + " size to large");
      return true;
    }
    return false;
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
