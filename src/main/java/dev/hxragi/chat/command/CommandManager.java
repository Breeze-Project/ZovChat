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
  public CommandManager(JavaPlugin plugin, ReplyService replyService, SettingsManager settingsManager) {
    PmCommand pmCommand = new PmCommand(replyService);
    ReplyCommand replyCommand = new ReplyCommand(replyService);
    ChatSettingsCommand chatSettingsCommand = new ChatSettingsCommand(settingsManager);

    plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
      var registrar = event.registrar();

      registrar.register(pmCommand.create(), "Отправить личное сообщение", List.of("msg", "tell", "w"));
      registrar.register(replyCommand.create(), "Ответить на последнее личное сообщение", List.of("reply"));
      registrar.register(chatSettingsCommand.create(), "Настройки чата", List.of("chatsettings", "cs"));
    });
  }
}
