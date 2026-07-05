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
        .requires(source -> source.getSender() instanceof Player)
        .then(Commands.argument("target", StringArgumentType.word())
            .suggests((context, builder) -> {
              String remaining = builder.getRemaining().toLowerCase();

              Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(remaining))
                .forEach(builder::suggest);
              return builder.buildFuture();
            })
            .then(Commands.argument("message", StringArgumentType.greedyString())
                .executes(context -> {
                  Player sender = (Player) context.getSource().getSender();

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
