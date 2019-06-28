# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# kotlin
-dontwarn kotlin.**
-dontwarn kotlin.reflect.jvm.internal.**
-keep class kotlin.reflect.jvm.internal.** { *; }

-dontwarn org.codehaus.**

# KOTLIN COROUTINES https://github.com/Kotlin/kotlinx.coroutines
# ServiceLoader support
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# SourceFile and LineNumberTable are needed to output useful obfuscated stack traces.
-keepattributes Signature, SourceFile, LineNumberTable, InnerClasses
-optimizationpasses 5

# ==================================================================================================
# RETROFIT Configuration
# ==================================================================================================
-dontwarn retrofit2.Platform$Java8

-keep class retrofit2.** { *; }
-keepclassmembernames class * {
    @retrofit2.http.* <methods>;
}
-keepclassmembernames interface * {
    @retrofit2.http.* <methods>;
}

# Retrofit, OkHttp, Gson
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class io.reactivex.** { *; }
-keep class org.reactivestreams.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# https://github.com/square/okhttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

# END --- Retrofit # -------------------------------------------------------------------------------

-keep @interface android.support.annotation.Keep
-keep @android.support.annotation.Keep class *
-keep @android.support.annotation.Keep interface *
-keep @android.support.annotation.Keep enum *
-keepclasseswithmembers class * {
  @android.support.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
  @android.support.annotation.Keep <methods>;
}

# -- SENTRY ------
# https://docs.sentry.io/clients/java/modules/android/
-keepattributes LineNumberTable,SourceFile
-dontwarn org.slf4j.**
-dontwarn javax.**
-keep class io.sentry.event.Event { *; }

# -- LOTTIE ------
-dontwarn com.airbnb.lottie.**
-keep class com.airbnb.lottie.** {*;}

# Can't find referenced class org.bouncycastle.**
-dontwarn com.nimbusds.jose.**