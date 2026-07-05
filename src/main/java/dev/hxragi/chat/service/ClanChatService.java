package dev.hxragi.chat.service;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.breeze.CustomClanBreeze.CustomClanBreeze;
import com.breeze.CustomClanBreeze.managers.ClanManager;
import com.breeze.CustomClanBreeze.models.Clan;

import dev.hxragi.chat.config.ConfigManager;
import dev.hxragi.chat.dto.FormattedMessage;
import dev.hxragi.chat.hook.AdvancedBanHook;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ClanChatService {
  private final ConfigManager configManager;
  private final MessageFormatter messageFormatter;
  private final ChatService chatService;
  private final AdvancedBanHook advancedBanHook;

  public ClanChatService(ConfigManager configManager, MessageFormatter messageFormatter, ChatService chatService,
      AdvancedBanHook advancedBanHook) {
    this.configManager = configManager;
    this.messageFormatter = messageFormatter;
    this.chatService = chatService;
    this.advancedBanHook = advancedBanHook;
  }

  public void sendClanMessage(Player sender, String message) {
    if (advancedBanHook.isMuted(sender)) {
      sender.sendMessage(Component.text("Вы не можете отправлять сообщения будучи замученным", NamedTextColor.RED));
      return;
    }

    Plugin clanPlugin = Bukkit.getPluginManager().getPlugin("CustomClanBreeze");
    if (clanPlugin == null) {
      return;
    }

    ClanManager clanManager = ((CustomClanBreeze)clanPlugin).getClanManager();
    Clan clan = clanManager.getClanOf(sender);

    if (clan == null) {
      sender.sendMessage(Component.text("Вы не состоите в клане", NamedTextColor.RED));
      return;
    }

    if (message.isBlank()) {
      sender.sendMessage(Component.text("Сообщение не может быть пустым", NamedTextColor.RED));
      return;
    }

    FormattedMessage formatted = messageFormatter.format(sender, message);
    Component finalMessage = chatService.buildFinalMessage(sender, formatted.message(), configManager.clanChatFormat());

    Bukkit.getConsoleSender().sendMessage(finalMessage);

    clan.getMembers().stream()
        .map(Bukkit::getPlayer)
        .filter(player -> player != null && player.isOnline())
        .forEach(player -> player.sendMessage(finalMessage));
  }
}
