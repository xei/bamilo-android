#------------ retrofit ------------------#
-keepattributes Signature
-keepattributes *Annotation*

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

#----------------- Models ---------------------#

-keepclassmembers class * extends com.bamilo.android.appmodule.modernbamilo.util.retrofit.pojo.BaseModel {*;}
-keep class com.bamilo.android.appmodule.bamiloapp.view.productdetail.network.model.** { *; }
-keep class com.bamilo.android.core.service.** { *; }

-keep class * implements android.os.Parcelable {
public static final android.os.Parcelable$Creator *;
}

-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
    @com.google.gson.annotations.Expose <fields>;
}

-keep @interface com.google.gson.annotations.**
-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}