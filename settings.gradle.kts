
rootProject.name = "ModernForgeKotlin-1.12"

pluginManagement {

    /**
     * Version of ProGuard to use. There's a weird bug I and others ran into with versioning for it, hense the
     * workaround below. If this gets fixed later, feel free to PR the template repo with that.
     */
    val proguardVersion = "7.5.+"

    repositories {
        gradlePluginPortal()
        maven("https://maven.minecraftforge.net/")
    }

    // Getting Proguard to work: https://github.com/Guardsquare/proguard/issues/225#issuecomment-1195015431
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.guardsquare.proguard") {
                useModule("com.guardsquare:proguard-gradle:$proguardVersion")
            }
        }
    }

    /**
     * If you need to update your plugins, do so by changing their versions here. ForgeGradle and Kotlin are the most
     * obvious things you might want to update.
     */
    plugins {
        id("net.minecraftforge.gradle") version "6.0.+"
        id("com.guardsquare.proguard") version proguardVersion
        kotlin("jvm") version "2.0.0"
    }

}
