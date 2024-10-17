import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

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
    androidTarget()

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

            implementation(libs.datastore.preferences)

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
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)

            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenModel)
            implementation(libs.voyager.transitions)
            implementation(libs.voyager.lifecycle.kmp)

            implementation(libs.coroutines.swing)

            implementation(libs.coil)
            implementation(libs.coil.compose)
            implementation(libs.coil.okhttp)

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
private val debug = System.getenv("DEBUG")?.toBoolean() ?: false
private val os = if (DefaultNativePlatform.getCurrentOperatingSystem().isWindows) "windows" else "linux"

android {
    namespace = globalPackageName
    compileSdk = 34
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
                    configurationFiles.from(project.file("proguard-rules.pro"))
                }
            }
        }
    }
}

buildkonfig {
    packageName = globalPackageName

    defaultConfigs {
        buildConfigField(FieldSpec.Type.BOOLEAN, "DEBUG", "$debug")
        buildConfigField(FieldSpec.Type.STRING, "OS", os)
        buildConfigField(FieldSpec.Type.STRING, "API_MAIN", "https://dog.ceo/")
        buildConfigField(FieldSpec.Type.STRING, "API_WEBSOCKETS", "wss://echo.websocket.org/")
        buildConfigField(FieldSpec.Type.STRING, "VERSION_NAME", globalVersionName)
        buildConfigField(FieldSpec.Type.INT, "VERSION_CODE", globalVersionCode.toString())
    }
}

task("generateReleaseApp") {
    dependsOn("createReleaseDistributable")
    doLast {
        copy {
            from("${project.rootDir}/composeApp/build/compose/binaries/main-release/app/${globalPackageName}")
            into("${project.rootDir}/distributableOutput/${globalVersionCode}/application")
        }
    }
}

task<Zip>("generateReleaseArchive") {
    dependsOn("generateReleaseApp")
    val buildType = if (debug) "debug" else "release"
    archiveFileName = "DemoKMP_${os.capitalized()}_v${globalVersionName}.${globalVersionCode}_${buildType}.zip"
    destinationDirectory = file("${project.rootDir}/distributableArchive")
    from("${project.rootDir}/distributableOutput/${globalVersionCode}")
}
