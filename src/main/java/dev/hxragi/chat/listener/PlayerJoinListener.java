package dev.hxragi.chat.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import dev.hxragi.chat.config.ConfigManager;
import dev.hxragi.chat.settings.SettingsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class PlayerJoinListener implements Listener {
  private final SettingsManager settingsManager;
  private final ConfigManager configManager;
  private final MiniMessage miniMessage = MiniMessage.builder().strict(false).build();

  public PlayerJoinListener(SettingsManager settingsManager, ConfigManager configManager) {
    this.settingsManager = settingsManager;
    this.configManager = configManager;
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    settingsManager.preloadSettings(event.getPlayer().getUniqueId());

    TagResolver playerResolver = TagResolver.resolver("player", Tag.inserting(Component.text(event.getPlayer().getName())));
    Component joinMessage = miniMessage.deserialize(configManager.joinFormat(), playerResolver);

    event.joinMessage(joinMessage);
  }
}
