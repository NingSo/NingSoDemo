# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/NingSo/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-ignorewarnings
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepattributes *Annotation*
-keepattributes Signature

-keep interface com.ningso.silence.downloader.interfaces.** { *; }

-keep public class com.ningso.silence.downloader.interfaces.** {
    <fields>;
    <methods>;
}

# Keep - Library. Keep all public and protected classes, fields, and methods.
-keep public class com.ningso.silence.PluginDexManager {
    public protected <fields>;
    public protected <methods>;
}

# Keep names - Native method names. Keep all native class/method names.
-keepclasseswithmembers,allowshrinking class * {
    native <methods>;
}
