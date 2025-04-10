import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val buildId = "com.mediusecho"
val buildVersion = "4.7.1"

plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

sourceSets {
    main {
        java {
            srcDir("ParticleHats/src")
        }
        resources {
            srcDirs("ParticleHats/src")
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
    mavenLocal()

    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://maven.citizensnpcs.co/repo")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")
    compileOnly("com.github.LeonMangler:SuperVanish:6.2.18-3") {
        exclude("*", "*")
    }
    compileOnly("com.github.mbax:VanishNoPacket:3.22")
    compileOnly("net.citizensnpcs:citizens-main:2.0.35-SNAPSHOT") {
        exclude("*", "*")
    }
    compileOnly("org.black_ixx:playerpoints:3.3.0-SNAPSHOT")
    compileOnly("com.github.Realizedd:TokenManager:3.2.4") {
        exclude("*", "*")
    }
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude("org.bukkit")
    }

    implementation("org.bstats:bstats-bukkit:2.2.1")
    implementation("com.zaxxer:HikariCP:4.0.3")
    implementation("org.jetbrains:annotations:16.0.2")
}

tasks.withType<ShadowJar> {
    archiveBaseName.set("ParticleHats")
    archiveClassifier.set("")
    archiveVersion.set("")
    minimize()
    relocate("org.bstats", "com.mediusecho.particlehats.metrics")
}