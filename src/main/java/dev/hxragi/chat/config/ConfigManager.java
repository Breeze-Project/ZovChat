package dev.hxragi.chat.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
  public final String globalSymbol;
  public final int localRadius;

  public final String globalSenderNameColor;
  public final String localSenderNameColor;

  public final String globalMessageColor;
  public final String localMessageColor;

  public final String separatorColor;

  public final String mentionColor;

  public final String databaseUrl;
  public final int poolSize;

  public ConfigManager(JavaPlugin plugin) {
    plugin.saveDefaultConfig();
    FileConfiguration config = plugin.getConfig();

    this.globalSymbol = config.getString("chat.globalSymbol");
    this.localRadius = config.getInt("chat.localChatRadius");

    this.globalSenderNameColor = config.getString("chat.globalSenderNameColor");
    this.localSenderNameColor = config.getString("chat.localSenderNameColor");

    this.localMessageColor = config.getString("chat.localMessageColor");
    this.globalMessageColor = config.getString("chat.globalMessageColor");

    this.separatorColor = config.getString("chat.separatorColor");

    this.mentionColor = config.getString("mention.color");

    this.databaseUrl = config.getString("database.url");
    this.poolSize = config.getInt("database.poolSize");
  }
}
