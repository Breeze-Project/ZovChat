plugins {
    java
}

repositories {
  mavenCentral()
  maven {
    name = "papermc"
    url = uri("https://repo.papermc.io/repository/maven-public/")
  }
}

dependencies {
  compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
  compileOnly("com.zaxxer:HikariCP:7.1.0")
  compileOnly("org.xerial:sqlite-jdbc:3.53.2.0")
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
