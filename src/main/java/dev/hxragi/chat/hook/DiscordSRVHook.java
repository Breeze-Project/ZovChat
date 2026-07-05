package dev.hxragi.chat.hook;

import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import dev.hxragi.chat.config.ConfigManager;
import dev.hxragi.chat.dto.ChatSettings;
import dev.hxragi.chat.settings.SettingsManager;
import dev.hxragi.chat.util.LegacyConverter;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePostProcessEvent;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.TextReplacementConfig;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class DiscordSRVHook {
  private final Plugin plugin;
  private final SettingsManager settingsManager;
  private final ConfigManager configManager;
  private final MiniMessage miniMessage = MiniMessage.builder().strict(false).build();
  private final String mentionColor;

  public DiscordSRVHook(Plugin plugin, SettingsManager settingsManager, ConfigManager configManager) {
    this.plugin = plugin;
    this.settingsManager = settingsManager;
    this.configManager = configManager;
    this.mentionColor = LegacyConverter.convert(configManager.mentionColor());

    DiscordSRV.api.subscribe(this);
  }

  public void unsubscribe() {
    DiscordSRV.api.unsubscribe(this);
  }

  @Subscribe
  public void onDiscordMessagePostProcess(DiscordGuildMessagePostProcessEvent event) {
    String rawContent = event.getMessage().getContentRaw();
    if (rawContent == null || rawContent.isBlank()) {
      return;
    }

    String lowerRawContent = rawContent.toLowerCase();

    Component discordMsg = event.getMinecraftMessage();
    boolean modified = false;

    for (Player target : Bukkit.getOnlinePlayers()) {
      String targetName = target.getName();

      if (!lowerRawContent.contains(targetName.toLowerCase())) {
        continue;
      }

      Pattern pattern = Pattern.compile("(?i)(?<![a-zA-Z0-9_])" + Pattern.quote(targetName) + "(?![a-zA-Z0-9_])");

      if (pattern.matcher(rawContent).find()) {
        modified = true;

        ChatSettings settings = settingsManager.getSettings(target.getUniqueId());
        if (settings.playerMentionSound()) {
          Bukkit.getScheduler().runTask(plugin, () -> {
            target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
          });
        }

        String mentionColor = LegacyConverter.convert(configManager.mentionColor());

        net.kyori.adventure.text.Component paperMention = miniMessage.deserialize(mentionColor + target.getName())
            .hoverEvent(net.kyori.adventure.text.event.HoverEvent
                .showText(net.kyori.adventure.text.Component.text("Онлайн: " + target.getName())))
            .clickEvent(net.kyori.adventure.text.event.ClickEvent.suggestCommand("/msg " + target.getName() + " "));

        String json = net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().serialize(paperMention);
        Component discordMention = GsonComponentSerializer.gson().deserialize(json);

        TextReplacementConfig replacement = TextReplacementConfig.builder()
            .match(pattern)
            .replacement(discordMention)
            .build();

        discordMsg = discordMsg.replaceText(replacement);
      }
    }

    if (modified) {
      event.setMinecraftMessage(discordMsg);
    }
  }
}
