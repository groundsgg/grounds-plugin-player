plugins {
    base
    java
}

group = "gg.grounds"
version = "1.0.0-SNAPSHOT"

allprojects {
    repositories {
        maven {
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    java {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }
}
