# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions


-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn okhttp3.internal.platform.*

-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.OpenSSLProvider

-dontwarn com.google.gson.Gson$6

-dontwarn com.ryanharter.auto.value.gson.GenerateTypeAdapter
-dontwarn com.google.gson.TypeAdapterFactory

-keep class com.google.gson** { *; }
-keepclassmembers class com.google.gson** {*;}

-keep class android.support.v4.** { *; }
-dontnote android.support.v4.**

##----------retrofit for storelletcommonlib----##
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
#retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-dontnote retrofit2.Platform
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions
##---------------------------------------------##