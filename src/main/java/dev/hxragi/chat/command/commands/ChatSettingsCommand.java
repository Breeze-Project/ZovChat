package dev.hxragi.chat.command.commands;

import org.bukkit.entity.Player;

import com.mojang.brigadier.tree.LiteralCommandNode;

import dev.hxragi.chat.gui.ChatSettingsGUI;
import dev.hxragi.chat.settings.SettingsManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class ChatSettingsCommand {
  private final SettingsManager settingsManager;

  public ChatSettingsCommand(SettingsManager settingsManager) {
    this.settingsManager = settingsManager;
  }

  public LiteralCommandNode<CommandSourceStack> create() {
    return Commands.literal("chatsettings")
        .requires(source -> source.getSender() instanceof Player)
        .executes(context -> {
          Player sender = (Player) context.getSource().getSender();
          ChatSettingsGUI.open(sender, settingsManager);
          return 1;
        }).build();
  }
}
