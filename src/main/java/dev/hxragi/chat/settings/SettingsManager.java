package dev.hxragi.chat.settings;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import dev.hxragi.chat.dto.ChatSettings;
import dev.hxragi.chat.database.DatabaseException;
import dev.hxragi.chat.database.DatabaseManager;

public class SettingsManager {
  private final Map<UUID, ChatSettings> cache = new ConcurrentHashMap<>();
  private final Plugin plugin;
  private final DatabaseManager databaseManager;

  public SettingsManager(Plugin plugin, DatabaseManager databaseManager) {
    this.plugin = plugin;
    this.databaseManager = databaseManager;
  }

  public void preloadSettings(UUID uuid) {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> getSettings(uuid));
  }

  public ChatSettings getSettings(UUID uuid) {
    return cache.computeIfAbsent(uuid, id -> databaseManager.loadSettings(id).orElse(ChatSettings.defaults(id)));
  }

  public void updateSettings(ChatSettings settings) {
    cache.put(settings.playerId(), settings);
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> databaseManager.saveSettings(settings));
  }

  public void removeFromCache(UUID uuid) {
    cache.remove(uuid);
  }

  public void flushAll() {
    for (ChatSettings settings : cache.values()) {
      try {
        databaseManager.saveSettings(settings);
      } catch (DatabaseException e) {
        plugin.getLogger().severe("Failed to flush settings for " + settings.playerId() + ": " + e);
      }
    }
  }
}
