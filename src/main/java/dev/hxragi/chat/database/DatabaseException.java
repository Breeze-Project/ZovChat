package dev.hxragi.chat.database;

public class DatabaseException extends RuntimeException {
  public DatabaseException(String message, Throwable cause) {
    super(message, cause);
  }
}
