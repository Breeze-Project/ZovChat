package dev.hxragi.chat.dto;

import java.util.List;

import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;

public record FormattedMessage(Component message, List<Player> mentionedPlayers) {
}
