package dev.hxragi.chat.database;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.plugin.Plugin;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import dev.hxragi.chat.dto.ChatSettings;
import dev.hxragi.chat.config.ConfigManager;

public class DatabaseManager {
  private final Plugin plugin;

  private final HikariDataSource dataSource;

  public DatabaseManager(Plugin plugin, ConfigManager configManager) {
    this.plugin = plugin;
    File databaseFile = new File(plugin.getDataFolder(), configManager.databaseUrl());

    HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setJdbcUrl("jdbc:sqlite:" + databaseFile.getAbsolutePath());
    hikariConfig.setMaximumPoolSize(configManager.poolSize());

    hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
    hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
    hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

    this.dataSource = new HikariDataSource(hikariConfig);

    createTable();
  }

  private void createTable() {
    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("""
            CREATE TABLE IF NOT EXISTS chat_settings (
                       player_uuid TEXT PRIMARY KEY,
                       allow_pm INTEGER DEFAULT 1,
                       play_mention_sound INTEGER DEFAULT 1,
                       show_local_chat INTEGER DEFAULT 1,
                       show_global_chat INTEGER DEFAULT 1
                   )
            """)) {
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to initialize database schema", e);
    }
  }

  public Optional<ChatSettings> loadSettings(UUID uuid) {
    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM chat_settings WHERE player_uuid = ?")) {
      stmt.setString(1, uuid.toString());
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return Optional.of(new ChatSettings(
              uuid,
              rs.getInt("allow_pm") == 1,
              rs.getInt("play_mention_sound") == 1,
              rs.getInt("show_local_chat") == 1,
              rs.getInt("show_global_chat") == 1));
        }
      }
    } catch (SQLException e) {
      plugin.getLogger().severe("Failed to load settings for " + uuid + ": " + e.getMessage());
    }
    return Optional.empty();
  }

  public void saveSettings(ChatSettings settings) {
    try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("""
            INSERT OR REPLACE INTO chat_settings
            (player_uuid, allow_pm, play_mention_sound, show_local_chat, show_global_chat)
            VALUES (?, ?, ?, ?, ?)
            """)) {
      stmt.setString(1, settings.playerId().toString());
      stmt.setInt(2, settings.allowPrivateMessages() ? 1 : 0);
      stmt.setInt(3, settings.playerMentionSound() ? 1 : 0);
      stmt.setInt(4, settings.showLocalChat() ? 1 : 0);
      stmt.setInt(5, settings.showGlobalChat() ? 1 : 0);
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void close() {
    if (!dataSource.isClosed()) {
      dataSource.close();
    }
  }
}
