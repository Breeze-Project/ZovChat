package dev.hxragi.chat.service;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import dev.hxragi.chat.dto.ChatSettings;
import dev.hxragi.chat.config.ConfigManager;
import dev.hxragi.chat.dto.FormattedMessage;
import dev.hxragi.chat.settings.SettingsManager;
import dev.hxragi.chat.util.LegacyConverter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ChatService {
  private final Plugin plugin;
  private final MessageFormatter messageFormatter;
  private final ConfigManager configManager;
  private final SettingsManager settingsManager;
  private final MiniMessage miniMessage = MiniMessage.builder().strict(false).build();

  private final boolean placeholderApiEnabled;

  public ChatService(Plugin plugin, MessageFormatter messageFormatter, ConfigManager configManager,
      SettingsManager settingsManager) {
    this.plugin = plugin;
    this.messageFormatter = messageFormatter;
    this.configManager = configManager;
    this.settingsManager = settingsManager;
    this.placeholderApiEnabled = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
  }

  public void handleChat(Player sender, Component originalMessage) {
    String rawMessage = PlainTextComponentSerializer.plainText().serialize(originalMessage);

    if (rawMessage.isBlank()) {
      sender.sendMessage(Component.text("Сообщение не может быть пустым", NamedTextColor.RED));
      return;
    }

    boolean isGlobal = rawMessage.startsWith(configManager.globalSymbol);
    String content = isGlobal ? rawMessage.substring(1).trim() : rawMessage.trim();

    if (content.isBlank()) {
      sender.sendMessage(Component.text("Сообщение не может быть пустым", NamedTextColor.RED));
      return;
    }

    FormattedMessage formatted = messageFormatter.format(sender, content);
    Component finalMessage = buildFinalMessage(sender, formatted.message(), isGlobal);

    broadcastMessage(sender, finalMessage, isGlobal);
    playMentionSound(formatted.mentionedPlayers());
  }

  public Component buildFinalMessage(Player sender, Component message, boolean isGlobal) {
    String lpPrefix = LegacyConverter.convert(parsePlaceholders(sender, "%luckperms_prefix%"));
    String ccbTag = LegacyConverter.convert(parsePlaceholders(sender, "%ccb_tag%"));

    if (!lpPrefix.isEmpty() && !lpPrefix.endsWith(" ")) {
      lpPrefix = lpPrefix + " ";
    }
    if (!ccbTag.isEmpty() && !ccbTag.startsWith(" ")) {
      ccbTag = " " + ccbTag;
    }

    String senderNameColor = LegacyConverter.convert(
        isGlobal ? configManager.globalSenderNameColor : configManager.localSenderNameColor);

    String combinedStr = lpPrefix + senderNameColor + " " + sender.getName() + " " + "<reset>" + ccbTag;

    int ticks = sender.getStatistic(Statistic.PLAY_ONE_MINUTE);
    long hours = ticks / 20 / 3600;

    Component hoverText = Component.text()
        .append(Component.text("Наиграно: ", NamedTextColor.GRAY))
        .append(Component.text(hours + "ч", NamedTextColor.GRAY))
        .build();

    Component senderName = miniMessage.deserialize(combinedStr)
        .hoverEvent(HoverEvent.showText(hoverText))
        .clickEvent(ClickEvent.suggestCommand("/msg " + sender.getName() + " "));

    String messageColorStr = LegacyConverter.convert(
        isGlobal ? configManager.globalMessageColor : configManager.localMessageColor);
    Component messageColor = miniMessage.deserialize(messageColorStr);

    Component separator = miniMessage.deserialize(
        LegacyConverter.convert(configManager.separatorColor) + ": ");

    return senderName.append(separator).append(messageColor.append(message));
  }

  private String parsePlaceholders(Player player, String text) {
    if (!placeholderApiEnabled) {
      return "";
    }
    return PlaceholderAPI.setPlaceholders(player, text);
  }

  private void broadcastMessage(Player sender, Component message, boolean isGlobal) {
    Bukkit.getConsoleSender().sendMessage(message);
    for (Player recipient : Bukkit.getOnlinePlayers()) {
      ChatSettings recipientSettings = settingsManager.getSettings(recipient.getUniqueId());
      boolean canSee = isGlobal ? recipientSettings.showGlobalChat() : recipientSettings.showLocalChat();

      if (canSee && (isGlobal || isInRange(sender, recipient))) {
        recipient.sendMessage(message);
      }
    }
  }

  private boolean isInRange(Player sender, Player recipient) {
    if (sender.equals(recipient)) {
      return true;
    }
    if (!sender.getWorld().equals(recipient.getWorld())) {
      return false;
    }
    return sender.getLocation().distance(recipient.getLocation()) <= configManager.localRadius;
  }

  private void playMentionSound(List<Player> mentionedPlayers) {
    if (mentionedPlayers.isEmpty()) {
      return;
    }
    Bukkit.getScheduler().runTask(plugin, () -> {
      for (Player target : mentionedPlayers) {
        ChatSettings settings = settingsManager.getSettings(target.getUniqueId());
        if (settings.playerMentionSound()) {
          target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
        }
      }
    });
  }
}
