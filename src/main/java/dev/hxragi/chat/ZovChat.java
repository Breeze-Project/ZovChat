package dev.hxragi.chat;

import org.bukkit.plugin.java.JavaPlugin;

import dev.hxragi.chat.command.CommandManager;
import dev.hxragi.chat.config.ConfigManager;
import dev.hxragi.chat.database.DatabaseManager;
import dev.hxragi.chat.listener.ChatListener;
import dev.hxragi.chat.listener.ChatSettingsGUIListener;
import dev.hxragi.chat.listener.PlayerQuitListener;
import dev.hxragi.chat.service.ChatService;
import dev.hxragi.chat.service.MessageFormatter;
import dev.hxragi.chat.service.ReplyService;
import dev.hxragi.chat.settings.SettingsManager;

public class ZovChat extends JavaPlugin {
  private DatabaseManager databaseManager;
  private SettingsManager settingsManager;

  @Override
  public void onEnable() {
    saveDefaultConfig();

    ConfigManager configManager = new ConfigManager(this);
    this.databaseManager = new DatabaseManager(this, configManager);
    this.settingsManager = new SettingsManager(this, databaseManager);

    MessageFormatter messageFormatter = new MessageFormatter(configManager);
    ChatService chatService = new ChatService(this, messageFormatter, configManager, settingsManager);
    ReplyService replyService = new ReplyService(settingsManager);

    getServer().getPluginManager().registerEvents(new ChatListener(chatService), this);
    getServer().getPluginManager().registerEvents(new ChatSettingsGUIListener(settingsManager), this);
    getServer().getPluginManager().registerEvents(new PlayerQuitListener(settingsManager, replyService), this);

    new CommandManager(this, replyService, settingsManager);
  }

  @Override
  public void onDisable() {
    settingsManager.flushAll();
    if (databaseManager != null) {
      databaseManager.close();
    }
  }
}
