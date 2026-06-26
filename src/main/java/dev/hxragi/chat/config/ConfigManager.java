package dev.hxragi.chat.config;

import org.bukkit.plugin.java.JavaPlugin;

public record ConfigManager(
    String globalSymbol,
    int localRadius,
    String globalSenderNameColor,
    String localSenderNameColor,
    String globalMessageColor,
    String localMessageColor,
    String separatorColor,
    String mentionColor,
    String databaseUrl,
    int poolSize) {
  public ConfigManager(JavaPlugin plugin) {
    this(
        plugin.getConfig().getString("chat.globalSymbol"),
        plugin.getConfig().getInt("chat.localChatRadius"),
        plugin.getConfig().getString("chat.globalSenderNameColor"),
        plugin.getConfig().getString("chat.localSenderNameColor"),
        plugin.getConfig().getString("chat.globalMessageColor"),
        plugin.getConfig().getString("chat.localMessageColor"),
        plugin.getConfig().getString("chat.separatorColor"),
        plugin.getConfig().getString("mention.color"),
        plugin.getConfig().getString("database.url"),
        plugin.getConfig().getInt("database.poolSize"));
  }
}
