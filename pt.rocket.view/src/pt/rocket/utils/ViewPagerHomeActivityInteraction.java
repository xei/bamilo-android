package pt.rocket.utils;

import android.view.View.OnClickListener;
import pt.rocket.view.fragments.FragmentType;

/**
 * Interface to communicate with the activity
 * If another type of fragment is created, add the identifier to {@link FragmentType}
 * @author manuelsilva
 *
 */
public interface ViewPagerHomeActivityInteraction {
    public void showViewPager();
}