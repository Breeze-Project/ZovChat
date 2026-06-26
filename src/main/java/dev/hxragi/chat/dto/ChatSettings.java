package dev.hxragi.chat.dto;

import java.util.UUID;

public record ChatSettings(UUID playerId, boolean allowPrivateMessages, boolean playerMentionSound,
    boolean showLocalChat, boolean showGlobalChat) {
  public static ChatSettings defaults(UUID playerId) {
    return new ChatSettings(playerId, true, true, true, true);
  }

  public ChatSettings withAllowPrivateMessages(boolean value) {
    return new ChatSettings(playerId, value, playerMentionSound, showLocalChat, showGlobalChat);
  }

  public ChatSettings withPlayerMentionSound(boolean value) {
    return new ChatSettings(playerId, allowPrivateMessages, value, showLocalChat, showGlobalChat);
  }

  public ChatSettings withShowLocalChat(boolean value) {
    return new ChatSettings(playerId, allowPrivateMessages, playerMentionSound, value, showGlobalChat);
  }

  public ChatSettings withShowGlobalChat(boolean value) {
    return new ChatSettings(playerId, allowPrivateMessages, playerMentionSound, showLocalChat, value);
  }
}
