package dev.hxragi.chat.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import dev.hxragi.chat.config.ChatSettings;
import dev.hxragi.chat.settings.SettingsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ReplyService {
  private final Map<UUID, UUID> lastSender = new ConcurrentHashMap<>();
  private final SettingsManager settingsManager;

  public ReplyService(SettingsManager settingsManager) {
    this.settingsManager = settingsManager;
  }

  public void setLastSender(UUID recipient, UUID sender) {
    lastSender.put(recipient, sender);
  }

  public Optional<UUID> getLastSender(UUID recipient) {
    return Optional.ofNullable(lastSender.get(recipient));
  }

  public void sendPrivateMessage(Player sender, Player target, String message) {
    ChatSettings targetSettings = settingsManager.getSettings(target.getUniqueId());

    if (!targetSettings.allowPrivateMessages()) {
      sender.sendMessage(Component.text("Игрок " + target.getName() + " отключил личные сообщения", NamedTextColor.RED));
      return;
    }

    Component senderView = Component.text()
        .append(Component.text("[Вы -> ", NamedTextColor.GRAY))
        .append(Component.text(target.getName(), NamedTextColor.GOLD))
        .append(Component.text("]: ", NamedTextColor.GRAY))
        .append(Component.text(message, NamedTextColor.WHITE))
        .build();

    Component targetView = Component.text()
      .append(Component.text("[", NamedTextColor.GRAY))
      .append(Component.text(sender.getName(), NamedTextColor.GOLD))
      .append(Component.text(" -> Вам]: ", NamedTextColor.GRAY))
      .append(Component.text(message, NamedTextColor.WHITE))
      .build();

    sender.sendMessage(senderView);
    target.sendMessage(targetView);

    lastSender.put(target.getUniqueId(), sender.getUniqueId());
    lastSender.put(sender.getUniqueId(), target.getUniqueId());

    target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
  }
}
