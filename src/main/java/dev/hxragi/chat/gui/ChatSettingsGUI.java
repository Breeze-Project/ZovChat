package dev.hxragi.chat.gui;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.hxragi.chat.dto.ChatSettings;
import dev.hxragi.chat.settings.SettingsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class ChatSettingsGUI {
  private static final String TITLE = "Настройки чата";

  public static final int INVENTORY_SIZE = 9;
  public static final int SLOT_ALLOW_PM = 0;
  public static final int SLOT_MENTION_SOUND = 1;
  public static final int SLOT_LOCAL_CHAT = 2;
  public static final int SLOT_GLOBAL_CHAT = 3;
  public static final int SLOT_CLOSE = 8;

  public static void open(Player player, SettingsManager settingsManager) {
    Inventory inv = Bukkit.createInventory(new SettingsInventoryHolder(), INVENTORY_SIZE, Component.text(TITLE));
    ChatSettings settings = settingsManager.getSettings(player.getUniqueId());

    inv.setItem(SLOT_ALLOW_PM,
        createToggleItem(Material.NAME_TAG, "Личные сообщения", settings.allowPrivateMessages()));
    inv.setItem(SLOT_MENTION_SOUND,
        createToggleItem(Material.NOTE_BLOCK, "Звук упоминания", settings.playerMentionSound()));
    inv.setItem(SLOT_LOCAL_CHAT, createToggleItem(Material.COMPASS, "Локальный чат", settings.showLocalChat()));
    inv.setItem(SLOT_GLOBAL_CHAT, createToggleItem(Material.ENDER_PEARL, "Глобальный чат", settings.showGlobalChat()));

    inv.setItem(SLOT_CLOSE, createCloseItem());

    player.openInventory(inv);
  }

  private static ItemStack createToggleItem(Material material, String name, boolean enabled) {
    ItemStack item = new ItemStack(material);
    ItemMeta meta = item.getItemMeta();

    NamedTextColor statusColor = enabled ? NamedTextColor.GREEN : NamedTextColor.RED;
    meta.displayName(Component.text(name, statusColor).decoration(TextDecoration.ITALIC, false));

    Component statusText = Component.text("Статус: ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
        .append(Component.text(enabled ? "ВКЛ" : "ВЫКЛ", statusColor).decoration(TextDecoration.ITALIC, false));
    Component actionText = Component.text("Нажмите чтобы переключить", NamedTextColor.YELLOW)
        .decoration(TextDecoration.ITALIC, false);

    meta.lore(List.of(statusText, actionText));
    item.setItemMeta(meta);
    return item;
  }

  private static ItemStack createCloseItem() {
    ItemStack item = new ItemStack(Material.BARRIER);
    ItemMeta meta = item.getItemMeta();
    meta.displayName(Component.text("Закрыть", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
    item.setItemMeta(meta);
    return item;
  }
}
