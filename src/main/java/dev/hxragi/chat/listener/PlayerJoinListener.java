package dev.hxragi.chat.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import dev.hxragi.chat.settings.SettingsManager;

public class PlayerJoinListener implements Listener {
  private final SettingsManager settingsManager;

  public PlayerJoinListener(SettingsManager settingsManager) {
    this.settingsManager = settingsManager;
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    settingsManager.preloadSettings(event.getPlayer().getUniqueId());
  }
}
