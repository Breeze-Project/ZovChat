package dev.hxragi.chat.settings;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;

import dev.hxragi.chat.config.ChatSettings;
import dev.hxragi.chat.database.DatabaseManager;

public class SettingsManager {
  private final Map<UUID, ChatSettings> cache = new ConcurrentHashMap<>();
  private final DatabaseManager databaseManager;

  public SettingsManager(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  public ChatSettings getSettings(UUID uuid) {
    return cache.computeIfAbsent(uuid, id -> databaseManager.loadSettings(id).orElse(ChatSettings.defaults(id)));
  }

  public void updateSettings(ChatSettings settings) {
    cache.put(settings.playerId(), settings);
    Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("ZovChat"),
        () -> databaseManager.saveSettings(settings));
  }

  public void removeFromCache(UUID uuid) {
    cache.remove(uuid);
  }
}
