package dev.hxragi.chat.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import dev.hxragi.chat.dto.ChatSettings;
import dev.hxragi.chat.gui.ChatSettingsGUI;
import dev.hxragi.chat.gui.SettingsInventoryHolder;
import dev.hxragi.chat.settings.SettingsManager;

public class ChatSettingsGUIListener implements Listener {
  private final SettingsManager settingsManager;

  public ChatSettingsGUIListener(SettingsManager settingsManager) {
    this.settingsManager = settingsManager;
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player player)) {
      return;
    }
    if (!(event.getInventory().getHolder() instanceof SettingsInventoryHolder)) {
      return;
    }

    event.setCancelled(true);
    int slot = event.getSlot();

    if (slot == ChatSettingsGUI.SLOT_CLOSE) {
      player.closeInventory();
      return;
    }
    if (slot < 0 || slot > ChatSettingsGUI.SLOT_GLOBAL_CHAT) {
      return;
    }

    ChatSettings current = settingsManager.getSettings(player.getUniqueId());
    ChatSettings updated = switch (slot) {
      case ChatSettingsGUI.SLOT_ALLOW_PM -> current.withAllowPrivateMessages(!current.allowPrivateMessages());
      case ChatSettingsGUI.SLOT_MENTION_SOUND -> current.withPlayerMentionSound(!current.playerMentionSound());
      case ChatSettingsGUI.SLOT_LOCAL_CHAT -> current.withShowLocalChat(!current.showLocalChat());
      case ChatSettingsGUI.SLOT_GLOBAL_CHAT -> current.withShowGlobalChat(!current.showGlobalChat());
      default -> current;
    };

    settingsManager.updateSettings(updated);
    ChatSettingsGUI.open(player, settingsManager);
  }
}
