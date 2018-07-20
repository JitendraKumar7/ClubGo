package in.clubgo.app.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import in.clubgo.app.wishlist.FragmentWishListEvents;
import in.clubgo.app.wishlist.FragmentWishListVenues;

/**
 * Created by Jitendra Soam on 13/4/16.
 */
public class WishListPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public WishListPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new FragmentWishListEvents();
            case 1:
                return new FragmentWishListVenues();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
