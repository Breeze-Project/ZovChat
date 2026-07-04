plugins {
    java
}

repositories {
  mavenCentral()
  maven {
    name = "papermc"
    url = uri("https://repo.papermc.io/repository/maven-public/")
  }
  maven {
    url = uri("https://repo.extendedclip.com/releases/")
  }
  maven {
    url = uri("https://nexus.scarsz.me/content/groups/public/")
  }
  maven {
    url = uri("https://jitpack.io")
  }
}

dependencies {
  compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
  compileOnly("com.zaxxer:HikariCP:7.1.0")
  compileOnly("org.xerial:sqlite-jdbc:3.53.2.0")
  compileOnly("me.clip:placeholderapi:2.12.2")
  compileOnly("com.discordsrv:discordsrv:1.28.0")
  compileOnly("com.github.DevLeoko.AdvancedBan:AdvancedBan-Bukkit:v2.3.0")
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
