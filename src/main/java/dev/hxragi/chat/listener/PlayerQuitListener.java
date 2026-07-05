package dev.hxragi.chat.listener;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import dev.hxragi.chat.config.ConfigManager;
import dev.hxragi.chat.service.ReplyService;
import dev.hxragi.chat.settings.SettingsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class PlayerQuitListener implements Listener {
  private final SettingsManager settingsManager;
  private final ReplyService replyService;
  private final ConfigManager configManager;
  private final MiniMessage miniMessage = MiniMessage.builder().strict(false).build();

  public PlayerQuitListener(SettingsManager settingsManager, ReplyService replyService, ConfigManager configManager) {
    this.settingsManager = settingsManager;
    this.replyService = replyService;
    this.configManager = configManager;
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    UUID playerId = event.getPlayer().getUniqueId();
    settingsManager.removeFromCache(playerId);
    replyService.clearLastSender(playerId);

    TagResolver playerResolver = TagResolver.resolver("player", Tag.inserting(Component.text(event.getPlayer().getName())));
    Component quitMessage = miniMessage.deserialize(configManager.quitFormat(), playerResolver);

    event.quitMessage(quitMessage);
  }
}
