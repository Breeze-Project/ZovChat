package dev.hxragi.chat.service;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import dev.hxragi.chat.dto.ChatSettings;
import dev.hxragi.chat.config.ConfigManager;
import dev.hxragi.chat.dto.FormattedMessage;
import dev.hxragi.chat.settings.SettingsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ChatService {
  private final JavaPlugin plugin;
  private final MessageFormatter messageFormatter;
  private final ConfigManager configManager;
  private final SettingsManager settingsManager;
  private final MiniMessage miniMessage = MiniMessage.miniMessage();

  public ChatService(JavaPlugin plugin, MessageFormatter messageFormatter, ConfigManager configManager,
      SettingsManager settingsManager) {
    this.plugin = plugin;
    this.messageFormatter = messageFormatter;
    this.configManager = configManager;
    this.settingsManager = settingsManager;
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
    String prefixStr = isGlobal ? configManager.globalPrefix : configManager.localPrefix;
    Component prefix = miniMessage.deserialize(prefixStr);

    int ticks = sender.getStatistic(Statistic.PLAY_ONE_MINUTE);
    long hours = ticks / 20 / 3600;

    Component hoverText = Component.text().append(Component.text("Наиграно: ", NamedTextColor.GRAY))
        .append(Component.text(hours + "ч", NamedTextColor.GRAY)).build();

    String senderNameColor = isGlobal ? configManager.globalSenderNameColor : configManager.localSenderNameColor;

    Component senderName = miniMessage.deserialize(senderNameColor + sender.getName())
        .hoverEvent(HoverEvent.showText(hoverText))
        .clickEvent(ClickEvent.suggestCommand("/msg " + sender.getName() + " "));

    String messageColorStr = isGlobal ? configManager.globalMessageColor : configManager.localMessageColor;
    Component messageColor = miniMessage.deserialize(messageColorStr);

    Component separator = miniMessage.deserialize(configManager.separatorColor + ": ");

    return prefix.append(senderName).append(separator)
        .append(messageColor.append(message));
  }

  public void broadcastMessage(Player sender, Component message, boolean isGlobal) {
    Bukkit.getConsoleSender().sendMessage(message);
    for (Player recipient : Bukkit.getOnlinePlayers()) {
      ChatSettings recipientSettings = settingsManager.getSettings(recipient.getUniqueId());
      boolean canSee = isGlobal ? recipientSettings.showGlobalChat() : recipientSettings.showLocalChat();

      if (canSee && (isGlobal || isInRange(sender, recipient))) {
        recipient.sendMessage(message);
      }
    }
  }

  public boolean isInRange(Player sender, Player recipient) {
    if (sender.equals(recipient)) {
      return true;
    }
    if (!sender.getWorld().equals(recipient.getWorld())) {
      return false;
    }
    return sender.getLocation().distance(recipient.getLocation()) <= configManager.localRadius;
  }

  public void playMentionSound(List<Player> mentionedPlayers) {
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
