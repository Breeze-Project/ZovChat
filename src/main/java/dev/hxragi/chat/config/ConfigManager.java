package dev.hxragi.chat.config;

import org.bukkit.plugin.java.JavaPlugin;

public record ConfigManager(
    String globalSymbol,
    int localRadius,
    String globalFormat,
    String localFormat,
    String mentionColor,
    String databaseUrl,
    int poolSize) {
  public ConfigManager(JavaPlugin plugin) {
    this(
        plugin.getConfig().getString("chat.globalSymbol"),
        plugin.getConfig().getInt("chat.localChatRadius"),
        plugin.getConfig().getString("chat.formats.global"),
        plugin.getConfig().getString("chat.formats.local"),
        plugin.getConfig().getString("mention.color"),
        plugin.getConfig().getString("database.url"),
        plugin.getConfig().getInt("database.poolSize"));
  }
}
