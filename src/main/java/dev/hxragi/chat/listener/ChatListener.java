package dev.hxragi.chat.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import dev.hxragi.chat.service.ChatService;
import io.papermc.paper.event.player.AsyncChatEvent;

public class ChatListener implements Listener {
  private final ChatService chatService;

  public ChatListener(ChatService chatService) {
    this.chatService = chatService;
  }

  @EventHandler
  public void onAsyncChat(AsyncChatEvent asyncChatEvent) {
    asyncChatEvent.setCancelled(true);
    chatService.handleChat(asyncChatEvent.getPlayer(), asyncChatEvent.message());
  }
}
