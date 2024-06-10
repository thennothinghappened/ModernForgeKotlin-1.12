#
# This is our ProGuard configuration, if you're not familiar with ProGuard feel free to just ignore this, just don't
# use the `:reobfReleaseJarJar` task in that case.
#

# We don't care about helper annotations.
-dontwarn org.jetbrains.annotations.**
-dontwarn javax.annotation.**

# Keep stuff that's found by Forge magic(TM).
-keep class mods.orca.examplemod.ExampleMod { *; }
-keep class mods.orca.examplemod.registry.RegistryHandler { *; }
-keep class mods.orca.examplemod.proxy.ClientProxy { *; }
-keep class mods.orca.examplemod.proxy.DedicatedProxy { *; }

# TODO: Supressing notes about duplicate classes, doesn't seem to cause issues but feels wrong.
-dontnote kotlin.**

# We don't care about obfuscation! We just want to minify.
-dontobfuscate
