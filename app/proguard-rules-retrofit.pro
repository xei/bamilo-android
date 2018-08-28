#------------ retrofit ------------------#
-keepattributes Signature

## Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

## Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

## Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

## Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

-dontwarn retrofit2.Platform$Java8

-dontwarn okhttp3.internal.platform.ConscryptPlatform

#----------------- Models ---------------------#

-keepclassmembers class * extends com.bamilo.modernbamilo.util.retrofit.pojo.BaseModel {*;}
-keep class com.bamilo.android.appmodule.bamiloapp.view.productdetail.model.** { *; }
-keep class com.bamilo.apicore.service.** { *; }