
package pt.rocket.components.customfontviews;

import pt.rocket.view.R;
import android.content.Context;
import android.view.Gravity;
import android.view.View;


public class Toast extends android.widget.Toast {
    public static final int LENGTH_LONG = android.widget.Toast.LENGTH_LONG;
    public static final int LENGTH_SHORT = android.widget.Toast.LENGTH_SHORT;

    public Toast(Context context) {
        super(context);
    }
    
    public static Toast makeText(Context context, CharSequence s, int duration) {
        Toast toast = new Toast(context);
        toast.setDuration(duration);
        TextView view = new TextView(context);
        view.setText(s);
        view.setTextColor(0xFFFFFFFF);
        view.setGravity(Gravity.CENTER);
        view.setBackgroundResource(R.drawable.toast_frame);
        toast.setView(view);
        return toast;
    }
    
//    public static Toast makeText(Context context, CharSequence s, int duration) {
//        final View view = LayoutInflater.from(context).inflate(R.layout.toast_notification, null);
//        ((TextView) view.findViewById(android.R.id.message)).setText(s);
//        Toast toast = new Toast(context);
//        toast.setView(view);
//        toast.setDuration(duration);
//        return toast;
//    }

    public static Toast makeText(Context context, int resId, int duration) {
        return Toast.makeText(context, context.getResources().getString(resId), duration);
    }

    @Override
    public void setText(CharSequence s) {
        final View view = getView();
        final TextView textView = view != null ? (TextView) view.findViewById(android.R.id.message) : null;
        if (textView == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        textView.setText(s);
    }
}
