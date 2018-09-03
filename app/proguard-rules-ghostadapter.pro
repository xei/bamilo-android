-keep class com.bamilo.android.framework.components.ghostadapter.BindItem
-keep @com.bamilo.android.framework.components.ghostadapter.BindItem public class * { *; }

-keepclassmembers class * extends android.support.v7.widget.RecyclerView$ViewHolder {
*;
}
