package dev.hxragi.chat.config;

import org.bukkit.plugin.java.JavaPlugin;

public record ConfigManager(
    String globalSymbol,
    int localRadius,
    String globalFormat,
    String localFormat,
    String mentionColor,
    String adminSymbol,
    String adminFormat,
    String adminPermission,
    String clanChatFormat,
    String databaseName,
    int poolSize) {
  public ConfigManager(JavaPlugin plugin) {
    this(
        plugin.getConfig().getString("chat.globalSymbol"),
        plugin.getConfig().getInt("chat.localChatRadius"),
        plugin.getConfig().getString("chat.formats.global"),
        plugin.getConfig().getString("chat.formats.local"),
        plugin.getConfig().getString("mention.color"),
        plugin.getConfig().getString("chat.adminChat.symbol"),
        plugin.getConfig().getString("chat.adminChat.format"),
        plugin.getConfig().getString("chat.adminChat.permission"),
        plugin.getConfig().getString("chat.formats.clan"),
        plugin.getConfig().getString("database.name"),
        plugin.getConfig().getInt("database.poolSize"));
  }
}
