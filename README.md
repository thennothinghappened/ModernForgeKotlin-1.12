# Modern Kotlin + ForgeGradle mod template for 1.12.2

I've recently re-embarked on modding for 1.12.2 as of a few days ago, and in that time I put a bit of work into getting
1.12.2 to play nice with a modern toolchain. There's been quite a few ups and downs along the way, but I thought putting
together a base Gradle project from what I've worked out might save some people a bit of time and frustration, so here
we go!

## What is this?

As the title says, this is a ForgeGradle project using Kotlin. I'm a big fan of Kotlin, so that was an important part of
this for me, but this template is totally suitable for a regular fully-Java mod, and I've used it on multiple different
testing projects without issue.

## How do I use this?

If you'd like to use this as a template, either **fork** this repository, or clone it and copy its contents to your own
one. The former will make sure that you're aware of any upstream changes, but will probably create clashes, and means
you can't make your repository private. I'd recommend the latter option for those reasons.

### Setting up

To set this up for your own mod, change the **Root project name** (`rootProject.name` in `settings.gradle.kts`) to your
project's name, change the `group` in `build.gradle.kts` to something appropriate (e.g. `mods.yourname.nameofmod`), and
let Gradle set up the project. I use [Intellij IDEA Community](https://www.jetbrains.com/idea/download/) as my IDE of
choice, especially as it has the best Kotlin integration, but overall this is up to you.

To run your test mod, first run the `:gen<IDE name>Runs` Gradle task for your IDE, and then you should get a `runClient`
task in your IDE. I was _not_ able to get the Gradle `:runClient` task to cooperate and I'm not entirely sure why. It
_may_ be related to issues with `mergetool`, but I've not had much luck in fixing that. For now, stick to your IDE's
provided task.

When you're ready to try your mod outside your test environment, run the `:reobfJarJar` task. You should find a Jar file
called `mymodname_1.12.2-version-all.jar` in the `build/libs` directory which can be run under a regular Forge install
for 1.12.2!

Also note the `:reobfReleaseJarJar` task - this task requires some setup to function correctly, and I'd recommend it if
you're concerned about your binary size. This task uses ProGuard to minify the output binary, and ProGuard is a bit
finicky. If you're interested in minifying the output, read further down on the section about this.

## Notable parts

These are the important bits to be aware of when using this as a template.

### Modern ForgeGradle

This template uses ForgeGradle `6.0.+` - i.e., whatever the newest `6.0.x` release is. When a new ForgeGradle version
comes around, feel free to give upgrading a shot. If all works out, I'd encourage you to PR that back here so it stays
up to date :)

### Kotlin

As I've already stated, I like Kotlin quite a bit, so this template is set up and ready to use with Kotlin. There
**are** exceptions to be aware of in this however, as Forge is picky when it comes to some things:

#### 1. Your `@Mod`-annotated class must be in Java.

I've not really tested this that far to be honest, but I'm fine with playing it safe here.

#### 2. Event subscribers (`@SubscribeEvent`) must be annotated with `@JvmStatic`, or Forge will not see them.

Kotlin by default doesn't generate `static` accessors for Java to see. The `@JvmStatic` annotation tells it to do so,
so Forge will actually see it.

#### 3. Registering to event buses must be done manually.

`@EventBusSubscriber` doesn't get picked up when applied to a Kotlin `object`. You can register manually to event buses
like so in your `@Mod` class:

```java
public ExampleMod() {
    // ...Other initialization, assigning instance, etc.
    MinecraftForge.EVENT_BUS.register(MyRegistryHandler.class);
}
```

### Shadowing and ProGuard (Optional)

Since we use Kotlin, that means we need the Kotlin standard library. Of course, Minecraft and Forge don't provide that,
since they use Java and have no reason to have it in their classpath. This means we have two options:

#### 1. **Depend on a library mod that provides Kotlin stdlib:**

This is the option used pretty commonly when I've seen other mods using Kotlin. There is a library I'm aware of that
provides [exactly this](https://github.com/shadowfacts/Forgelin), but I can't recommend it at this point given how long
since its last update, and the extremely outdated version of Kotlin it uses.

You could actually use this exact repository to make such a library mod yourself, if you wish, but given what I'm about
to mention, I don't feel that it's all that worth it in the scheme of things.

####  2. **Bundle stdlib**:

The other option is to use a so-called "Fat Jar" to bundle the standard library with your mod. This has the benefit that
you don't need to worry about matching dependency versions, and that you just have a single Jar file to install as the
end user of the mod.

The downside, of course, is that Kotlin has a pretty big standard library. This brought my mod up from around ~90KB to
~1.5MB when I tested originally. That's really not the end of the world, but it feels a bit silly that a pretty tiny mod
should take up so much space just because it uses Kotlin.

For this, I looked into _ProGuard_. It's traditionally a tool for obfuscating your code, but it's also pretty good at
minifying and stripping out stuff that goes unused. It's also traditionally used in Android development
(_until R8 replaced it_), which meant another hurdle. After a bunch of testing and tweaking I eventually got it to play
nice with ForgeGradle, and there's a task called `:reobfReleaseJarJar` which builds the fat Jar, which Forge refers to
as `jarJar`, then pipes it through ProGuard to minify, and finally re-obfuscates the Minecraft mappings so it can run
against non-development environment MC.

##### The actual important part, no more backstory:

This was a pretty finicky process and ProGuard doesn't always like to play nice. Importantly you'll need to make sure
to set up `-keep` rules for anything that isn't referenced directly by your code - stuff Forge implicitly finds and
loads for you. This means rules for **your `@Mod` class**, and your **Client & Dedicated `Proxy`**, as those are
intentionally never loaded by your mod itself.

If you're fine with a +1.5MB filesize increase on your mod, which is totally reasonable, then you can ignore ProGuard.

## Final notes.

You are free to use this project as a base if you're wanting to write a 1.12.2 Forge mod with a modern toolchain. While
I did put quite a bit of time into getting this working for my own project first and I'd appreciate mention if you use
this, you are under no obligation to do so.

### Bugs?

If you find an issue with the template, feel free to create an issue on this repository. I'll try to have a look, but
there's no guarantees whether I'll get around to it quickly.

If you find an issue and are able to resolve it, I'd encourage you to make a Pull Request so that improvement can make
its way back to this repository for others to benefit from!
