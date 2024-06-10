import net.minecraftforge.gradle.userdev.UserDevExtension
import net.minecraftforge.gradle.userdev.tasks.JarJar
import proguard.gradle.ProGuardTask
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Pretty simply the name of you as the author. This is used for the package group and manifest, so make sure
 * it only includes English alphabetical characters.
 */
val author = "orca"

/**
 * The mod's identifying name. Make sure this matches what you've chosen as your Mod ID in the `@Mod` class.
 */
val modIdName = "examplemod"

group = "mods.$author.$modIdName"
version = "0.1.0"

/**
 * We're using ProGuard to minify our binary. We include the Kotlin Standard Library inside our release Jar, so
 * minification is pretty important to make sure the binary size doesn't get too big. If you don't intend to use
 * minification, then you can safely remove references to ProGuard and use the regular `:reobfJarJar` task to build
 * your final binary.
 *
 * I spent quite a bit of time messing around with ProGuard to get it happy with Forge - there's not many examples
 * out there for using it outside Android development in the first place, so narrowing it to KTS + Forge kinda put
 * me on my own on this. If there's a better way to do this, feel free to PR this repo with it!
 */
buildscript {
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.5.+")
    }
}

plugins {
    id("net.minecraftforge.gradle")
    kotlin("jvm")
}

repositories {

    mavenCentral()

    /**
     * We include CurseMaven by default, as you'll likely want to use them if this mod intends to integrate with other
     * mods. See https://cursemaven.com/ for details.
     */
    exclusiveContent {
        forRepository {
            maven("https://cursemaven.com")
        }
        filter {
            includeGroup("curse.maven")
        }
    }

}

/**
 * Configuration for libraries to be included into the fat jar.
 * We use this for the Kotlin Standard Library as it is not available in Minecraft's classpath.
 *
 * If you have any other dependencies that need to be included directly - i.e., that won't be supplied by any other
 * mods, then use `shadow("package:artifact:version")` and they'll be included in your Jar. Use this option sparingly
 * as it will considerably increase filesize.
 */
val shadow: Configuration by configurations.creating {
    exclude("org.jetbrains", "annotations")
}

// Enable creating a fat jar.
jarJar.enable()

/**
 * In this block you can specify mod dependencies using `implementation("package:artifact:version")`.
 *
 */
dependencies {

    minecraft("net.minecraftforge:forge:1.12.2-14.23.5.2860")

    // Shadow dependencies for Kotlin stdlib to be included in the fat jar
    shadow("org.jetbrains.kotlin:kotlin-stdlib:${kotlin.coreLibrariesVersion}")
    shadow("org.jetbrains.kotlin:kotlin-stdlib-common:${kotlin.coreLibrariesVersion}")

    // You can add dependent mods
    // implementation("curse.maven:industrialcraft-242638:3078604")

}

/**
 * Configuration for Kotlin compilation.
 */
kotlin {

    compilerOptions {
        // Tells the compiler to generate interop default methods for interfaces. You can disable this if you
        // don't care about interop with Java as much, and it may reduce output size slightly.
        freeCompilerArgs.add("-Xjvm-default=all")
    }

    // 1.12.2 uses Java 8.
    jvmToolchain(8)

}

/**
 * This is the configuration block for setting up your run configs. To be honest I haven't had any luck trying to make
 * my own configs - Intellij didn't pick them up correctly, and their Gradle tasks were completely messed up. I'd
 * suggest just leaving this be apart from changing logging settings.
 */
configure<UserDevExtension> {

    /**
     * These are the latest stable mappings for 1.12.2. Do note that there's some changes between these and the ones
     * used in some popular open source mods I've seen for the version. I've fixed up a few examples of these and
     * as long as you have a little patience with it, it isn't a big deal to convert over names.
     *
     * The most common one that pops up is `setUnlocalizedName`, which changed to the more accurate `setTranslationKey`
     * in these mappings.
     */
    mappings("stable",  "39-1.12")

    runs {

        create("client") {

            workingDirectory(project.file("run"))

            // Recommended logging data for a userdev environment
            property("forge.logging.markers", "SCAN,REGISTRIES,REGISTRYDUMP")

            // Recommended logging level for the console
            property("forge.logging.console.level", "debug")

        }

        create("server") {

            // Recommended logging data for a userdev environment
            property("forge.logging.markers", "SCAN,REGISTRIES,REGISTRYDUMP")

            // Recommended logging level for the console
            property("forge.logging.console.level", "debug")

        }

    }
}

tasks {

    /**
     * Base filename of your output jar file.
     */
    val outputFileName = "mymodname_1.12.2"

    /**
     * Configuration for the task that produces your Jar.
     */
    withType<Jar> {

        // Set the base filename as we described earlier.
        archiveBaseName.set(outputFileName)

        /**
         * This is your manifest file, which contains information identifying the Jar. It doesn't seem to be of
         * massive importance from what I've seen, but regardless I'd suggest having a look at this briefly to make
         * sure it is correct.
         */
        manifest {
            attributes(
                "Specification-Title" to modIdName,
                "Specification-Vendor" to author,
                "Specification-Version" to "1",
                "Implementation-Title" to project.name,
                "Implementation-Version" to version,
                "Implementation-Vendor" to author,
                "Implementation-Timestamp" to LocalDateTime.now()
                    .atOffset(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"))
            )
        }

    }

    /**
     * Include shadow dependencies in fat jar.
     */
    jarJar.configure {
        from(provider { shadow.map(::zipTree).toTypedArray() })
    }

    /**
     * Task that runs ProGuard to minify (not obfuscate!) the fat jar. We use this since we're including the
     * Kotlin stdlib, which otherwise increases size significantly.
     */
    val proguardReleaseJarJar = register<ProGuardTask>("proguardReleaseJarJar") {

        dependsOn("jarJar")

        /**
         * Relative path to your proguard configuration file.
         */
        configuration("src/proguard-rules.pro")

        named<JarJar>("jarJar").let { jarTask ->

            injars(jarTask.flatMap { it.archiveFile })
            outjars(jarTask.flatMap {
                layout.buildDirectory.file("libs/${it.archiveFile.get().asFile.nameWithoutExtension}-minified.jar")
            })

        }

        // Inform Proguard of the libraries we have!
        libraryjars("${System.getProperty("java.home")}/lib/rt.jar")
        libraryjars(configurations.runtimeClasspath)

    }

    /**
     * Task that produces the minified release fat Jar output. Note that this output is not yet reobfuscated, and
     * the task `:reobfReleaseJarJar` needs to be run to get the output compatible with regular MC.
     */
    register<JarJar>("releaseJarJar") {

        archiveBaseName.set("$outputFileName-release")

        dependsOn(proguardReleaseJarJar)

        from(proguardReleaseJarJar.map { zipTree(it.outputs.files.asPath) })
        configuration(project.configurations.jarJar.get())

    }

}

/**
 * Set up our output to include the resources directory in the Jar and for the development environment.
 * Without this line, your mod won't load resources correctly and everything will be missing textures and whatnot.
 */
sourceSets.all {
    output.setResourcesDir(output.classesDirs.files.iterator().next())
}
