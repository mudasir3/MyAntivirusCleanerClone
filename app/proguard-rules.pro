# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
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
-dontwarn javax.jdo.**
-dontwarn com.google.api.client.googleapis.extensions.android.gms.**
-dontwarn com.google.android.gms.**
-keep class android.view.accessibility.** {*;}
-dontwarn org.xmlpull.v1.**
-dontwarn javax.jdo.**
-dontwarn com.google.api.client.googleapis.extensions.android.gms.**
-keep class android.support.v4.** { *; }
-dontwarn android.support.v4.**
-keep public class org.xmlpull.v1.** { *; }
-dontwarn org.xmlpull.v1.**
-keep class com.the.together.** { *; }
-keep class com.google.ads.** { *; }
-keep class com.google.firebase.** { *; }

-keep class com.inmobi.** { *; }
-keep class org.joda.time.** { *; }
-dontwarn org.joda.time.**


-keep class android.** { *; }
-keep class **.R$* {*;}
-keep class com.google.android.gms.** { *; }
-dontwarn android.support.v4.**
-dontwarn com.adsdk.sdk.customevents.**
-dontwarn org.simpleframework.xml.stream.**
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment
-dontwarn com.inmobi.**
-keep class com.startapp.** {
      *;
}

-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile,
LineNumberTable, *Annotation*, EnclosingMethod
-dontwarn android.webkit.JavascriptInterface
-dontwarn com.startapp.**