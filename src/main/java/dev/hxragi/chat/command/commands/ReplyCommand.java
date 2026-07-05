package dev.hxragi.chat.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import dev.hxragi.chat.service.ReplyService;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ReplyCommand {
  private final ReplyService replyService;

  public ReplyCommand(ReplyService replyService) {
    this.replyService = replyService;
  }

  public LiteralCommandNode<CommandSourceStack> create() {
    return Commands.literal("r")
        .requires(source -> source.getSender() instanceof Player)
        .then(Commands.argument("message", StringArgumentType.greedyString())
            .executes(context -> {
              Player sender = (Player) context.getSource().getSender();

              String message = StringArgumentType.getString(context, "message");

              var lastSenderId = replyService.getLastSender(sender.getUniqueId());
              if (lastSenderId.isEmpty()) {
                sender.sendMessage(Component.text("Вам некому отвечать", NamedTextColor.RED));
                return 0;
              }

              Player target = Bukkit.getPlayer(lastSenderId.get());
              if (target == null) {
                sender.sendMessage(Component.text("Игрок больше не в сети", NamedTextColor.RED));
                return 0;
              }

              replyService.sendPrivateMessage(sender, target, message);
              return 1;
            }))
        .build();
  }
}
