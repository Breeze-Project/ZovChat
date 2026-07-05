package dev.hxragi.chat.service;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import dev.hxragi.chat.dto.ChatSettings;
import dev.hxragi.chat.config.ConfigManager;
import dev.hxragi.chat.dto.FormattedMessage;
import dev.hxragi.chat.hook.AdvancedBanHook;
import dev.hxragi.chat.settings.SettingsManager;
import dev.hxragi.chat.util.LegacyConverter;
import github.scarsz.discordsrv.DiscordSRV;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ChatService {
  private static final float MENTION_SOUND_VOLUME = 1.0f;
  private static final float MENTION_SOUND_PITCH = 1.0f;

  private static final int TICKS_PER_SECOND = 20;
  private static final int SECONDS_PER_HOUR = 3600;

  private final Plugin plugin;
  private final MessageFormatter messageFormatter;
  private final ConfigManager configManager;
  private final SettingsManager settingsManager;
  private final AdvancedBanHook advancedBanHook;
  private final MiniMessage miniMessage = MiniMessage.builder().strict(false).build();

  private final boolean placeholderApiEnabled;

  public ChatService(Plugin plugin, MessageFormatter messageFormatter, ConfigManager configManager,
      SettingsManager settingsManager, AdvancedBanHook advancedBanHook) {
    this.plugin = plugin;
    this.messageFormatter = messageFormatter;
    this.configManager = configManager;
    this.settingsManager = settingsManager;
    this.advancedBanHook = advancedBanHook;
    this.placeholderApiEnabled = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
  }

  public void handleChat(Player sender, Component originalMessage) {
    if (advancedBanHook.isMuted(sender)) {
      return;
    }
    String rawMessage = PlainTextComponentSerializer.plainText().serialize(originalMessage);

    if (rawMessage.isBlank()) {
      sender.sendMessage(Component.text("Сообщение не может быть пустым", NamedTextColor.RED));
      return;
    }

    if (rawMessage.startsWith(configManager.adminSymbol()) && sender.hasPermission(configManager.adminPermission())) {
      String content = rawMessage.substring(configManager.adminSymbol().length()).trim();
      if (content.isBlank()) {
        sender.sendMessage(Component.text("Сообщение не может быть пустым", NamedTextColor.RED));
        return;
      }
      handleAdminChat(sender, content);
      return;
    }

    boolean isGlobal = rawMessage.startsWith(configManager.globalSymbol());
    String content = isGlobal ? rawMessage.substring(1).trim() : rawMessage.trim();

    if (content.isBlank()) {
      sender.sendMessage(Component.text("Сообщение не может быть пустым", NamedTextColor.RED));
      return;
    }

    FormattedMessage formatted = messageFormatter.format(sender, content);
    Component finalMessage = buildFinalMessage(sender, formatted.message(), isGlobal);

    String plainContent = PlainTextComponentSerializer.plainText().serialize(formatted.message());

    broadcastMessage(sender, finalMessage, plainContent, isGlobal);
    playMentionSound(formatted.mentionedPlayers());
  }

  private void handleAdminChat(Player sender, String content) {
    FormattedMessage formatted = messageFormatter.format(sender, content);
    Component finalMessage = buildFinalMessage(sender, formatted.message(), configManager.adminFormat());

    Bukkit.getConsoleSender().sendMessage(finalMessage);

    String permission = configManager.adminPermission();
    for (Player recipient : Bukkit.getOnlinePlayers()) {
      if (recipient.hasPermission(permission)) {
        recipient.sendMessage(finalMessage);
      }
    }
  }

  private Component buildFinalMessage(Player sender, Component message, boolean isGlobal) {
    String format = isGlobal ? configManager.globalFormat() : configManager.localFormat();
    return buildFinalMessage(sender, message, format);
  }

  public Component buildFinalMessage(Player sender, Component message, String format) {
    if (placeholderApiEnabled) {
      format = PlaceholderAPI.setPlaceholders(sender, format);
    }

    format = format.replaceAll("%[^%\\s]+%", "");

    format = LegacyConverter.convert(format);

    TagResolver playerResolver = TagResolver.resolver("player", Tag.selfClosingInserting(buildSenderName(sender)));
    TagResolver messageResolver = TagResolver.resolver("message", Tag.selfClosingInserting(message));

    return miniMessage.deserialize(format, playerResolver, messageResolver);
  }

  private Component buildSenderName(Player sender) {
    Component hoverText = Component.text()
        .append(Component.text("Наиграно: ", NamedTextColor.GRAY))
        .append(Component.text(getPlayTimeHours(sender) + "ч", NamedTextColor.GRAY))
        .build();

    return Component.text(sender.getName())
        .hoverEvent(HoverEvent.showText(hoverText))
        .clickEvent(ClickEvent.suggestCommand("/msg " + sender.getName() + " "));
  }

  private long getPlayTimeHours(Player sender) {
    int ticks = sender.getStatistic(Statistic.PLAY_ONE_MINUTE);
    return ticks / (TICKS_PER_SECOND * SECONDS_PER_HOUR);
  }

  private void broadcastMessage(Player sender, Component message, String plainContent, boolean isGlobal) {
    Bukkit.getConsoleSender().sendMessage(message);

    for (Player recipient : Bukkit.getOnlinePlayers()) {
      ChatSettings recipientSettings = settingsManager.getSettings(recipient.getUniqueId());
      boolean canSee = isGlobal ? recipientSettings.showGlobalChat() : recipientSettings.showLocalChat();

      if (canSee && (isGlobal || isInRange(sender, recipient))) {
        recipient.sendMessage(message);
      }
    }

    if (isGlobal && Bukkit.getPluginManager().isPluginEnabled("DiscordSRV")) {
      DiscordSRV.getPlugin().processChatMessage(sender, plainContent, "global", false);
    }
  }

  private boolean isInRange(Player sender, Player recipient) {
    if (sender.equals(recipient)) {
      return true;
    }
    if (!sender.getWorld().equals(recipient.getWorld())) {
      return false;
    }
    return sender.getLocation().distance(recipient.getLocation()) <= configManager.localRadius();
  }

  private void playMentionSound(List<UUID> mentionedPlayers) {
    if (mentionedPlayers.isEmpty()) {
      return;
    }
    Bukkit.getScheduler().runTask(plugin, () -> {
      for (UUID targetId : mentionedPlayers) {
        Player target = Bukkit.getPlayer(targetId);
        if (target != null) {
          ChatSettings settings = settingsManager.getSettings(target.getUniqueId());
          if (settings.playerMentionSound()) {
            target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, MENTION_SOUND_VOLUME,
                MENTION_SOUND_PITCH);
          }
        }
      }
    });
  }
}
