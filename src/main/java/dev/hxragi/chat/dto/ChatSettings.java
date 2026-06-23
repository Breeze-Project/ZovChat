package dev.hxragi.chat.dto;

import java.util.UUID;

public record ChatSettings(UUID playerId, boolean allowPrivateMessages, boolean playerMentionSound, boolean showLocalChat, boolean showGlobalChat) {
  public static ChatSettings defaults(UUID playerId){
    return new ChatSettings(playerId, true, true, true, true);
  }
}


