import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink

plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    mingwX64 {
        binaries {
            executable {
                baseName = "launcher"
                entryPoint = "ru.rznnike.demokmp.launcher.main"
                linkerOpts("-Wl,--subsystem,windows")
            }
        }
    }

    linuxX64 {
        binaries {
            executable {
                baseName = "launcher"
                entryPoint = "ru.rznnike.demokmp.launcher.main"
            }
        }
    }

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

val compileWindowsResources = tasks.register<Exec>("compileWindowResources") {
    if (!DefaultNativePlatform.getCurrentOperatingSystem().isWindows) return@register

    description = "compileWindowResources"
    val konanDir = System.getProperty("user.home") + "/.konan"
    val windres = fileTree(konanDir) {
        include("**/bin/windres.exe")
    }.files.firstOrNull() ?: error("windres.exe not found in $konanDir")
    environment("PATH", windres.parentFile.absolutePath + File.pathSeparator + System.getenv("PATH").orEmpty())

    val iconFile = file("src/mingwX64Main/resources/icon.ico")
    val rcFile = file("src/mingwX64Main/resources/app.rc")
    val resFile = layout.buildDirectory.file("windowsResources/app.res").get().asFile

    inputs.file(rcFile)
    inputs.file(iconFile)
    outputs.file(resFile)

    doFirst {
        resFile.parentFile.mkdirs()
    }

    commandLine(
        windres.absolutePath,
        rcFile.absolutePath,
        "-O", "coff",
        "-o", resFile.absolutePath
    )
}

tasks.withType<KotlinNativeLink>().configureEach {
    if (!DefaultNativePlatform.getCurrentOperatingSystem().isWindows) return@configureEach

    if (name.contains("mingw", ignoreCase = true)) {
        dependsOn(compileWindowsResources)
        val resFile = compileWindowsResources.get().outputs.files.singleFile
        binary.linkerOpts(resFile.absolutePath)
    }
}
