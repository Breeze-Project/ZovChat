package dev.hxragi.chat.command.commands;

import org.bukkit.entity.Player;

import com.mojang.brigadier.tree.LiteralCommandNode;

import dev.hxragi.chat.gui.ChatSettingsGUI;
import dev.hxragi.chat.settings.SettingsManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ChatSettingsCommand {
  private final SettingsManager settingsManager;

  public ChatSettingsCommand(SettingsManager settingsManager) {
    this.settingsManager = settingsManager;
  }

  public LiteralCommandNode<CommandSourceStack> create() {
    return Commands.literal("chatsettings").executes(context -> {
      CommandSourceStack source = context.getSource();
      if (!(source.getSender() instanceof Player sender)) {
        source.getSender()
            .sendMessage(Component.text("Только игроки могут использовать эту команду", NamedTextColor.RED));
        return 0;
      }
      ChatSettingsGUI.open(sender, settingsManager);
      return 1;
    }).build();
  }
}
