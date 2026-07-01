package dev.hxragi.chat.command;

import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import dev.hxragi.chat.command.commands.ChatSettingsCommand;
import dev.hxragi.chat.command.commands.PmCommand;
import dev.hxragi.chat.command.commands.ReplyCommand;
import dev.hxragi.chat.service.ReplyService;
import dev.hxragi.chat.settings.SettingsManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

public class CommandManager {
  private final JavaPlugin plugin;
  private final ReplyService replyService;
  private final SettingsManager settingsManager;

  public CommandManager(JavaPlugin plugin, ReplyService replyService, SettingsManager settingsManager) {
    this.plugin = plugin;
    this.replyService = replyService;
    this.settingsManager = settingsManager;
  }

  public void registerAll() {
    plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
      var registrar = event.registrar();

      registrar.register(new PmCommand(replyService).create(), "Отправить личное сообщение",
          List.of("msg", "tell", "w"));
      registrar.register(new ReplyCommand(replyService).create(), "Ответить на последнее личное сообщение",
          List.of("reply"));
      registrar.register(new ChatSettingsCommand(settingsManager).create(), "Настройки чата",
          List.of("chatsettings", "cs"));
    });
  }
}
