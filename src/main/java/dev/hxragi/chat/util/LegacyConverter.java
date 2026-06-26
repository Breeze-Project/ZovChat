package dev.hxragi.chat.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LegacyConverter {
  private static final Pattern LEGACY_PATTERN = Pattern.compile("[\u00A7&]([0-9a-fk-orA-FK-OR])");

  public static String convert(String input) {
    if (input == null || input.isEmpty()) {
      return "";
    }
    Matcher matcher = LEGACY_PATTERN.matcher(input);
    StringBuilder sb = new StringBuilder();
    while (matcher.find()) {
      char code = Character.toLowerCase(matcher.group(1).charAt(0));
      String replacement = switch (code) {
        case '0' -> "<black>";
        case '1' -> "<dark_blue>";
        case '2' -> "<dark_green>";
        case '3' -> "<dark_aqua>";
        case '4' -> "<dark_red>";
        case '5' -> "<dark_purple>";
        case '6' -> "<gold>";
        case '7' -> "<gray>";
        case '8' -> "<dark_gray>";
        case '9' -> "<blue>";
        case 'a' -> "<green>";
        case 'b' -> "<aqua>";
        case 'c' -> "<red>";
        case 'd' -> "<light_purple>";
        case 'e' -> "<yellow>";
        case 'f' -> "<white>";
        case 'k' -> "<obfuscated>";
        case 'l' -> "<bold>";
        case 'm' -> "<strikethrough>";
        case 'n' -> "<underlined>";
        case 'o' -> "<italic>";
        case 'r' -> "<reset>";
        default -> matcher.group(0);
      };
      matcher.appendReplacement(sb, replacement);
    }
    matcher.appendTail(sb);
    return sb.toString();
  }
}
