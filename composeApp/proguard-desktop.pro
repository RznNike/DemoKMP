#noinspection ShrinkerUnresolvedReference

# Common
-keepattributes Exceptions,InnerClasses,Signature,*Annotation*,EnclosingMethod

# Project classes
-keep class ru.rznnike.demokmp.data.network.model.** { *; }
-keep class ru.rznnike.demokmp.domain.model.** { *; }
-keepclassmembers enum * { *; }

# Logs
-dontwarn ch.qos.logback.**

# Serialization
-keep class * {
    @kotlinx.serialization.SerialName <fields>;
}

# SQLite
-keep class androidx.sqlite.** { *; }

# Ktor
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# PdfBox
-dontwarn org.apache.commons.logging.**
-dontwarn org.apache.pdfbox.io.IOUtils

# FileKit
-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.** { *; }
-keep class org.freedesktop.** { *; }
-keep class io.github.vinceglb.filekit.** { *; }


### OkHttp ###

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keeppackagenames okhttp3.internal.publicsuffix.*
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**


### Coroutines ###

# ServiceLoader support
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keep class kotlinx.coroutines.swing.SwingDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Same story for the standard library's SafeContinuation that also uses AtomicReferenceFieldUpdater
-keepclassmembers class kotlin.coroutines.SafeContinuation {
    volatile <fields>;
}

# Only used in `kotlinx.coroutines.internal.ExceptionsConstructor`.
# The case when it is not available is hidden in a `try`-`catch`, as well as a check for Android.
-dontwarn java.lang.ClassValue