package dev.hxragi.chat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dev.hxragi.chat.config.ConfigManager;
import dev.hxragi.chat.dto.FormattedMessage;
import dev.hxragi.chat.util.LegacyConverter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class MessageFormatter {
  private static final int PING_LOW_THRESHOLD = 100;
  private static final int PING_MEDIUM_THRESHOLD = 200;

  private static final Pattern NON_ALPHANUMERIC_PATTERN = Pattern.compile("[^a-zA-Z0-9_]");

  private final MiniMessage miniMessage = MiniMessage.builder().strict(false).build();
  private final ConfigManager configManager;

  private final String mentionColor;

  public MessageFormatter(ConfigManager configManager) {
    this.configManager = configManager;
    this.mentionColor = LegacyConverter.convert(configManager.mentionColor());
  }

  public FormattedMessage format(Player sender, String content) {
    List<UUID> mentionedPlayers = new ArrayList<>();
    String processedContent = processMentions(sender, content, mentionedPlayers);

    TagResolver itemResolver = TagResolver.resolver("item", Tag.inserting(formatItemPlaceholder(sender)));
    TagResolver pingResolver = TagResolver.resolver("ping", Tag.inserting(formatPingPlaceholder(sender)));
    TagResolver locResolver = TagResolver.resolver("loc", Tag.inserting(formatLocPlaceholder(sender)));
    TagResolver mentionResolver = TagResolver.resolver("mention", (ArgumentQueue queue, Context ctx) -> {
      String targetName = queue.popOr("A player name is required").value();
      Player target = Bukkit.getPlayerExact(targetName);
      if (target != null) {
        return Tag.inserting(createMentionComponent(target));
      }
      return Tag.inserting(Component.text(targetName));
    });

    String finalString = LegacyConverter.convert(processedContent.toString().trim());

    Component message = miniMessage.deserialize(finalString, itemResolver, pingResolver, locResolver, mentionResolver);
    return new FormattedMessage(message, List.copyOf(mentionedPlayers));
  }

  private String processMentions(Player sender, String content, List<UUID> mentionedPlayers) {
    StringBuilder processedContent = new StringBuilder();

    for (String word : content.split(" ")) {
      Optional<Player> mentioned = findMention(sender, word);
      if (mentioned.isPresent()) {
        Player target = mentioned.get();
        mentionedPlayers.add(target.getUniqueId());

        String cleanWord = NON_ALPHANUMERIC_PATTERN.matcher(word).replaceAll("");
        if (!cleanWord.isEmpty()) {
          int index = word.indexOf(cleanWord);
          String before = word.substring(0, index);
          String after = word.substring(index + cleanWord.length());

          processedContent.append(before)
              .append("<mention:").append(target.getName()).append(">")
              .append(after)
              .append(" ");
        } else {
          processedContent.append(word).append(" ");
        }
      } else {
        processedContent.append(word).append(" ");
      }
    }

    return processedContent.toString();
  }

  private Component formatItemPlaceholder(Player sender) {
    ItemStack item = sender.getInventory().getItemInMainHand();
    if (item.getType().isAir()) {
      return Component.text("", NamedTextColor.WHITE);
    }
    return Component.text().append(item.displayName()).hoverEvent(item).build();
  }

  private Component formatPingPlaceholder(Player sender) {
    int ping = sender.getPing();
    TextColor color = ping < PING_LOW_THRESHOLD ? NamedTextColor.GREEN
        : ping < PING_MEDIUM_THRESHOLD ? NamedTextColor.YELLOW : NamedTextColor.RED;
    return Component.text("[" + ping + "ms]", color);
  }

  private Component formatLocPlaceholder(Player sender) {
    Location loc = sender.getLocation();
    return Component.text(String.format("%d, %d, %d", loc.getBlockX(),
        loc.getBlockY(), loc.getBlockZ()), NamedTextColor.WHITE);
  }

  private Optional<Player> findMention(Player sender, String word) {
    String cleanWord = NON_ALPHANUMERIC_PATTERN.matcher(word).replaceAll("");
    if (cleanWord.isEmpty()) {
      return Optional.empty();
    }
    return Optional.ofNullable(Bukkit.getPlayerExact(cleanWord))
        .filter(player -> sender == null || !player.equals(sender));
  }

  private Component createMentionComponent(Player target) {
    return miniMessage.deserialize(mentionColor + target.getName())
        .hoverEvent(HoverEvent.showText(Component.text("Онлайн: " + target.getName())))
        .clickEvent(ClickEvent.suggestCommand("/msg " + target.getName() + " "));
  }
}
