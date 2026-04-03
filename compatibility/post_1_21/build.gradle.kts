dependencies {
    compileOnly("org.spigotmc:spigot-api:26.1-R0.1-SNAPSHOT")
    implementation(project(":compatibility"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}