import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.room)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.viewmodel.compose)

            implementation(libs.multiplatform.settings)

            implementation(libs.coroutines.core)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.kotlinx.serialization)

            implementation(libs.ktorfit)
            implementation(libs.ktor.core)
            implementation(libs.ktor.okhttp)
            implementation(libs.ktor.negotiation)
            implementation(libs.ktor.json)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.websockets)
            implementation(libs.logback)

            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

            implementation(libs.voyager.navigator)
            implementation(libs.voyager.transitions)
            implementation(libs.voyager.lifecycle.kmp)

            implementation(libs.coil)
            implementation(libs.coil.compose)
            implementation(libs.coil.okhttp)
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.preference)

            implementation(libs.koin.android)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)

            implementation(libs.coroutines.swing)

            implementation(libs.pdfbox)
        }
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    add("kspDesktop", libs.room.compiler)
}

private val globalPackageName = "ru.rznnike.demokmp"
private val globalVersionName = "1.0.0"
private val globalVersionCode = 1
private val buildType = BuildType[System.getenv("BUILD_TYPE")]
private val debug = buildType != BuildType.RELEASE
private val runFromIDE = System.getenv("RUN_FROM_IDE").toBoolean()
private val os = if (DefaultNativePlatform.getCurrentOperatingSystem().isWindows) "windows" else "linux"

android {
    namespace = globalPackageName
    compileSdk = libs.versions.android.targetSdk.get().toInt()

    defaultConfig {
        applicationId = globalPackageName
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = globalVersionCode
        versionName = globalVersionName
    }
    packaging {
        resources {
            excludes += "/META-INF/**"
        }
    }
    buildTypes {
        register("staging") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}

compose {
    resources {
        packageOfResClass = "$globalPackageName.generated.resources"
    }

    desktop.application {
        mainClass = "$globalPackageName.app.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Deb)
            packageName = globalPackageName
            packageVersion = globalVersionName

            includeAllModules = true

            windows {
                iconFile.set(project.file("src/commonMain/composeResources/drawable/icon.ico"))
            }
            linux {
                iconFile.set(project.file("src/commonMain/composeResources/drawable/icon.png"))
            }
        }

        buildTypes {
            release {
                proguard {
                    obfuscate.set(true)
                    optimize.set(false)
                    configurationFiles.from(project.file("proguard-rules.pro"))
                }
            }
        }
    }
}

buildkonfig {
    packageName = globalPackageName

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "BUILD_TYPE", buildType.tag)
        buildConfigField(FieldSpec.Type.BOOLEAN, "DEBUG", "$debug")
        buildConfigField(FieldSpec.Type.BOOLEAN, "RUN_FROM_IDE", "$runFromIDE")
        buildConfigField(FieldSpec.Type.STRING, "OS", os)
        buildConfigField(FieldSpec.Type.STRING, "API_MAIN", "https://dog.ceo/")
        buildConfigField(FieldSpec.Type.STRING, "API_WEBSOCKETS", "wss://echo.websocket.org/")
        buildConfigField(FieldSpec.Type.STRING, "VERSION_NAME", globalVersionName)
        buildConfigField(FieldSpec.Type.INT, "VERSION_CODE", globalVersionCode.toString())
    }
}

task("clearAppBuildJarsDir") {
    doLast {
        delete("${project.rootDir}/composeApp/build/compose/jars")
    }
}

task("generateReleaseApp") {
    dependsOn("clearAppBuildJarsDir", "packageReleaseUberJarForCurrentOS")
    doLast {
        val outputPath = "${project.rootDir}/distributableOutput/$globalVersionCode"
        val executablePath = "$outputPath/application"

        delete(outputPath)
        File(executablePath).mkdirs()
        File("${project.rootDir}/composeApp/build/compose/jars")
            .listFiles()
            ?.firstOrNull()
            ?.copyTo(File("$executablePath/app.jar"))
        copy {
            from("${project.rootDir}/runScripts/${os}")
            into(outputPath)
        }
        File("$outputPath/launcher_configuration.ini").writeText(
            """
                java_path=
                single_instance_port=62740
            """.trimIndent()
        )
    }
}

task<Zip>("generateReleaseArchive") {
    dependsOn("generateReleaseApp")
    val flags = mutableListOf(buildType.tag)
    archiveFileName = "DemoKMP_${os.capitalized()}_v${globalVersionName}.${globalVersionCode}_${flags.joinToString(separator = "_")}.zip"
    destinationDirectory = file("${project.rootDir}/distributableArchive")
    from("${project.rootDir}/distributableOutput/${globalVersionCode}")
}

private enum class BuildType(
    val tag: String
) {
    DEBUG("debug"),
    STAGING("staging"),
    RELEASE("release");

    companion object {
        val default = DEBUG

        operator fun get(tag: String?) = values().find { it.tag == tag } ?: default
    }
}
