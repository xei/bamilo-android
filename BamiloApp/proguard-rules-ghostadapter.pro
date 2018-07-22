-keep class com.mobile.components.ghostadapter.BindItem
-keep @com.mobile.components.ghostadapter.BindItem public class * { *; }

-keepclassmembers class * extends android.support.v7.widget.RecyclerView$ViewHolder {
*;
}
