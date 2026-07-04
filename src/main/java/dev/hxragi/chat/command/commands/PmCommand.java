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

public class PmCommand {
  private final ReplyService replyService;

  public PmCommand(ReplyService replyService) {
    this.replyService = replyService;
  }

  public LiteralCommandNode<CommandSourceStack> create() {
    return Commands.literal("pm")
        .then(Commands.argument("target", StringArgumentType.word())
            .suggests((context, builder) -> {
              Bukkit.getOnlinePlayers().stream().map(Player::getName).forEach(builder::suggest);
              return builder.buildFuture();
            })
            .then(Commands.argument("message", StringArgumentType.greedyString())
                .executes(context -> {
                  CommandSourceStack source = context.getSource();
                  if (!(source.getSender() instanceof Player sender)) {
                    source.getSender().sendMessage(
                        Component.text("Только игроки могут использовать эту команду", NamedTextColor.RED));
                    return 0;
                  }

                  String targetName = StringArgumentType.getString(context, "target");
                  String message = StringArgumentType.getString(context, "message");

                  Player target = Bukkit.getPlayerExact(targetName);
                  if (target == null) {
                    sender.sendMessage(Component.text("Игрок не найден", NamedTextColor.RED));
                    return 0;
                  }

                  if (target.equals(sender)) {
                    sender.sendMessage(Component.text("Нельзя отправить сообщение самому себе", NamedTextColor.RED));
                    return 0;
                  }

                  replyService.sendPrivateMessage(sender, target, message);
                  return 1;
                })))
        .build();
  }
}
