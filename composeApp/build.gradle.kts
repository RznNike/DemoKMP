import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.internal.extensions.stdlib.toDefaultLowerCase
import org.gradle.kotlin.dsl.register
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

private val globalPackageName = "ru.rznnike.demokmp"
private val globalVersionName = "1.0.0"
private val globalVersionCode = 1
private val buildType = BuildType[System.getenv("BUILD_TYPE")]
private val debug = buildType != BuildType.RELEASE
private val runFromIDE = System.getenv("RUN_FROM_IDE").toBoolean()
private val os = if (DefaultNativePlatform.getCurrentOperatingSystem().isWindows) "windows" else "linux"
private val apkName = "DemoKMP"

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
            implementation(libs.ui.backhandler)

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
            implementation(libs.ktor.websockets)

            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

            implementation(libs.filekit.core)
            implementation(libs.filekit.dialogs.compose)

            implementation(libs.navigation)

            implementation(libs.coil)
            implementation(libs.coil.compose)
            implementation(libs.coil.okhttp)

            implementation(libs.vico.multiplatform)
            implementation(libs.vico.multiplatform.m3)
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.preference)

            implementation(libs.koin.android)

            implementation(libs.permissions)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)

            implementation(libs.coroutines.swing)

            implementation(libs.pdfbox)

            implementation(libs.logback)
        }
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspDesktop", libs.room.compiler)
}

android {
    namespace = globalPackageName
    compileSdk = libs.versions.android.targetSdk.get().toInt()
    buildToolsVersion = "36.0.0"

    signingConfigs {
        create("config") {
            storeFile = file("../demokmp.jks")
            keyAlias = "demoKey"
            if (project.hasProperty("PROJECT_KEY_PASSWORD") && project.hasProperty("PROJECT_KEYSTORE_PASSWORD")) {
                keyPassword = project.property("PROJECT_KEY_PASSWORD") as String
                storePassword = project.property("PROJECT_KEYSTORE_PASSWORD") as String
            } else {
                throw GradleException("Not found signing config password properties")
            }
        }
    }

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
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("config")
            versionNameSuffix = ".${globalVersionCode} debug"
        }
        register("staging") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-android.pro")
            signingConfig = signingConfigs.getByName("config")
            versionNameSuffix = ".${globalVersionCode} staging"
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-android.pro")
            signingConfig = signingConfigs.getByName("config")
            versionNameSuffix = ".${globalVersionCode}"
        }
    }

    lint {
        abortOnError = true
        checkAllWarnings = true
        ignoreWarnings = false
        warningsAsErrors = false
        checkDependencies = true
        htmlReport = true
        explainIssues = true
        noLines = false
        textOutput = file("stdout")
        disable.add("MissingClass")
        disable.add("NewApi")
    }

    applicationVariants.all {
        outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach {
                it.outputFileName = "${apkName}_${globalVersionName}.${globalVersionCode}_${buildType.name}.apk"
            }
    }
    afterEvaluate {
        applicationVariants.configureEach {
            val variantName = name.capitalized()
            if (variantName != "Debug") {
                project.tasks["compile${variantName}Sources"].dependsOn(project.tasks["lint${variantName}"])
            }
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }

    bundle.language.enableSplit = false

    dependencies {
        val stagingImplementation by configurations

        coreLibraryDesugaring(libs.desugaring)

        debugImplementation(libs.chucker)
        stagingImplementation(libs.chucker)
        releaseImplementation(libs.chucker.noop)
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
                    configurationFiles.from(project.file("proguard-desktop.pro"))
                }
            }
        }
    }
}

configureBuildKonfigFlavorFromAndroidTasks()

buildkonfig {
    packageName = globalPackageName

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "BUILD_TYPE", buildType.tag)
        buildConfigField(FieldSpec.Type.BOOLEAN, "DEBUG", "$debug")
        buildConfigField(FieldSpec.Type.BOOLEAN, "RUN_FROM_IDE", "$runFromIDE")
        buildConfigField(FieldSpec.Type.STRING, "OS", "")
        buildConfigField(FieldSpec.Type.STRING, "API_MAIN", "https://dog.ceo/")
        buildConfigField(FieldSpec.Type.STRING, "API_WEBSOCKETS", "wss://echo.websocket.org/")
        buildConfigField(FieldSpec.Type.STRING, "VERSION_NAME", globalVersionName)
        buildConfigField(FieldSpec.Type.INT, "VERSION_CODE", globalVersionCode.toString())
    }

    targetConfigs {
        create("android") {
            buildConfigField(FieldSpec.Type.STRING, "OS", "android")
        }
        create("desktop") {
            buildConfigField(FieldSpec.Type.STRING, "OS", os)
        }
    }

    targetConfigs("debug") {
        create("android") {
            buildConfigField(FieldSpec.Type.STRING, "BUILD_TYPE", BuildType.DEBUG.tag)
            buildConfigField(FieldSpec.Type.BOOLEAN, "DEBUG", "true")
        }
    }
    targetConfigs("staging") {
        create("android") {
            buildConfigField(FieldSpec.Type.STRING, "BUILD_TYPE", BuildType.STAGING.tag)
            buildConfigField(FieldSpec.Type.BOOLEAN, "DEBUG", "true")
        }
    }
    targetConfigs("release") {
        create("android") {
            buildConfigField(FieldSpec.Type.STRING, "BUILD_TYPE", BuildType.RELEASE.tag)
            buildConfigField(FieldSpec.Type.BOOLEAN, "DEBUG", "false")
        }
    }
}

fun configureBuildKonfigFlavorFromAndroidTasks() {
    val pattern = ":composeApp:[assemble|install|generate].*(Debug|Staging|Release)"
    val runningTasks = project.gradle.startParameter.taskNames
    runningTasks.firstOrNull { it.matches(pattern.toRegex()) }?.let { matchingTask ->
        val matcher = pattern.toPattern().matcher(matchingTask)
        if (matcher.find()) {
            val flavor = matcher.group(1).toDefaultLowerCase()
            project.setProperty("buildkonfig.flavor", flavor)
        }
    }
}

tasks.register("clearAppBuildJarsDir") {
    doLast {
        delete("${project.rootDir}/composeApp/build/compose/jars")
    }
}

tasks.register("generateReleaseApp") {
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

tasks.register<Zip>("generateReleaseArchive") {
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
