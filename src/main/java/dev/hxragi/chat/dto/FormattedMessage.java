package dev.hxragi.chat.dto;

import java.util.List;
import java.util.UUID;

import net.kyori.adventure.text.Component;

public record FormattedMessage(Component message, List<UUID> mentionedPlayers) {
}
