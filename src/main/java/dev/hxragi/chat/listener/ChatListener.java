package dev.hxragi.chat.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import dev.hxragi.chat.service.ChatService;
import io.papermc.paper.event.player.AsyncChatEvent;

public class ChatListener implements Listener {
  private final ChatService chatService;

  public ChatListener(ChatService chatService) {
    this.chatService = chatService;
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onAsyncChat(AsyncChatEvent event) {
    event.setCancelled(true);
    chatService.handleChat(event.getPlayer(), event.message());
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onLegacyAsyncChat(AsyncPlayerChatEvent event) {
    event.setCancelled(true);
  }
}
