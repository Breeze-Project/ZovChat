package dev.hxragi.chat.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import dev.hxragi.chat.dto.ChatSettings;
import dev.hxragi.chat.hook.AdvancedBanHook;
import dev.hxragi.chat.settings.SettingsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ReplyService {
  private static final float PM_SOUND_VOLUME = 1.0f;
  private static final float PM_SOUND_PITCH = 1.5f;

  private final Map<UUID, UUID> lastSender = new ConcurrentHashMap<>();
  private final SettingsManager settingsManager;
  private final AdvancedBanHook advancedBanHook;

  public ReplyService(SettingsManager settingsManager, AdvancedBanHook advancedBanHook) {
    this.settingsManager = settingsManager;
    this.advancedBanHook = advancedBanHook;
  }

  public void setLastSender(UUID recipient, UUID sender) {
    lastSender.put(recipient, sender);
  }

  public Optional<UUID> getLastSender(UUID recipient) {
    return Optional.ofNullable(lastSender.get(recipient));
  }

  public void clearLastSender(UUID playerUuid) {
    lastSender.remove(playerUuid);
  }

  public void sendPrivateMessage(Player sender, Player target, String message) {
    if (advancedBanHook.isMuted(sender)) {
      return;
    }

    ChatSettings targetSettings = settingsManager.getSettings(target.getUniqueId());

    if (!targetSettings.allowPrivateMessages()) {
      sender
          .sendMessage(Component.text("Игрок " + target.getName() + " отключил личные сообщения", NamedTextColor.RED));
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

    target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, PM_SOUND_VOLUME, PM_SOUND_PITCH);
  }
}
