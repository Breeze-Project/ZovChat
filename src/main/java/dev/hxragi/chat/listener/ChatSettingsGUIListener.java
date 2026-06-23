package dev.hxragi.chat.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import dev.hxragi.chat.config.ChatSettings;
import dev.hxragi.chat.gui.ChatSettingsGUI;
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
    if (!ChatSettingsGUI.isSettingsInventory(event.getView())) {
      return;
    }

    event.setCancelled(true);
    int slot = event.getSlot();
    if (slot == 8) {
      player.closeInventory();
      return;
    }
    if (slot < 0 || slot > 3)
      return;

    ChatSettings current = settingsManager.getSettings(player.getUniqueId());
    ChatSettings updated = switch (slot) {
      case 0 -> new ChatSettings(current.playerId(), !current.allowPrivateMessages(), current.playerMentionSound(),
          current.showLocalChat(), current.showGlobalChat());
      case 1 -> new ChatSettings(current.playerId(), current.allowPrivateMessages(), !current.playerMentionSound(),
          current.showLocalChat(), current.showGlobalChat());
      case 2 -> new ChatSettings(current.playerId(), current.allowPrivateMessages(), current.playerMentionSound(),
          !current.showLocalChat(), current.showGlobalChat());
      case 3 -> new ChatSettings(current.playerId(), current.allowPrivateMessages(), current.playerMentionSound(),
          current.showLocalChat(), !current.showGlobalChat());
      default -> current;
    };

    settingsManager.updateSettings(updated);
    ChatSettingsGUI.open(player, settingsManager);
  }
}
