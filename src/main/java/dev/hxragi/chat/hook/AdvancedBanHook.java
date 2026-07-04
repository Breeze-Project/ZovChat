package dev.hxragi.chat.hook;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.manager.UUIDManager;
import me.leoko.advancedban.utils.Punishment;

public class AdvancedBanHook {
  private final boolean enabled;

  public AdvancedBanHook() {
    this.enabled = Bukkit.getPluginManager().getPlugin("AdvancedBan") != null;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public Optional<Punishment> getMute(Player player) {
    if (!enabled) {
      return Optional.empty();
    }
    String uuid = UUIDManager.get().getUUID(player.getName());
    if (uuid == null) {
      return Optional.empty();
    }
    Punishment mute = PunishmentManager.get().getMute(uuid);
    return Optional.ofNullable(mute);
  }

  public boolean isMuted(Player player) {
    return getMute(player).isPresent();
  }
}
