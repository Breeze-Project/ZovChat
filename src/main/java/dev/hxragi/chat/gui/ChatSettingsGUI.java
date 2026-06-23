package dev.hxragi.chat.gui;

import java.net.http.WebSocket.Listener;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.hxragi.chat.config.ChatSettings;
import dev.hxragi.chat.settings.SettingsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class ChatSettingsGUI {
  private static final String TITLE = "Настройки чата";

  public static void open(Player player, SettingsManager settingsManager) {
    Inventory inv = Bukkit.createInventory(null, 9, Component.text(TITLE));
    ChatSettings settings = settingsManager.getSettings(player.getUniqueId());

    inv.setItem(0, createToggleItem(Material.NAME_TAG, "Личные сообщения", settings.allowPrivateMessages()));
    inv.setItem(1, createToggleItem(Material.NOTE_BLOCK, "Звук упоминания", settings.playerMentionSound()));
    inv.setItem(2, createToggleItem(Material.COMPASS, "Локальный чат", settings.showLocalChat()));
    inv.setItem(3, createToggleItem(Material.ENDER_PEARL, "Глобальный чат", settings.showGlobalChat()));

    inv.setItem(8, createCloseItem());

    player.openInventory(inv);
  }

  private static ItemStack createToggleItem(Material material, String name, boolean enabled) {
    ItemStack item = new ItemStack(enabled ? Material.LIME_DYE : Material.GRAY_DYE);
    ItemMeta meta = item.getItemMeta();
    meta.displayName(Component.text(name, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));

    Component statusText = Component.text("Статус: ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
        .append(enabled ? Component.text("ВКЛ", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)
            : Component.text("ВЫКЛ", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
    Component actionText = Component.text("Нажмите чтобы переключить", NamedTextColor.YELLOW)
        .decoration(TextDecoration.ITALIC, false);

    meta.lore(List.of(statusText, actionText));
    item.setItemMeta(meta);
    return item;
  }

  public static ItemStack createCloseItem() {
    ItemStack item = new ItemStack(Material.BARRIER);
    ItemMeta meta = item.getItemMeta();
    meta.displayName(Component.text("Закрыть", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
    item.setItemMeta(meta);
    return item;
  }

  public static boolean isSettingsInventory(InventoryView view) {
    return view != null && TITLE.equals(PlainTextComponentSerializer.plainText().serialize(view.title()));
  }
}
