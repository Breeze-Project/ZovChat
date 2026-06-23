package dev.hxragi.chat;

import org.bukkit.plugin.java.JavaPlugin;

import dev.hxragi.chat.command.CommandManager;
import dev.hxragi.chat.config.ConfigManager;
import dev.hxragi.chat.database.DatabaseManager;
import dev.hxragi.chat.listener.ChatListener;
import dev.hxragi.chat.listener.ChatSettingsGUIListener;
import dev.hxragi.chat.service.ChatService;
import dev.hxragi.chat.service.MessageFormatter;
import dev.hxragi.chat.service.ReplyService;
import dev.hxragi.chat.settings.SettingsManager;

public class ZovChat extends JavaPlugin {
  private ChatService chatService;
  private ReplyService replyService;
  private DatabaseManager databaseManager;
  private SettingsManager settingsManager;

  @Override
  public void onEnable() {
    saveDefaultConfig();

    ConfigManager configManager = new ConfigManager(this);
    this.databaseManager = new DatabaseManager(this, configManager);
    this.settingsManager = new SettingsManager(databaseManager);

    MessageFormatter messageFormatter = new MessageFormatter(configManager);
    this.chatService = new ChatService(this, messageFormatter, configManager, settingsManager);
    this.replyService = new ReplyService(settingsManager);

    getServer().getPluginManager().registerEvents(new ChatListener(chatService), this);
    getServer().getPluginManager().registerEvents(new ChatSettingsGUIListener(settingsManager), this);

    new CommandManager(this, replyService, settingsManager);
  }

  @Override
  public void onDisable() {
    if (databaseManager != null) {
      databaseManager.close();
    }
  }
}
