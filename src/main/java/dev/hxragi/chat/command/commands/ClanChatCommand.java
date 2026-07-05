package dev.hxragi.chat.command.commands;

import org.bukkit.entity.Player;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import dev.hxragi.chat.service.ClanChatService;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class ClanChatCommand {
  private final ClanChatService clanChatService;

  public ClanChatCommand(ClanChatService clanChatService) {
    this.clanChatService = clanChatService;
  }

  public LiteralCommandNode<CommandSourceStack> create() {
    return Commands.literal("cchat")
        .requires(source -> source.getSender() instanceof Player)
        .then(Commands.argument("message", StringArgumentType.greedyString())
            .executes(context -> {
              Player sender = (Player) context.getSource().getSender();
              String message = StringArgumentType.getString(context, "message");
              clanChatService.sendClanMessage(sender, message);
              return 1;
            }))
        .build();
  }
}
