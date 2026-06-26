package dev.hxragi.chat.listener;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import dev.hxragi.chat.service.ReplyService;
import dev.hxragi.chat.settings.SettingsManager;

public class PlayerQuitListener implements Listener {
  private final SettingsManager settingsManager;
  private final ReplyService replyService;

  public PlayerQuitListener(SettingsManager settingsManager, ReplyService replyService) {
    this.settingsManager = settingsManager;
    this.replyService = replyService;
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    UUID playerId = event.getPlayer().getUniqueId();
    settingsManager.removeFromCache(playerId);
    replyService.clearLastSender(playerId);
  }
}
