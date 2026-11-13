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
